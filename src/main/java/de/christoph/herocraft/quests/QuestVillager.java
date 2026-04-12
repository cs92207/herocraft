package de.christoph.herocraft.quests;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class QuestVillager implements Listener {
    public static final String PREFIX = "§e§lQuestgeber §7§l| ";
    public static final String VILLAGER_NAME = "§e§lQuestgeber";
    public UUID entityID;
    public Quest currentQuest;
    public long giveQuest;
    public int progress;
    public String land;
    public FileConfiguration config;
    public static File questsDataFile = new File("plugins/HeroCraft/quests_data.yml");
    public static YamlConfiguration questsDataConfig = YamlConfiguration.loadConfiguration(questsDataFile);

    public QuestVillager(UUID entityID, String currentQuest, long giveQuest, int progress, String land) {
        this.land = land;
        this.currentQuest = DailyQuest.getQuestByDescription(currentQuest);
        this.entityID = entityID;
        this.giveQuest = giveQuest;
        this.progress = progress;
        if (this.currentQuest != null)
            this.currentQuest.setProgress(progress);
        this.config = questsDataConfig;
    }

    public void finishQuest() {
        this.currentQuest = null;
        saveInConfig();
        Land land1 = HeroCraft.getPlugin().getLandManager().getLandByName(this.land);
        land1.setCoins(land1.getCoins() + 5000.0D);
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (land1.canBuild(all)) {
                all.sendMessage("§e§lAnyBlocks §7§l| §7Neue §e§lQuestgeber §7Quest abgeschlossen.");
                all.sendMessage("§0[§a+§0] §75000.0 Coins §0(Für dein Land)§7.");
            }
        }
    }

    public void onInteract(Player player) {
        Land land1 = HeroCraft.getPlugin().getLandManager().getLandByName(this.land);
        if (!land1.canBuild(player)) {
            player.sendMessage("§e§lQuestgeber §7§l| §7Du gehörst nicht zu meinem Land!");
            return;
        }
        long last = this.giveQuest;
        long now = System.currentTimeMillis();
        if (now - last > 86400000L) {
            Quest quest = null;
            while (quest == null || quest instanceof CraftItemQuest || quest instanceof FindBiomeQuest) {
                quest = ((Quest)DailyQuest.questTemplates.get((new Random()).nextInt(DailyQuest.questTemplates.size()))).copy();
            }
            this.currentQuest = quest;
            System.out.println(this.currentQuest.getDescription());
            this.progress = 0;
            this.currentQuest.setProgress(this.progress);
            player.sendMessage("");
            player.sendMessage("§e§lQuestgeber §7§l| §7" + this.currentQuest.getDescription());
            player.sendMessage(ChatColor.GRAY + "Fortschritt: " + ChatColor.GRAY + "/" + this.currentQuest.getProgress());
            player.sendMessage("");
            this.giveQuest = System.currentTimeMillis();
            saveInConfig();
        }
        if (this.currentQuest == null) {
            player.sendMessage("§e§lQuestgeber §7§l| §7Ich habe heute nichts mehr für dich zu tun. Komme morgen wieder.");
        } else {
            player.sendMessage("");
            player.sendMessage("§e§lQuestgeber §7§l| §7" + this.currentQuest.getDescription());
            player.sendMessage(ChatColor.GRAY + "Fortschritt: " + ChatColor.GRAY + "/" + this.currentQuest.getProgress());
            player.sendMessage("");
        }
    }

    public void saveInConfig() {
        if (this.currentQuest == null) {
            questsDataConfig.set("villagers." + this.entityID.toString() + ".quest", "");
        } else {
            questsDataConfig.set("villagers." + this.entityID.toString() + ".quest", this.currentQuest.getDescription());
        }
        questsDataConfig.set("villagers." + this.entityID.toString() + ".giveQuest", this.giveQuest);
        questsDataConfig.set("villagers." + this.entityID.toString() + ".progress", this.progress);
        questsDataConfig.set("villagers." + this.entityID.toString() + ".land", this.land);
        System.out.println("[QuestVillager] Speichere Progress: Villager=" + this.entityID + ", Quest='" + (this.currentQuest != null ? this.currentQuest.getDescription() : "") + "', Progress=" + this.progress);
        try {
            questsDataConfig.save(questsDataFile);
            System.out.println("[QuestVillager] Speichern erfolgreich.");
        } catch (IOException e) {
            System.out.println("[QuestVillager] Fehler beim Speichern!");
            e.printStackTrace();
        }
    }

    public static HashMap<Quest, String> getAllQuestVillagerQuestsByLand(String land1) {
        if (!questsDataConfig.contains("villagersByLand." + land1)) {
            return new HashMap<>();
        }
        List<String> landQuestVillagers = questsDataConfig.getStringList("villagersByLand." + land1);
        HashMap<Quest, String> quests = new HashMap<>();
        for (String landQuestVillager : landQuestVillagers) {
            Quest quest = DailyQuest.getQuestByDescription(questsDataConfig.getString("villagers." + landQuestVillager + ".quest"));
            if (quest == null)
                continue;
            quest.setProgress(Objects.requireNonNull(getQuestVillagerByEntityID(UUID.fromString(landQuestVillager))).progress);
            quests.put(quest, landQuestVillager);
        }
        return quests;
    }

    public static void spawnNewQuestVillager(Player player) {
        List<String> landQuestVillagers;
        Land land1 = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land1 == null) {
            player.sendMessage("§e§lAnyBlocks §7§l| §7Du bist in keinem Land.");
            return;
        }
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setCustomName("§e§lQuestgeber");
        villager.setCustomNameVisible(true);
        villager.setPersistent(true);
        villager.setRemoveWhenFarAway(false);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 500));
        villager.setAI(false);
        villager.setGravity(false);
        new QuestVillager(villager.getUniqueId(), "", 0L, 0, land1.getName()).saveInConfig();
        if (questsDataConfig.contains("villagersByLand." + land1.getName())) {
            landQuestVillagers = questsDataConfig.getStringList("villagersByLand." + land1.getName());
        } else {
            landQuestVillagers = new ArrayList<>();
        }
        landQuestVillagers.add(villager.getUniqueId().toString());
        questsDataConfig.set("villagersByLand." + land1.getName(), landQuestVillagers);
        try {
            questsDataConfig.save(questsDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendMessage("§e§lAnyBlocks §7§l| §7Questgeber §agespawnt§7. Rechtsklicke ihn, für die erste Quest.");
    }

    public void queueSave() {
        QuestStorageQueue storage = getStorage();
        if (storage == null)
            return;
        String questDesc = (this.currentQuest == null) ? "" : this.currentQuest.getDescription();
        storage.updateVillager(this.entityID, questDesc, this.giveQuest, this.progress, this.land);
    }

    private QuestStorageQueue getStorage() {
        return (HeroCraft.getPlugin().dailyQuest != null) ? HeroCraft.getPlugin().dailyQuest.storage : null;
    }

    @Nullable
    public static QuestVillager getQuestVillagerByEntityID(UUID entityID) {
        if (!questsDataConfig.contains("villagers." + entityID.toString() + ".quest")) {
            return null;
        }
        return new QuestVillager(
            entityID,
            questsDataConfig.getString("villagers." + entityID.toString() + ".quest"),
            questsDataConfig.getLong("villagers." + entityID.toString() + ".giveQuest"),
            questsDataConfig.getInt("villagers." + entityID.toString() + ".progress"),
            questsDataConfig.getString("villagers." + entityID.toString() + ".land")
        );
    }

    @EventHandler
    public void onQuestVillagerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Villager)) return;
        Villager villager = (Villager) event.getEntity();
        if (villager.getCustomName() != null && villager.getCustomName().equals(VILLAGER_NAME)) {
            event.setCancelled(true);
        }
    }
}