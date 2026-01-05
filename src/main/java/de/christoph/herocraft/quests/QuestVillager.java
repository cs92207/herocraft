/*     */ package de.christoph.herocraft.quests;
/*     */
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import de.christoph.herocraft.lands.Land;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Random;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Villager;
/*     */ import org.bukkit.potion.PotionEffect;
/*     */ import org.bukkit.potion.PotionEffectType;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class QuestVillager
        /*     */ {
    /*     */   public static final String PREFIX = "§e§lQuestgeber §7§l| ";
    /*     */   public static final String VILLAGER_NAME = "§e§lQuestgeber";
    /*     */   public UUID entityID;
    /*     */   public Quest currentQuest;
    /*     */   public long giveQuest;
    /*     */   public int progress;
    /*     */   public String land;
    /*     */   public FileConfiguration config;
    /*     */
    /*     */   public QuestVillager(UUID entityID, String currentQuest, long giveQuest, int progress, String land) {
        /*  39 */     this.land = land;
        /*  40 */     this.currentQuest = DailyQuest.getQuestByDescription(currentQuest);
        /*  41 */     this.entityID = entityID;
        /*  42 */     this.giveQuest = giveQuest;
        /*  43 */     this.progress = progress;
        /*  44 */     if (this.currentQuest != null)
            /*  45 */       this.currentQuest.setProgress(progress);
        /*  46 */     this.config = HeroCraft.getPlugin().getConfig();
        /*     */   }
    /*     */
    /*     */   public void finishQuest() {
        /*  50 */     this.currentQuest = null;
        /*  51 */     saveInConfig();
        /*  52 */     Land land1 = HeroCraft.getPlugin().getLandManager().getLandByName(this.land);
        /*  53 */     land1.setCoins(land1.getCoins() + 5000.0D);
        /*  54 */     for (Player all : Bukkit.getOnlinePlayers()) {
            /*  55 */       if (land1.canBuild(all)) {
                /*  56 */         all.sendMessage("§e§lAnyBlocks §7§l| §7Neue §e§lQuestgeber §7Quest abgeschlossen.");
                /*  57 */         all.sendMessage("§0[§a+§0] §75000.0 Coins §0(Für dein Land)§7.");
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void onInteract(Player player) {
        /*  63 */     Land land1 = HeroCraft.getPlugin().getLandManager().getLandByName(this.land);
        /*  64 */     if (!land1.canBuild(player)) {
            /*  65 */       player.sendMessage("§e§lQuestgeber §7§l| §7Du gehörst nicht zu meinem Land!");
            /*     */       return;
            /*     */     }
        /*  68 */     long last = this.giveQuest;
        /*  69 */     long now = System.currentTimeMillis();
        /*  70 */     if (now - last > 86400000L) {
            /*  71 */       Quest quest = null;
            /*  72 */       while (quest == null || quest instanceof CraftItemQuest || quest instanceof FindBiomeQuest) {
                /*  73 */         quest = ((Quest)DailyQuest.questTemplates.get((new Random()).nextInt(DailyQuest.questTemplates.size()))).copy();
                /*     */       }
            /*  75 */       this.currentQuest = quest;
            /*  76 */       System.out.println(this.currentQuest.getDescription());
            /*  77 */       this.progress = 0;
            /*  78 */       this.currentQuest.setProgress(this.progress);
            /*  79 */       player.sendMessage("");
            /*  80 */       player.sendMessage("§e§lQuestgeber §7§l| §7" + this.currentQuest.getDescription());
            /*  81 */       player.sendMessage("" + ChatColor.GRAY + "Fortschritt: " + ChatColor.GRAY + "/" + this.currentQuest.getProgress());
            /*  82 */       player.sendMessage("");
            /*  83 */       this.giveQuest = System.currentTimeMillis();
            /*  84 */       saveInConfig();
            /*     */     }
        /*  86 */     if (this.currentQuest == null) {
            /*  87 */       player.sendMessage("§e§lQuestgeber §7§l| §7Ich habe heute nichts mehr für dich zu tun. Komme morgen wieder.");
            /*     */     } else {
            /*  89 */       player.sendMessage("");
            /*  90 */       player.sendMessage("§e§lQuestgeber §7§l| §7" + this.currentQuest.getDescription());
            /*  91 */       player.sendMessage("" + ChatColor.GRAY + "Fortschritt: " + ChatColor.GRAY + "/" + this.currentQuest.getProgress());
            /*  92 */       player.sendMessage("");
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void saveInConfig() {
        /*  97 */     if (this.currentQuest == null) {
            /*  98 */       this.config.set(this.entityID.toString() + ".Quest", "");
            /*     */     } else {
            /* 100 */       this.config.set(this.entityID.toString() + ".Quest", this.currentQuest.getDescription());
            /*     */     }
        /* 102 */     this.config.set(this.entityID.toString() + ".GiveQuest", Long.valueOf(this.giveQuest));
        /* 103 */     this.config.set(this.entityID.toString() + ".Progress", Integer.valueOf(this.progress));
        /* 104 */     this.config.set(this.entityID.toString() + ".Land", this.land);
        /* 105 */     HeroCraft.getPlugin().saveConfig();
        /*     */   }
    /*     */
    /*     */   public static HashMap<Quest, String> getAllQuestVillagerQuestsByLand(String land1) {
        /* 109 */     FileConfiguration config = HeroCraft.getPlugin().getConfig();
        /* 110 */     System.out.println("t1");
        /* 111 */     if (!config.contains("LandQuestVillagers." + land1)) {
            /* 112 */       return new HashMap<>();
            /*     */     }
        /* 114 */     System.out.println("t2");
        /* 115 */     List<String> landQuestVillagers = config.getStringList("LandQuestVillagers." + land1);
        /* 116 */     HashMap<Quest, String> quests = new HashMap<>();
        /* 117 */     System.out.println(quests);
        /* 118 */     for (String landQuestVillager : landQuestVillagers) {
            /* 119 */       System.out.println("t3");
            /* 120 */       Quest quest = DailyQuest.getQuestByDescription(config.getString(landQuestVillager + ".Quest"));
            /* 121 */       if (quest == null)
                /*     */         continue;
            /* 123 */       System.out.println("t4");
            /* 124 */       quest.setProgress(((QuestVillager)Objects.requireNonNull(getQuestVillagerByEntityID(UUID.fromString(landQuestVillager)))).progress);
            /* 125 */       quests.put(quest, landQuestVillager);
            /*     */     }
        /* 127 */     return quests;
        /*     */   }
    /*     */   public static void spawnNewQuestVillager(Player player) {
        /*     */     List<String> landQuestVillagers;
        /* 131 */     Land land1 = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 132 */     if (land1 == null) {
            /* 133 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Du bist in keinem Land.");
            /*     */       return;
            /*     */     }
        /* 136 */     Villager villager = (Villager)player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        /* 137 */     villager.setCustomName("§e§lQuestgeber");
        /* 138 */     villager.setCustomNameVisible(true);
        /* 139 */     villager.setPersistent(true);
        /* 140 */     villager.setRemoveWhenFarAway(false);
        /* 141 */     villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 2147483647, 500));
        /* 142 */     villager.setAI(false);
        /* 143 */     villager.setGravity(false);
        /* 144 */     (new QuestVillager(villager.getUniqueId(), "", 0L, 0, land1.getName())).saveInConfig();
        /*     */
        /* 146 */     if (HeroCraft.getPlugin().getConfig().contains("LandQuestVillagers." + land1.getName())) {
            /* 147 */       landQuestVillagers = HeroCraft.getPlugin().getConfig().getStringList("LandQuestVillagers." + land1.getName());
            /*     */     } else {
            /* 149 */       landQuestVillagers = new ArrayList<>();
            /*     */     }
        /* 151 */     landQuestVillagers.add(villager.getUniqueId().toString());
        /* 152 */     HeroCraft.getPlugin().getConfig().set("LandQuestVillagers." + land1.getName(), landQuestVillagers);
        /* 153 */     HeroCraft.getPlugin().saveConfig();
        /* 154 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Questgeber §agespawnt§7. Rechtsklicke ihn, für die erste Quest.");
        /*     */   }
    /*     */
    /*     */
    /*     */   public void queueSave() {
        /* 159 */     QuestStorageQueue storage = getStorage();
        /* 160 */     if (storage == null)
            /* 161 */       return;  String questDesc = (this.currentQuest == null) ? "" : this.currentQuest.getDescription();
        /* 162 */     storage.updateVillager(this.entityID, questDesc, Long.valueOf(this.giveQuest), Integer.valueOf(this.progress), this.land);
        /*     */   }
    /*     */
    /*     */   private QuestStorageQueue getStorage() {
        /* 166 */     return ((HeroCraft.getPlugin()).dailyQuest != null) ?
                /* 167 */       (HeroCraft.getPlugin()).dailyQuest.storage :
                /* 168 */       null;
        /*     */   }
    /*     */
    /*     */
    /*     */   @Nullable
    /*     */   public static QuestVillager getQuestVillagerByEntityID(UUID entityID) {
        /* 174 */     FileConfiguration config = HeroCraft.getPlugin().getConfig();
        /* 175 */     if (!config.contains(entityID.toString() + ".Quest")) {
            /* 176 */       return null;
            /*     */     }
        /* 178 */     return new QuestVillager(entityID, config
/*     */
/* 180 */         .getString(entityID.toString() + ".Quest"), config
/* 181 */         .getLong(entityID.toString() + ".GiveQuest"), config
/* 182 */         .getInt(entityID.toString() + ".Progress"), config
/* 183 */         .getString(entityID.toString() + ".Land"));
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\quests\QuestVillager.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */