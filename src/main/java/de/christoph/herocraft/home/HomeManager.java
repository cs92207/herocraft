package de.christoph.herocraft.home;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeManager implements Listener {

    private HashMap<Player, ArrayList<Home>> playerHomes;

    public HomeManager() {
        this.playerHomes = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerHomes.put(player, loadHomesFromPlayer(player));
    }

    private ArrayList<Home> loadHomesFromPlayer(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `homes` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Home> homes = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                String world = resultSet.getString("world");
                homes.add(new Home(player, name, new Location(Bukkit.getWorld(world), x, y, z)));
            }
            return homes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<Player, ArrayList<Home>> getPlayerHomes() {
        return playerHomes;
    }

}
