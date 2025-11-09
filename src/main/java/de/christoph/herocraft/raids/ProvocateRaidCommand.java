package de.christoph.herocraft.raids;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


// Sicherheit: mobs mit namen removen
// Jahreszeiten Dimensionen (größere Städte)


public class ProvocateRaidCommand implements CommandExecutor {

    public static ArrayList<Player> confirmPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem Land.");
            return false;
        }
        if(!land.isModeratorUUID(player.getUniqueId().toString()) && !land.isOwnerUUID(player.getUniqueId().toString())) {
            player.sendMessage(Constant.PREFIX + "§7Dies kann nur ein Moderator des Landes.");
            return false;
        }
        int raidLevel = loadLandRaidLevel(land);
        double costs = Constant.PROVOCATE_COSTS * raidLevel;
        if(confirmPlayers.contains(player)) {
            if(HeroCraft.getPlugin().raidManager.isLandInRaid(land))
                return false;
            if(land.getCoins() < costs) {
                player.sendMessage(Constant.PREFIX + "§7Dein Land hat nicht genug §cCoins§7.");
                return false;
            }
            land.setCoins(land.getCoins() - costs);
            HeroCraft.getPlugin().raidManager.startRaid(land);
            confirmPlayers.remove(player);
            return false;
        }
        player.sendMessage(Constant.PREFIX + "§7Dies würde deinem Land §e" + costs + " Coins §7kosten. Gebe den Befehl erneut ein, um zu bestätigen.");
        confirmPlayers.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                confirmPlayers.remove(player);
            }
        }, 20*60);

        return false;
    }

    private int loadLandRaidLevel(Land land) {
        try {
            if(!isInLandRaidLevel(land)) {
                return 1;
            }
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_raid_levels` WHERE `land` = ?");
            preparedStatement.setString(1, land.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean isInLandRaidLevel(Land land) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_raid_levels` WHERE `land` = ?");
            preparedStatement.setString(1, land.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
