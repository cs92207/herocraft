package de.christoph.herocraft.quests;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Refactored DailyQuest:
 * - Keine direkten YAML-Saves in Events mehr.
 * - Fortschritt wird in Memory aktualisiert und über QuestStorageQueue gebatcht gespeichert.
 * - Gemeinsame Datei: quests_data.yml
 */
public class DailyQuest implements Listener, CommandExecutor {

    public FileConfiguration questConfig;
    private File questFile;

    private final Map<UUID, ArrayList<Quest>> playerQuests = new HashMap<>();

    // Gemeinsamer, gebufferter Storage (Flush Standard alle 5s)
    QuestStorageQueue storage;

    // ======== QUEST TEMPLATE LIST (unverändert aus deiner Version) ========
    public static final List<Quest> questTemplates = Arrays.asList(
            new BreakBlockQuest("Zerstöre 5 Obsidianblöcke", Material.OBSIDIAN, 5),
            new BreakBlockQuest("Zerstöre 200 Deepslate", Material.DEEPSLATE, 200),
            new BreakBlockQuest("Zerstöre 100 Netherziegel", Material.NETHER_BRICKS, 100),
            new BreakBlockQuest("Zerstöre 300 Schwarzer Basalt", Material.BLACKSTONE, 300),
            new BreakBlockQuest("Zerstöre 50 Endsteinziegel", Material.END_STONE_BRICKS, 50),
            new BreakBlockQuest("Zerstöre 150 Eis", Material.ICE, 150),
            new BreakBlockQuest("Zerstöre 5 Crying Obsidian", Material.CRYING_OBSIDIAN, 5),
            new BreakBlockQuest("Zerstöre 100 Ancient Debris", Material.ANCIENT_DEBRIS, 100),
            new BreakBlockQuest("Zerstöre 200 Prismarinziegel", Material.PRISMARINE_BRICKS, 200),
            new BreakBlockQuest("Zerstöre 120 Purpurblöcke", Material.PURPUR_BLOCK, 120),
            new BreakBlockQuest("Zerstöre 200 Bruchstein", Material.COBBLESTONE, 200),
            new BreakBlockQuest("Zerstöre 150 Quarzblöcke", Material.QUARTZ_BLOCK, 150),
            new BreakBlockQuest("Zerstöre 180 Netherrack", Material.NETHERRACK, 180),
            new BreakBlockQuest("Zerstöre 140 Seelensand", Material.SOUL_SAND, 140),
            new BreakBlockQuest("Zerstöre 160 Glowstone", Material.GLOWSTONE, 160),
            new BreakBlockQuest("Zerstöre 100 Schleimblöcke", Material.SLIME_BLOCK, 100),
            new BreakBlockQuest("Zerstöre 130 Knochenblöcke", Material.BONE_BLOCK, 130),
            new BreakBlockQuest("Zerstöre 170 Sandstein", Material.SANDSTONE, 170),
            new BreakBlockQuest("Zerstöre 110 Lapislazuliblöcke", Material.LAPIS_BLOCK, 110),

            new CatchFishQuest("Fange 15 Kugelfische", Material.PUFFERFISH, 15),
            new CatchFishQuest("Fange 25 Tropenfische", Material.TROPICAL_FISH, 25),
            new CatchFishQuest("Fange 30 Lachs", Material.SALMON, 30),
            new CatchFishQuest("Fange 50 Kabeljau", Material.COD, 50),

            new CraftItemQuest("Stelle 3 Netherite-Schwerter her", Material.NETHERITE_SWORD, 3),
            new CraftItemQuest("Stelle 15 Leuchtfeuer her", Material.BEACON, 15),
            new CraftItemQuest("Stelle 64 Bücherregale her", Material.BOOKSHELF, 64),
            new CraftItemQuest("Stelle 20 Enderkisten her", Material.ENDER_CHEST, 20),
            new CraftItemQuest("Stelle 8 Goldene Karotten her", Material.GOLDEN_CARROT, 8),
            new CraftItemQuest("Stelle 32 Tränke des Feuerwiderstands her", Material.POTION, 32),
            new CraftItemQuest("Stelle 64 TNT her", Material.TNT, 64),
            new CraftItemQuest("Stelle 20 Bruchsteinöfen her", Material.BLAST_FURNACE, 20),
            new CraftItemQuest("Stelle 10 Kartentische her", Material.CARTOGRAPHY_TABLE, 10),
            new CraftItemQuest("Stelle 15 Zielblöcke her", Material.TARGET, 15),
            new CraftItemQuest("Stelle 5 Verzauberungstische her", Material.ENCHANTING_TABLE, 5),
            new CraftItemQuest("Stelle 20 Ambosse her", Material.ANVIL, 20),
            new CraftItemQuest("Stelle 64 Papier her", Material.PAPER, 64),
            new CraftItemQuest("Stelle 16 Pfeile der Heilung her", Material.TIPPED_ARROW, 16),
            new CraftItemQuest("Stelle 10 Zielscheiben her", Material.TARGET, 10),
            new CraftItemQuest("Stelle 20 Rüstungsständer her", Material.ARMOR_STAND, 20),
            new CraftItemQuest("Stelle 12 Netherziegelzäune her", Material.NETHER_BRICK_FENCE, 12),
            new CraftItemQuest("Stelle 8 Beobachter her", Material.OBSERVER, 8),
            new CraftItemQuest("Stelle 15 Schleimblöcke her", Material.SLIME_BLOCK, 15),
            new CraftItemQuest("Stelle 6 Trichter her", Material.HOPPER, 6),

            new EatItemQuest("Iss 10 Goldene Äpfel", Material.GOLDEN_APPLE, 10),
            new EatItemQuest("Iss 5 Verzauberte Goldene Äpfel", Material.ENCHANTED_GOLDEN_APPLE, 5),
            new EatItemQuest("Iss 20 Goldene Karotten", Material.GOLDEN_CARROT, 20),
            new EatItemQuest("Iss 30 Steak", Material.COOKED_BEEF, 30),
            new EatItemQuest("Iss 50 gebratenen Lachs", Material.COOKED_SALMON, 50),
            new EatItemQuest("Iss 100 gebackene Kartoffeln", Material.BAKED_POTATO, 100),
            new EatItemQuest("Iss 64 Kürbiskuchen", Material.PUMPKIN_PIE, 64),
            new EatItemQuest("Iss 20 Pilzsuppen", Material.MUSHROOM_STEW, 20),
            new EatItemQuest("Iss 10 verdorbene Fleischstücke", Material.ROTTEN_FLESH, 10),
            new EatItemQuest("Iss 32 getrocknete Seetangblätter", Material.DRIED_KELP, 32),
            new EatItemQuest("Iss 40 gebratene Schweinekoteletts", Material.COOKED_PORKCHOP, 40),
            new EatItemQuest("Iss 15 Spinnenaugen", Material.SPIDER_EYE, 15),
            new EatItemQuest("Iss 25 gekochter Kabeljau", Material.COOKED_COD, 25),
            new EatItemQuest("Iss 50 Brot", Material.BREAD, 50),
            new EatItemQuest("Iss 30 Melonenscheiben", Material.MELON_SLICE, 30),
            new EatItemQuest("Iss 18 Karotten", Material.CARROT, 18),
            new EatItemQuest("Iss 12 verrottetes Fleisch", Material.ROTTEN_FLESH, 12),
            new EatItemQuest("Iss 20 Süßbeeren", Material.SWEET_BERRIES, 20),
            new EatItemQuest("Iss 10 Chorus Früchte", Material.CHORUS_FRUIT, 10),
            new EatItemQuest("Iss 22 gebackene Kartoffeln", Material.BAKED_POTATO, 22),

            new FindBiomeQuest("Finde das Pilzland-Biom", Biome.MUSHROOM_FIELDS),
            new FindBiomeQuest("Finde das Eis-Spikes-Biom", Biome.ICE_SPIKES),
            new FindBiomeQuest("Finde das Bambus-Dschungel-Biom", Biome.BAMBOO_JUNGLE),
            new FindBiomeQuest("Finde das Badlands-Biom", Biome.BADLANDS),
            new FindBiomeQuest("Finde das Tieferdunkel-Biom", Biome.DEEP_DARK),
            new FindBiomeQuest("Finde das Warmes-Ozean-Biom", Biome.WARM_OCEAN),
            new FindBiomeQuest("Finde das Mangrovensumpf-Biom", Biome.MANGROVE_SWAMP),
            new FindBiomeQuest("Finde das Karmesinwald-Biom", Biome.CRIMSON_FOREST),
            new FindBiomeQuest("Finde das Seelensandtal-Biom", Biome.SOUL_SAND_VALLEY),

            new KillMobQuest("Töte 25 Witherskelette", EntityType.WITHER_SKELETON, 25),
            new KillMobQuest("Töte 30 Endermänner", EntityType.ENDERMAN, 30),
            new KillMobQuest("Töte 50 Ertrunkene", EntityType.DROWNED, 50),
            new KillMobQuest("Töte 10 Eisengolems", EntityType.IRON_GOLEM, 10),
            new KillMobQuest("Töte 20 Hexen", EntityType.WITCH, 20),
            new KillMobQuest("Töte 15 Evoker", EntityType.EVOKER, 15),
            new KillMobQuest("Töte 40 Pillager", EntityType.PILLAGER, 40),
            new KillMobQuest("Töte 10 Ravager", EntityType.RAVAGER, 10),
            new KillMobQuest("Töte 3 Wither", EntityType.WITHER, 3),
            new KillMobQuest("Töte 20 Skelette", EntityType.SKELETON, 20),
            new KillMobQuest("Töte 25 Spinnen", EntityType.SPIDER, 25),
            new KillMobQuest("Töte 30 Zombies", EntityType.ZOMBIE, 30),
            new KillMobQuest("Töte 10 Creeper", EntityType.CREEPER, 10),
            new KillMobQuest("Töte 12 Slimes", EntityType.SLIME, 12),
            new KillMobQuest("Töte 8 Ghasts", EntityType.GHAST, 8),
            new KillMobQuest("Töte 6 Phantome", EntityType.PHANTOM, 6),
            new KillMobQuest("Töte 15 Hoglins", EntityType.HOGLIN, 15),
            new KillMobQuest("Töte 18 Blazes", EntityType.BLAZE, 18),
            new KillMobQuest("Töte 20 Wächter", EntityType.GUARDIAN, 20),

            new KillWithWeaponQuest("Töte 15 Creeper mit einer Holzaxt", EntityType.CREEPER, Material.WOODEN_AXE, 15),
            new KillWithWeaponQuest("Töte 10 Endermänner mit einem Goldschwert", EntityType.ENDERMAN, Material.GOLDEN_SWORD, 10),
            new KillWithWeaponQuest("Töte 5 Ghasts mit einer Armbrust", EntityType.GHAST, Material.CROSSBOW, 5),
            new KillWithWeaponQuest("Töte 7 Magmawürfel mit einem Steinschwert", EntityType.MAGMA_CUBE, Material.STONE_SWORD, 7),
            new KillWithWeaponQuest("Töte 10 Pillager mit einer Eisenspitzhacke", EntityType.PILLAGER, Material.IRON_PICKAXE, 10),
            new KillWithWeaponQuest("Töte 3 Wither mit einer Diamantspitzhacke", EntityType.WITHER, Material.DIAMOND_PICKAXE, 3),
            new KillWithWeaponQuest("Töte 20 Zombies mit einer Eisen Schaufel", EntityType.ZOMBIE, Material.IRON_SHOVEL, 20),
            new KillWithWeaponQuest("Töte 5 Schweinezombies mit einer Goldaxt", EntityType.ZOMBIFIED_PIGLIN, Material.GOLDEN_AXE, 5),
            new KillWithWeaponQuest("Töte 3 Evoker mit einer Steinaxt", EntityType.EVOKER, Material.STONE_AXE, 3),
            new KillWithWeaponQuest("Töte 10 Husk mit einer Eisenhacke", EntityType.HUSK, Material.IRON_HOE, 10),
            new KillWithWeaponQuest("Töte 8 Skelette mit einem Bogen", EntityType.SKELETON, Material.BOW, 8),
            new KillWithWeaponQuest("Töte 12 Spinnen mit einem Steinschwert", EntityType.SPIDER, Material.STONE_SWORD, 12),
            new KillWithWeaponQuest("Töte 6 Zombies mit einer Goldaxt", EntityType.ZOMBIE, Material.GOLDEN_AXE, 6),
            new KillWithWeaponQuest("Töte 10 Blazes mit einer Eisenspitzhacke", EntityType.BLAZE, Material.IRON_PICKAXE, 10),
            new KillWithWeaponQuest("Töte 5 Ghasts mit einer Armbrust", EntityType.GHAST, Material.CROSSBOW, 5),
            new KillWithWeaponQuest("Töte 9 Slimes mit einer Eisenschaufel", EntityType.SLIME, Material.IRON_SHOVEL, 9),
            new KillWithWeaponQuest("Töte 7 Phantome mit einer Eisenaxt", EntityType.PHANTOM, Material.IRON_AXE, 7),
            new KillWithWeaponQuest("Töte 4 Hoglins mit einer Holzhacke", EntityType.HOGLIN, Material.WOODEN_HOE, 4),
            new KillWithWeaponQuest("Töte 6 Witherskelette mit einer Diamantaxt", EntityType.WITHER_SKELETON, Material.DIAMOND_AXE, 6),
            new KillWithWeaponQuest("Töte 3 Wächter mit einer Steinhacke", EntityType.GUARDIAN, Material.STONE_HOE, 3),

            new TradeVillagerQuest("Kaufe 20 verzauberte Bücher bei einem Villager", Material.ENCHANTED_BOOK, 20),
            new TradeVillagerQuest("Kaufe 10 Namensschilder bei einem Villager", Material.NAME_TAG, 10),
            new TradeVillagerQuest("Kaufe 32 glitzernde Melonenscheiben bei einem Villager", Material.GLISTERING_MELON_SLICE, 32),
            new TradeVillagerQuest("Kaufe 64 Pfeile bei einem Villager", Material.ARROW, 64),
            new TradeVillagerQuest("Kaufe 15 Flaschen der Verzauberung bei einem Villager", Material.EXPERIENCE_BOTTLE, 15),
            new TradeVillagerQuest("Kaufe 5 Glocken bei einem Villager", Material.BELL, 5),
            new TradeVillagerQuest("Kaufe 64 Lapislazuli bei einem Villager", Material.LAPIS_LAZULI, 64),
            new TradeVillagerQuest("Kaufe 40 Papier bei einem Villager", Material.PAPER, 40),
            new TradeVillagerQuest("Kaufe 16 Brote bei einem Villager", Material.BREAD, 16),
            new TradeVillagerQuest("Kaufe 24 Smaragde bei einem Villager", Material.EMERALD, 24),
            new TradeVillagerQuest("Kaufe 12 Bücher bei einem Villager", Material.BOOK, 12),
            new TradeVillagerQuest("Kaufe 20 Kürbisse bei einem Villager", Material.PUMPKIN, 20)
    );
    // ======== END TEMPLATE LIST ========

    public DailyQuest() {
        loadQuestConfig();
        // Start des gebufferten Writers (Flush alle 5 Sekunden)
        storage = new QuestStorageQueue(HeroCraft.getPlugin(), questFile, questConfig, 5);
    }

    private void loadQuestConfig() {
        questFile = new File(HeroCraft.getPlugin().getDataFolder(), "quests_data.yml");
        if (!questFile.exists()) {
            try {
                questFile.getParentFile().mkdirs();
                questFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        questConfig = YamlConfiguration.loadConfiguration(questFile);
    }

    private void generateDailyQuests() {
        playerQuests.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            assignRandomQuest(player);
        }
    }

    // ===================== EVENTS =====================

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getInventory().getItemInMainHand().hasItemMeta()) return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        String displayName = itemStack.getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lQuestgeber")) {
            Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            Province province = ProvinceManager.getProvinceAtLocation(player.getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
            if(land == null || !land.canBuild(player)) {
                if(province == null || !province.canBuild(player)) {
                    player.sendMessage(Constant.PREFIX + "§7Bitte platziere den Questgeber in deinem Land, oder in deiner Stadt).");
                    return;
                }
            }
            if(itemStack.getAmount() > 1) {
                ItemStack newStack = itemStack.clone();
                newStack.setAmount(itemStack.getAmount() - 1);
                player.getInventory().setItemInMainHand(newStack);
            } else {
                player.getInventory().remove(itemStack);
            }
            QuestVillager.spawnNewQuestVillager(player);
        }
    }

    @EventHandler
    public void onPlayerClickQuestVillager(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clicked = event.getRightClicked();
        if(!(clicked instanceof Villager)) return;
        Villager villager = (Villager) clicked;
        if(villager.getCustomName() == null || !QuestVillager.VILLAGER_NAME.equalsIgnoreCase(villager.getCustomName())) return;

        event.setCancelled(true);
        QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(villager.getUniqueId());
        if(questVillager == null) return;
        questVillager.onInteract(player);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests != null) {
            for(Quest quest : quests) {
                if (quest instanceof EatItemQuest) {
                    ((EatItemQuest) quest).onEat(event.getItem());
                    storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    if (quest.isComplete()) completeQuest(player, quest);
                }
            }
        }
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) return;
        HashMap<Quest, String> landVillagerQuests = (HashMap<Quest, String>) QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        for(Quest quest : landVillagerQuests.keySet()) {
            if (quest instanceof EatItemQuest) {
                QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                if (questVillager == null) continue;
                EatItemQuest eq = (EatItemQuest) quest;
                eq.setProgress(questVillager.progress);
                eq.onEat(event.getItem());
                questVillager.progress = eq.getProgress();
                questVillager.queueSave();
                if (quest.isComplete()) questVillager.finishQuest();
            }
        }
    }

    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return;

        for(Quest quest : quests) {
            if (quest instanceof FindBiomeQuest) {
                ((FindBiomeQuest) quest).onMove(event.getTo());
                storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                if (quest.isComplete()) completeQuest(player, quest);
            }
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item)) return;
        Player player = event.getPlayer();
        ItemStack caughtItem = ((Item) event.getCaught()).getItemStack();

        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests != null) {
            for(Quest quest : quests) {
                if (quest instanceof CatchFishQuest) {
                    CatchFishQuest fishQuest = (CatchFishQuest) quest;
                    fishQuest.onFishCaught(caughtItem);
                    storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    if (quest.isComplete()) completeQuest(player, quest);
                }
            }
        }

        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) return;
        HashMap<Quest, String> landVillagerQuests = (HashMap<Quest, String>) QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        for(Quest quest : landVillagerQuests.keySet()) {
            if (quest instanceof CatchFishQuest) {
                CatchFishQuest fishQuest = (CatchFishQuest) quest;
                QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                if (questVillager == null) continue;
                fishQuest.setProgress(questVillager.progress);
                fishQuest.onFishCaught(caughtItem);
                questVillager.progress = fishQuest.getProgress();
                questVillager.queueSave();
                if (quest.isComplete()) questVillager.finishQuest();
            }
        }
    }

    @EventHandler
    public void onEntityDeathWeapon(org.bukkit.event.entity.EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests != null) {
            for(Quest quest : quests) {
                if (quest instanceof KillWithWeaponQuest) {
                    KillWithWeaponQuest weaponQuest = (KillWithWeaponQuest) quest;
                    ItemStack weapon = player.getInventory().getItemInMainHand();
                    weaponQuest.onKill(event.getEntity().getType(), weapon);
                    storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    if (quest.isComplete()) completeQuest(player, quest);
                }
            }
        }

        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) return;
        HashMap<Quest, String> landVillagerQuests = (HashMap<Quest, String>) QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        for(Quest quest : landVillagerQuests.keySet()) {
            if (quest instanceof KillWithWeaponQuest) {
                QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                if (questVillager == null) continue;
                KillWithWeaponQuest fishQuest = (KillWithWeaponQuest) quest;
                fishQuest.setProgress(questVillager.progress);
                ItemStack weapon = player.getInventory().getItemInMainHand();
                fishQuest.onKill(event.getEntity().getType(), weapon);
                questVillager.progress = fishQuest.getProgress();
                questVillager.queueSave();
                if (quest.isComplete()) questVillager.finishQuest();
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests != null) {
            for(Quest quest : quests) {
                if (quest instanceof KillMobQuest) {
                    KillMobQuest kmq = (KillMobQuest) quest;
                    kmq.onKill(event.getEntity().getType());
                    storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    if (quest.isComplete()) completeQuest(player, quest);
                }
            }
        }

        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) return;
        HashMap<Quest, String> landVillagerQuests = (HashMap<Quest, String>) QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        for(Quest quest : landVillagerQuests.keySet()) {
            if (quest instanceof KillMobQuest) {
                QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                if (questVillager == null) continue;
                KillMobQuest fishQuest = (KillMobQuest) quest;
                fishQuest.setProgress(questVillager.progress);
                fishQuest.onKill(event.getEntity().getType());
                questVillager.progress = fishQuest.getProgress();
                questVillager.queueSave();
                if (quest.isComplete()) questVillager.finishQuest();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests != null) {
            for(Quest quest : quests) {
                if (quest instanceof BreakBlockQuest) {
                    BreakBlockQuest bbq = (BreakBlockQuest) quest;
                    bbq.onBreak(event.getBlock().getType());
                    storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    if (quest.isComplete()) completeQuest(player, quest);
                }
            }
        }

        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) return;
        HashMap<Quest, String> landVillagerQuests = (HashMap<Quest, String>) QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        for(Quest quest : landVillagerQuests.keySet()) {
            if (quest instanceof BreakBlockQuest) {
                QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                if (questVillager == null) continue;
                BreakBlockQuest bq = (BreakBlockQuest) quest;
                bq.setProgress(questVillager.progress);
                bq.onBreak(event.getBlock().getType());
                questVillager.progress = bq.getProgress();
                questVillager.queueSave();
                if (quest.isComplete()) questVillager.finishQuest();
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests != null) {
            for(Quest quest : quests) {
                if (quest instanceof CraftItemQuest) {
                    CraftItemQuest ciq = (CraftItemQuest) quest;
                    ciq.onCraft(event.getRecipe().getResult().getType());
                    storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    if (quest.isComplete()) completeQuest(player, quest);
                }
            }
        }

        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) return;
        HashMap<Quest, String> landVillagerQuests = (HashMap<Quest, String>) QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        for(Quest quest : landVillagerQuests.keySet()) {
            if (quest instanceof CraftItemQuest) {
                QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                if (questVillager == null) continue;
                CraftItemQuest cq = (CraftItemQuest) quest;
                cq.setProgress(questVillager.progress);
                cq.onCraft(event.getRecipe().getResult().getType());
                questVillager.progress = cq.getProgress();
                questVillager.queueSave();
                if (quest.isComplete()) questVillager.finishQuest();
            }
        }
    }

    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory() instanceof MerchantInventory)) return;

        Player player = (Player) event.getWhoClicked();
        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests != null) {
            for(Quest quest : quests) {
                if (quest instanceof TradeVillagerQuest) {
                    TradeVillagerQuest tvq = (TradeVillagerQuest) quest;
                    ItemStack result = event.getCurrentItem();
                    if (result != null) {
                        tvq.onTrade(result);
                        storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                        if (quest.isComplete()) completeQuest(player, quest);
                    }
                }
            }
        }
    }

    // ===================== JOIN / Zuweisung / Command =====================

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // neues Schema
        long last = questConfig.getLong("players." + uuid + ".last", 0L);
        long now = System.currentTimeMillis();

        if (!playerQuests.containsKey(uuid) && (now - last > 1000L * 60 * 60 * 24)) {
            assignRandomQuest(player);
            storage.setPlayerLast(uuid, now);
        } else if(!playerQuests.containsKey(uuid)) {
            List<String> questsFrom = questConfig.getStringList("players." + uuid + ".questsFrom");
            if(questsFrom == null || questsFrom.isEmpty()) {
                // Fallback auf Alt-Schema (dein altes "QuestsFrom.<uuid>")
                questsFrom = questConfig.getStringList("QuestsFrom." + uuid.toString());
            }

            ArrayList<Quest> quests = new ArrayList<Quest>();
            if (questsFrom != null) {
                for (String questDescription : questsFrom) {
                    Quest quest = getQuestByDescription(questDescription);
                    if (quest == null) continue;
                    int prog = questConfig.getInt("players." + uuid + ".progress." + questDescription,
                            questConfig.getInt(questDescription + "." + uuid.toString(), 0)); // Alt-Fallback
                    quest.setProgress(prog);
                    quests.add(quest);
                }
            }

            if (quests.isEmpty()) {
                assignRandomQuest(player);
                storage.setPlayerLast(uuid, now);
                return;
            }
            playerQuests.put(uuid, quests);
        }
    }

    private void assignRandomQuest(Player player) {
        ArrayList<Quest> quests = new ArrayList<Quest>();
        Random r = new Random();
        quests.add(questTemplates.get(r.nextInt(questTemplates.size())).copy());
        quests.add(questTemplates.get(r.nextInt(questTemplates.size())).copy());
        quests.add(questTemplates.get(r.nextInt(questTemplates.size())).copy());

        List<String> questsFrom = new ArrayList<String>();
        for(Quest quest : quests) {
            questsFrom.add(quest.getDescription());
            storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
        }
        storage.setPlayerQuestsFrom(player.getUniqueId(), questsFrom);

        playerQuests.put(player.getUniqueId(), quests);
        player.sendMessage(Constant.PREFIX + "§7Mache deine §aTägliche Quests§7 für eine große Belohnung §0(§e/taeglichequest§0)");
    }

    @Nullable
    public static Quest getQuestByDescription(String description) {
        for(Quest quest : questTemplates) {
            if(quest.getDescription().equalsIgnoreCase(description)) {
                return quest.copy();
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null || quests.isEmpty()) {
            player.sendMessage(Constant.PREFIX + "§7Du hast deine tägliche Quest bereits erledigt. Versuche es §cMorgen erneut§7.");
            return true;
        }
        player.sendMessage("");
        player.sendMessage(Constant.PREFIX + "§0--- §e§lHeutige Quest §0---");
        for(Quest quest : quests) {
            player.sendMessage("");
            player.sendMessage(Constant.PREFIX + "§7" + quest.getDescription());
            player.sendMessage(ChatColor.GRAY + "Fortschritt: " + quest.getProgress() + "/" + quest.getGoal());
        }
        player.sendMessage(Constant.PREFIX + "§0--- §e§lHeutige Quest §0---");
        player.sendMessage("");
        return true;
    }

    public void completeQuest(Player player, Quest quest) {
        ArrayList<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return;

        quests.remove(quest);
        playerQuests.put(player.getUniqueId(), quests);
        player.sendMessage(Constant.PREFIX + "§7✔ Quest abgeschlossen!");
        player.sendMessage(Constant.PREFIX + "§0[§a+§0] §7" + Constant.QUEST_COINS + " Coins");
        HeroCraft.getPlugin().coin.addMoney(player, Constant.QUEST_COINS);

        // questsFrom aktualisieren
        List<String> from = questConfig.getStringList("players." + player.getUniqueId() + ".questsFrom");
        if (from == null || from.isEmpty()) {
            // Fallback auf Memory, falls Datei noch leer war
            from = new ArrayList<String>();
            for (Quest q : quests) from.add(q.getDescription());
        }
        // entfernen
        Iterator<String> it = from.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.equalsIgnoreCase(quest.getDescription())) {
                it.remove();
                break;
            }
        }
        storage.setPlayerQuestsFrom(player.getUniqueId(), from);

        if(quests.isEmpty()) {
            player.sendMessage(Constant.PREFIX + "§a§l✔ Alle Tagesquests abgeschlossen!");
            setChestsForPlayer(player, getChestsFromPlayer(player) + 2);
            player.sendMessage(Constant.PREFIX + "§0[§a+§0] §72 SurvivalLands Kisten");
        }
    }

    // ===================== DB-METHODEN (unverändert aus deiner Version) =====================

    public boolean isInDatabase(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getChestsFromPlayer(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return resultSet.getInt("amount");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setChestsForPlayer(Player player, int amount) {
        try {
            PreparedStatement preparedStatement;
            if(isInDatabase(player)) {
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
            e.printStackTrace();
        }
    }

    // ==================================================

}
