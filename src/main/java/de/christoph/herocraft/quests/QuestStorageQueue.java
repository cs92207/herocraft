package de.christoph.herocraft.quests;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zentraler Write-Buffer für alle Quest-Daten.
 * - Keine Disk-Writes mehr in Events.
 * - Batch-Flush alle N Sekunden (default 5s) im Main-Thread.
 */
public class QuestStorageQueue implements Listener {

    private final HeroCraft plugin;
    private final File questFile;
    private final FileConfiguration cfg;

    // Buffer: nur Deltas halten → minimiert Schreibarbeit
    // players:<uuid>: { last?, questsFrom?[], progress?<map> }
    private final Map<UUID, PlayerDelta> playerDeltas = new ConcurrentHashMap<>();

    // villagers:<uuid>: full fields gespeichert als Delta
    private final Map<UUID, VillagerDelta> villagerDeltas = new ConcurrentHashMap<>();

    // villagersByLand:<land> -> set of UUIDs (wir halten auch Deltas dafür)
    private final Map<String, Set<UUID>> landVillagerSetsToMerge = new ConcurrentHashMap<>();

    private int flushTaskId = -1;
    private final int flushSeconds;

    public QuestStorageQueue(HeroCraft plugin, File questFile, FileConfiguration cfg, int flushSeconds) {
        this.plugin = plugin;
        this.questFile = questFile;
        this.cfg = cfg;
        this.flushSeconds = Math.max(2, flushSeconds);
        // Listener registrieren, damit wir bei Disable flushen
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startFlushScheduler();
    }

    public static class PlayerDelta {
        public Long last; // nullable
        public List<String> questsFrom; // nullable - ersetzt komplett
        public Map<String, Integer> progress = new HashMap<>(); // questDesc -> progress increment/absolute (wir speichern absolute)
    }

    public static class VillagerDelta {
        public String quest; // "" = keine
        public Long giveQuest;
        public Integer progress;
        public String land;
    }

    /** Spieler: last setzen */
    public void setPlayerLast(UUID player, long last) {
        playerDeltas.computeIfAbsent(player, k -> new PlayerDelta()).last = last;
    }

    /** Spieler: gesamte questsFrom Liste ersetzen */
    public void setPlayerQuestsFrom(UUID player, List<String> questsFrom) {
        playerDeltas.computeIfAbsent(player, k -> new PlayerDelta()).questsFrom = new ArrayList<>(questsFrom);
    }

    /** Spieler: Quest-Fortschritt als ABSOLUTEN Wert setzen */
    public void setPlayerQuestProgress(UUID player, String questDescription, int progress) {
        playerDeltas.computeIfAbsent(player, k -> new PlayerDelta()).progress.put(questDescription, progress);
    }

    /** Villager: kompletten Satz Felder aktualisieren (nur was != null ist wird übernommen) */
    public void updateVillager(UUID villagerId, String quest, Long giveQuest, Integer progress, String land) {
        VillagerDelta d = villagerDeltas.computeIfAbsent(villagerId, k -> new VillagerDelta());
        if (quest != null) d.quest = quest;
        if (giveQuest != null) d.giveQuest = giveQuest;
        if (progress != null) d.progress = progress;
        if (land != null) d.land = land;
        if (land != null) { // auch Mapping pflegen
            landVillagerSetsToMerge.computeIfAbsent(land, k -> new HashSet<>()).add(villagerId);
        }
    }

    /** Villager: Zuordnung Land → Villager-UUID mergen */
    public void addVillagerToLand(String land, UUID villagerId) {
        landVillagerSetsToMerge.computeIfAbsent(land, k -> new HashSet<>()).add(villagerId);
    }

    /** Öffentliche Flush-Methode (falls du manuell flushen willst) */
    public void flushNow() {
        // wir führen Flush synchron im Main-Thread aus
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(plugin, this::applyAndSave);
        } else {
            applyAndSave();
        }
    }

    private void startFlushScheduler() {
        // Alle N Sekunden im Main Thread flushen
        flushTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                applyAndSave();
            }
        }.runTaskTimer(plugin, 20L * flushSeconds, 20L * flushSeconds).getTaskId();
    }

    private void applyAndSave() {
        boolean dirty = false;

        // Players
        if (!playerDeltas.isEmpty()) {
            for (Map.Entry<UUID, PlayerDelta> e : playerDeltas.entrySet()) {
                UUID uuid = e.getKey();
                PlayerDelta d = e.getValue();

                String base = "players." + uuid.toString();
                if (d.last != null) {
                    cfg.set(base + ".last", d.last);
                    dirty = true;
                }
                if (d.questsFrom != null) {
                    cfg.set(base + ".questsFrom", d.questsFrom);
                    dirty = true;
                }
                if (!d.progress.isEmpty()) {
                    for (Map.Entry<String, Integer> p : d.progress.entrySet()) {
                        cfg.set(base + ".progress." + p.getKey(), p.getValue());
                        dirty = true;
                    }
                }
            }
            playerDeltas.clear();
        }

        // Villagers
        if (!villagerDeltas.isEmpty()) {
            for (Map.Entry<UUID, VillagerDelta> e : villagerDeltas.entrySet()) {
                UUID id = e.getKey();
                VillagerDelta d = e.getValue();

                String base = "villagers." + id.toString();
                if (d.quest != null) {
                    cfg.set(base + ".quest", d.quest);
                    dirty = true;
                }
                if (d.giveQuest != null) {
                    cfg.set(base + ".giveQuest", d.giveQuest);
                    dirty = true;
                }
                if (d.progress != null) {
                    cfg.set(base + ".progress", d.progress);
                    dirty = true;
                }
                if (d.land != null) {
                    cfg.set(base + ".land", d.land);
                    dirty = true;
                }
            }
            villagerDeltas.clear();
        }

        // villagersByLand
        if (!landVillagerSetsToMerge.isEmpty()) {
            for (Map.Entry<String, Set<UUID>> e : landVillagerSetsToMerge.entrySet()) {
                String land = e.getKey();
                Set<UUID> toAdd = e.getValue();

                List<String> current = cfg.getStringList("villagersByLand." + land);
                Set<String> merged = new LinkedHashSet<>(current);
                for (UUID id : toAdd) merged.add(id.toString());
                cfg.set("villagersByLand." + land, new ArrayList<>(merged));
                dirty = true;
            }
            landVillagerSetsToMerge.clear();
        }

        if (dirty) {
            try {
                cfg.save(questFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        if (e.getPlugin() == plugin) {
            // Letzter Flush beim Plugin-Disable
            applyAndSave();
            if (flushTaskId != -1) Bukkit.getScheduler().cancelTask(flushTaskId);
        }
    }
}
