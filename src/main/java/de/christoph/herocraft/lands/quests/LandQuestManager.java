package de.christoph.herocraft.lands.quests;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.quests.BreakBlockQuest;
import de.christoph.herocraft.quests.CatchFishQuest;
import de.christoph.herocraft.quests.CraftItemQuest;
import de.christoph.herocraft.quests.DailyQuest;
import de.christoph.herocraft.quests.EatItemQuest;
import de.christoph.herocraft.quests.KillMobQuest;
import de.christoph.herocraft.quests.KillWithWeaponQuest;
import de.christoph.herocraft.quests.Quest;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class LandQuestManager implements Listener {

    public static final String PREFIX = "§e§lAnyBlocks §7§l| ";
    public static final String LAND_QUEST_ITEM_NAME = "§e§lLand Quest Geber";
    private static final String VILLAGER_NAME = "§e§lLand Questgeber";
    private static final String QUEST_GIVER_TAG = "land_quest_giver";
    private static final long SAVE_INTERVAL_TICKS = 20L * 30L;

    private final Connection connection;
    private final Map<String, List<LandQuestGiver>> questGiversByLand;
    private final Map<String, LandQuestGiver> questGiversByLocation;
    private final Set<String> dirtyQuestGiverKeys;
    private final List<Quest> questTemplates;
    private final Random random;
    private final BukkitTask autoSaveTask;

    public LandQuestManager() {
        connection = HeroCraft.getPlugin().getMySQL().getConnection();
        questGiversByLand = new HashMap<>();
        questGiversByLocation = new HashMap<>();
        dirtyQuestGiverKeys = new HashSet<>();
        questTemplates = new ArrayList<>();
        random = new Random();

        loadQuestTemplates();
        createTable();
        loadQuestGivers();
        spawnLoadedQuestGivers();
        autoSaveTask = Bukkit.getScheduler().runTaskTimer(HeroCraft.getPlugin(), this::flushDirtyQuestGivers, SAVE_INTERVAL_TICKS, SAVE_INTERVAL_TICKS);
    }

    public static ItemStack getLandQuestItem() {
        return new ItemBuilder(Material.PAPER)
                .setDisplayName(LAND_QUEST_ITEM_NAME)
                .setLore(
                        "",
                        "§7Rechtsklick auf einen Block",
                        "§7in deinem Land, um den",
                        "§7Land Quest Geber zu platzieren.",
                        "",
                        "§7Mitglieder teilen sich",
                        "§7den Fortschritt.",
                        "§7Admin: Sneak + Rechtsklick",
                        "§7zum Abbauen."
                )
                .build();
    }

    private void loadQuestTemplates() {
        for (Quest quest : DailyQuest.questTemplates) {
            if (quest instanceof BreakBlockQuest || quest instanceof CatchFishQuest || quest instanceof EatItemQuest
                    || quest instanceof KillMobQuest || quest instanceof KillWithWeaponQuest) {
                questTemplates.add(quest);
            }
        }
    }

    private void createTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `land_quest_givers` ("
                            + "`land_name` VARCHAR(100) NOT NULL, "
                            + "`x` DOUBLE NOT NULL, "
                            + "`y` DOUBLE NOT NULL, "
                            + "`z` DOUBLE NOT NULL, "
                            + "`world` VARCHAR(50) NOT NULL, "
                            + "`quest_description` VARCHAR(255), "
                            + "`progress` INT NOT NULL DEFAULT 0"
                            + ")"
            );
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Erstellen der LandQuest-Tabelle: " + e.getMessage());
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "ALTER TABLE `land_quest_givers` DROP PRIMARY KEY"
            );
            preparedStatement.execute();
        } catch (SQLException ignored) {
        }
    }

    private void loadQuestGivers() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `land_quest_givers`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LandQuestGiver questGiver = new LandQuestGiver(
                        resultSet.getString("land_name"),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        resultSet.getString("world"),
                        resultSet.getString("quest_description"),
                        resultSet.getInt("progress")
                );
                ensureQuestAssigned(questGiver);
                registerQuestGiver(questGiver);
            }
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Laden der LandQuests: " + e.getMessage());
        }
    }

    private void spawnLoadedQuestGivers() {
        for (LandQuestGiver questGiver : questGiversByLocation.values()) {
            spawnQuestGiver(questGiver);
        }
    }

    private void registerQuestGiver(LandQuestGiver questGiver) {
        questGiversByLand.computeIfAbsent(questGiver.getLandName(), key -> new ArrayList<>()).add(questGiver);
        questGiversByLocation.put(getLocationKey(questGiver), questGiver);
    }

    private void unregisterQuestGiver(LandQuestGiver questGiver) {
        List<LandQuestGiver> landQuestGivers = questGiversByLand.get(questGiver.getLandName());
        if (landQuestGivers != null) {
            landQuestGivers.remove(questGiver);
            if (landQuestGivers.isEmpty()) {
                questGiversByLand.remove(questGiver.getLandName());
            }
        }
        String locationKey = getLocationKey(questGiver);
        questGiversByLocation.remove(locationKey);
        dirtyQuestGiverKeys.remove(locationKey);
    }

    private void saveQuestGiver(LandQuestGiver questGiver) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE `land_quest_givers` SET `land_name` = ?, `quest_description` = ?, `progress` = ? "
                            + "WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?"
            );
            preparedStatement.setString(1, questGiver.getLandName());
            preparedStatement.setString(2, questGiver.getQuestDescription());
            preparedStatement.setInt(3, questGiver.getProgress());
            preparedStatement.setString(4, questGiver.getWorld());
            preparedStatement.setDouble(5, questGiver.getX());
            preparedStatement.setDouble(6, questGiver.getY());
            preparedStatement.setDouble(7, questGiver.getZ());

            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows == 0) {
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO `land_quest_givers` (`land_name`, `x`, `y`, `z`, `world`, `quest_description`, `progress`) VALUES (?,?,?,?,?,?,?)"
                );
                preparedStatement.setString(1, questGiver.getLandName());
                preparedStatement.setDouble(2, questGiver.getX());
                preparedStatement.setDouble(3, questGiver.getY());
                preparedStatement.setDouble(4, questGiver.getZ());
                preparedStatement.setString(5, questGiver.getWorld());
                preparedStatement.setString(6, questGiver.getQuestDescription());
                preparedStatement.setInt(7, questGiver.getProgress());
                preparedStatement.execute();
            }
            dirtyQuestGiverKeys.remove(getLocationKey(questGiver));
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Speichern des LandQuestgebers: " + e.getMessage());
        }
    }

    private void deleteQuestGiver(LandQuestGiver questGiver) {
        dirtyQuestGiverKeys.remove(getLocationKey(questGiver));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM `land_quest_givers` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?"
            );
            preparedStatement.setString(1, questGiver.getWorld());
            preparedStatement.setDouble(2, questGiver.getX());
            preparedStatement.setDouble(3, questGiver.getY());
            preparedStatement.setDouble(4, questGiver.getZ());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Löschen des LandQuestgebers: " + e.getMessage());
        }
    }

    private void markQuestGiverDirty(LandQuestGiver questGiver) {
        if (questGiver == null) {
            return;
        }
        dirtyQuestGiverKeys.add(getLocationKey(questGiver));
    }

    private void flushDirtyQuestGivers() {
        if (dirtyQuestGiverKeys.isEmpty()) {
            return;
        }

        List<String> dirtyLocationKeys = new ArrayList<>(dirtyQuestGiverKeys);
        for (String locationKey : dirtyLocationKeys) {
            LandQuestGiver questGiver = questGiversByLocation.get(locationKey);
            if (questGiver == null) {
                dirtyQuestGiverKeys.remove(locationKey);
                continue;
            }
            saveQuestGiver(questGiver);
        }
    }

    public void shutdown() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
        flushDirtyQuestGivers();
    }

    private void ensureQuestAssigned(LandQuestGiver questGiver) {
        if (questGiver.getCurrentQuest() != null || questTemplates.isEmpty()) {
            return;
        }
        assignRandomQuest(questGiver);
        saveQuestGiver(questGiver);
    }

    private void assignRandomQuest(LandQuestGiver questGiver) {
        if (questTemplates.isEmpty()) {
            questGiver.setQuest(null);
            return;
        }

        Quest oldQuest = questGiver.getCurrentQuest();
        Quest nextQuest = questTemplates.get(random.nextInt(questTemplates.size())).copy();
        if (oldQuest != null && questTemplates.size() > 1) {
            int tries = 0;
            while (nextQuest.getDescription().equalsIgnoreCase(oldQuest.getDescription()) && tries < 8) {
                nextQuest = questTemplates.get(random.nextInt(questTemplates.size())).copy();
                tries++;
            }
        }
        questGiver.setQuest(nextQuest);
    }

    private void spawnQuestGiver(LandQuestGiver questGiver) {
        World world = Bukkit.getWorld(questGiver.getWorld());
        if (world == null) {
            return;
        }

        Location location = new Location(world, questGiver.getX(), questGiver.getY(), questGiver.getZ());
        Chunk chunk = location.getChunk();
        if (!chunk.isLoaded()) {
            return;
        }

        Villager villager = findQuestVillager(questGiver);
        if (villager == null) {
            villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
        }
        configureVillager(villager);
    }

    private Villager findQuestVillager(LandQuestGiver questGiver) {
        World world = Bukkit.getWorld(questGiver.getWorld());
        if (world == null) {
            return null;
        }

        Location location = new Location(world, questGiver.getX(), questGiver.getY(), questGiver.getZ());
        Chunk chunk = location.getChunk();
        if (!chunk.isLoaded()) {
            return null;
        }

        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Villager)) {
                continue;
            }
            if (isSameBlock(entity.getLocation(), location)
                    && (entity.getScoreboardTags().contains(QUEST_GIVER_TAG)
                    || VILLAGER_NAME.equalsIgnoreCase(entity.getCustomName()))) {
                return (Villager) entity;
            }
        }
        return null;
    }

    private void configureVillager(Villager villager) {
        villager.setCustomName(VILLAGER_NAME);
        villager.setCustomNameVisible(true);
        villager.setPersistent(true);
        villager.setRemoveWhenFarAway(false);
        villager.setAI(false);
        villager.setGravity(false);
        villager.setCollidable(false);
        villager.setInvulnerable(true);
        villager.setCanPickupItems(false);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 250, false, false));
        villager.addScoreboardTag(QUEST_GIVER_TAG);
        villager.setProfession(Villager.Profession.LIBRARIAN);
    }

    private boolean isQuestVillager(Entity entity) {
        if (!(entity instanceof Villager)) {
            return false;
        }
        if (entity.getScoreboardTags().contains(QUEST_GIVER_TAG)) {
            return true;
        }
        return questGiversByLocation.containsKey(getLocationKey(entity.getLocation()));
    }

    private boolean isSameBlock(Location first, Location second) {
        return first.getWorld() != null
                && second.getWorld() != null
                && first.getWorld().getName().equalsIgnoreCase(second.getWorld().getName())
                && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ();
    }

    private String getLocationKey(LandQuestGiver questGiver) {
        return questGiver.getWorld()
                + ":" + ((int) Math.floor(questGiver.getX()))
                + ":" + ((int) Math.floor(questGiver.getY()))
                + ":" + ((int) Math.floor(questGiver.getZ()));
    }

    private String getLocationKey(Location location) {
        if (location == null || location.getWorld() == null) {
            return "";
        }
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    private LandQuestGiver getQuestGiverAt(Location location) {
        LandQuestGiver directMatch = questGiversByLocation.get(getLocationKey(location));
        if (directMatch != null) {
            return directMatch;
        }
        if (location == null || location.getWorld() == null) {
            return null;
        }

        LandQuestGiver nearestQuestGiver = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LandQuestGiver questGiver : questGiversByLocation.values()) {
            if (!questGiver.getWorld().equalsIgnoreCase(location.getWorld().getName())) {
                continue;
            }

            Location questLocation = new Location(location.getWorld(), questGiver.getX(), questGiver.getY(), questGiver.getZ());
            double distance = questLocation.distanceSquared(location);
            if (distance <= 2.25D && distance < nearestDistance) {
                nearestQuestGiver = questGiver;
                nearestDistance = distance;
            }
        }
        return nearestQuestGiver;
    }

    private List<LandQuestGiver> getQuestGiversForPlayer(Player player) {
        List<LandQuestGiver> questGivers = new ArrayList<>();
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land != null) {
            List<LandQuestGiver> landQuestGivers = questGiversByLand.get(land.getName());
            if (landQuestGivers != null && land.canBuild(player)) {
                questGivers.addAll(landQuestGivers);
            }
        }

        if (!questGivers.isEmpty()) {
            return questGivers;
        }

        for (List<LandQuestGiver> landQuestGivers : questGiversByLand.values()) {
            for (LandQuestGiver questGiver : landQuestGivers) {
                Land targetLand = HeroCraft.getPlugin().getLandManager().getLandByName(questGiver.getLandName());
                if (targetLand != null && targetLand.canBuild(player)) {
                    questGivers.add(questGiver);
                }
            }
        }
        return questGivers;
    }

    private void processProgressChange(LandQuestGiver questGiver, int previousProgress, Player actor) {
        Quest quest = questGiver.getCurrentQuest();
        if (quest == null || quest.getProgress() == previousProgress) {
            return;
        }
        System.out.println(quest.getProgress() + " processProgressChange");
        questGiver.setProgress(quest.getProgress());
        if (quest.isComplete()) {
            completeQuest(questGiver, actor);
            return;
        }
        markQuestGiverDirty(questGiver);
    }

    private void completeQuest(LandQuestGiver questGiver, Player actor) {
        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(questGiver.getLandName());
        if (land == null) {
            return;
        }

        String completedQuest = questGiver.getCurrentQuest() == null ? "" : questGiver.getCurrentQuest().getDescription();
        land.setCoins(land.getCoins() + Constant.QUEST_COINS);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!land.canBuild(onlinePlayer)) {
                continue;
            }
            onlinePlayer.sendMessage(PREFIX + "§aLand-Quest abgeschlossen!");
            if (actor != null) {
                onlinePlayer.sendMessage(PREFIX + "§7" + actor.getName() + " §7hat die letzte Aktion für §f" + completedQuest + " §7erledigt.");
            }
            onlinePlayer.sendMessage(PREFIX + "§7Dein Land erhält §e" + String.format("%.0f", Constant.QUEST_COINS) + " Coins§7.");
        }

        giveRandomOnlineLandPlayerChest(land);

        assignRandomQuest(questGiver);
        saveQuestGiver(questGiver);
    }

    private void giveRandomOnlineLandPlayerChest(Land land) {
        if (random.nextDouble() >= 0.40D) {
            return;
        }

        List<Player> onlineLandPlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (land.canBuild(onlinePlayer)) {
                onlineLandPlayers.add(onlinePlayer);
            }
        }

        if (onlineLandPlayers.isEmpty()) {
            return;
        }

        Player rewardPlayer = onlineLandPlayers.get(random.nextInt(onlineLandPlayers.size()));
        setNormalChestAmount(rewardPlayer, getNormalChestAmount(rewardPlayer) + 1);
        rewardPlayer.sendMessage(PREFIX + "§7Du hast eine §aSurvivalLands Kiste §7erhalten.");
    }

    private int getNormalChestAmount(Player player) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("amount");
            }
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Laden der normalen Kisten: " + e.getMessage());
        }
        return 0;
    }

    private void setNormalChestAmount(Player player, int amount) {
        try {
            PreparedStatement preparedStatement;
            if (hasNormalChestEntry(player)) {
                preparedStatement = connection.prepareStatement("UPDATE `survivalland_cases` SET `amount` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `survivalland_cases` (`uuid`, `amount`) VALUES (?, ?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, amount);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Speichern der normalen Kisten: " + e.getMessage());
        }
    }

    private boolean hasNormalChestEntry(Player player) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Prüfen der normalen Kisten: " + e.getMessage());
        }
        return false;
    }

    private void removeOneItem(Player player, ItemStack itemStack) {
        if (itemStack.getAmount() > 1) {
            itemStack.setAmount(itemStack.getAmount() - 1);
            return;
        }
        player.getInventory().setItemInMainHand(null);
    }

    private void giveQuestItem(Player player) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(getLandQuestItem());
        if (leftovers == null || leftovers.isEmpty()) {
            return;
        }
        for (ItemStack itemStack : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }
    }

    private boolean hasSpaceForVillager(Block clickedBlock) {
        Block first = clickedBlock.getRelative(BlockFace.UP);
        Block second = clickedBlock.getRelative(BlockFace.UP, 2);
        return first.isPassable() && second.isPassable();
    }

    private Land getOwnedLandForQuestPlacement(Player player, Block clickedBlock) {
        Land ownLand = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (ownLand == null || !ownLand.isOwnerUUID(player.getUniqueId().toString())) {
            return null;
        }

        Land directLand = LandManager.getLandAtLocation(clickedBlock.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if (directLand != null && directLand.getName().equalsIgnoreCase(ownLand.getName())) {
            return directLand;
        }

        Province province = ProvinceManager.getProvinceAtLocation(clickedBlock.getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        if (province != null && province.getLand().equalsIgnoreCase(ownLand.getName())) {
            return ownLand;
        }

        return null;
    }

    private void sendQuestInfo(Player player, LandQuestGiver questGiver, boolean admin) {
        Quest quest = questGiver.getCurrentQuest();
        if (quest == null) {
            player.sendMessage(PREFIX + "§7Aktuell ist keine Quest verfügbar.");
            return;
        }

        player.sendMessage("");
        player.sendMessage(PREFIX + "§7Aktuelle Land-Quest:");
        player.sendMessage(PREFIX + "§f" + quest.getDescription());
        player.sendMessage(PREFIX + "§7Fortschritt: §e" + quest.getProgress() + "§8/§e" + quest.getGoal());
        player.sendMessage(PREFIX + "§7Belohnung: §e" + String.format("%.0f", Constant.QUEST_COINS) + " Coins §7für dein Land");
        if (admin) {
            player.sendMessage(PREFIX + "§8Sneak + Rechtsklick zum Abbauen.");
        }
        player.sendMessage("");
    }

    @EventHandler
    public void onPlaceLandQuestGiver(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }
        if (!LAND_QUEST_ITEM_NAME.equalsIgnoreCase(event.getItem().getItemMeta().getDisplayName())) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            player.sendMessage(PREFIX + "§7Rechtsklicke auf einen Block in deinem Land.");
            return;
        }

        Land land = getOwnedLandForQuestPlacement(player, clickedBlock);
        if (land == null) {
            player.sendMessage(PREFIX + "§7Platziere den Land Quest Geber nur in deinem eigenen Land oder einer deiner Städte.");
            return;
        }
        if (!hasSpaceForVillager(clickedBlock)) {
            player.sendMessage(PREFIX + "§7Über dem Block ist nicht genug Platz für den Villager.");
            return;
        }

        Location spawnLocation = clickedBlock.getLocation().add(0.5D, 1.0D, 0.5D);
        LandQuestGiver questGiver = new LandQuestGiver(
                land.getName(),
                spawnLocation.getX(),
                spawnLocation.getY(),
                spawnLocation.getZ(),
                spawnLocation.getWorld().getName(),
                "",
                0
        );
        assignRandomQuest(questGiver);
        registerQuestGiver(questGiver);
        saveQuestGiver(questGiver);
        spawnQuestGiver(questGiver);
        removeOneItem(player, event.getItem());

        player.sendMessage(PREFIX + "§aLand Quest Geber platziert.");
        player.sendMessage(PREFIX + "§7Mitglieder mit Baurechten teilen sich nun diese Land-Quest.");
        player.sendMessage(PREFIX + "§7Admin: Sneak + Rechtsklick zum Abbauen.");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 0.8F, 1.2F);
    }

    @EventHandler
    public void onInteractQuestVillager(PlayerInteractEntityEvent event) {
        if (!isQuestVillager(event.getRightClicked())) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        LandQuestGiver questGiver = getQuestGiverAt(event.getRightClicked().getLocation());
        if (questGiver == null) {
            return;
        }

        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(questGiver.getLandName());
        if (land == null) {
            return;
        }

        if (player.isSneaking()) {
            if (!land.isOwnerUUID(player.getUniqueId().toString())) {
                if (land.canBuild(player)) {
                    player.sendMessage(PREFIX + "§7Nur der Admin des Landes kann mich abbauen.");
                } else {
                    player.sendMessage(PREFIX + "§7Du gehörst nicht zu meinem Land.");
                }
                return;
            }

            unregisterQuestGiver(questGiver);
            deleteQuestGiver(questGiver);
            event.getRightClicked().remove();
            giveQuestItem(player);
            player.sendMessage(PREFIX + "§7Land Quest Geber entfernt und als Papier zurückgegeben.");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            return;
        }

        if (!land.canBuild(player)) {
            player.sendMessage(PREFIX + "§7Du gehörst nicht zu meinem Land.");
            return;
        }

        ensureQuestAssigned(questGiver);
        sendQuestInfo(player, questGiver, land.isOwnerUUID(player.getUniqueId().toString()));
    }

    @EventHandler
    public void onQuestVillagerDamage(EntityDamageEvent event) {
        if (!isQuestVillager(event.getEntity())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onQuestVillagerDamageByEntity(EntityDamageByEntityEvent event) {
        if (!isQuestVillager(event.getEntity())) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        LandQuestGiver questGiver = getQuestGiverAt(event.getEntity().getLocation());
        if (questGiver == null) {
            return;
        }

        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(questGiver.getLandName());
        if (land != null && land.isOwnerUUID(player.getUniqueId().toString())) {
            player.sendMessage(PREFIX + "§7Zum Entfernen sneake und rechtsklicke den Land Quest Geber.");
        }
    }

    @EventHandler
    public void onQuestVillagerTransform(EntityTransformEvent event) {
        if (!isQuestVillager(event.getEntity())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (LandQuestGiver questGiver : questGiversByLocation.values()) {
            if (!questGiver.getWorld().equalsIgnoreCase(event.getWorld().getName())) {
                continue;
            }
            int chunkX = ((int) Math.floor(questGiver.getX())) >> 4;
            int chunkZ = ((int) Math.floor(questGiver.getZ())) >> 4;
            if (chunkX == event.getChunk().getX() && chunkZ == event.getChunk().getZ()) {
                spawnQuestGiver(questGiver);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        for (LandQuestGiver questGiver : getQuestGiversForPlayer(event.getPlayer())) {
            if (!(questGiver.getCurrentQuest() instanceof BreakBlockQuest)) {
                continue;
            }

            BreakBlockQuest quest = (BreakBlockQuest) questGiver.getCurrentQuest();
            int previousProgress = quest.getProgress();
            quest.onBreak(event.getBlock().getType());
            processProgressChange(questGiver, previousProgress, event.getPlayer());
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH || !(event.getCaught() instanceof Item)) {
            return;
        }

        for (LandQuestGiver questGiver : getQuestGiversForPlayer(event.getPlayer())) {
            if (!(questGiver.getCurrentQuest() instanceof CatchFishQuest)) {
                continue;
            }

            CatchFishQuest quest = (CatchFishQuest) questGiver.getCurrentQuest();
            int previousProgress = quest.getProgress();
            quest.onFishCaught(((Item) event.getCaught()).getItemStack());
            processProgressChange(questGiver, previousProgress, event.getPlayer());
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        for (LandQuestGiver questGiver : getQuestGiversForPlayer(event.getPlayer())) {
            if (!(questGiver.getCurrentQuest() instanceof EatItemQuest)) {
                continue;
            }

            EatItemQuest quest = (EatItemQuest) questGiver.getCurrentQuest();
            int previousProgress = quest.getProgress();
            quest.onEat(event.getItem());
            processProgressChange(questGiver, previousProgress, event.getPlayer());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        for (LandQuestGiver questGiver : getQuestGiversForPlayer(killer)) {
            if (questGiver.getCurrentQuest() instanceof KillMobQuest) {
                KillMobQuest quest = (KillMobQuest) questGiver.getCurrentQuest();
                int previousProgress = quest.getProgress();
                quest.onKill(event.getEntity().getType());
                System.out.println(quest.getProgress());
                processProgressChange(questGiver, previousProgress, killer);
            }

            if (questGiver.getCurrentQuest() instanceof KillWithWeaponQuest) {
                KillWithWeaponQuest quest = (KillWithWeaponQuest) questGiver.getCurrentQuest();
                int previousProgress = quest.getProgress();
                ItemStack weapon = killer.getInventory().getItemInMainHand();
                quest.onKill(event.getEntity().getType(), weapon);
                processProgressChange(questGiver, previousProgress, killer);
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (event.getRecipe() == null) {
            return;
        }

        for (LandQuestGiver questGiver : getQuestGiversForPlayer(player)) {
            if (!(questGiver.getCurrentQuest() instanceof CraftItemQuest)) {
                continue;
            }

            CraftItemQuest quest = (CraftItemQuest) questGiver.getCurrentQuest();
            int previousProgress = quest.getProgress();
            quest.onCraft(event.getRecipe().getResult().getType());
            processProgressChange(questGiver, previousProgress, player);
        }
    }
}