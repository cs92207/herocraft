package de.christoph.herocraft.lands.province;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.roles.LandPermission;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

public class ProvinceManager implements Listener {

    private ArrayList<Province> provinces;

    private Map<Player, Province> playerProvinceCache;

    private final Map<String, Province> provinceChunkCache = new HashMap<>();

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

        // Nur prüfen, wenn Block gewechselt wurde
        if (to == null || (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ())) return;

        Province last = playerProvinceCache.get(player);
        Province now = getProvinceAtLocation(to, provinces);

        if (last == now) return; // gleiches Gebiet oder beide null → keine Änderung

        if (now != null && (last == null || !now.equals(last))) {
            player.sendTitle("§a§l" + now.getName(),
                    "Stadt: §a" + HeroCraft.getPlugin().getLandManager().getLandByName(now.getLand()).getName(),
                    5, 40, 5);
            playerProvinceCache.put(player, now);
            return;
        }

        if (last != null && now == null) {
            player.sendTitle("§c§l" + last.getName(),
                    "Stadt: §c" + HeroCraft.getPlugin().getLandManager().getLandByName(last.getLand()).getName(),
                    5, 40, 5);
            playerProvinceCache.remove(player);
        }
    }


    private void indexProvince(Province province) {
        if (province == null) return;

        int minChunkX = (int) Math.floor(Math.min(province.getX1(), province.getX2()) / 16.0);
        int maxChunkX = (int) Math.floor(Math.max(province.getX1(), province.getX2()) / 16.0);
        int minChunkZ = (int) Math.floor(Math.min(province.getZ1(), province.getZ2()) / 16.0);
        int maxChunkZ = (int) Math.floor(Math.max(province.getZ1(), province.getZ2()) / 16.0);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                provinceChunkCache.put(province.getWorld() + ":" + cx + ":" + cz, province);
            }
        }
    }

    private void removeProvinceFromIndex(Province province) {
        provinceChunkCache.entrySet().removeIf(e -> e.getValue().equals(province));
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        Province land = getProvinceAtLocation(damaged.getLocation(), provinces);
        if(land == null)
            return;

        // Fall 1: Spieler trifft Spieler
        if (damager instanceof Player && damaged instanceof Player) {
            Player attacker = (Player) damager;
            Player victim = (Player) damaged;
            if(!land.canBuild(attacker)) {
                event.setCancelled(true);
            } else {
                ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                if(perms == null)
                    return;
                if(!perms.contains(LandPermission.PVP)) {
                    event.setCancelled(true);
                }
            }
            return;
        }

        // Fall 2 & 3: Spieler trifft ein freundliches oder feindliches Entity
        if (damager instanceof Player) {
            Player attacker = (Player) damager;

            if (isFriendlyEntity(damaged)) {
                if(!land.canBuild(attacker)) {
                    event.setCancelled(true);
                } else {
                    ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                    if(perms == null)
                        return;
                    if(!perms.contains(LandPermission.PVE_FRIENDLY)) {

                        event.setCancelled(true);
                    }
                }
                return;
            }

            if (isHostileEntity(damaged)) {
                if(!land.canBuild(attacker)) {
                    if(HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()).isSemiTrusted(attacker)) {
                        return;
                    }
                    event.setCancelled(true);
                } else {
                    ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                    if(perms == null)
                        return;
                    if(!perms.contains(LandPermission.PVE_UNFRIENDLY)) {

                        event.setCancelled(true);
                    }
                }
                return;
            }
        }

        // Fall 4: Projektil trifft Spieler, Projektil stammt von Spieler
        if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;

            if (projectile.getShooter() instanceof Player) {
                Player attacker = (Player) projectile.getShooter();

                // Fall 4: Spieler wird von Projektil getroffen
                if (damaged instanceof Player) {
                    if(!land.canBuild(attacker)) {
                        event.setCancelled(true);
                    } else {
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        if(perms == null)
                            return;
                        if(!perms.contains(LandPermission.PVP)) {

                            event.setCancelled(true);
                        }
                    }
                    return;
                }

                // Fall 5: Freundliches Entity wird von Projektil getroffen
                if (isFriendlyEntity(damaged)) {
                    if(!land.canBuild(attacker)) {
                        event.setCancelled(true);
                    } else {
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        if(perms == null)
                            return;
                        if(!perms.contains(LandPermission.PVE_FRIENDLY)) {

                            event.setCancelled(true);
                        }
                    }
                    return;
                }

                // Fall 6: Feindliches Entity wird von Projektil getroffen
                if (isHostileEntity(damaged)) {
                    if(!land.canBuild(attacker)) {
                        event.setCancelled(true);
                    } else {
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        if(perms == null)
                            return;
                        if(!perms.contains(LandPermission.PVE_UNFRIENDLY)) {

                            event.setCancelled(true);
                        }
                    }
                    return;
                }
            }
        }
    }

    private boolean isFriendlyEntity(Entity entity) {
        EntityType type = entity.getType();
        return type == EntityType.VILLAGER ||
                type == EntityType.IRON_GOLEM ||
                type == EntityType.SNOW_GOLEM ||
                type == EntityType.WANDERING_TRADER ||
                type == EntityType.ALLAY ||
                type == EntityType.HORSE ||
                type == EntityType.COW ||
                type == EntityType.SHEEP ||
                type == EntityType.PIG ||
                type == EntityType.CHICKEN ||
                type == EntityType.RABBIT ||
                type == EntityType.WOLF ||
                type == EntityType.CAT ||
                type == EntityType.PARROT;
    }

    private boolean isHostileEntity(Entity entity) {
        EntityType type = entity.getType();
        return type == EntityType.ZOMBIE ||
                type == EntityType.SKELETON ||
                type == EntityType.CREEPER ||
                type == EntityType.SPIDER ||
                type == EntityType.ENDERMAN ||
                type == EntityType.DROWNED ||
                type == EntityType.PILLAGER ||
                type == EntityType.WITCH ||
                type == EntityType.BLAZE ||
                type == EntityType.SLIME ||
                type == EntityType.PHANTOM;
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
        if(!land.canBuild(player)) {
            event.setCancelled(true);
        } else {
            ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
            if(perms == null)
                return;
            if(!perms.contains(LandPermission.BUILD)) {

                event.setCancelled(true);
            }
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
        if(!land.canBuild(player)) {
            event.setCancelled(true);
        } else {
            ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
            if(perms == null)
                return;
            if(!perms.contains(LandPermission.BREAK)) {
                event.setCancelled(true);

            }
        }
    }

    private boolean isDoor(Material type) {
        return type == Material.OAK_DOOR ||
                type == Material.BIRCH_DOOR ||
                type == Material.SPRUCE_DOOR ||
                type == Material.JUNGLE_DOOR ||
                type == Material.ACACIA_DOOR ||
                type == Material.DARK_OAK_DOOR ||
                type == Material.MANGROVE_DOOR ||
                type == Material.CHERRY_DOOR ||
                type == Material.CRIMSON_DOOR ||
                type == Material.WARPED_DOOR;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Province land = getProvinceAtLocation(player.getLocation(), provinces);
        if(land == null)
            return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                Material type = clickedBlock.getType();
                if (type == Material.CHEST || type == Material.TRAPPED_CHEST) {
                    if(!land.canBuild(player)) {
                        event.setCancelled(true);
                    } else {
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        if(perms == null)
                            return;
                        if(!perms.contains(LandPermission.OPEN_CHESTS)) {

                            event.setCancelled(true);
                        }
                    }
                    return;
                }

                if (isDoor(type)) {
                    if (!land.canBuild(player)) {
                        if(HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()).isSemiTrusted(player)) {
                            return;
                        }
                        event.setCancelled(true);
                    } else {
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        if (perms == null)
                            return;
                        if (!perms.contains(LandPermission.OPEN_DOOR)) {

                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
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
        }  else {
            ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
            if(perms == null)
                return;
            if(!perms.contains(LandPermission.INTERACT)) {
                if(HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()).isOwnerUUID(player.getUniqueId().toString())) {
                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    private void loadProvinces() {
        try {
            PreparedStatement ps = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("SELECT * FROM `provinces`");
            ResultSet rs = ps.executeQuery();

            provinces.clear();
            provinceChunkCache.clear();

            while (rs.next()) {
                Province p = new Province(
                        rs.getString("land"),
                        rs.getString("name"),
                        rs.getDouble("x1"),
                        rs.getDouble("z1"),
                        rs.getDouble("x2"),
                        rs.getDouble("z2"),
                        rs.getString("world")
                );
                provinces.add(p);
                indexProvince(p); // ✅ Chunk-Cache sofort aufbauen
            }

            HeroCraft.getPlugin().getLogger().info("[HeroCraft] " + provinces.size() + " Provinzen geladen und indexiert.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveProvince(Province province) {
        try {
            Province existing = getProvinceByName(province.getLand(), province.getName());
            boolean newProvince = existing == null;

            if (!newProvince) {
                // Prüfe, ob sich Grenzen geändert haben
                if (!existing.getWorld().equalsIgnoreCase(province.getWorld()) ||
                        existing.getX1() != province.getX1() ||
                        existing.getZ1() != province.getZ1() ||
                        existing.getX2() != province.getX2() ||
                        existing.getZ2() != province.getZ2()) {
                    removeProvinceFromIndex(existing);
                    indexProvince(province);
                }
                PreparedStatement del = HeroCraft.getPlugin().getMySQL().getConnection()
                        .prepareStatement("DELETE FROM `provinces` WHERE `land` = ? AND `name` = ?");
                del.setString(1, province.getLand());
                del.setString(2, province.getName());
                del.execute();
            } else {
                // Neue Province → direkt indexieren
                indexProvince(province);
            }

            PreparedStatement ps = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("INSERT INTO `provinces` (`land`,`name`,`x1`,`z1`,`x2`,`z2`,`world`) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1, province.getLand());
            ps.setString(2, province.getName());
            ps.setDouble(3, province.getX1());
            ps.setDouble(4, province.getZ1());
            ps.setDouble(5, province.getX2());
            ps.setDouble(6, province.getZ2());
            ps.setString(7, province.getWorld());
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*
    08.11.2025

    public static Province getProvinceAtLocation(Location location, ArrayList<Province> provinces) {
        if (location == null || location.getWorld() == null) return null;

        // Greife auf den Cache der aktiven ProvinceManager-Instanz zu
        ProvinceManager manager = HeroCraft.getPlugin().getProvinceManager();
        Map<String, Province> cache = manager.provinceChunkCache;

        String world = location.getWorld().getName();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        String key = world + ":" + chunkX + ":" + chunkZ;

        // 1️⃣ Schnell über Cache prüfen
        Province cached = cache.get(key);
        if (cached != null) {
            double x = location.getX();
            double z = location.getZ();
            double x1 = cached.getX1(), x2 = cached.getX2(), z1 = cached.getZ1(), z2 = cached.getZ2();
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                    z >= Math.min(z1, z2) && z <= Math.max(z1, z2)) {
                return cached;
            }
        }

        // 2️⃣ Fallback – falls Cache leer oder ungenau
        for (Province p : provinces) {
            if (!world.equalsIgnoreCase(p.getWorld())) continue;
            double x = location.getX();
            double z = location.getZ();
            if (x >= Math.min(p.getX1(), p.getX2()) && x <= Math.max(p.getX1(), p.getX2()) &&
                    z >= Math.min(p.getZ1(), p.getZ2()) && z <= Math.max(p.getZ1(), p.getZ2())) {
                return p;
            }
        }

        return null;
    }*/


    public static Province getProvinceAtLocation(Location location, ArrayList<Province> provinces) {
        if (location == null || location.getWorld() == null) return null;

        ProvinceManager manager = HeroCraft.getPlugin().getProvinceManager();
        Map<String, Province> cache = manager.provinceChunkCache;

        String world = location.getWorld().getName();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        String key = world + ":" + chunkX + ":" + chunkZ;

        // O(1) lookup
        return cache.get(key);
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
