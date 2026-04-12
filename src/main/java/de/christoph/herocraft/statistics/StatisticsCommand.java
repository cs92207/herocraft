package de.christoph.herocraft.statistics;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;

        if(strings.length == 0) {
            showOwnStatistics(player);
        } else if(strings.length == 1) {
            String action = strings[0].toLowerCase();
            if(action.equals("info") || action.equals("help")) {
                player.sendMessage(Constant.PREFIX + "§7/statistiken - Zeige deine Statistiken");
                player.sendMessage(Constant.PREFIX + "§7/statistiken info - Zeige diese Hilfe");
            }
        }
        return false;
    }

    private void showOwnStatistics(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("SELECT * FROM `player_statistics` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                player.sendMessage(Constant.PREFIX + "§7Du hast §knoch§r §7keine Statistiken.");
                return;
            }

            player.sendMessage("§e§l╔════════════════════════════════════════════════════════════════════════════════╗");
            player.sendMessage("§e§l║ §f§lDeine Statistiken");
            player.sendMessage("§e§l║");
            player.sendMessage("§e§l║ §7Beigetreten: §e" + formatDate(resultSet.getTimestamp("join_date")));

            String landCreated = resultSet.getTimestamp("land_created_date") != null ? "§a✔ " + formatDate(resultSet.getTimestamp("land_created_date")) : "§c✗ Noch nicht erledigt";
            player.sendMessage("§e§l║ §7Land erstellt: " + landCreated);

            String landJoined = resultSet.getTimestamp("land_joined_date") != null ? "§a✔ " + formatDate(resultSet.getTimestamp("land_joined_date")) : "§c✗ Noch nicht erledigt";
            player.sendMessage("§e§l║ §7Land beigetreten: " + landJoined);

            String questCompleted = resultSet.getTimestamp("quest_completed_date") != null ? "§a✔ " + formatDate(resultSet.getTimestamp("quest_completed_date")) : "§c✗ Noch nicht erledigt";
            player.sendMessage("§e§l║ §7Quest abgeschlossen: " + questCompleted);

            String teleporterUsed = resultSet.getTimestamp("teleporter_used_date") != null ? "§a✔ " + formatDate(resultSet.getTimestamp("teleporter_used_date")) : "§c✗ Noch nicht erledigt";
            player.sendMessage("§e§l║ §7Teleporter benutzt: " + teleporterUsed);

            String firstLeave = resultSet.getTimestamp("first_leave_date") != null ? "§a✔ " + formatDate(resultSet.getTimestamp("first_leave_date")) : "§c✗ Noch nicht erledigt";
            player.sendMessage("§e§l║ §7Ersten Mal den Server verlassen: " + firstLeave);

            player.sendMessage("§e§l║");
            player.sendMessage("§e§l╚════════════════════════════════════════════════════════════════════════════════╝");

        } catch (SQLException e) {
            System.out.println("[HeroCraft Statistics] Fehler beim Abrufen der Statistiken: " + e.getMessage());
            player.sendMessage(Constant.PREFIX + "§7Ein Fehler ist bei der Abfrage aufgetreten.");
        }
    }

    private String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        return new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date(timestamp.getTime()));
    }
}
