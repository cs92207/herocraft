package de.christoph.herocraft.status;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Status implements Listener, CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!player.hasPermission("status.use")) {
            player.sendMessage(Constant.NO_PERMISSION);
            return false;
        }
        if(strings.length == 0) {
            player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/status set <TEXT> §7oder §e/status remove§7.");
            return false;
        }
        if(strings[0].equalsIgnoreCase("set")) {
            String status = "";
            for(int i = 0; i < strings.length; i++) {
                if(i == 0)
                    continue;
                status += strings[i] + " ";
            }
            setStatus(player, status);
            player.sendMessage(Constant.PREFIX + "§7Du hast deinen Status §agesetzt§7.");
        } else if(strings[0].equalsIgnoreCase("remove")) {
            setStatus(player, "");
            player.sendMessage(Constant.PREFIX + "§7Du hast deinen Status §centfernt§7.");
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("status.use")) {
            event.setJoinMessage("§e§lHeroWars §7§l| §7" + event.getPlayer().getName() + " hat HeroCraft §abetreten§7.");
            return;
        }
        String status = getStatus(player);
        if(status.isEmpty()) {
            event.setJoinMessage("§e§lHeroWars §7§l| §7" + event.getPlayer().getName() + " hat HeroCraft §abetreten§7.");
            return;
        }
        event.setJoinMessage("");
        sendStatusToServer(player, status);
    }

    private void setStatus(Player player, String text) {
        try {
            PreparedStatement preparedStatement;
            if(hasStatus(player)) {
                preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `status` SET `status` = ? WHERE `uuid` = ?");
                preparedStatement.setString(1, text);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `status` (`uuid`,`status`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, text);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasStatus(Player player) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `status` WHERE `uuid` = ?");
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

    private String getStatus(Player player) {
        if(!hasStatus(player))
            return "";
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `status` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("status");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private void sendStatusToServer(Player player, String status) {
        for(Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage("§e" + player.getName() + " §7(Status) | " + ChatColor.translateAlternateColorCodes('&', status));
        }
    }

}
