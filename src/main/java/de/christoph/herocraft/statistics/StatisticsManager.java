package de.christoph.herocraft.statistics;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class StatisticsManager implements Listener {

    public StatisticsManager() {
        createTableIfNotExists();
    }

    /**
     * Erstellt die Tabelle "player_statistics" wenn sie nicht existiert
     */
    private void createTableIfNotExists() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS `player_statistics` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`uuid` VARCHAR(36) UNIQUE NOT NULL," +
                    "`player_name` VARCHAR(16) NOT NULL," +
                    "`join_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "`land_created_date` TIMESTAMP NULL DEFAULT NULL," +
                    "`land_joined_date` TIMESTAMP NULL DEFAULT NULL," +
                    "`quest_completed_date` TIMESTAMP NULL DEFAULT NULL," +
                    "`teleporter_used_date` TIMESTAMP NULL DEFAULT NULL," +
                    "`first_leave_date` TIMESTAMP NULL DEFAULT NULL," +
                    "INDEX idx_uuid (uuid)," +
                    "INDEX idx_join_date (join_date)" +
                    ")";

            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement(sql);
            preparedStatement.execute();
            System.out.println("[HeroCraft Statistics] Tabelle 'player_statistics' wurde erstellt oder existiert bereits.");
        } catch (SQLException e) {
            System.out.println("[HeroCraft Statistics] Fehler beim Erstellen der Tabelle: " + e.getMessage());
        }
    }

    /**
     * Prüft ob ein Spieler bereits in der Statistik-Tabelle existiert
     */
    public boolean playerExistsInStatistics(UUID uuid) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("SELECT * FROM `player_statistics` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("[HeroCraft Statistics] Fehler beim Prüfen der Spieler-Existenz: " + e.getMessage());
        }
        return false;
    }

    /**
     * Erstellt einen neuen Eintrag für einen Spieler
     */
    public void createPlayerStatistics(Player player) {
        if (playerExistsInStatistics(player.getUniqueId())) {
            return;
        }

        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("INSERT INTO `player_statistics` (`uuid`, `player_name`, `join_date`) VALUES (?, ?, ?)");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.execute();
            System.out.println("[HeroCraft Statistics] Neue Statistiken für Spieler " + player.getName() + " erstellt.");
        } catch (SQLException e) {
            System.out.println("[HeroCraft Statistics] Fehler beim Erstellen von Spieler-Statistiken: " + e.getMessage());
        }
    }

    /**
     * Aktualisiert das Feld mit dem angegebenen Namen für einen Spieler
     */
    public void updatePlayerStatistic(UUID uuid, String fieldName) {
        if (!playerExistsInStatistics(uuid)) {
            return;
        }

        try {
            String sql = "UPDATE `player_statistics` SET `" + fieldName + "` = ? WHERE `uuid` = ? AND `" + fieldName + "` IS NULL";
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft Statistics] Fehler beim Aktualisieren der Statistik: " + e.getMessage());
        }
    }

    /**
     * Markiert, dass ein Spieler ein Land erstellt hat
     */
    public void markLandCreated(UUID uuid) {
        updatePlayerStatistic(uuid, "land_created_date");
    }

    /**
     * Markiert, dass ein Spieler einem Land beigetreten ist
     */
    public void markLandJoined(UUID uuid) {
        updatePlayerStatistic(uuid, "land_joined_date");
    }

    /**
     * Markiert, dass ein Spieler eine Quest abgeschlossen hat
     */
    public void markQuestCompleted(UUID uuid) {
        updatePlayerStatistic(uuid, "quest_completed_date");
    }

    /**
     * Markiert, dass ein Spieler den Teleporter benutzt hat
     */
    public void markTeleporterUsed(UUID uuid) {
        updatePlayerStatistic(uuid, "teleporter_used_date");
    }

    /**
     * Markiert, dass ein Spieler zum ersten Mal den Server verlassen hat
     */
    public void markFirstLeave(UUID uuid) {
        updatePlayerStatistic(uuid, "first_leave_date");
    }

    // ==================== EVENT HANDLER ====================

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!playerExistsInStatistics(player.getUniqueId())) {
            createPlayerStatistics(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (playerExistsInStatistics(player.getUniqueId())) {
            // Prüfe ob der Spieler das erste Mal den Server verlässt
            try {
                PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                        .prepareStatement("SELECT `first_leave_date` FROM `player_statistics` WHERE `uuid` = ?");
                preparedStatement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                
                if (resultSet.next()) {
                    if (resultSet.getTimestamp("first_leave_date") == null) {
                        markFirstLeave(player.getUniqueId());
                    }
                }
            } catch (SQLException e) {
                System.out.println("[HeroCraft Statistics] Fehler beim Markieren des ersten Verlassens: " + e.getMessage());
            }
        }
    }
}
