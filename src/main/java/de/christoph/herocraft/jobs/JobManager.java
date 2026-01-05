package de.christoph.herocraft.jobs;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class JobManager implements Listener {

    private Connection connection;
    private HashMap<UUID, Job> playerJobs;
    private HashMap<UUID, Job> dirtyJobs;

    public JobManager() {
        this.connection = HeroCraft.getPlugin().getMySQL().getConnection();
        this.playerJobs = new HashMap<>();
        this.dirtyJobs = new HashMap<>();
        createTable();
        startAutoSaveTask();
    }

    private void createTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `jobs` (" +
                            "`uuid` VARCHAR(36) NOT NULL, " +
                            "`job_type` VARCHAR(50) NOT NULL, " +
                            "`level` INT NOT NULL DEFAULT 1, " +
                            "`xp` DOUBLE NOT NULL DEFAULT 0, " +
                            "PRIMARY KEY (`uuid`))"
            );
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Erstellen der Jobs-Tabelle: " + e.getMessage());
        }
    }

    public Job getJob(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerJobs.containsKey(uuid)) {
            return playerJobs.get(uuid);
        }
        return loadJobFromDatabase(player);
    }

    private Job loadJobFromDatabase(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `jobs` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                JobType jobType = JobType.valueOf(resultSet.getString("job_type"));
                int level = resultSet.getInt("level");
                double xp = resultSet.getDouble("xp");
                Job job = new Job(uuid, jobType, level, xp);
                playerJobs.put(uuid, job);
                return job;
            }
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Laden des Jobs: " + e.getMessage());
        }
        return null;
    }

    public void setJob(Player player, JobType jobType) {
        UUID uuid = player.getUniqueId();
        Job job = new Job(uuid, jobType, 1, 0);
        playerJobs.put(uuid, job);
        dirtyJobs.put(uuid, job); // Markiere als "dirty" (muss gespeichert werden)
        saveJobToDatabase(job); // Sofort speichern beim Job-Wechsel, da wichtig
    }

    public void saveJobToDatabase(Job job) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO `jobs` (`uuid`, `job_type`, `level`, `xp`) VALUES (?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE `job_type` = ?, `level` = ?, `xp` = ?"
            );
            preparedStatement.setString(1, job.getUuid().toString());
            preparedStatement.setString(2, job.getJobType().name());
            preparedStatement.setInt(3, job.getLevel());
            preparedStatement.setDouble(4, job.getXp());
            preparedStatement.setString(5, job.getJobType().name());
            preparedStatement.setInt(6, job.getLevel());
            preparedStatement.setDouble(7, job.getXp());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Speichern des Jobs: " + e.getMessage());
        }
    }

    public void updateJob(Job job) {
        playerJobs.put(job.getUuid(), job);
        dirtyJobs.put(job.getUuid(), job); // Markiere als "dirty" (muss gespeichert werden)
    }

    private void startAutoSaveTask() {
        // Speichere alle geänderten Jobs alle 30 Sekunden
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!dirtyJobs.isEmpty()) {
                    saveDirtyJobs();
                }
            }
        }.runTaskTimer(HeroCraft.getPlugin(), 20L * 30, 20L * 30); // Start nach 30 Sekunden, dann alle 30 Sekunden
    }

    private void saveDirtyJobs() {
        if (dirtyJobs.isEmpty()) {
            return;
        }

        // Kopiere die dirtyJobs, damit wir sie verarbeiten können
        HashMap<UUID, Job> jobsToSave = new HashMap<>(dirtyJobs);
        dirtyJobs.clear();
        
        // Asynchron speichern (nicht blockierend)
        Bukkit.getScheduler().runTaskAsynchronously(HeroCraft.getPlugin(), () -> {
            for (Job job : jobsToSave.values()) {
                saveJobToDatabase(job);
            }
        });
    }

    public void savePlayerJob(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerJobs.containsKey(uuid)) {
            Job job = playerJobs.get(uuid);
            // Synchron speichern beim Logout (wichtig, da Spieler weg ist)
            saveJobToDatabase(job);
            dirtyJobs.remove(uuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerJob(player); // Beim Logout sofort speichern
        playerJobs.remove(player.getUniqueId());
        dirtyJobs.remove(player.getUniqueId());
    }

    public boolean hasJob(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerJobs.containsKey(uuid)) {
            return true;
        }
        Job job = loadJobFromDatabase(player);
        return job != null;
    }

    public void loadPlayerJob(Player player) {
        loadJobFromDatabase(player);
    }

    public void saveAllJobs() {
        // Speichere alle Jobs im Cache (auch die, die nicht "dirty" sind, zur Sicherheit)
        for (Job job : playerJobs.values()) {
            saveJobToDatabase(job);
        }
        dirtyJobs.clear();
    }
}

