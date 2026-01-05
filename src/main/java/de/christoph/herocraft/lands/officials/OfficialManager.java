package de.christoph.herocraft.lands.officials;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.event.world.ChunkLoadEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class OfficialManager implements Listener {

    private Connection connection;
    private HashMap<String, Official> officials;

    public OfficialManager() {
        this.connection = HeroCraft.getPlugin().getMySQL().getConnection();
        this.officials = new HashMap<>();
        createTable();
        addMissingColumns();
        loadOfficials();
        startPeriodicUpdateTask();
    }

    private void createTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `land_officials` (" +
                            "`land_name` VARCHAR(100) NOT NULL, " +
                            "`x` DOUBLE NOT NULL, " +
                            "`y` DOUBLE NOT NULL, " +
                            "`z` DOUBLE NOT NULL, " +
                            "`world` VARCHAR(50) NOT NULL, " +
                            "`type` VARCHAR(20) NOT NULL, " +
                            "`last_salary_time` BIGINT NOT NULL DEFAULT 0, " +
                            "`salary_count` INT NOT NULL DEFAULT 0, " +
                            "PRIMARY KEY (`world`, `x`, `y`, `z`))"
            );
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Erstellen der land_officials-Tabelle: " + e.getMessage());
        }
    }

    /**
     * Fügt fehlende Spalten zur Tabelle hinzu (für Updates)
     */
    private void addMissingColumns() {
        // Keine zusätzlichen Spalten nötig für jetzt
    }

    private void loadOfficials() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `land_officials`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String landName = resultSet.getString("land_name");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                String world = resultSet.getString("world");
                String type = resultSet.getString("type");
                long lastSalaryTime = resultSet.getLong("last_salary_time");
                int salaryCount = resultSet.getInt("salary_count");

                Official official = new Official(landName, x, y, z, world, type, lastSalaryTime, salaryCount);
                officials.put(official.getLocationKey(), official);

                // Beamte werden nicht direkt gespawnt - sie werden beim Chunk-Load gespawnt
                // (siehe onChunkLoad Event Handler)
            }
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Laden der Beamten: " + e.getMessage());
        }
    }

    public void spawnNewOfficial(Player player, String type) {
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem Land.");
            return;
        }

        Location spawnLocation = player.getLocation();
        Husk villager = (Husk) player.getWorld().spawnEntity(
                spawnLocation, EntityType.HUSK);

        Official official = new Official(
                land.getName(),
                spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(),
                spawnLocation.getWorld().getName(), type
        );

        villager.setCustomName(official.getVillagerName());
        villager.setCustomNameVisible(true);
        villager.setPersistent(true);
        villager.setRemoveWhenFarAway(false);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 500));
        villager.setAI(false);
        villager.setGravity(false);

        // 🔴 ROTE LEDERRÜSTUNG
        EntityEquipment eq = villager.getEquipment();
        if (eq != null) {
            if(type.equalsIgnoreCase("DOCTOR")) {
                eq.setChestplate(createPinkLeatherArmor(Material.LEATHER_CHESTPLATE));
                eq.setLeggings(createPinkLeatherArmor(Material.LEATHER_LEGGINGS));
                eq.setBoots(createPinkLeatherArmor(Material.LEATHER_BOOTS));
            } else if(type.equalsIgnoreCase("POLICE")) {
                eq.setChestplate(createBlueLeatherArmor(Material.LEATHER_CHESTPLATE));
                eq.setLeggings(createBlueLeatherArmor(Material.LEATHER_LEGGINGS));
                eq.setBoots(createBlueLeatherArmor(Material.LEATHER_BOOTS));
            } else {
                eq.setChestplate(createRedLeatherArmor(Material.LEATHER_CHESTPLATE));
                eq.setLeggings(createRedLeatherArmor(Material.LEATHER_LEGGINGS));
                eq.setBoots(createRedLeatherArmor(Material.LEATHER_BOOTS));
            }
            // nichts droppen lassen
            eq.setHelmetDropChance(0.0f);
            eq.setChestplateDropChance(0.0f);
            eq.setLeggingsDropChance(0.0f);
            eq.setBootsDropChance(0.0f);
        }

        officials.put(official.getLocationKey(), official);
        saveOfficialToDatabase(official);

        player.sendMessage(Constant.PREFIX + "§7" + official.getVillagerName()
                + " §agespawnt§7. Rechtsklicke ihn, um das Gehalt zu zahlen.");
    }

    private ItemStack createRedLeatherArmor(Material material) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.RED);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPinkLeatherArmor(Material material) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.PURPLE);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBlueLeatherArmor(Material material) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.BLUE);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void spawnOfficialVillager(Official official) {
        org.bukkit.World world = Bukkit.getWorld(official.getWorld());
        if (world == null) {
            System.out.println("[HeroCraft] Warnung: Welt '" + official.getWorld() + "' nicht gefunden für Beamten bei " + official.getLocationKey());
            return;
        }

        Location location = new Location(world, official.getX(), official.getY(), official.getZ());
        org.bukkit.Chunk chunk = location.getChunk();

        // Stelle sicher, dass der Chunk geladen ist
        if (!chunk.isLoaded()) {
            // Chunk ist nicht geladen - kann nicht spawnen (wird beim ChunkLoad gespawnt)
            return;
        }

        // Prüfe ob bereits ein Villager an dieser Location existiert (basierend auf Location, nicht Name)
        for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
            if (entity instanceof Husk) {
                Husk villager = (Husk) entity;
                Location entityLoc = villager.getLocation();
                // Prüfe nur anhand der Location (nicht des Namens, da sich der Name ändern kann)
                if (Math.abs(entityLoc.getX() - official.getX()) < 0.5 &&
                    Math.abs(entityLoc.getY() - official.getY()) < 0.5 &&
                    Math.abs(entityLoc.getZ() - official.getZ()) < 0.5) {
                    // Villager existiert bereits an dieser Location - nur Name updaten
                    villager.setCustomName(official.getVillagerName());
                    villager.setCustomNameVisible(true);
                    villager.setPersistent(true);
                    villager.setRemoveWhenFarAway(false);
                    villager.setAI(false);
                    villager.setGravity(false);
                    return;
                }
            }
        }

        try {
            Husk villager = (Husk) world.spawnEntity(location, EntityType.HUSK);
            villager.setCustomName(official.getVillagerName());
            villager.setCustomNameVisible(true);
            villager.setPersistent(true);
            villager.setRemoveWhenFarAway(false);
            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 500));
            villager.setAI(false);
            villager.setGravity(false);
        } catch (Exception e) {
            System.out.println("[HeroCraft] Fehler beim Spawnen des Beamten-Villagers bei " + location.toString() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveOfficialToDatabase(Official official) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO `land_officials` (`land_name`, `x`, `y`, `z`, `world`, `type`, `last_salary_time`, `salary_count`) " +
                            "VALUES (?,?,?,?,?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE `last_salary_time` = ?, `salary_count` = ?"
            );
            preparedStatement.setString(1, official.getLandName());
            preparedStatement.setDouble(2, official.getX());
            preparedStatement.setDouble(3, official.getY());
            preparedStatement.setDouble(4, official.getZ());
            preparedStatement.setString(5, official.getWorld());
            preparedStatement.setString(6, official.getType());
            preparedStatement.setLong(7, official.getLastSalaryTime());
            preparedStatement.setInt(8, official.getSalaryCount());
            preparedStatement.setLong(9, official.getLastSalaryTime());
            preparedStatement.setInt(10, official.getSalaryCount());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Speichern des Beamten: " + e.getMessage());
        }
    }

    public void updateOfficial(Official official) {
        saveOfficialToDatabase(official);
        updateVillagerName(official);
    }

    private void updateVillagerName(Official official) {
        System.out.println("test1");
        org.bukkit.World world = Bukkit.getWorld(official.getWorld());
        if (world == null) return;
        System.out.println("test2");
        Location location = new Location(world, official.getX(), official.getY(), official.getZ());
        org.bukkit.Chunk chunk = location.getChunk();

        if (!chunk.isLoaded()) return;
        // Suche den Villager an dieser Location
        for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
            if (entity instanceof Husk && entity.isValid() && !entity.isDead()) {
                Husk villager = (Husk) entity;
                // Prüfe ob es der richtige Beamte ist (anhand des Namens und der Location)
                if (villager.getCustomName() != null && villager.getCustomName().contains("§")) {
                    Location entityLoc = villager.getLocation();
                    if (Math.abs(entityLoc.getX() - official.getX()) < 0.1 &&
                        Math.abs(entityLoc.getY() - official.getY()) < 0.1 &&
                        Math.abs(entityLoc.getZ() - official.getZ()) < 0.1) {
                        villager.setCustomName(official.getVillagerName());
                        villager.setCustomNameVisible(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = event.getItem().getItemMeta().getDisplayName();
        String type = null;
        if (displayName.equalsIgnoreCase("§c§lFeuerwehrmann")) {
            type = "FIREFIGHTER";
        } else if (displayName.equalsIgnoreCase("§b§lPolizist")) {
            type = "POLICE";
        } else if (displayName.equalsIgnoreCase("§a§lNotarzt")) {
            type = "DOCTOR";
        } else {
            return;
        }

        Player player = event.getPlayer();
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem Land.");
            return;
        }

        Land landAtLocation = HeroCraft.getPlugin().getLandManager().getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if (landAtLocation == null || !landAtLocation.canBuild(player)) {
            player.sendMessage(Constant.PREFIX + "§7Bitte platziere den Beamten in deinem Land.");
            return;
        }

        if (event.getItem().getAmount() > 1) {
            org.bukkit.inventory.ItemStack newStack = event.getItem().clone();
            newStack.setAmount(event.getItem().getAmount() - 1);
            player.getInventory().setItemInMainHand(newStack);
        } else {
            player.getInventory().remove(event.getItem());
        }

        spawnNewOfficial(player, type);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Husk)) return;
        if (!isOfficial(event.getEntity())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Husk)) return;
        if (!isOfficial(event.getEntity())) return;

        // Nur Land-Admin kann durch Sneak + Hit entfernen
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Official official = getOfficialByLocation(event.getEntity().getLocation());
            if (official != null) {
                Land land = HeroCraft.getPlugin().getLandManager().getLandByName(official.getLandName());
                if (land != null && land.isOwnerUUID(player.getUniqueId().toString()) && player.isSneaking()) {
                    // Entfernen erlauben
                    event.setCancelled(false);
                    event.getEntity().remove();
                    removeOfficial(official);
                    return;
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Husk)) return;
        if (!isOfficial(event.getRightClicked())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        Official official = getOfficialByLocation(event.getRightClicked().getLocation());
        if (official == null) return;

        updateVillagerName(official);

        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(official.getLandName());
        if (land == null || !land.canBuild(player)) {
            player.sendMessage("§e§lBeamter §7§l| §7Du gehörst nicht zu meinem Land!");
            return;
        }

        handleSalaryPayment(player, official, land);
    }

    private void handleSalaryPayment(Player player, Official official, Land land) {
        // Prüfe ob überfällig (2 Tage)
        if (official.isSalaryOverdue()) {
            // Entfernen - finde Entity an Location
            org.bukkit.World world = Bukkit.getWorld(official.getWorld());
            if (world != null) {
                Location location = new Location(world, official.getX(), official.getY(), official.getZ());
                org.bukkit.Chunk chunk = location.getChunk();
                if (chunk.isLoaded()) {
                    for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
                        if (entity instanceof Husk && entity.isValid() && !entity.isDead()) {
                            Location entityLoc = entity.getLocation();
                            if (Math.abs(entityLoc.getX() - official.getX()) < 0.1 &&
                                Math.abs(entityLoc.getY() - official.getY()) < 0.1 &&
                                Math.abs(entityLoc.getZ() - official.getZ()) < 0.1) {
                                entity.remove();
                                break;
                            }
                        }
                    }
                }
            }
            removeOfficial(official);

            // Nachricht an alle Land-Mitglieder
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (land.canBuild(onlinePlayer)) {
                    onlinePlayer.sendMessage(Constant.PREFIX + "§cEin " + official.getVillagerName() + " §chat " + land.getName() + " §cverlassen (nicht bezahlt).");
                }
            }
            return;
        }

        // Prüfe ob heute bereits gezahlt
        if (official.isSalaryPaidToday()) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBeamter §7§l| §cDas Gehalt wurde heute bereits gezahlt!");
            return;
        }

        double salary = official.getCurrentSalary();
        double playerCoins = HeroCraft.getPlugin().coin.getCoins(player);

        if (playerCoins < salary) {
            player.sendMessage("§e§lBeamter §7§l| §cDu hast nicht genug Coins! Du brauchst §e" + String.format("%.0f", salary) + " Coins §cfür das Gehalt.");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }

        HeroCraft.getPlugin().coin.removeMoney(player, salary);
        official.setLastSalaryTime(System.currentTimeMillis());
        official.incrementSalaryCount();

        player.sendMessage("§e§lBeamter §7§l| §7Gehalt gezahlt: §a" + String.format("%.0f", salary) + " Coins");
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);

        updateOfficial(official);
        // Namen sofort aktualisieren, damit der Status (bezahlt) sofort sichtbar ist
        updateVillagerName(official);
    }

    public boolean isOfficial(Entity entity) {
        if (!(entity instanceof Husk)) return false;
        return getOfficialByLocation(entity.getLocation()) != null;
    }

    public Official getOfficialByLocation(Location location) {
        String locationKey = Official.createLocationKey(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        return officials.get(locationKey);
    }

    public void removeOfficial(Official official) {
        officials.remove(official.getLocationKey());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `land_officials` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
            preparedStatement.setString(1, official.getWorld());
            preparedStatement.setDouble(2, official.getX());
            preparedStatement.setDouble(3, official.getY());
            preparedStatement.setDouble(4, official.getZ());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Entfernen des Beamten: " + e.getMessage());
        }
    }

    /**
     * Prüft ob im Umkreis von 10 Blöcken alle benötigten Offiziere vorhanden sind
     */
    public boolean hasRequiredOfficialsInRadius(Location location, String landName) {
        boolean hasFirefighter = false;
        boolean hasPolice = false;
        boolean hasDoctor = false;

        for (Official official : officials.values()) {
            if (!official.getLandName().equals(landName)) continue;

            Location officialLocation = new Location(
                    Bukkit.getWorld(official.getWorld()),
                    official.getX(),
                    official.getY(),
                    official.getZ()
            );

            if (location.getWorld().getName().equals(officialLocation.getWorld().getName())) {
                double distance = location.distance(officialLocation);
                if (distance <= 10.0) {
                    switch (official.getType()) {
                        case "FIREFIGHTER":
                            hasFirefighter = true;
                            break;
                        case "POLICE":
                            hasPolice = true;
                            break;
                        case "DOCTOR":
                            hasDoctor = true;
                            break;
                    }
                }
            }
        }

        return hasFirefighter && hasPolice && hasDoctor;
    }

    /**
     * Gibt eine Liste der fehlenden Offiziere zurück (für Fehlermeldung)
     */
    public String getMissingOfficialsMessage(Location location, String landName) {
        boolean hasFirefighter = false;
        boolean hasPolice = false;
        boolean hasDoctor = false;

        for (Official official : officials.values()) {
            if (!official.getLandName().equals(landName)) continue;

            Location officialLocation = new Location(
                    Bukkit.getWorld(official.getWorld()),
                    official.getX(),
                    official.getY(),
                    official.getZ()
            );

            if (location.getWorld().getName().equals(officialLocation.getWorld().getName())) {
                double distance = location.distance(officialLocation);
                if (distance <= 10.0) {
                    switch (official.getType()) {
                        case "FIREFIGHTER":
                            hasFirefighter = true;
                            break;
                        case "POLICE":
                            hasPolice = true;
                            break;
                        case "DOCTOR":
                            hasDoctor = true;
                            break;
                    }
                }
            }
        }

        StringBuilder missing = new StringBuilder();
        if (!hasFirefighter) missing.append("Feuerwehrmann");
        if (!hasPolice) {
            if (missing.length() > 0) missing.append(", ");
            missing.append("Polizist");
        }
        if (!hasDoctor) {
            if (missing.length() > 0) missing.append(", ");
            missing.append("Notarzt");
        }

        return missing.toString();
    }

    public HashMap<String, Official> getOfficials() {
        return officials;
    }

    /**
     * Startet den periodischen Update-Task (alle 15 Minuten)
     */
    private void startPeriodicUpdateTask() {
        // Alle 15 Minuten (900 Sekunden = 18000 Ticks)
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                updateAllOfficialNames();
            }
        }.runTaskTimer(HeroCraft.getPlugin(), 20L * 5, 20L * 60 * 15); // Start nach 15 Min, dann alle 15 Min
    }

    /**
     * Updated alle Beamten-Namen (Status der Bezahlung)
     */
    private void updateAllOfficialNames() {
        for (Official official : new java.util.ArrayList<>(officials.values())) {
            // Namen updaten (nur wenn geladen)
            updateVillagerName(official);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        org.bukkit.Chunk chunk = event.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Spawne alle Beamten in diesem Chunk
        for (Official official : officials.values()) {
            Location officialLocation = new Location(
                    Bukkit.getWorld(official.getWorld()),
                    official.getX(),
                    official.getY(),
                    official.getZ()
            );

            if (officialLocation.getChunk().getX() == chunkX && officialLocation.getChunk().getZ() == chunkZ) {
                // Prüfe ob bereits ein Villager an dieser Location existiert (basierend auf Location, nicht Name)
                boolean found = false;
                for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
                    if (entity instanceof Husk) {
                        Husk villager = (Husk) entity;
                        Location entityLoc = villager.getLocation();
                        // Prüfe nur anhand der Location (nicht des Namens, da sich der Name ändern kann)
                        if (Math.abs(entityLoc.getX() - official.getX()) < 0.5 &&
                            Math.abs(entityLoc.getY() - official.getY()) < 0.5 &&
                            Math.abs(entityLoc.getZ() - official.getZ()) < 0.5) {
                            // Villager existiert bereits an dieser Location - nur Name updaten
                            updateVillagerName(official);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    // Spawne neuen Villager nur wenn keiner an dieser Location existiert
                    spawnOfficialVillager(official);
                }
            }
        }

        // Prüfe alle Entities im Chunk auf bereits existierende Beamte (für Name-Update)
        for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Husk)) continue;
            Official official = getOfficialByLocation(entity.getLocation());
            if (official != null) {
                // Name sofort updaten wenn Chunk geladen wird
                updateVillagerName(official);
            }
        }
    }
}

