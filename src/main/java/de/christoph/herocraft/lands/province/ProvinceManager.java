package de.christoph.herocraft.lands.province;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProvinceManager implements Listener {

    private ArrayList<Province> provinces;

    private Map<Player, Province> playerProvinceCache;

    public ProvinceManager() {
        playerProvinceCache = new HashMap<>();
        provinces = new ArrayList<>();
        loadProvinces();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        Province cachedProvince = playerProvinceCache.get(player);
        Province enteredProvince = getProvinceAtLocation(to, provinces);
        if (enteredProvince != null && (cachedProvince == null || !enteredProvince.getName().equals(cachedProvince.getName()))) {
            player.sendTitle("§a§l" + enteredProvince.getName(), "Stadt: §a" + HeroCraft.getPlugin().getLandManager().getLandByName(enteredProvince.getLand()).getName());
            playerProvinceCache.put(player, enteredProvince);
            return;
        }
        if (cachedProvince != null && (enteredProvince == null || !cachedProvince.getName().equals(enteredProvince.getName()))) {
            player.sendTitle("§c§l" + cachedProvince.getName(), "Stadt: §c" + HeroCraft.getPlugin().getLandManager().getLandByName(cachedProvince.getLand()).getName());
            playerProvinceCache.remove(player);
        }
    }

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        Province land = getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), provinces);
        if(land == null)
            return;
        if(!land.canBuild(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Province land = getProvinceAtLocation(event.getBlock().getLocation(), provinces);
        if(land == null) {
            return;
        }
        if(!land.canBuild(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Province land = getProvinceAtLocation(player.getLocation(), provinces);
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
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Province land = getProvinceAtLocation(event.getBlock().getLocation(), provinces);
        if(land == null) {
            return;
        }
        if(!land.canBuild(player))
            event.setCancelled(true);
    }

    private void loadProvinces() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `provinces`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                provinces.add(new Province(
                    resultSet.getString("land"),
                    resultSet.getString("name"),
                    resultSet.getDouble("x1"),
                    resultSet.getDouble("z1"),
                    resultSet.getDouble("x2"),
                    resultSet.getDouble("z2"),
                    resultSet.getString("world")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProvince(Province province) {
        try {
            if(getProvinceByName(province.getLand(), province.getName()) != null) {
                PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `provinces` WHERE `land` = ? AND `name` = ?");
                preparedStatement.setString(1, province.getLand());
                preparedStatement.setString(2, province.getName());
                preparedStatement.execute();
            }
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `provinces` (`land`,`name`,`x1`,`z1`,`x2`,`z2`,`world`) VALUES (?,?,?,?,?,?,?)");
            preparedStatement.setString(1, province.getLand());
            preparedStatement.setString(2, province.getName());
            preparedStatement.setDouble(3, province.getX1());
            preparedStatement.setDouble(4, province.getZ1());
            preparedStatement.setDouble(5, province.getX2());
            preparedStatement.setDouble(6, province.getZ2());
            preparedStatement.setString(7, province.getWorld());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Province getProvinceAtLocation(Location location, ArrayList<Province> provinces) {
        double x = location.getX();
        double z = location.getZ();
        for (Province province : provinces) {
            if(!location.getWorld().getName().equalsIgnoreCase(province.getWorld()))
                continue;
            double x1 = province.getX1();
            double x2 = province.getX2();
            double z1 = province.getZ1();
            double z2 = province.getZ2();
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                    z >= Math.min(z1, z2) && z <= Math.max(z1, z2)) {
                return province;
            }
        }
        return null;
    }

    @Nullable
    public Province getProvinceByName(String landName, String provinceName) {
        for(Province current : provinces) {
            if(current.getName().equalsIgnoreCase(provinceName) && current.getLand().equalsIgnoreCase(landName))
                return current;
        }
        return null;
    }

    public ArrayList<Province> getProvinces() {
        return provinces;
    }

}
