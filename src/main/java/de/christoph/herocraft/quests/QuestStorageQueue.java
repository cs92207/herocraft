/*     */ package de.christoph.herocraft.quests;
/*     */
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.server.PluginDisableEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.K;

/*     */
/*     */
/*     */ public class QuestStorageQueue
        /*     */   implements Listener
        /*     */ {
    /*     */   private final HeroCraft plugin;
    /*     */   private final File questFile;
    /*     */   private final FileConfiguration cfg;
    /*  30 */   private final Map<UUID, PlayerDelta> playerDeltas = new ConcurrentHashMap<>();
    /*     */
    /*     */
    /*  33 */   private final Map<UUID, VillagerDelta> villagerDeltas = new ConcurrentHashMap<>();
    /*     */
    /*     */
    /*  36 */   private final Map<String, Set<UUID>> landVillagerSetsToMerge = new ConcurrentHashMap<>();
    /*     */
    /*  38 */   private int flushTaskId = -1;
    /*     */   private final int flushSeconds;
    /*     */
    /*     */   public QuestStorageQueue(HeroCraft plugin, File questFile, FileConfiguration cfg, int flushSeconds) {
        /*  42 */     this.plugin = plugin;
        /*  43 */     this.questFile = questFile;
        /*  44 */     this.cfg = cfg;
        /*  45 */     this.flushSeconds = Math.max(2, flushSeconds);
        /*     */
        /*  47 */     Bukkit.getPluginManager().registerEvents(this, (Plugin)plugin);
        /*  48 */     startFlushScheduler();
        /*     */   }
    /*     */
    /*     */   public static class PlayerDelta {
        /*     */     public Long last;
        /*     */     public List<String> questsFrom;
        /*  54 */     public Map<String, Integer> progress = new HashMap<>();
        /*     */   }
    /*     */
    /*     */   public static class VillagerDelta
            /*     */   {
        /*     */     public String quest;
        /*     */     public Long giveQuest;
        /*     */     public Integer progress;
        /*     */     public String land;
        /*     */   }
    /*     */
    /*     */   public void setPlayerLast(UUID player, long last) {
        /*  66 */     ((PlayerDelta)this.playerDeltas.computeIfAbsent(player, k -> new PlayerDelta())).last = Long.valueOf(last);
        /*     */   }
    /*     */
    /*     */
    /*     */   public void setPlayerQuestsFrom(UUID player, List<String> questsFrom) {
        /*  71 */     ((PlayerDelta)this.playerDeltas.computeIfAbsent(player, k -> new PlayerDelta())).questsFrom = new ArrayList<>(questsFrom);
        /*     */   }
    /*     */
    /*     */
    /*     */   public void setPlayerQuestProgress(UUID player, String questDescription, int progress) {
        /*  76 */     ((PlayerDelta)this.playerDeltas.computeIfAbsent(player, k -> new PlayerDelta())).progress.put(questDescription, Integer.valueOf(progress));
        /*     */   }
    /*     */
    /*     */
    /*     */   public void updateVillager(UUID villagerId, String quest, Long giveQuest, Integer progress, String land) {
        /*  81 */     VillagerDelta d = this.villagerDeltas.computeIfAbsent(villagerId, k -> new VillagerDelta());
        /*  82 */     if (quest != null) d.quest = quest;
        /*  83 */     if (giveQuest != null) d.giveQuest = giveQuest;
        /*  84 */     if (progress != null) d.progress = progress;
        /*  85 */     if (land != null) d.land = land;
        /*  86 */     if (land != null) {
            /*  87 */       ((Set<UUID>)this.landVillagerSetsToMerge.computeIfAbsent(land, k -> new HashSet())).add(villagerId);
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   public void addVillagerToLand(String land, UUID villagerId) {
        /*  93 */     ((Set<UUID>)this.landVillagerSetsToMerge.computeIfAbsent(land, k -> new HashSet())).add(villagerId);
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */   public void flushNow() {
        /*  99 */     if (!Bukkit.isPrimaryThread()) {
            /* 100 */       Bukkit.getScheduler().runTask((Plugin)this.plugin, this::applyAndSave);
            /*     */     } else {
            /* 102 */       applyAndSave();
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private void startFlushScheduler() {
        /* 108 */     this
                /*     */
                /*     */
                /*     */
                /*     */
                /* 113 */       .flushTaskId = (new BukkitRunnable() { public void run() { QuestStorageQueue.this.applyAndSave(); } }).runTaskTimer((Plugin)this.plugin, 20L * this.flushSeconds, 20L * this.flushSeconds).getTaskId();
        /*     */   }
    /*     */
    /*     */   private void applyAndSave() {
        /* 117 */     boolean dirty = false;
        /*     */
        /*     */
        /* 120 */     if (!this.playerDeltas.isEmpty()) {
            /* 121 */       for (Map.Entry<UUID, PlayerDelta> e : this.playerDeltas.entrySet()) {
                /* 122 */         UUID uuid = e.getKey();
                /* 123 */         PlayerDelta d = e.getValue();
                /*     */
                /* 125 */         String base = "players." + uuid.toString();
                /* 126 */         if (d.last != null) {
                    /* 127 */           this.cfg.set(base + ".last", d.last);
                    /* 128 */           dirty = true;
                    /*     */         }
                /* 130 */         if (d.questsFrom != null) {
                    /* 131 */           this.cfg.set(base + ".questsFrom", d.questsFrom);
                    /* 132 */           dirty = true;
                    /*     */         }
                /* 134 */         if (!d.progress.isEmpty()) {
                    /* 135 */           for (Map.Entry<String, Integer> p : d.progress.entrySet()) {
                        /* 136 */             this.cfg.set(base + ".progress." + base, p.getValue());
                        /* 137 */             dirty = true;
                        /*     */           }
                    /*     */         }
                /*     */       }
            /* 141 */       this.playerDeltas.clear();
            /*     */     }
        /*     */
        /*     */
        /* 145 */     if (!this.villagerDeltas.isEmpty()) {
            /* 146 */       for (Map.Entry<UUID, VillagerDelta> e : this.villagerDeltas.entrySet()) {
                /* 147 */         UUID id = e.getKey();
                /* 148 */         VillagerDelta d = e.getValue();
                /*     */
                /* 150 */         String base = "villagers." + id.toString();
                /* 151 */         if (d.quest != null) {
                    /* 152 */           this.cfg.set(base + ".quest", d.quest);
                    /* 153 */           dirty = true;
                    /*     */         }
                /* 155 */         if (d.giveQuest != null) {
                    /* 156 */           this.cfg.set(base + ".giveQuest", d.giveQuest);
                    /* 157 */           dirty = true;
                    /*     */         }
                /* 159 */         if (d.progress != null) {
                    /* 160 */           this.cfg.set(base + ".progress", d.progress);
                    /* 161 */           dirty = true;
                    /*     */         }
                /* 163 */         if (d.land != null) {
                    /* 164 */           this.cfg.set(base + ".land", d.land);
                    /* 165 */           dirty = true;
                    /*     */         }
                /*     */       }
            /* 168 */       this.villagerDeltas.clear();
            /*     */     }
        /*     */
        /*     */
        /* 172 */     if (!this.landVillagerSetsToMerge.isEmpty()) {
            /* 173 */       for (Map.Entry<String, Set<UUID>> e : this.landVillagerSetsToMerge.entrySet()) {
                /* 174 */         String land = e.getKey();
                /* 175 */         Set<UUID> toAdd = e.getValue();
                /*     */
                /* 177 */         List<String> current = this.cfg.getStringList("villagersByLand." + land);
                /* 178 */         Set<String> merged = new LinkedHashSet<>(current);
                /* 179 */         for (UUID id : toAdd) merged.add(id.toString());
                /* 180 */         this.cfg.set("villagersByLand." + land, new ArrayList<>(merged));
                /* 181 */         dirty = true;
                /*     */       }
            /* 183 */       this.landVillagerSetsToMerge.clear();
            /*     */     }
        /*     */
        /* 186 */     if (dirty) {
            /*     */       try {
                /* 188 */         this.cfg.save(this.questFile);
                /* 189 */       } catch (IOException ex) {
                /* 190 */         ex.printStackTrace();
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onDisable(PluginDisableEvent e) {
        /* 197 */     if (e.getPlugin() == this.plugin) {
            /*     */
            /* 199 */       applyAndSave();
            /* 200 */       if (this.flushTaskId != -1) Bukkit.getScheduler().cancelTask(this.flushTaskId);
            /*     */     }
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\quests\QuestStorageQueue.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */