package de.christoph.herocraft.jobs;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class JobListener implements Listener {

    // Materialien für Holzfäller
    private static final Set<Material> HOLZ_MATERIALIEN = new HashSet<>();
    
    // Materialien für Minenarbeiter
    private static final Set<Material> STEIN_MATERIALIEN = new HashSet<>();
    
    // Materialien für Farmer
    private static final Set<Material> FARMER_MATERIALIEN = new HashSet<>();
    
    // Materialien für Landschaftsbauer
    private static final Set<Material> LANDSCHAFTSBAUER_MATERIALIEN = new HashSet<>();
    
    // Tiere für Schlachter
    private static final Set<EntityType> SCHLACHTER_TIERE = new HashSet<>();

    static {
        // Holzmaterialien
        HOLZ_MATERIALIEN.add(Material.OAK_LOG);
        HOLZ_MATERIALIEN.add(Material.BIRCH_LOG);
        HOLZ_MATERIALIEN.add(Material.SPRUCE_LOG);
        HOLZ_MATERIALIEN.add(Material.JUNGLE_LOG);
        HOLZ_MATERIALIEN.add(Material.ACACIA_LOG);
        HOLZ_MATERIALIEN.add(Material.DARK_OAK_LOG);
        HOLZ_MATERIALIEN.add(Material.MANGROVE_LOG);
        HOLZ_MATERIALIEN.add(Material.CHERRY_LOG);
        HOLZ_MATERIALIEN.add(Material.CRIMSON_STEM);
        HOLZ_MATERIALIEN.add(Material.WARPED_STEM);
        
        // Steinmaterialien
        STEIN_MATERIALIEN.add(Material.STONE);
        STEIN_MATERIALIEN.add(Material.COBBLESTONE);
        STEIN_MATERIALIEN.add(Material.GRAVEL);
        STEIN_MATERIALIEN.add(Material.GRANITE);
        STEIN_MATERIALIEN.add(Material.DIORITE);
        STEIN_MATERIALIEN.add(Material.ANDESITE);
        STEIN_MATERIALIEN.add(Material.DEEPSLATE);
        STEIN_MATERIALIEN.add(Material.COBBLED_DEEPSLATE);
        STEIN_MATERIALIEN.add(Material.BLACKSTONE);
        STEIN_MATERIALIEN.add(Material.BASALT);
        STEIN_MATERIALIEN.add(Material.NETHERRACK);
        STEIN_MATERIALIEN.add(Material.END_STONE);
        
        // Farmermaterialien (Pflanzen)
        FARMER_MATERIALIEN.add(Material.WHEAT);
        FARMER_MATERIALIEN.add(Material.CARROTS);
        FARMER_MATERIALIEN.add(Material.POTATOES);
        FARMER_MATERIALIEN.add(Material.BEETROOTS);
        FARMER_MATERIALIEN.add(Material.MELON);
        FARMER_MATERIALIEN.add(Material.PUMPKIN);
        FARMER_MATERIALIEN.add(Material.COCOA_BEANS);
        FARMER_MATERIALIEN.add(Material.NETHER_WART);
        FARMER_MATERIALIEN.add(Material.SUGAR_CANE);
        FARMER_MATERIALIEN.add(Material.BAMBOO);
        FARMER_MATERIALIEN.add(Material.CACTUS);
        FARMER_MATERIALIEN.add(Material.KELP);
        FARMER_MATERIALIEN.add(Material.SWEET_BERRIES);
        FARMER_MATERIALIEN.add(Material.GLOW_BERRIES);
        
        // Landschaftsbauermaterialien
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.DIRT);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.GRASS_BLOCK);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.COARSE_DIRT);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.PODZOL);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.MYCELIUM);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.SAND);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.RED_SAND);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.CLAY);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.GRAVEL);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.SNOW_BLOCK);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.SOUL_SAND);
        LANDSCHAFTSBAUER_MATERIALIEN.add(Material.SOUL_SOIL);
        
        // Tiere für Schlachter
        SCHLACHTER_TIERE.add(EntityType.COW);
        SCHLACHTER_TIERE.add(EntityType.PIG);
        SCHLACHTER_TIERE.add(EntityType.SHEEP);
        SCHLACHTER_TIERE.add(EntityType.CHICKEN);
        SCHLACHTER_TIERE.add(EntityType.RABBIT);
        SCHLACHTER_TIERE.add(EntityType.HORSE);
        SCHLACHTER_TIERE.add(EntityType.DONKEY);
        SCHLACHTER_TIERE.add(EntityType.MULE);
        SCHLACHTER_TIERE.add(EntityType.LLAMA);
        SCHLACHTER_TIERE.add(EntityType.FOX);
        SCHLACHTER_TIERE.add(EntityType.PANDA);
        SCHLACHTER_TIERE.add(EntityType.POLAR_BEAR);
        SCHLACHTER_TIERE.add(EntityType.BEE);
        SCHLACHTER_TIERE.add(EntityType.MOOSHROOM);
        SCHLACHTER_TIERE.add(EntityType.VILLAGER);
        SCHLACHTER_TIERE.add(EntityType.WOLF);
    }

    private static final double BASE_COINS_HOLZFAELLER = 0.5;
    private static final double BASE_COINS_MINENARBEITER = 0.4;
    private static final double BASE_COINS_FARMER = 0.3;
    private static final double BASE_COINS_SCHLACHTER = 0.7;
    private static final double BASE_COINS_LANDSCHAFTSBAUER = 0.2;
    private static final double BASE_XP = 2.0;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HeroCraft.getPlugin().getJobManager().loadPlayerJob(player);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();

        JobManager jobManager = HeroCraft.getPlugin().getJobManager();
        
        if (!jobManager.hasJob(player)) {
            return;
        }

        Job job = jobManager.getJob(player);
        JobType jobType = job.getJobType();

        boolean isJobMaterial = false;
        double coins = 0;
        double xp = BASE_XP;

        if (jobType == JobType.HOLZFAELLER && HOLZ_MATERIALIEN.contains(material)) {
            isJobMaterial = true;
            coins = BASE_COINS_HOLZFAELLER;
        } else if (jobType == JobType.MINENARBEITER && STEIN_MATERIALIEN.contains(material)) {
            isJobMaterial = true;
            coins = BASE_COINS_MINENARBEITER;
        } else if (jobType == JobType.FARMER && FARMER_MATERIALIEN.contains(material)) {
            isJobMaterial = true;
            coins = BASE_COINS_FARMER;
        } else if (jobType == JobType.LANDSCHAFTSBAUER && LANDSCHAFTSBAUER_MATERIALIEN.contains(material)) {
            isJobMaterial = true;
            coins = BASE_COINS_LANDSCHAFTSBAUER;
        }

        if (isJobMaterial) {
            // Coins mit Level-Multiplikator berechnen
            double coinsMultiplier = job.getCoinsMultiplier();
            double finalCoins = coins * coinsMultiplier;
            
            // Maximal 5 Coins pro Aktion
            if (finalCoins > 5.0) {
                finalCoins = 5.0;
            }

            // Coins hinzufügen
            HeroCraft.getPlugin().coin.addMoney(player, finalCoins);

            // XP hinzufügen und prüfen ob Level-Up
            int oldLevel = job.getLevel();
            job.addXp(xp);
            jobManager.updateJob(job);

            // Actionbar-Nachricht senden
            String actionBarMessage = "§7+§a" + String.format("%.1f", finalCoins) + " Coins §7(§e+" + String.format("%.1f", xp) + " XP§7)";
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));

            // Level-Up Nachricht in Actionbar
            if (job.getLevel() > oldLevel) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a§lLevel Up! §7Du bist jetzt Level §a" + job.getLevel() + "§7! §7(§6" + String.format("%.1fx", job.getCoinsMultiplier()) + "§7)"));
                giveRandomNormalChest(player, 0.5D);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player player = event.getEntity().getKiller();
        
        // Prüfe ob Spieler in fremdem Land/Stadt ist
        if (!canKillEntity(player, event.getEntity().getLocation())) {
            return;
        }
        
        EntityType entityType = event.getEntityType();

        JobManager jobManager = HeroCraft.getPlugin().getJobManager();

        if (!jobManager.hasJob(player)) {
            return;
        }

        Job job = jobManager.getJob(player);

        if (job.getJobType() != JobType.SCHLACHTER) {
            return;
        }

        if (!SCHLACHTER_TIERE.contains(entityType)) {
            return;
        }

        // Coins mit Level-Multiplikator berechnen
        double coinsMultiplier = job.getCoinsMultiplier();
        double finalCoins = BASE_COINS_SCHLACHTER * coinsMultiplier;
        
        // Maximal 5 Coins pro Aktion
        if (finalCoins > 5.0) {
            finalCoins = 5.0;
        }

        // Coins hinzufügen
        HeroCraft.getPlugin().coin.addMoney(player, finalCoins);

        // XP hinzufügen und prüfen ob Level-Up
        int oldLevel = job.getLevel();
        job.addXp(BASE_XP);
        jobManager.updateJob(job);

        // Actionbar-Nachricht senden
        String actionBarMessage = "§7+§a" + String.format("%.1f", finalCoins) + " Coins §7(§e+" + String.format("%.1f", BASE_XP) + " XP§7)";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));

        // Level-Up Nachricht in Actionbar
        if (job.getLevel() > oldLevel) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a§lLevel Up! §7Du bist jetzt Level §a" + job.getLevel() + "§7! §7(§6" + String.format("%.1fx", job.getCoinsMultiplier()) + "§7)"));
            giveRandomNormalChest(player, 0.5D);
        }
    }

    private void giveRandomNormalChest(Player player, double chance) {
        if (new Random().nextDouble() >= chance) {
            return;
        }

        int currentChests = getNormalChestAmount(player);
        setNormalChestAmount(player, currentChests + 1);
        player.sendMessage(Constant.PREFIX + "§7Du hast eine §aSurvivalLands Kiste §7erhalten.");
    }

    private int getNormalChestAmount(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("amount");
            }
        } catch (SQLException e) {
            System.out.println("[HeroCraft Jobs] Fehler beim Laden der normalen Kisten: " + e.getMessage());
        }
        return 0;
    }

    private void setNormalChestAmount(Player player, int amount) {
        try {
            PreparedStatement preparedStatement;
            if (hasNormalChestEntry(player)) {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("UPDATE `survivalland_cases` SET `amount` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("INSERT INTO `survivalland_cases` (`uuid`,`amount`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, amount);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft Jobs] Fehler beim Speichern der normalen Kisten: " + e.getMessage());
        }
    }

    private boolean hasNormalChestEntry(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("[HeroCraft Jobs] Fehler beim Prüfen der normalen Kisten: " + e.getMessage());
        }
        return false;
    }

    private boolean canKillEntity(Player player, org.bukkit.Location location) {
        // Prüfe ob Spieler Build-Rechte hat (herowars.build Permission)
        if (player.hasPermission("herowars.build")) {
            return true;
        }

        // Prüfe Land-Schutz
        LandManager landManager = HeroCraft.getPlugin().getLandManager();
        Land land = LandManager.getLandAtLocation(location, landManager.getAllLands());
        if (land != null) {
            if (!land.canBuild(player)) {
                // Spieler ist in fremdem Land - kein Töten erlaubt
                return false;
            }
        }

        // Prüfe Stadt/Province-Schutz
        ProvinceManager provinceManager = HeroCraft.getPlugin().getProvinceManager();
        Province province = ProvinceManager.getProvinceAtLocation(location, provinceManager.getProvinces());
        if (province != null) {
            if (!province.canBuild(player)) {
                // Spieler ist in fremder Stadt - kein Töten erlaubt
                return false;
            }
        }

        return true; // Kein Land/Stadt gefunden oder Spieler hat Rechte
    }
}

