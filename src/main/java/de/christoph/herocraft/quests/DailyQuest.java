/*     */ package de.christoph.herocraft.quests;
/*     */
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import de.christoph.herocraft.lands.Land;
/*     */ import de.christoph.herocraft.lands.LandManager;
/*     */ import de.christoph.herocraft.lands.province.Province;
/*     */ import de.christoph.herocraft.lands.province.ProvinceManager;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Biome;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.Item;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Villager;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.entity.EntityDeathEvent;
/*     */ import org.bukkit.event.inventory.CraftItemEvent;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.player.PlayerFishEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEntityEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.event.player.PlayerItemConsumeEvent;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerMoveEvent;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */
/*     */
/*     */
/*     */
/*     */ public class DailyQuest
        /*     */   implements Listener, CommandExecutor
        /*     */ {
    /*     */   public FileConfiguration questConfig;
    /*     */   private File questFile;
    /*  58 */   private final Map<UUID, ArrayList<Quest>> playerQuests = new HashMap<>();
    /*     */
    /*     */
    /*     */   QuestStorageQueue storage;
    /*     */
    /*     */
    /*  64 */   public static final List<Quest> questTemplates = Arrays.asList(new Quest[] { new BreakBlockQuest("Zerstöre 5 Obsidianblöcke", Material.OBSIDIAN, 5), new BreakBlockQuest("Zerstöre 200 Deepslate", Material.DEEPSLATE, 200), new BreakBlockQuest("Zerstöre 100 Netherziegel", Material.NETHER_BRICKS, 100), new BreakBlockQuest("Zerstöre 300 Schwarzer Basalt", Material.BLACKSTONE, 300), new BreakBlockQuest("Zerstöre 50 Endsteinziegel", Material.END_STONE_BRICKS, 50), new BreakBlockQuest("Zerstöre 150 Eis", Material.ICE, 150), new BreakBlockQuest("Zerstöre 5 Crying Obsidian", Material.CRYING_OBSIDIAN, 5), new BreakBlockQuest("Zerstöre 100 Ancient Debris", Material.ANCIENT_DEBRIS, 100), new BreakBlockQuest("Zerstöre 200 Prismarinziegel", Material.PRISMARINE_BRICKS, 200), new BreakBlockQuest("Zerstöre 120 Purpurblöcke", Material.PURPUR_BLOCK, 120), new BreakBlockQuest("Zerstöre 200 Bruchstein", Material.COBBLESTONE, 200), new BreakBlockQuest("Zerstöre 150 Quarzblöcke", Material.QUARTZ_BLOCK, 150), new BreakBlockQuest("Zerstöre 180 Netherrack", Material.NETHERRACK, 180), new BreakBlockQuest("Zerstöre 140 Seelensand", Material.SOUL_SAND, 140), new BreakBlockQuest("Zerstöre 160 Glowstone", Material.GLOWSTONE, 160), new BreakBlockQuest("Zerstöre 100 Schleimblöcke", Material.SLIME_BLOCK, 100), new BreakBlockQuest("Zerstöre 130 Knochenblöcke", Material.BONE_BLOCK, 130), new BreakBlockQuest("Zerstöre 170 Sandstein", Material.SANDSTONE, 170), new BreakBlockQuest("Zerstöre 110 Lapislazuliblöcke", Material.LAPIS_BLOCK, 110), new CatchFishQuest("Fange 15 Kugelfische", Material.PUFFERFISH, 15), new CatchFishQuest("Fange 25 Tropenfische", Material.TROPICAL_FISH, 25), new CatchFishQuest("Fange 30 Lachs", Material.SALMON, 30), new CatchFishQuest("Fange 50 Kabeljau", Material.COD, 50), new CraftItemQuest("Stelle 3 Netherite-Schwerter her", Material.NETHERITE_SWORD, 3), new CraftItemQuest("Stelle 15 Leuchtfeuer her", Material.BEACON, 15), new CraftItemQuest("Stelle 64 Bücherregale her", Material.BOOKSHELF, 64), new CraftItemQuest("Stelle 20 Enderkisten her", Material.ENDER_CHEST, 20), new CraftItemQuest("Stelle 8 Goldene Karotten her", Material.GOLDEN_CARROT, 8), new CraftItemQuest("Stelle 32 Tränke des Feuerwiderstands her", Material.POTION, 32), new CraftItemQuest("Stelle 64 TNT her", Material.TNT, 64), new CraftItemQuest("Stelle 20 Bruchsteinöfen her", Material.BLAST_FURNACE, 20), new CraftItemQuest("Stelle 10 Kartentische her", Material.CARTOGRAPHY_TABLE, 10), new CraftItemQuest("Stelle 15 Zielblöcke her", Material.TARGET, 15), new CraftItemQuest("Stelle 5 Verzauberungstische her", Material.ENCHANTING_TABLE, 5), new CraftItemQuest("Stelle 20 Ambosse her", Material.ANVIL, 20), new CraftItemQuest("Stelle 64 Papier her", Material.PAPER, 64), new CraftItemQuest("Stelle 16 Pfeile der Heilung her", Material.TIPPED_ARROW, 16), new CraftItemQuest("Stelle 10 Zielscheiben her", Material.TARGET, 10), new CraftItemQuest("Stelle 20 Rüstungsständer her", Material.ARMOR_STAND, 20), new CraftItemQuest("Stelle 12 Netherziegelzäune her", Material.NETHER_BRICK_FENCE, 12), new CraftItemQuest("Stelle 8 Beobachter her", Material.OBSERVER, 8), new CraftItemQuest("Stelle 15 Schleimblöcke her", Material.SLIME_BLOCK, 15), new CraftItemQuest("Stelle 6 Trichter her", Material.HOPPER, 6), new EatItemQuest("Iss 10 Goldene Äpfel", Material.GOLDEN_APPLE, 10), new EatItemQuest("Iss 5 Verzauberte Goldene Äpfel", Material.ENCHANTED_GOLDEN_APPLE, 5), new EatItemQuest("Iss 20 Goldene Karotten", Material.GOLDEN_CARROT, 20), new EatItemQuest("Iss 30 Steak", Material.COOKED_BEEF, 30), new EatItemQuest("Iss 50 gebratenen Lachs", Material.COOKED_SALMON, 50), new EatItemQuest("Iss 100 gebackene Kartoffeln", Material.BAKED_POTATO, 100), new EatItemQuest("Iss 64 Kürbiskuchen", Material.PUMPKIN_PIE, 64), new EatItemQuest("Iss 20 Pilzsuppen", Material.MUSHROOM_STEW, 20), new EatItemQuest("Iss 10 verdorbene Fleischstücke", Material.ROTTEN_FLESH, 10), new EatItemQuest("Iss 32 getrocknete Seetangblätter", Material.DRIED_KELP, 32), new EatItemQuest("Iss 40 gebratene Schweinekoteletts", Material.COOKED_PORKCHOP, 40), new EatItemQuest("Iss 15 Spinnenaugen", Material.SPIDER_EYE, 15), new EatItemQuest("Iss 25 gekochter Kabeljau", Material.COOKED_COD, 25), new EatItemQuest("Iss 50 Brot", Material.BREAD, 50), new EatItemQuest("Iss 30 Melonenscheiben", Material.MELON_SLICE, 30), new EatItemQuest("Iss 18 Karotten", Material.CARROT, 18), new EatItemQuest("Iss 12 verrottetes Fleisch", Material.ROTTEN_FLESH, 12), new EatItemQuest("Iss 20 Süßbeeren", Material.SWEET_BERRIES, 20), new EatItemQuest("Iss 10 Chorus Früchte", Material.CHORUS_FRUIT, 10), new EatItemQuest("Iss 22 gebackene Kartoffeln", Material.BAKED_POTATO, 22), new FindBiomeQuest("Finde das Pilzland-Biom", Biome.MUSHROOM_FIELDS), new FindBiomeQuest("Finde das Eis-Spikes-Biom", Biome.ICE_SPIKES), new FindBiomeQuest("Finde das Bambus-Dschungel-Biom", Biome.BAMBOO_JUNGLE), new FindBiomeQuest("Finde das Badlands-Biom", Biome.BADLANDS), new FindBiomeQuest("Finde das Tieferdunkel-Biom", Biome.DEEP_DARK), new FindBiomeQuest("Finde das Warmes-Ozean-Biom", Biome.WARM_OCEAN), new FindBiomeQuest("Finde das Mangrovensumpf-Biom", Biome.MANGROVE_SWAMP), new FindBiomeQuest("Finde das Karmesinwald-Biom", Biome.CRIMSON_FOREST), new FindBiomeQuest("Finde das Seelensandtal-Biom", Biome.SOUL_SAND_VALLEY), new KillMobQuest("Töte 25 Witherskelette", EntityType.WITHER_SKELETON, 25), new KillMobQuest("Töte 30 Endermänner", EntityType.ENDERMAN, 30), new KillMobQuest("Töte 50 Ertrunkene", EntityType.DROWNED, 50), new KillMobQuest("Töte 10 Eisengolems", EntityType.IRON_GOLEM, 10), new KillMobQuest("Töte 20 Hexen", EntityType.WITCH, 20), new KillMobQuest("Töte 15 Evoker", EntityType.EVOKER, 15), new KillMobQuest("Töte 40 Pillager", EntityType.PILLAGER, 40), new KillMobQuest("Töte 10 Ravager", EntityType.RAVAGER, 10), new KillMobQuest("Töte 3 Wither", EntityType.WITHER, 3), new KillMobQuest("Töte 20 Skelette", EntityType.SKELETON, 20), new KillMobQuest("Töte 25 Spinnen", EntityType.SPIDER, 25), new KillMobQuest("Töte 30 Zombies", EntityType.ZOMBIE, 30), new KillMobQuest("Töte 10 Creeper", EntityType.CREEPER, 10), new KillMobQuest("Töte 12 Slimes", EntityType.SLIME, 12), new KillMobQuest("Töte 8 Ghasts", EntityType.GHAST, 8), new KillMobQuest("Töte 6 Phantome", EntityType.PHANTOM, 6), new KillMobQuest("Töte 15 Hoglins", EntityType.HOGLIN, 15), new KillMobQuest("Töte 18 Blazes", EntityType.BLAZE, 18), new KillMobQuest("Töte 20 Wächter", EntityType.GUARDIAN, 20), new KillWithWeaponQuest("Töte 15 Creeper mit einer Holzaxt", EntityType.CREEPER, Material.WOODEN_AXE, 15), new KillWithWeaponQuest("Töte 10 Endermänner mit einem Goldschwert", EntityType.ENDERMAN, Material.GOLDEN_SWORD, 10), new KillWithWeaponQuest("Töte 5 Ghasts mit einer Armbrust", EntityType.GHAST, Material.CROSSBOW, 5), new KillWithWeaponQuest("Töte 7 Magmawürfel mit einem Steinschwert", EntityType.MAGMA_CUBE, Material.STONE_SWORD, 7), new KillWithWeaponQuest("Töte 10 Pillager mit einer Eisenspitzhacke", EntityType.PILLAGER, Material.IRON_PICKAXE, 10), new KillWithWeaponQuest("Töte 3 Wither mit einer Diamantspitzhacke", EntityType.WITHER, Material.DIAMOND_PICKAXE, 3), new KillWithWeaponQuest("Töte 20 Zombies mit einer Eisen Schaufel", EntityType.ZOMBIE, Material.IRON_SHOVEL, 20), new KillWithWeaponQuest("Töte 5 Schweinezombies mit einer Goldaxt", EntityType.ZOMBIFIED_PIGLIN, Material.GOLDEN_AXE, 5), new KillWithWeaponQuest("Töte 3 Evoker mit einer Steinaxt", EntityType.EVOKER, Material.STONE_AXE, 3), new KillWithWeaponQuest("Töte 10 Husk mit einer Eisenhacke", EntityType.HUSK, Material.IRON_HOE, 10), new KillWithWeaponQuest("Töte 8 Skelette mit einem Bogen", EntityType.SKELETON, Material.BOW, 8), new KillWithWeaponQuest("Töte 12 Spinnen mit einem Steinschwert", EntityType.SPIDER, Material.STONE_SWORD, 12), new KillWithWeaponQuest("Töte 6 Zombies mit einer Goldaxt", EntityType.ZOMBIE, Material.GOLDEN_AXE, 6), new KillWithWeaponQuest("Töte 10 Blazes mit einer Eisenspitzhacke", EntityType.BLAZE, Material.IRON_PICKAXE, 10), new KillWithWeaponQuest("Töte 5 Ghasts mit einer Armbrust", EntityType.GHAST, Material.CROSSBOW, 5), new KillWithWeaponQuest("Töte 9 Slimes mit einer Eisenschaufel", EntityType.SLIME, Material.IRON_SHOVEL, 9), new KillWithWeaponQuest("Töte 7 Phantome mit einer Eisenaxt", EntityType.PHANTOM, Material.IRON_AXE, 7), new KillWithWeaponQuest("Töte 4 Hoglins mit einer Holzhacke", EntityType.HOGLIN, Material.WOODEN_HOE, 4), new KillWithWeaponQuest("Töte 6 Witherskelette mit einer Diamantaxt", EntityType.WITHER_SKELETON, Material.DIAMOND_AXE, 6), new KillWithWeaponQuest("Töte 3 Wächter mit einer Steinhacke", EntityType.GUARDIAN, Material.STONE_HOE, 3), new TradeVillagerQuest("Kaufe 20 verzauberte Bücher bei einem Villager", Material.ENCHANTED_BOOK, 20), new TradeVillagerQuest("Kaufe 10 Namensschilder bei einem Villager", Material.NAME_TAG, 10), new TradeVillagerQuest("Kaufe 32 glitzernde Melonenscheiben bei einem Villager", Material.GLISTERING_MELON_SLICE, 32), new TradeVillagerQuest("Kaufe 64 Pfeile bei einem Villager", Material.ARROW, 64), new TradeVillagerQuest("Kaufe 15 Flaschen der Verzauberung bei einem Villager", Material.EXPERIENCE_BOTTLE, 15), new TradeVillagerQuest("Kaufe 5 Glocken bei einem Villager", Material.BELL, 5), new TradeVillagerQuest("Kaufe 64 Lapislazuli bei einem Villager", Material.LAPIS_LAZULI, 64), new TradeVillagerQuest("Kaufe 40 Papier bei einem Villager", Material.PAPER, 40), new TradeVillagerQuest("Kaufe 16 Brote bei einem Villager", Material.BREAD, 16), new TradeVillagerQuest("Kaufe 24 Smaragde bei einem Villager", Material.EMERALD, 24), new TradeVillagerQuest("Kaufe 12 Bücher bei einem Villager", Material.BOOK, 12), new TradeVillagerQuest("Kaufe 20 Kürbisse bei einem Villager", Material.PUMPKIN, 20) });
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   public DailyQuest() {
        /* 199 */     loadQuestConfig();
        /*     */
        /* 201 */     this.storage = new QuestStorageQueue(HeroCraft.getPlugin(), this.questFile, this.questConfig, 5);
        /*     */   }
    /*     */
    /*     */   private void loadQuestConfig() {
        /* 205 */     this.questFile = new File(HeroCraft.getPlugin().getDataFolder(), "quests_data.yml");
        /* 206 */     if (!this.questFile.exists()) {
            /*     */       try {
                /* 208 */         this.questFile.getParentFile().mkdirs();
                /* 209 */         this.questFile.createNewFile();
                /* 210 */       } catch (IOException e) {
                /* 211 */         e.printStackTrace();
                /*     */       }
            /*     */     }
        /* 214 */     this.questConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.questFile);
        /*     */   }
    /*     */
    /*     */   private void generateDailyQuests() {
        /* 218 */     this.playerQuests.clear();
        /* 219 */     for (Player player : Bukkit.getOnlinePlayers()) {
            /* 220 */       assignRandomQuest(player);
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerInteract(PlayerInteractEvent event) {
        /* 228 */     Player player = event.getPlayer();
        /* 229 */     if (!player.getInventory().getItemInMainHand().hasItemMeta())
            /* 230 */       return;  if (!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            /*     */       return;
        /* 232 */     ItemStack itemStack = player.getInventory().getItemInMainHand();
        /* 233 */     String displayName = itemStack.getItemMeta().getDisplayName();
        /* 234 */     if (displayName.equalsIgnoreCase("§4§lQuestgeber")) {
            /* 235 */       Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            /* 236 */       Province province = ProvinceManager.getProvinceAtLocation(player.getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
            /* 237 */       if ((land == null || !land.canBuild(player)) && (
                    /* 238 */         province == null || !province.canBuild(player))) {
                /* 239 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Bitte platziere den Questgeber in deinem Land, oder in deiner Stadt).");
                /*     */
                /*     */         return;
                /*     */       }
            /* 243 */       if (itemStack.getAmount() > 1) {
                /* 244 */         ItemStack newStack = itemStack.clone();
                /* 245 */         newStack.setAmount(itemStack.getAmount() - 1);
                /* 246 */         player.getInventory().setItemInMainHand(newStack);
                /*     */       } else {
                /* 248 */         player.getInventory().remove(itemStack);
                /*     */       }
            /* 250 */       QuestVillager.spawnNewQuestVillager(player);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerClickQuestVillager(PlayerInteractEntityEvent event) {
        /* 256 */     Player player = event.getPlayer();
        /* 257 */     Entity clicked = event.getRightClicked();
        /* 258 */     if (!(clicked instanceof Villager))
            /* 259 */       return;  Villager villager = (Villager)clicked;
        /* 260 */     if (villager.getCustomName() == null || !"§e§lQuestgeber".equalsIgnoreCase(villager.getCustomName()))
            /*     */       return;
        /* 262 */     event.setCancelled(true);
        /* 263 */     QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(villager.getUniqueId());
        /* 264 */     if (questVillager == null)
            /* 265 */       return;  questVillager.onInteract(player);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onItemConsume(PlayerItemConsumeEvent event) {
        /* 270 */     Player player = event.getPlayer();
        /* 271 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 272 */     if (quests != null)
            /* 273 */       for (Quest quest : quests) {
                /* 274 */         if (quest instanceof EatItemQuest) {
                    /* 275 */           ((EatItemQuest)quest).onEat(event.getItem());
                    /* 276 */           this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    /* 277 */           if (quest.isComplete()) completeQuest(player, quest);
                    /*     */
                    /*     */         }
                /*     */       }
        /* 281 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 282 */     if (land == null)
            /* 283 */       return;  HashMap<Quest, String> landVillagerQuests = QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        /* 284 */     for (Quest quest : landVillagerQuests.keySet()) {
            /* 285 */       if (quest instanceof EatItemQuest) {
                /* 286 */         QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                /* 287 */         if (questVillager == null)
                    /* 288 */           continue;  EatItemQuest eq = (EatItemQuest)quest;
                /* 289 */         eq.setProgress(questVillager.progress);
                /* 290 */         eq.onEat(event.getItem());
                /* 291 */         questVillager.progress = eq.getProgress();
                /* 292 */         questVillager.queueSave();
                /* 293 */         if (quest.isComplete()) questVillager.finishQuest();
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerMove(PlayerMoveEvent event) {
        /* 300 */     Player player = event.getPlayer();
        /* 301 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 302 */     if (quests == null)
            /*     */       return;
        /* 304 */     for (Quest quest : quests) {
            /* 305 */       if (quest instanceof FindBiomeQuest) {
                /* 306 */         ((FindBiomeQuest)quest).onMove(event.getTo());
                /* 307 */         this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                /* 308 */         if (quest.isComplete()) completeQuest(player, quest);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFish(PlayerFishEvent event) {
        /* 315 */     if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
            /* 316 */       return;  if (!(event.getCaught() instanceof Item))
            /* 317 */       return;  Player player = event.getPlayer();
        /* 318 */     ItemStack caughtItem = ((Item)event.getCaught()).getItemStack();
        /*     */
        /* 320 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 321 */     if (quests != null) {
            /* 322 */       for (Quest quest : quests) {
                /* 323 */         if (quest instanceof CatchFishQuest) {
                    /* 324 */           CatchFishQuest fishQuest = (CatchFishQuest)quest;
                    /* 325 */           fishQuest.onFishCaught(caughtItem);
                    /* 326 */           this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    /* 327 */           if (quest.isComplete()) completeQuest(player, quest);
                    /*     */
                    /*     */         }
                /*     */       }
            /*     */     }
        /* 332 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 333 */     if (land == null)
            /* 334 */       return;  HashMap<Quest, String> landVillagerQuests = QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        /* 335 */     for (Quest quest : landVillagerQuests.keySet()) {
            /* 336 */       if (quest instanceof CatchFishQuest) {
                /* 337 */         CatchFishQuest fishQuest = (CatchFishQuest)quest;
                /* 338 */         QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                /* 339 */         if (questVillager == null)
                    /* 340 */           continue;  fishQuest.setProgress(questVillager.progress);
                /* 341 */         fishQuest.onFishCaught(caughtItem);
                /* 342 */         questVillager.progress = fishQuest.getProgress();
                /* 343 */         questVillager.queueSave();
                /* 344 */         if (quest.isComplete()) questVillager.finishQuest();
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onEntityDeathWeapon(EntityDeathEvent event) {
        /* 351 */     Player player = event.getEntity().getKiller();
        /* 352 */     if (player == null)
            /*     */       return;
        /* 354 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 355 */     if (quests != null) {
            /* 356 */       for (Quest quest : quests) {
                /* 357 */         if (quest instanceof KillWithWeaponQuest) {
                    /* 358 */           KillWithWeaponQuest weaponQuest = (KillWithWeaponQuest)quest;
                    /* 359 */           ItemStack weapon = player.getInventory().getItemInMainHand();
                    /* 360 */           weaponQuest.onKill(event.getEntity().getType(), weapon);
                    /* 361 */           this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    /* 362 */           if (quest.isComplete()) completeQuest(player, quest);
                    /*     */
                    /*     */         }
                /*     */       }
            /*     */     }
        /* 367 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 368 */     if (land == null)
            /* 369 */       return;  HashMap<Quest, String> landVillagerQuests = QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        /* 370 */     for (Quest quest : landVillagerQuests.keySet()) {
            /* 371 */       if (quest instanceof KillWithWeaponQuest) {
                /* 372 */         QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                /* 373 */         if (questVillager == null)
                    /* 374 */           continue;  KillWithWeaponQuest fishQuest = (KillWithWeaponQuest)quest;
                /* 375 */         fishQuest.setProgress(questVillager.progress);
                /* 376 */         ItemStack weapon = player.getInventory().getItemInMainHand();
                /* 377 */         fishQuest.onKill(event.getEntity().getType(), weapon);
                /* 378 */         questVillager.progress = fishQuest.getProgress();
                /* 379 */         questVillager.queueSave();
                /* 380 */         if (quest.isComplete()) questVillager.finishQuest();
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onEntityDeath(EntityDeathEvent event) {
        /* 387 */     Player player = event.getEntity().getKiller();
        /* 388 */     if (player == null)
            /*     */       return;
        /* 390 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 391 */     if (quests != null) {
            /* 392 */       for (Quest quest : quests) {
                /* 393 */         if (quest instanceof KillMobQuest) {
                    /* 394 */           KillMobQuest kmq = (KillMobQuest)quest;
                    /* 395 */           kmq.onKill(event.getEntity().getType());
                    /* 396 */           this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    /* 397 */           if (quest.isComplete()) completeQuest(player, quest);
                    /*     */
                    /*     */         }
                /*     */       }
            /*     */     }
        /* 402 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 403 */     if (land == null)
            /* 404 */       return;  HashMap<Quest, String> landVillagerQuests = QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        /* 405 */     for (Quest quest : landVillagerQuests.keySet()) {
            /* 406 */       if (quest instanceof KillMobQuest) {
                /* 407 */         QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                /* 408 */         if (questVillager == null)
                    /* 409 */           continue;  KillMobQuest fishQuest = (KillMobQuest)quest;
                /* 410 */         fishQuest.setProgress(questVillager.progress);
                /* 411 */         fishQuest.onKill(event.getEntity().getType());
                /* 412 */         questVillager.progress = fishQuest.getProgress();
                /* 413 */         questVillager.queueSave();
                /* 414 */         if (quest.isComplete()) questVillager.finishQuest();
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onBlockBreak(BlockBreakEvent event) {
        /* 421 */     Player player = event.getPlayer();
        /* 422 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 423 */     if (quests != null) {
            /* 424 */       for (Quest quest : quests) {
                /* 425 */         if (quest instanceof BreakBlockQuest) {
                    /* 426 */           BreakBlockQuest bbq = (BreakBlockQuest)quest;
                    /* 427 */           bbq.onBreak(event.getBlock().getType());
                    /* 428 */           this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    /* 429 */           if (quest.isComplete()) completeQuest(player, quest);
                    /*     */
                    /*     */         }
                /*     */       }
            /*     */     }
        /* 434 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 435 */     if (land == null)
            /* 436 */       return;  HashMap<Quest, String> landVillagerQuests = QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        /* 437 */     for (Quest quest : landVillagerQuests.keySet()) {
            /* 438 */       if (quest instanceof BreakBlockQuest) {
                /* 439 */         QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                /* 440 */         if (questVillager == null)
                    /* 441 */           continue;  BreakBlockQuest bq = (BreakBlockQuest)quest;
                /* 442 */         bq.setProgress(questVillager.progress);
                /* 443 */         bq.onBreak(event.getBlock().getType());
                /* 444 */         questVillager.progress = bq.getProgress();
                /* 445 */         questVillager.queueSave();
                /* 446 */         if (quest.isComplete()) questVillager.finishQuest();
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onCraft(CraftItemEvent event) {
        /* 453 */     if (!(event.getWhoClicked() instanceof Player))
            /* 454 */       return;  Player player = (Player)event.getWhoClicked();
        /*     */
        /* 456 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 457 */     if (quests != null) {
            /* 458 */       for (Quest quest : quests) {
                /* 459 */         if (quest instanceof CraftItemQuest) {
                    /* 460 */           CraftItemQuest ciq = (CraftItemQuest)quest;
                    /* 461 */           ciq.onCraft(event.getRecipe().getResult().getType());
                    /* 462 */           this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                    /* 463 */           if (quest.isComplete()) completeQuest(player, quest);
                    /*     */
                    /*     */         }
                /*     */       }
            /*     */     }
        /* 468 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 469 */     if (land == null)
            /* 470 */       return;  HashMap<Quest, String> landVillagerQuests = QuestVillager.getAllQuestVillagerQuestsByLand(land.getName());
        /* 471 */     for (Quest quest : landVillagerQuests.keySet()) {
            /* 472 */       if (quest instanceof CraftItemQuest) {
                /* 473 */         QuestVillager questVillager = QuestVillager.getQuestVillagerByEntityID(UUID.fromString(landVillagerQuests.get(quest)));
                /* 474 */         if (questVillager == null)
                    /* 475 */           continue;  CraftItemQuest cq = (CraftItemQuest)quest;
                /* 476 */         cq.setProgress(questVillager.progress);
                /* 477 */         cq.onCraft(event.getRecipe().getResult().getType());
                /* 478 */         questVillager.progress = cq.getProgress();
                /* 479 */         questVillager.queueSave();
                /* 480 */         if (quest.isComplete()) questVillager.finishQuest();
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onTrade(InventoryClickEvent event) {
        /* 487 */     if (!(event.getWhoClicked() instanceof Player))
            /* 488 */       return;  if (!(event.getInventory() instanceof org.bukkit.inventory.MerchantInventory))
            /*     */       return;
        /* 490 */     Player player = (Player)event.getWhoClicked();
        /* 491 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 492 */     if (quests != null) {
            /* 493 */       for (Quest quest : quests) {
                /* 494 */         if (quest instanceof TradeVillagerQuest) {
                    /* 495 */           TradeVillagerQuest tvq = (TradeVillagerQuest)quest;
                    /* 496 */           ItemStack result = event.getCurrentItem();
                    /* 497 */           if (result != null) {
                        /* 498 */             tvq.onTrade(result);
                        /* 499 */             this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
                        /* 500 */             if (quest.isComplete()) completeQuest(player, quest);
                        /*     */
                        /*     */           }
                    /*     */         }
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerJoin(PlayerJoinEvent event) {
        /* 511 */     Player player = event.getPlayer();
        /* 512 */     UUID uuid = player.getUniqueId();
        /*     */
        /*     */
        /* 515 */     long last = this.questConfig.getLong("players." + uuid + ".last", 0L);
        /* 516 */     long now = System.currentTimeMillis();
        /*     */
        /* 518 */     if (!this.playerQuests.containsKey(uuid) && now - last > 86400000L) {
            /* 519 */       assignRandomQuest(player);
            /* 520 */       this.storage.setPlayerLast(uuid, now);
            /* 521 */     } else if (!this.playerQuests.containsKey(uuid)) {
            /* 522 */       List<String> questsFrom = this.questConfig.getStringList("players." + uuid + ".questsFrom");
            /* 523 */       if (questsFrom == null || questsFrom.isEmpty())
                /*     */       {
                /* 525 */         questsFrom = this.questConfig.getStringList("QuestsFrom." + uuid.toString());
                /*     */       }
            /*     */
            /* 528 */       ArrayList<Quest> quests = new ArrayList<>();
            /* 529 */       if (questsFrom != null) {
                /* 530 */         for (String questDescription : questsFrom) {
                    /* 531 */           Quest quest = getQuestByDescription(questDescription);
                    /* 532 */           if (quest == null)
                        /* 533 */             continue;  int prog = this.questConfig.getInt("players." + uuid + ".progress." + questDescription, this.questConfig
/* 534 */               .getInt(questDescription + "." + questDescription, 0));
                    /* 535 */           quest.setProgress(prog);
                    /* 536 */           quests.add(quest);
                    /*     */         }
                /*     */       }
            /*     */
            /* 540 */       if (quests.isEmpty()) {
                /* 541 */         assignRandomQuest(player);
                /* 542 */         this.storage.setPlayerLast(uuid, now);
                /*     */         return;
                /*     */       }
            /* 545 */       this.playerQuests.put(uuid, quests);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   private void assignRandomQuest(Player player) {
        /* 550 */     ArrayList<Quest> quests = new ArrayList<>();
        /* 551 */     Random r = new Random();
        /* 552 */     quests.add(((Quest)questTemplates.get(r.nextInt(questTemplates.size()))).copy());
        /* 553 */     quests.add(((Quest)questTemplates.get(r.nextInt(questTemplates.size()))).copy());
        /* 554 */     quests.add(((Quest)questTemplates.get(r.nextInt(questTemplates.size()))).copy());
        /*     */
        /* 556 */     List<String> questsFrom = new ArrayList<>();
        /* 557 */     for (Quest quest : quests) {
            /* 558 */       questsFrom.add(quest.getDescription());
            /* 559 */       this.storage.setPlayerQuestProgress(player.getUniqueId(), quest.getDescription(), quest.getProgress());
            /*     */     }
        /* 561 */     this.storage.setPlayerQuestsFrom(player.getUniqueId(), questsFrom);
        /*     */
        /* 563 */     this.playerQuests.put(player.getUniqueId(), quests);
        /* 564 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Mache deine §aTägliche Quests§7 für eine große Belohnung §0(§e/taeglichequest§0)");
        /*     */   }
    /*     */
    /*     */   @Nullable
    /*     */   public static Quest getQuestByDescription(String description) {
        /* 569 */     for (Quest quest : questTemplates) {
            /* 570 */       if (quest.getDescription().equalsIgnoreCase(description)) {
                /* 571 */         return quest.copy();
                /*     */       }
            /*     */     }
        /* 574 */     return null;
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* 579 */     if (!(sender instanceof Player)) return true;
        /* 580 */     Player player = (Player)sender;
        /* 581 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 582 */     if (quests == null || quests.isEmpty()) {
            /* 583 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Du hast deine tägliche Quest bereits erledigt. Versuche es §cMorgen erneut§7.");
            /* 584 */       return true;
            /*     */     }
        /* 586 */     player.sendMessage("");
        /* 587 */     player.sendMessage("§e§lAnyBlocks §7§l| §0--- §e§lHeutige Quest §0---");
        /* 588 */     for (Quest quest : quests) {
            /* 589 */       player.sendMessage("");
            /* 590 */       player.sendMessage("§e§lAnyBlocks §7§l| §7" + quest.getDescription());
            /* 591 */       player.sendMessage("" + ChatColor.GRAY + "Fortschritt: " + ChatColor.GRAY + "/" + quest.getProgress());
            /*     */     }
        /* 593 */     player.sendMessage("§e§lAnyBlocks §7§l| §0--- §e§lHeutige Quest §0---");
        /* 594 */     player.sendMessage("");
        /* 595 */     return true;
        /*     */   }
    /*     */
    /*     */   public void completeQuest(Player player, Quest quest) {
        /* 599 */     ArrayList<Quest> quests = this.playerQuests.get(player.getUniqueId());
        /* 600 */     if (quests == null)
            /*     */       return;
        /* 602 */     quests.remove(quest);
        /* 603 */     this.playerQuests.put(player.getUniqueId(), quests);
        /* 604 */     player.sendMessage("§e§lAnyBlocks §7§l| §7✔ Quest abgeschlossen!");
        /* 605 */     player.sendMessage("§e§lAnyBlocks §7§l| §0[§a+§0] §75000.0 Coins");
        /* 606 */     (HeroCraft.getPlugin()).coin.addMoney(player, 5000.0D);
        /*     */
        /*     */
        /* 609 */     List<String> from = this.questConfig.getStringList("players." + player.getUniqueId() + ".questsFrom");
        /* 610 */     if (from == null || from.isEmpty()) {
            /*     */
            /* 612 */       from = new ArrayList<>();
            /* 613 */       for (Quest q : quests) from.add(q.getDescription());
            /*     */
            /*     */     }
        /* 616 */     Iterator<String> it = from.iterator();
        /* 617 */     while (it.hasNext()) {
            /* 618 */       String s = it.next();
            /* 619 */       if (s.equalsIgnoreCase(quest.getDescription())) {
                /* 620 */         it.remove();
                /*     */         break;
                /*     */       }
            /*     */     }
        /* 624 */     this.storage.setPlayerQuestsFrom(player.getUniqueId(), from);
        /*     */
        /* 626 */     if (quests.isEmpty()) {
            /* 627 */       player.sendMessage("§e§lAnyBlocks §7§l| §a§l✔ Alle Tagesquests abgeschlossen!");
            /* 628 */       setChestsForPlayer(player, getChestsFromPlayer(player) + 2);
            /* 629 */       player.sendMessage("§e§lAnyBlocks §7§l| §0[§a+§0] §72 SurvivalLands Kisten");
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */   public boolean isInDatabase(Player player) {
        /*     */     try {
            /* 637 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            /* 638 */       preparedStatement.setString(1, player.getUniqueId().toString());
            /* 639 */       ResultSet resultSet = preparedStatement.executeQuery();
            /* 640 */       if (resultSet.next())
                /* 641 */         return true;
            /* 642 */     } catch (SQLException e) {
            /* 643 */       e.printStackTrace();
            /*     */     }
        /* 645 */     return false;
        /*     */   }
    /*     */
    /*     */   public int getChestsFromPlayer(Player player) {
        /*     */     try {
            /* 650 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            /* 651 */       preparedStatement.setString(1, player.getUniqueId().toString());
            /* 652 */       ResultSet resultSet = preparedStatement.executeQuery();
            /* 653 */       if (resultSet.next())
                /* 654 */         return resultSet.getInt("amount");
            /* 655 */     } catch (SQLException e) {
            /* 656 */       e.printStackTrace();
            /*     */     }
        /* 658 */     return 0;
        /*     */   }
    /*     */
    /*     */   public void setChestsForPlayer(Player player, int amount) {
        /*     */     try {
            /*     */       PreparedStatement preparedStatement;
            /* 664 */       if (isInDatabase(player)) {
                /* 665 */         preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("UPDATE `survivalland_cases` SET `amount` = ? WHERE `uuid` = ?");
                /* 666 */         preparedStatement.setInt(1, amount);
                /* 667 */         preparedStatement.setString(2, player.getUniqueId().toString());
                /*     */       } else {
                /* 669 */         preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("INSERT INTO `survivalland_cases` (`uuid`,`amount`) VALUES (?,?)");
                /* 670 */         preparedStatement.setString(1, player.getUniqueId().toString());
                /* 671 */         preparedStatement.setInt(2, amount);
                /*     */       }
            /* 673 */       preparedStatement.execute();
            /* 674 */     } catch (SQLException e) {
            /* 675 */       e.printStackTrace();
            /*     */     }
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\quests\DailyQuest.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */