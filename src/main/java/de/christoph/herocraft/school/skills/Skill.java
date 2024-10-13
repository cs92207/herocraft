package de.christoph.herocraft.school.skills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public abstract class Skill {

    private String name;
    private String description;
    private int firstCosts;
    public HashMap<Player, Integer> players;  // Contains every player, that have learned current skill
    public HashMap<Player, Integer> trainingPlayers; // Player, Training progress
    public Location trainingLocation;
    public String trainingDescription;
    public int trainingProgressUntilUpgrade;

    public Skill(String name, String description, int firstCosts, Location trainingLocation, String trainingDescription, int trainingProgressUntilUpgrade) {
        this.name = name;
        this.description = description;
        this.firstCosts = firstCosts;
        this.trainingPlayers = new HashMap<>();
        this.trainingLocation = trainingLocation;
        this.trainingDescription = trainingDescription;
        this.trainingProgressUntilUpgrade = trainingProgressUntilUpgrade;
        players = new HashMap<>();
    }

    public void learnSkill(Player player) {
        if(HeroCraft.getPlugin().coin.getCoins(player) < getFirstCosts()) {
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            player.closeInventory();
            return;
        }
        HeroCraft.getPlugin().coin.removeMoney(player, getFirstCosts());
        if(players.containsKey(player))
            return;
        players.put(player, 1);
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*5, 500));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 1000));
        player.sendTitle("§e§l...", "§7Du lernst die Fähigkeit " + name);
        saveInConfig(player);
        onSkillLearned(player);
    }

    protected void saveInConfig(Player player) {
        Connection connection = HeroCraft.getPlugin().getMySQL().getConnection();
        PreparedStatement preparedStatement;
        try {
            if(isInDatabase(player)) {
                preparedStatement = connection.prepareStatement("UPDATE `skills` SET level = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, players.get(player));
                preparedStatement.setString(2, player.getUniqueId().toString() + getName());
            } else {
               preparedStatement = connection.prepareStatement("INSERT INTO `skills` (`uuid`, `level`) VALUES (?,?)");
               preparedStatement.setString(1, player.getUniqueId().toString() + getName());
               preparedStatement.setInt(2, players.get(player));
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected int getSkillLevel(Player player) {
        Connection connection = HeroCraft.getPlugin().getMySQL().getConnection();
        if(!isInDatabase(player))
            return 0;
        try {
            System.out.println(player.getUniqueId().toString() + getName());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `skills` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString() + getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("level"));
                return resultSet.getInt("level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected boolean isInDatabase(Player player) {
        Connection connection = HeroCraft.getPlugin().getMySQL().getConnection();
        try {
            System.out.println(player.getUniqueId().toString() + getName());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `skills` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString() + getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void activateSkill(Player player, int skillLevel) {
        if(players.containsKey(player))
            return;
        players.put(player, skillLevel);
        onSkillActivated(player);
    }

    public void levelUpSkill(Player player) {
        players.put(player, players.get(player) + 1);
        player.sendTitle(name + "§e§l trainiert", "§7Neues Level §e" + players.get(player));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 1);
        trainingPlayers.put(player, 0);
        saveInConfig(player);
        onSkillUpgraded(player);
    }

    public void startTraining(Player player) {
        if(HeroCraft.getPlugin().coin.getCoins(player) < getMinTrainingCoins(player)) {
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            return;
        }
        HeroCraft.getPlugin().coin.removeMoney(player, getMinTrainingCoins(player));
        trainingPlayers.put(player, 0);
        player.sendTitle("§e§lTraining gestartet", trainingDescription);
        player.teleport(trainingLocation);
    }

    public void endTraining(Player player) {
        if(!trainingPlayers.containsKey(player))
            return;
        trainingPlayers.remove(player);
    }

    public void leaveTraining(Player player) {
        if(!player.getWorld().getName().equalsIgnoreCase("hero"))
            return;
        endTraining(player);
        player.teleport(new Location(Bukkit.getWorld("world"), 172.55, 132, -216.5));
        player.sendTitle("§e§lTraining verlassen", "");
    }

    public void makeTrainingProgress(Player player) {
        if(!player.getWorld().getName().equalsIgnoreCase("hero")) {
            endTraining(player);
            player.sendTitle("§e§lTraining verlassen", "");
            return;
        }
        if(trainingPlayers.containsKey(player)) {
            trainingPlayers.put(player, trainingPlayers.get(player) + 1);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§e" + trainingPlayers.get(player) + " / " + trainingProgressUntilUpgrade*players.get(player)*players.get(player)));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3, 1);
            if(trainingPlayers.get(player) >= trainingProgressUntilUpgrade * players.get(player)*players.get(player)) {
                levelUpSkill(player);
            }
        }
    }

    public int getMinTrainingCoins(Player player) {
        return trainingProgressUntilUpgrade * players.get(player);
    }

    public void onSkillUpgraded(Player player) {}

    public void onSkillActivated(Player player) {}

    public void onSkillLearned(Player player) {}

    public String getName() {
        return name;
    }

    public int getFirstCosts() {
        return firstCosts;
    }

    public String getDescription() {
        return description;
    }

}
