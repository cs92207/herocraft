package de.christoph.herocraft.quests;

import com.sun.tools.jconsole.JConsoleContext;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class QuestVillager {

    public static final String PREFIX = "§e§lQuestgeber §7§l| ";
    public static final String VILLAGER_NAME = "§e§lQuestgeber";

    public UUID entityID;
    public Quest currentQuest;
    public long giveQuest;
    public int progress;
    public String land;

    public FileConfiguration config;

    public QuestVillager(UUID entityID, String currentQuest, long giveQuest, int progress, String land) {
        this.land = land;
        this.currentQuest = DailyQuest.getQuestByDescription(currentQuest);
        this.entityID = entityID;
        this.giveQuest = giveQuest;
        this.progress = progress;
        if(this.currentQuest != null)
            this.currentQuest.setProgress(progress);
        this.config = HeroCraft.getPlugin().getConfig();
    }

    public void finishQuest() {
        this.currentQuest = null;
        saveInConfig();
        Land land1 = HeroCraft.getPlugin().getLandManager().getLandByName(land);
        land1.setCoins(land1.getCoins() + Constant.QUEST_COINS);
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(land1.canBuild(all)) {
                all.sendMessage(Constant.PREFIX + "§7Neue §e§lQuestgeber §7Quest abgeschlossen.");
                all.sendMessage("§0[§a+§0] §7" + Constant.QUEST_COINS + " Coins §0(Für dein Land)§7.");
            }
        }
    }

    public void onInteract(Player player) {
        Land land1 = HeroCraft.getPlugin().getLandManager().getLandByName(land);
        if(!land1.canBuild(player)) {
            player.sendMessage(PREFIX + "§7Du gehörst nicht zu meinem Land!");
            return;
        }
        long last = giveQuest;
        long now = System.currentTimeMillis();
        if((now - last > 1000L * 60 * 60 * 24)) {
            Quest quest = null;
            while (quest == null || (quest instanceof CraftItemQuest) || (quest instanceof FindBiomeQuest)) {
                quest = DailyQuest.questTemplates.get(new Random().nextInt(DailyQuest.questTemplates.size())).copy();
            }
            currentQuest = quest;
            System.out.println(currentQuest.getDescription());
            progress = 0;
            currentQuest.setProgress(progress);
            player.sendMessage("");
            player.sendMessage(PREFIX + "§7" + currentQuest.getDescription());
            player.sendMessage(ChatColor.GRAY + "Fortschritt: " + currentQuest.getProgress() + "/" + currentQuest.getGoal());
            player.sendMessage("");
            giveQuest = System.currentTimeMillis();
            saveInConfig();
        }
        if(this.currentQuest == null) {
            player.sendMessage(PREFIX + "§7Ich habe heute nichts mehr für dich zu tun. Komme morgen wieder.");
        } else {
            player.sendMessage("");
            player.sendMessage(PREFIX + "§7" + currentQuest.getDescription());
            player.sendMessage(ChatColor.GRAY + "Fortschritt: " + currentQuest.getProgress() + "/" + currentQuest.getGoal());
            player.sendMessage("");
        }
    }

    public void saveInConfig() {
        if(currentQuest == null) {
            config.set(entityID.toString() + ".Quest", "");
        } else {
            config.set(entityID.toString() + ".Quest", currentQuest.getDescription());
        }
        config.set(entityID.toString() + ".GiveQuest", giveQuest);
        config.set(entityID.toString() + ".Progress", progress);
        config.set(entityID.toString() + ".Land", land);
        HeroCraft.getPlugin().saveConfig();
    }

    public static HashMap<Quest, String> getAllQuestVillagerQuestsByLand(String land1) {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        System.out.println("t1");
        if(!config.contains("LandQuestVillagers." + land1)) {
            return new HashMap<>();
        }
        System.out.println("t2");
        List<String> landQuestVillagers = config.getStringList("LandQuestVillagers." + land1);
        HashMap<Quest, String> quests = new HashMap<>();
        System.out.println(quests);
        for(String landQuestVillager : landQuestVillagers) {
            System.out.println("t3");
            Quest quest = DailyQuest.getQuestByDescription(config.getString(landQuestVillager + ".Quest"));
            if(quest == null)
                continue;
            System.out.println("t4");
            quest.setProgress(Objects.requireNonNull(getQuestVillagerByEntityID(UUID.fromString(landQuestVillager))).progress);
            quests.put(quest, landQuestVillager);
        }
        return quests;
    }

    public static void spawnNewQuestVillager(Player player) {
        Land land1 = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land1 == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem Land.");
            return;
        }
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setCustomName(VILLAGER_NAME);
        villager.setCustomNameVisible(true);
        villager.setPersistent(true);
        villager.setRemoveWhenFarAway(false);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 500));
        villager.setAI(false);
        villager.setGravity(false);
        new QuestVillager(villager.getUniqueId(), "", 0, 0, land1.getName()).saveInConfig();
        List<String> landQuestVillagers;
        if(HeroCraft.getPlugin().getConfig().contains("LandQuestVillagers." + land1.getName())) {
            landQuestVillagers = HeroCraft.getPlugin().getConfig().getStringList("LandQuestVillagers." + land1.getName());
        } else {
            landQuestVillagers = new ArrayList<>();
        }
        landQuestVillagers.add(villager.getUniqueId().toString());
        HeroCraft.getPlugin().getConfig().set("LandQuestVillagers." + land1.getName(), landQuestVillagers);
        HeroCraft.getPlugin().saveConfig();
        player.sendMessage(Constant.PREFIX + "§7Questgeber §agespawnt§7. Rechtsklicke ihn, für die erste Quest.");
    }

    // NEU in QuestVillager.java einfügen (im selben Package de.christoph.herocraft.quests)
    public void queueSave() {
        QuestStorageQueue storage = getStorage();
        if (storage == null) return;
        String questDesc = (currentQuest == null ? "" : currentQuest.getDescription());
        storage.updateVillager(entityID, questDesc, giveQuest, progress, land);
    }

    private QuestStorageQueue getStorage() {
        return (HeroCraft.getPlugin().dailyQuest != null)
                ? HeroCraft.getPlugin().dailyQuest.storage
                : null;
    }


    @Nullable
    public static QuestVillager getQuestVillagerByEntityID(UUID entityID) {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        if(!config.contains(entityID.toString() + ".Quest")) {
            return null;
        }
        return new QuestVillager(
            entityID,
            config.getString(entityID.toString() + ".Quest"),
            config.getLong(entityID.toString() + ".GiveQuest"),
            config.getInt(entityID.toString() + ".Progress"),
            config.getString(entityID.toString() + ".Land")
        );
    }

}
