package de.christoph.herocraft.dungeons;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DungeonManager implements Listener {

    public static final String FARMWORLD_NAME = "farmwelt";

    private ArrayList<Dungeon> dungeons;
    private ArrayList<DungeonType> dungeonTypes;

    // TODO: Dungeons bringen einem Land Coins. Man hat ein GUI in der Regierung mit allen besetzten Dungeons. Da kann man dann regelmäßig hinreisen und sich die Coins abholen (und standart Mobs besiegen)


    // TODO: Als nächstes: Methoden in Dungeons füllen und einen Dungeon Type hinzufügen.

    public DungeonManager() {
        this.dungeons = new ArrayList<>();
        this.dungeonTypes = new ArrayList<>();
        loadDungeonTypes();
        loadDungeons();
    }

    private void loadDungeonTypes() {

    }

    private void loadDungeons() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `dungeons`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dungeons.add(new Dungeon(
                    resultSet.getInt("dungeon_type"),
                    resultSet.getInt("x"),
                    resultSet.getInt("y"),
                    resultSet.getInt("z"),
                    resultSet.getString("owned_land"),
                    resultSet.getString("placed_mobs")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void regenerateDungeons() {
        // Clear old Dungeons
        for(Dungeon allDungeons : dungeons) {
            allDungeons.delete();
        }
        dungeons.clear();
        // Generate new Dungeons
        for(int i = 0; i < 15; i++) {
            int type = new Random().nextInt(dungeonTypes.size() - 1);
            if(type < 0) {
                type++;
            }
            Location randomLoc = getRandomLocation();
            dungeons.add(new Dungeon(
                type,
                randomLoc.getBlock().getX(),
                randomLoc.getBlock().getY(),
                randomLoc.getBlock().getZ(),
                "",
                ""
            ));
        }
        // Save new Dungeons
        for(Dungeon allDungeons : dungeons) {
            allDungeons.save();
            allDungeons.generate();
        }
        try {
            LocalDate currentDate = LocalDate.now();
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `dungeon_generation` SET `last_regenerated` = ? WHERE `id` = ?");
            preparedStatement.setDate(1, java.sql.Date.valueOf(currentDate));
            preparedStatement.setInt(1, 1);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Location getRandomLocation() {
        int x = new Random().nextInt(1000);
        int z = new Random().nextInt(1000);
        int y = Bukkit.getWorld(FARMWORLD_NAME).getHighestBlockYAt(x, z);
        return new Location(Bukkit.getWorld(FARMWORLD_NAME), x, y, z);
    }

    @EventHandler
    public void checkForRegenerationDungeon(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("anyblocks.dungeons.admin"))
            return;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT `last_regenerated` FROM `dungeon_generation` WHERE `id` = ?");
            preparedStatement.setInt(1, 1);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Date date = resultSet.getDate("last_regenerated");
                if(isAtLeast30DaysOld(date)) {
                    regenerateDungeons();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isAtLeast30DaysOld(Date sqlDate) {
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
        Calendar currentDate = Calendar.getInstance();
        Calendar date30DaysAgo = Calendar.getInstance();
        date30DaysAgo.add(Calendar.DAY_OF_YEAR, -30);
        return utilDate.before(date30DaysAgo.getTime()) || utilDate.equals(date30DaysAgo.getTime());
    }

    public ArrayList<Dungeon> getDungeons() {
        return dungeons;
    }

}
