package de.christoph.herocraft.home;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.geom.RectangularShape;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Home {

    private Player player;
    private String name;
    private Location location;

    public Home(Player player, String name, Location location) {
        this.player = player;
        this.name = name;
        this.location = location;
    }

    public void delete() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `homes` WHERE `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, name);
            preparedStatement.execute();
            ArrayList<Home> playerHomes = HeroCraft.getPlugin().getHomeManager().getPlayerHomes().get(player);
            playerHomes.removeIf(home -> home.getName().equalsIgnoreCase(name));
            HeroCraft.getPlugin().getHomeManager().getPlayerHomes().put(player, playerHomes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `homes` WHERE `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `homes` WHERE `uuid` = ? AND `name` = ?");
                preparedStatement1.setString(1, player.getUniqueId().toString());
                preparedStatement1.setString(2, name);
                preparedStatement1.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Home> playerHomes = HeroCraft.getPlugin().getHomeManager().getPlayerHomes().get(player);
        playerHomes.removeIf(home -> home.getName().equalsIgnoreCase(name));
        playerHomes.add(this);
        HeroCraft.getPlugin().getHomeManager().getPlayerHomes().put(player, playerHomes);
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `homes` (`uuid`,`name`,`x`,`y`,`z`,`world`) VALUES (?,?,?,?,?,?)");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, location.getX());
            preparedStatement.setDouble(4, location.getY());
            preparedStatement.setDouble(5, location.getZ());
            preparedStatement.setString(6, location.getWorld().getName());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void teleport() {
        player.teleport(location);
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

}
