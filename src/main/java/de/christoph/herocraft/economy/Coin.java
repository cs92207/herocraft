package de.christoph.herocraft.economy;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Coin {

    Connection connection = HeroCraft.getPlugin().getMySQL().getConnection();

    public double getCoins(Player player) {
        if(!isInDatabase(player))
            return 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `coins` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                double coins = resultSet.getDouble("value");
                coins = Math.round(coins * 100.0) / 100.0;
                return coins;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void setCoins(Player player, double amount) {
        try {
            PreparedStatement preparedStatement;
            if(isInDatabase(player)) {
                preparedStatement = connection.prepareStatement("UPDATE `coins` SET `value` = ? WHERE `uuid` = ?");
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `coins` (`uuid`, `value`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setDouble(2, amount);
            }
            preparedStatement.execute();
            ScoreboardManager.setScoreboard(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isInDatabase(Player player) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `coins` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void removeMoney(Player player, double amount) {
        setCoins(player, getCoins(player) - amount);
    }

    public void addMoney(Player player, double amount) {
        setCoins(player, getCoins(player) + amount);
    }






    /*private FileConfiguration config = HeroWars.getPlugin().getConfig();

    public double getCoins(Player player) {
        if(config.contains("Coins." + player.getUniqueId().toString())){
            return config.getDouble("Coins." + player.getUniqueId().toString());
        } else
            return 0;
    }

    public void setCoins(Player player, double amount) {
        config.set("Coins." + player.getUniqueId().toString(), amount);
        HeroWars.getPlugin().saveConfig();
        ScoreboardManager.showScoreboard(player);
    }

    public void removeMoney(Player player, double amount) {
        config.set("Coins." + player.getUniqueId().toString(), getCoins(player) - amount);
        HeroWars.getPlugin().saveConfig();
        ScoreboardManager.showScoreboard(player);
    }

    public void addMoney(Player player, double amount) {
        config.set("Coins." + player.getUniqueId().toString(), getCoins(player) + amount);
        HeroWars.getPlugin().saveConfig();
        ScoreboardManager.showScoreboard(player);
    }*/

}
