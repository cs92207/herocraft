package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.lands.roles.LandPermission;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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

public class LandManager implements Listener {

    private ArrayList<Land> allLands;
    private Map<Player, Land> playerLandCache;
    private final Map<String, Land> landChunkCache = new HashMap<>();

    public LandManager() {
        this.allLands = new ArrayList<>();
        this.playerLandCache = new HashMap<>();
        loadSavedLands();
    }

    @EventHandler
    public void onFastGovermentLandCreationPlaced(FurniturePlaceSuccessEvent event) {
        Player player = event.getPlayer();
        if(!event.getFurniture().getItemStack().hasItemMeta())
            return;
        if(!event.getFurniture().getItemStack().getItemMeta().hasDisplayName())
            return;
        if(!event.getFurniture().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lLand erstellen §0(Item platzieren)"))
            return;
        if(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player) != null) {
            if(!HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).isInLand(player)) {
                event.getFurniture().remove(true);
                player.sendMessage(Constant.PREFIX + "§7Platziere das Gebäude in deinem Land.");
            }
            return;
        }
        double x = event.getFurniture().getEntity().getLocation().getX();
        double z = event.getFurniture().getEntity().getLocation().getZ();
        double x1 = x + 50;
        double z1 = z + 50;
        double x2 = x - 50;
        double z2 = z - 50;
        if(!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
            event.getFurniture().remove(true);
            player.sendMessage(Constant.PREFIX + "§7Dein Land ist zu nahe an einem anderen Land oder am Spawn.");
            TextComponent textComponent = new TextComponent("§a§l(RandomTP)");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp 1288"));
            player.spigot().sendMessage(textComponent);
            return;
        }
        if(!LandManager.canCreateLandProvinceLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), "world", "", "")) {
            event.getFurniture().remove(true);
            player.sendMessage(Constant.PREFIX + "§7Dein Land ist zu nahe an einer Stadt oder am Spawn.");
            TextComponent textComponent = new TextComponent("§a§l(RandomTP)");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp 1288"));
            player.spigot().sendMessage(textComponent);
            return;
        }
        String name = player.getName() + "Land";
        double y = Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x, 1, z));
        if(Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), x, y, z)).isLiquid()) {
            return;
        }
        Land land = new Land(
                name,
                player.getUniqueId().toString(),
                player.getName(),
                new String[]{""},
                new String[]{""},
                new String[]{""},
                new String[]{""},
                x1,
                z1,
                x2,
                z2,
                x,
                y,
                z,
                0,
                4500,
                new String[]{""},
                new String[]{""},
                0,
                0,
                0,
                0,
                0,
                0
        );
        HeroCraft.getPlugin().getLandManager().getAllLands().add(land);
        HeroCraft.getPlugin().getLandManager().saveLand(land);
        player.sendMessage(Constant.PREFIX + "§7Sehr gut, du besitzt nun dein eigenes Land!");
    }

    public void removeLandFromIndex(Land land) {
        landChunkCache.entrySet().removeIf(entry -> entry.getValue().equals(land));
    }

    public void loadSavedLands() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `lands`");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Leere ggf. alte Daten (wichtig bei Reloads)
            allLands.clear();
            landChunkCache.clear();

            while (resultSet.next()) {
                String semiTrusted = resultSet.getString("spawnYaw");
                String[] semiTrustedArray;
                if (semiTrusted == null) {
                    semiTrustedArray = new String[]{""};
                } else {
                    semiTrustedArray = semiTrusted.split(",");
                }

                // Neues Land aus der Datenbank laden
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
                        resultSet.getString("trusted").split(","),
                        semiTrustedArray,
                        resultSet.getDouble("spawnYaw"),
                        resultSet.getDouble("spawnPitch"),
                        resultSet.getDouble("armee_coins"),
                        resultSet.getDouble("prison_spawn_x"),
                        resultSet.getDouble("prison_spawn_z"),
                        resultSet.getDouble("prison_spawn_Y")
                );

                // ✅ hier kommt die Indexierung rein
                allLands.add(loadedLand);
                indexLand(loadedLand);
            }

            Bukkit.getLogger().info("[HeroCraft] " + allLands.size() + " Lands geladen und indexiert.");

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
            boolean exists = hasOwnLand(land.getFounderUUID());

            if (exists) {
                // Bestehendes Land → vorher Index entfernen, falls sich Position geändert hat
                Land existing = getLandByName(land.getName());
                if (existing != null && (
                        existing.getX1() != land.getX1() ||
                                existing.getZ1() != land.getZ1() ||
                                existing.getX2() != land.getX2() ||
                                existing.getZ2() != land.getZ2())) {

                    // Koordinaten geändert → Cache neu aufbauen
                    removeLandFromIndex(existing);
                    indexLand(land);
                }
                PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                        .prepareStatement("DELETE FROM `lands` WHERE `founderUUID` = ?");
                preparedStatement.setString(1, land.getFounderUUID());
                preparedStatement.execute();
            } else {
                // Neues Land → direkt indexieren
                indexLand(land);
            }

            PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement(
                    "INSERT INTO `lands`(`name`,`founderUUID`,`founderName`,`coFounderUUIDs`,`coFounderNames`,`memberUUIDs`,`memberNames`,`x1`,`z1`,`x2`,`z2`,`spawnX`,`spawnY`,`spawnZ`,`coins`,`max_blocks`,`trusted`, `semi_trusted`, `spawnYaw`, `spawnPitch`, `prison_spawn_x`, `prison_spawn_z`, `prison_spawn_y`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            );

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
            preparedStatement1.setString(18, arrayToString(land.getSemiTrusted(), ","));
            preparedStatement1.setDouble(19, land.getSpawnYaw());
            preparedStatement1.setDouble(20, land.getSpawnPitch());
            preparedStatement1.setDouble(21, land.getPrisonSpawnX());
            preparedStatement1.setDouble(22, land.getPrisonSpawnZ());
            preparedStatement1.setDouble(23, land.getPrisonSpawnY());
            preparedStatement1.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        Land land = getLandAtLocation(damaged.getLocation(), allLands);
        if(land == null)
            return;

        // Fall 1: Spieler trifft Spieler
        if (damager instanceof Player && damaged instanceof Player) {
            Player attacker = (Player) damager;
            Player victim = (Player) damaged;
            if(!land.canBuild(attacker)) {
                event.setCancelled(true);
            } else {
                ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, land);
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
                    ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, land);
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
                    if(land.isSemiTrusted(attacker)) {
                        return;
                    }
                    event.setCancelled(true);
                } else {
                    ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, land);
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
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, land);
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
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, land);
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
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(attacker, land);
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




    /*@EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            Land land = getLandAtLocation(event.getEntity().getLocation(), allLands);
            if(land == null)
                return;
            if(event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                if(projectile.getShooter() instanceof Player) {
                    Player shooter = (Player) projectile.getShooter();
                    if(!land.canBuild(shooter)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if(!(event.getDamager() instanceof Player))
                return;


            if(!land.canBuild((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
        if(event.getDamager() instanceof Projectile) {
            Land land = getLandAtLocation(event.getEntity().getLocation(), allLands);
            if(land == null)
                return;
            Projectile projectile = (Projectile) event.getDamager();
            if(projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                if(!land.canBuild(shooter))
                    event.setCancelled(true);
            }
            return;
        }
        if(!(event.getDamager() instanceof Player))
            return;
        Land land = getLandAtLocation(event.getEntity().getLocation(), allLands);
        if(land == null)
            return;
        if(!land.canBuild((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("herowars.build"))
            return;
        Land land = getLandAtLocation(event.getBlock().getLocation(), allLands);
        if(land == null) {
            return;
        }
        if(!land.canBuild(player)) {
            event.setCancelled(true);
        } else {
            ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, land);
            if(perms == null)
                return;
            if(!perms.contains(LandPermission.BREAK)) {
                event.setCancelled(true);
                
            }
        }
    }

    public boolean isInOtherLand(Player player) {
        Land land = getLandAtLocation(player.getLocation(), allLands);
        if(land == null)
            return false;
        return !land.canBuild(player);
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
        Land land = getLandAtLocation(player.getLocation(), allLands);
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
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, land);
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
                        if(land.isSemiTrusted(player)) {
                            return;
                        }
                        event.setCancelled(true);
                    } else {
                        ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, land);
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
            ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, land);
            if(perms == null)
                return;
            if(!perms.contains(LandPermission.INTERACT)) {
                if(land.isOwnerUUID(player.getUniqueId().toString())) {
                    return;
                }
                
                event.setCancelled(true);
            }
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
        if(!land.canBuild(player)) {
            event.setCancelled(true);
        } else {
            ArrayList<LandPermission> perms = HeroCraft.getPlugin().landRoleManager.getLandPermissionFromPlayer(player, land);
            if(perms == null)
                return;
            if(!perms.contains(LandPermission.BUILD)) {
                
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        // 1️⃣ Nur reagieren, wenn sich der Spieler wirklich bewegt (nicht nur dreht)
        if (to == null || (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ())) {
            return;
        }

        // 2️⃣ Optional: alle paar Blöcke statt jedes Mal prüfen
        Land cachedLand = playerLandCache.get(player);
        Land enteredLand = getLandAtLocation(to, allLands);

        // 3️⃣ Wenn Spieler im gleichen Land bleibt, abbrechen
        if ((cachedLand == null && enteredLand == null) ||
                (cachedLand != null && enteredLand != null && cachedLand.equals(enteredLand))) {
            return;
        }

        // 4️⃣ Nur Titel senden, wenn sich Land wirklich ändert
        if (enteredLand != null && (cachedLand == null || !enteredLand.equals(cachedLand))) {
            player.sendTitle("§a§l" + enteredLand.getName(), "§7Wurde betreten...", 5, 40, 5);
            playerLandCache.put(player, enteredLand);
            return;
        }

        if (cachedLand != null && enteredLand == null) {
            player.sendTitle("§c§l" + cachedLand.getName(), "§7Wurde verlassen...", 5, 40, 5);
            playerLandCache.remove(player);
        }
    }


    private void indexLand(Land land) {
        if (land == null) return;

        int minChunkX = (int) Math.floor(Math.min(land.getX1(), land.getX2()) / 16.0);
        int maxChunkX = (int) Math.floor(Math.max(land.getX1(), land.getX2()) / 16.0);
        int minChunkZ = (int) Math.floor(Math.min(land.getZ1(), land.getZ2()) / 16.0);
        int maxChunkZ = (int) Math.floor(Math.max(land.getZ1(), land.getZ2()) / 16.0);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                landChunkCache.put("world:" + cx + ":" + cz, land);
            }
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
        if (location == null || location.getWorld() == null) return null;
        if (!location.getWorld().getName().equalsIgnoreCase("world")) return null;

        // O(1) chunk lookup
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        String key = location.getWorld().getName() + ":" + chunkX + ":" + chunkZ;

        Land cachedLand = HeroCraft.getPlugin().getLandManager().landChunkCache.get(key);
        return cachedLand; // ENDE. fertig. kein fallback mehr.
    }


    /*
    08.11.2025

    public static Land getLandAtLocation(Location location, ArrayList<Land> lands) {
        if (location == null || location.getWorld() == null) return null;
        if (!location.getWorld().getName().equalsIgnoreCase("world")) return null;

        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        String key = location.getWorld().getName() + ":" + chunkX + ":" + chunkZ;

        // Versuche schnellen Chunk-Lookup
        Land cachedLand = HeroCraft.getPlugin().getLandManager().landChunkCache.get(key);
        if (cachedLand != null) {
            double x = location.getX();
            double z = location.getZ();
            double x1 = cachedLand.getX1();
            double x2 = cachedLand.getX2();
            double z1 = cachedLand.getZ1();
            double z2 = cachedLand.getZ2();
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                    z >= Math.min(z1, z2) && z <= Math.max(z1, z2)) {
                return cachedLand;
            }
        }

        // Fallback (falls Chunk-Cache leer oder veraltet)
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
    } */


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
