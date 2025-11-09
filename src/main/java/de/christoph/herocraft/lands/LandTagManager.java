package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LandTagManager implements Listener {

    public HashMap<Player, String> landTags;

    public LandTagManager() {
        this.landTags = new HashMap<>();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        landTags.remove(player);
    }

    public String getTagFromLand(String land) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_suffixes` WHERE `land` = ?");
            preparedStatement.setString(1, land);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tag = resultSet.getString("suffix");
                ChatColor color = ChatColor.valueOf(resultSet.getString("color").toUpperCase());
                return "§" + color.getChar() + tag;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

}
