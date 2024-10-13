package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LandManager implements Listener {

    private ArrayList<Land> allLands;
    private Map<Player, Land> playerLandCache;

    public LandManager() {
        this.allLands = new ArrayList<>();
        this.playerLandCache = new HashMap<>();
        loadSavedLands();
    }

    public void loadSavedLands() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `lands`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Land loadedLand = new Land(
                    resultSet.getString("name"),
                    resultSet.getString("founderUUID"),
                    resultSet.getString("founderName"),
                    resultSet.getString("coFounderUUIDs").split(","),
                    resultSet.getString("coFounderNames").split(","),
                    resultSet.getString("memberUUIDs").split(","),
                    resultSet.getString("memberNames").split(","),
                    resultSet.getDouble("x1"),
                    resultSet.getDouble("z1"),
                    resultSet.getDouble("x2"),
                    resultSet.getDouble("z2"),
                    resultSet.getDouble("spawnX"),
                    resultSet.getDouble("spawnY"),
                    resultSet.getDouble("spawnZ"),
                    resultSet.getDouble("coins"),
                    resultSet.getInt("max_blocks"),
                    resultSet.getString("trusted").split(",")
                        );
                allLands.add(loadedLand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void scanForLand(Player player) {
        Land land = getLandAtLocation(player.getLocation(), allLands);
        if(land == null) {
            player.sendMessage(Constant.PREFIX + "§7Hier gibt es kein §cLand§7.");
            return;
        }
        land.showLandBorder(player);
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("§7-- §e§l" + land.getName() + " §7--");
        player.sendMessage("");
        player.sendMessage("§7Gründer: §e" + land.getFounderName());
        String moderators = "";
        if(land.getCoFounderUUIDs().length != 0) {
            for(String i : land.getCoFounderNames())
                moderators += i + ", ";
        } else
            moderators = "Keine";
        if(moderators.equals(", ")) {
            moderators = "Keine";
        }
        player.sendMessage("§7Moderatoren: §e" + moderators);
        player.sendMessage("");
        String members = "";
        if(land.getMemberUUIDs().length != 0) {
            for(String i : land.getMemberUUIDs())
                members += i + ", ";
        } else
            members = "Keine";
        if(members.equals(", ")) {
            members = "Keine";
        }
        player.sendMessage("§7Mitglieder: §e" + members);
        player.sendMessage("");
    }

    public void saveLand(Land land) {
        try {
            if(hasOwnLand(land.getFounderUUID())) {
                PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `lands` WHERE `founderUUID` = ?");
                preparedStatement.setString(1, land.getFounderUUID());
                preparedStatement.execute();
            }
            PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `lands`(`name`,`founderUUID`,`founderName`,`coFounderUUIDs`,`coFounderNames`,`memberUUIDs`,`memberNames`,`x1`,`z1`,`x2`,`z2`,`spawnX`,`spawnY`,`spawnZ`,`coins`,`max_blocks`,`trusted`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement1.setString(1, land.getName());
            preparedStatement1.setString(2, land.getFounderUUID());
            preparedStatement1.setString(3, land.getFounderName());
            preparedStatement1.setString(4, arrayToString(land.getCoFounderUUIDs(), ","));
            preparedStatement1.setString(5, arrayToString(land.getCoFounderNames(), ","));
            preparedStatement1.setString(6, arrayToString(land.getMemberUUIDs(), ","));
            preparedStatement1.setString(7, arrayToString(land.getMemberNames(), ","));
            preparedStatement1.setDouble(8, land.getX1());
            preparedStatement1.setDouble(9, land.getZ1());
            preparedStatement1.setDouble(10, land.getX2());
            preparedStatement1.setDouble(11, land.getZ2());
            preparedStatement1.setDouble(12, land.getSpawnX());
            preparedStatement1.setDouble(13, land.getSpawnY());
            preparedStatement1.setDouble(14, land.getSpawnZ());
            preparedStatement1.setDouble(15, land.getCoins());
            preparedStatement1.setInt(16, land.getMaxBlocks());
            preparedStatement1.setString(17, arrayToString(land.getTrusted(), ","));
            preparedStatement1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            if(!(event.getDamager() instanceof Player))
                return;
            Land land = getLandAtLocation(event.getEntity().getLocation(), allLands);
            if(land == null)
                return;
            if(!land.canBuild((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
        if(!(event.getDamager() instanceof Player))
            return;
        Land land = getLandAtLocation(event.getEntity().getLocation(), allLands);
        if(land == null)
            return;
        if(!land.canBuild((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Land land = getLandAtLocation(event.getBlock().getLocation(), allLands);
        if(land == null) {
            return;
        }
        if(!land.canBuild(player))
            event.setCancelled(true);
    }

    public boolean isInOtherLand(Player player) {
        Land land = getLandAtLocation(player.getLocation(), allLands);
        if(land == null)
            return false;
        return !land.canBuild(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Land land = getLandAtLocation(player.getLocation(), allLands);
        if(land == null)
            return;
        if(!land.canBuild(player)) {
            if(event.getClickedBlock() == null)
                return;
            /*if(player.getInventory().getItemInMainHand().getType().equals(Material.FIREWORK_ROCKET) || player.getInventory().getItemInOffHand().getType().equals(Material.FIREWORK_ROCKET))
                return;
            if(player.getInventory().getItemInMainHand().getType().toString().contains("HELMET") || player.getInventory().getItemInMainHand().getType().toString().contains("HELMET"))
                return;
            if(player.getInventory().getItemInMainHand().getType().toString().contains("CHESTPLATE") || player.getInventory().getItemInMainHand().getType().toString().contains("CHESTPLATE"))
                return;
            if(player.getInventory().getItemInMainHand().getType().toString().contains("LEGGINS") || player.getInventory().getItemInMainHand().getType().toString().contains("LEGGINS"))
                return;
            if(player.getInventory().getItemInMainHand().getType().toString().contains("BOOTS") || player.getInventory().getItemInMainHand().getType().toString().contains("BOOTS"))
                return;*/
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Land land = getLandAtLocation(event.getBlock().getLocation(), allLands);
        if(land == null) {
            return;
        }
        if(!land.canBuild(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        Land cachedLand = playerLandCache.get(player);
        Land enteredLand = getLandAtLocation(to, allLands);
        if (enteredLand != null && (cachedLand == null || !enteredLand.getName().equals(cachedLand.getName()))) {
            player.sendTitle("§a§l" + enteredLand.getName(), "§7Wurde betreten...");
            playerLandCache.put(player, enteredLand);
            return;
        }
        if (cachedLand != null && (enteredLand == null || !cachedLand.getName().equals(enteredLand.getName()))) {
            player.sendTitle("§c§l" + cachedLand.getName(), "§7Wurde verlassen...");
            playerLandCache.remove(player);
        }
    }



    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        Land land = getLandAtLocation(event.getFurniture().getEntity().getLocation(), allLands);
        if(land == null)
            return;
        if(!land.canBuild(player))
            event.setCancelled(true);
    }

    @Nullable
    public Land getLandFromPlayer(Player player) {
        for(Land i : allLands) {
            if(i.isInLand(player))
                return i;
        }
        return null;
    }

    @Nullable
    public Land getLandByName(String name) {
        for(Land i : allLands) {
            if(i.getName().equalsIgnoreCase(name))
                return i;
        }
        return null;
    }

    public static String arrayToString(String[] array, String delimiter) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i < array.length - 1) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }


    public boolean hasOwnLand(String uuid) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `lands` WHERE `founderUUID` = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static Land getLandAtLocation(Location location, ArrayList<Land> lands) {
        if(!location.getWorld().getName().equalsIgnoreCase("world"))
            return null;
        double x = location.getX();
        double z = location.getZ();
        for (Land land : lands) {
            double x1 = land.getX1();
            double x2 = land.getX2();
            double z1 = land.getZ1();
            double z2 = land.getZ2();
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                    z >= Math.min(z1, z2) && z <= Math.max(z1, z2)) {
                return land;
            }
        }
        return null;
    }

    public static boolean canCreateLandSize(double x1, double z1, double x2, double z2, int maxBlocks) {
        double area = Math.abs((x2 - x1) * (z2 - z1));
        if (area > maxBlocks) {
            return false;
        }
        if(area < 4) {
            return false;
        }
        return true;
    }
    public static boolean canCreateLandLocation(double x1, double z1, double x2, double z2, ArrayList<Land> existingLands, String igonringLandName) {
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);
        for (Land land : existingLands) {
            double existingMinX = Math.min(land.getX1(), land.getX2());
            double existingMaxX = Math.max(land.getX1(), land.getX2());
            double existingMinZ = Math.min(land.getZ1(), land.getZ2());
            double existingMaxZ = Math.max(land.getZ1(), land.getZ2());
            if (!(maxX <= existingMinX || minX >= existingMaxX || maxZ <= existingMinZ || minZ >= existingMaxZ)) {
                if(!igonringLandName.equalsIgnoreCase("")) {
                    if(igonringLandName.equalsIgnoreCase(land.getName())) {
                        continue;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public static boolean canCreateLandProvinceLocation(double x1, double z1, double x2, double z2, ArrayList<Province> existingProvinces, String worldName, String igonringLandName, String ignoringProvinceName) {
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);
        for (Province province : existingProvinces) {
            if(!province.getWorld().equalsIgnoreCase(worldName))
                continue;
            double existingMinX = Math.min(province.getX1(), province.getX2());
            double existingMaxX = Math.max(province.getX1(), province.getX2());
            double existingMinZ = Math.min(province.getZ1(), province.getZ2());
            double existingMaxZ = Math.max(province.getZ1(), province.getZ2());
            if (!(maxX <= existingMinX || minX >= existingMaxX || maxZ <= existingMinZ || minZ >= existingMaxZ)) {
                if(!igonringLandName.equalsIgnoreCase("")) {
                    if(igonringLandName.equalsIgnoreCase(province.getLand()) && ignoringProvinceName.equalsIgnoreCase(province.getName())) {
                        continue;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public ArrayList<Land> getAllLands() {
        return allLands;
    }

}
