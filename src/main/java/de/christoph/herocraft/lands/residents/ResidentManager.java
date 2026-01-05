package de.christoph.herocraft.lands.residents;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class ResidentManager implements Listener {

    private Connection connection;
    private HashMap<String, Resident> residents; // LocationKey (world:x:y:z) -> Resident

    // Score-Reduktion pro Update (alle 15 Minuten)
    // Ziel: In 2 Tagen (2880 Minuten) von 600 auf ~0 (sehr unglücklich = 200)
    // 2880 Min / 15 Min = 192 Updates
    // 600 - 200 = 400 Punkte Reduktion nötig
    // 400 / 192 = ~2.1 Punkte pro Update
    // Wir nehmen -3 Punkte pro Update für sanfte aber sichtbare Reduktion
    private static final int SCORE_REDUCTION_PER_UPDATE = 3;

    public ResidentManager() {
        this.connection = HeroCraft.getPlugin().getMySQL().getConnection();
        this.residents = new HashMap<>();
        createTable();
        addMissingColumns();
        loadResidents();
        startPeriodicUpdateTask();
    }
    
    /**
     * Fügt fehlende Spalten zur Tabelle hinzu (für Updates)
     */
    private void addMissingColumns() {
        addColumnIfNotExists("action_food_count", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("action_rest_count", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("action_health_count", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("action_social_count", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("action_entertainment_count", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("action_adventure_count", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("active_needs", "VARCHAR(50) NOT NULL DEFAULT ''");
        addColumnIfNotExists("seen_items", "TEXT");
        addColumnIfNotExists("social_time_tracking", "TEXT");
        addColumnIfNotExists("status", "VARCHAR(20) NOT NULL DEFAULT 'NEED'");
        addColumnIfNotExists("status_data", "VARCHAR(100)");
        addColumnIfNotExists("status_count", "INT NOT NULL DEFAULT 0");
    }
    
    /**
     * Hilfsmethode zum Hinzufügen einer Spalte, falls sie nicht existiert
     */
    private void addColumnIfNotExists(String columnName, String columnDefinition) {
        try {
            // Prüfe ob Spalte existiert
            PreparedStatement checkColumn = connection.prepareStatement(
                "SELECT COUNT(*) AS count FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'land_residents' AND COLUMN_NAME = ?"
            );
            checkColumn.setString(1, columnName);
            ResultSet rs = checkColumn.executeQuery();
            boolean columnExists = false;
            if (rs.next()) {
                columnExists = rs.getInt("count") > 0;
            }
            rs.close();
            checkColumn.close();
            
            if (!columnExists) {
                // Spalte existiert nicht, füge sie hinzu
                PreparedStatement alterTable = connection.prepareStatement(
                    "ALTER TABLE `land_residents` ADD COLUMN `" + columnName + "` " + columnDefinition
                );
                alterTable.execute();
                alterTable.close();
                System.out.println("[HeroCraft] Spalte " + columnName + " zur land_residents Tabelle hinzugefügt.");
            }
        } catch (SQLException e) {
            // Fehler beim Prüfen/Hinzufügen der Spalte
            System.out.println("[HeroCraft] Fehler beim Hinzufügen der Spalte " + columnName + ": " + e.getMessage());
        }
    }

    private void createTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `land_residents` (" +
                            "`land_name` VARCHAR(100) NOT NULL, " +
                            "`x` DOUBLE NOT NULL, " +
                            "`y` DOUBLE NOT NULL, " +
                            "`z` DOUBLE NOT NULL, " +
                            "`world` VARCHAR(50) NOT NULL, " +
                            "`happiness_score` INT NOT NULL DEFAULT 600, " +
                            "`last_tax_time` BIGINT NOT NULL DEFAULT 0, " +
                            "`current_demand` VARCHAR(500), " +
                            "`demand_completed` BOOLEAN NOT NULL DEFAULT FALSE, " +
                            "`last_interaction_time` BIGINT NOT NULL DEFAULT 0, " +
                            "`action_food_count` INT NOT NULL DEFAULT 0, " +
                            "`action_rest_count` INT NOT NULL DEFAULT 0, " +
                            "`action_health_count` INT NOT NULL DEFAULT 0, " +
                            "`action_social_count` INT NOT NULL DEFAULT 0, " +
                            "`action_entertainment_count` INT NOT NULL DEFAULT 0, " +
                            "`action_adventure_count` INT NOT NULL DEFAULT 0, " +
                            "`active_needs` VARCHAR(50) NOT NULL DEFAULT '', " +
                            "`seen_items` TEXT, " +
                            "`social_time_tracking` TEXT, " +
                            "`status` VARCHAR(20) NOT NULL DEFAULT 'NEED', " +
                            "`status_data` VARCHAR(100), " +
                            "`status_count` INT NOT NULL DEFAULT 0, " +
                            "PRIMARY KEY (`world`, `x`, `y`, `z`))"
            );
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Erstellen der land_residents-Tabelle: " + e.getMessage());
        }
    }

    private void loadResidents() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `land_residents`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String landName = resultSet.getString("land_name");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                String world = resultSet.getString("world");
                // Alte DB-Struktur unterstützen (happiness_level) oder neue (happiness_score)
                int happinessScore;
                try {
                    happinessScore = resultSet.getInt("happiness_score");
                } catch (SQLException e) {
                    // Fallback für alte DB-Struktur
                    int oldLevel = resultSet.getInt("happiness_level");
                    happinessScore = convertOldLevelToScore(oldLevel);
                }
                long lastTaxTime = resultSet.getLong("last_tax_time");
                String currentDemand = resultSet.getString("current_demand");
                boolean demandCompleted = resultSet.getBoolean("demand_completed");
                long lastInteractionTime = resultSet.getLong("last_interaction_time");
                
                // Action-Counts (mit Default-Werten für Rückwärtskompatibilität)
                int actionFoodCount, actionRestCount, actionHealthCount;
                int actionSocialCount, actionEntertainmentCount, actionAdventureCount;
                String activeNeeds, seenItems, socialTimeTracking;
                String status, statusData;
                int statusCount;
                try {
                    actionFoodCount = resultSet.getInt("action_food_count");
                    actionRestCount = resultSet.getInt("action_rest_count");
                    actionHealthCount = resultSet.getInt("action_health_count");
                    actionSocialCount = resultSet.getInt("action_social_count");
                    actionEntertainmentCount = resultSet.getInt("action_entertainment_count");
                    actionAdventureCount = resultSet.getInt("action_adventure_count");
                    activeNeeds = resultSet.getString("active_needs");
                    seenItems = resultSet.getString("seen_items");
                    socialTimeTracking = resultSet.getString("social_time_tracking");
                    status = resultSet.getString("status");
                    statusData = resultSet.getString("status_data");
                    statusCount = resultSet.getInt("status_count");
                } catch (SQLException e) {
                    // Rückwärtskompatibilität: Wenn Spalten nicht existieren, setze Defaults
                    actionFoodCount = 0;
                    actionRestCount = 0;
                    actionHealthCount = 0;
                    actionSocialCount = 0;
                    actionEntertainmentCount = 0;
                    actionAdventureCount = 0;
                    activeNeeds = null;
                    seenItems = null;
                    socialTimeTracking = null;
                    status = null;
                    statusData = null;
                    statusCount = 0;
                }

                Resident resident = new Resident(landName, x, y, z, world, 
                        happinessScore, lastTaxTime, currentDemand, demandCompleted, lastInteractionTime,
                        actionFoodCount, actionRestCount, actionHealthCount,
                        actionSocialCount, actionEntertainmentCount, actionAdventureCount,
                        activeNeeds, seenItems, socialTimeTracking, status, statusData, statusCount);
                
                // Wenn Status null/leer oder "NEED" ist, initialisiere mit zufälligem Status
                // (für bestehende Bewohner, die noch den Standard-Status haben)
                if (status == null || status.isEmpty() || status.equals("NEED")) {
                    generateNewStatus(resident);
                    updateResident(resident); // Speichere den neuen Status sofort
                }
                
                residents.put(resident.getLocationKey(), resident);
                
                // Bewohner werden nicht direkt gespawnt - sie werden beim Chunk-Load gespawnt
                // (siehe onChunkLoad Event Handler)
            }
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Laden der Bewohner: " + e.getMessage());
        }
    }

    public void spawnNewResident(Player player) {
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if (land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem Land.");
            return;
        }

        Location spawnLocation = player.getLocation();
        Villager villager = (Villager) player.getWorld().spawnEntity(spawnLocation, org.bukkit.entity.EntityType.VILLAGER);
        
        Resident resident = new Resident(land.getName(), 
                spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), 
                spawnLocation.getWorld().getName());
        
        // Initialisiere Status beim Erstellen
        generateNewStatus(resident);
        
        villager.setCustomName(resident.getVillagerName());
        villager.setCustomNameVisible(true);
        villager.setPersistent(true);
        villager.setRemoveWhenFarAway(false);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 500));
        villager.setAI(false);
        villager.setGravity(false);
        
        residents.put(resident.getLocationKey(), resident);
        saveResidentToDatabase(resident);

        player.sendMessage(Constant.PREFIX + "§7Bewohner §agespawnt§7. Rechtsklicke ihn, um mit ihm zu interagieren.");
    }

    private void spawnResidentVillager(Resident resident) {
        org.bukkit.World world = Bukkit.getWorld(resident.getWorld());
        if (world == null) {
            System.out.println("[HeroCraft] Warnung: Welt '" + resident.getWorld() + "' nicht gefunden für Bewohner bei " + resident.getLocationKey());
            return;
        }

        Location location = new Location(world, resident.getX(), resident.getY(), resident.getZ());
        org.bukkit.Chunk chunk = location.getChunk();
        
        // Stelle sicher, dass der Chunk geladen ist
        if (!chunk.isLoaded()) {
            // Chunk ist nicht geladen - kann nicht spawnen (wird beim ChunkLoad gespawnt)
            return;
        }
        
        // Prüfe ob bereits ein Villager an dieser Location existiert (anhand des Namens)
        for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
            if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                if (villager.getCustomName() != null && villager.getCustomName().startsWith("§e§lBewohner von " + resident.getLandName())) {
                    // Villager existiert bereits - nur Name updaten
                    villager.setCustomName(resident.getVillagerName());
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
            Villager villager = (Villager) world.spawnEntity(location, org.bukkit.entity.EntityType.VILLAGER);
            villager.setCustomName(resident.getVillagerName());
            villager.setCustomNameVisible(true);
            villager.setPersistent(true);
            villager.setRemoveWhenFarAway(false);
            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 500));
            villager.setAI(false);
            villager.setGravity(false);
        } catch (Exception e) {
            System.out.println("[HeroCraft] Fehler beim Spawnen des Bewohner-Villagers bei " + location.toString() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Konvertiert altes Level (1-5) zu Score für DB-Migration
     */
    private int convertOldLevelToScore(int oldLevel) {
        switch (oldLevel) {
            case 1: return 100;  // Sehr unglücklich
            case 2: return 300;  // Unglücklich
            case 3: return 600;  // Neutral
            case 4: return 700;  // Zufrieden
            case 5: return 900;  // Sehr zufrieden
            default: return 600;
        }
    }

    private void saveResidentToDatabase(Resident resident) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO `land_residents` (`land_name`, `x`, `y`, `z`, `world`, `happiness_score`, `last_tax_time`, `current_demand`, `demand_completed`, `last_interaction_time`, `action_food_count`, `action_rest_count`, `action_health_count`, `action_social_count`, `action_entertainment_count`, `action_adventure_count`, `active_needs`, `seen_items`, `social_time_tracking`, `status`, `status_data`, `status_count`) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
                            "ON DUPLICATE KEY UPDATE `happiness_score` = ?, `last_tax_time` = ?, `current_demand` = ?, `demand_completed` = ?, `last_interaction_time` = ?, `action_food_count` = ?, `action_rest_count` = ?, `action_health_count` = ?, `action_social_count` = ?, `action_entertainment_count` = ?, `action_adventure_count` = ?, `active_needs` = ?, `seen_items` = ?, `social_time_tracking` = ?, `status` = ?, `status_data` = ?, `status_count` = ?"
            );
            preparedStatement.setString(1, resident.getLandName());
            preparedStatement.setDouble(2, resident.getX());
            preparedStatement.setDouble(3, resident.getY());
            preparedStatement.setDouble(4, resident.getZ());
            preparedStatement.setString(5, resident.getWorld());
            preparedStatement.setInt(6, resident.getHappinessScore());
            preparedStatement.setLong(7, resident.getLastTaxTime());
            preparedStatement.setString(8, resident.getCurrentDemand());
            preparedStatement.setBoolean(9, resident.isDemandCompleted());
            preparedStatement.setLong(10, resident.getLastInteractionTime());
            preparedStatement.setInt(11, resident.getActionFoodCount());
            preparedStatement.setInt(12, resident.getActionRestCount());
            preparedStatement.setInt(13, resident.getActionHealthCount());
            preparedStatement.setInt(14, resident.getActionSocialCount());
            preparedStatement.setInt(15, resident.getActionEntertainmentCount());
            preparedStatement.setInt(16, resident.getActionAdventureCount());
            preparedStatement.setString(17, resident.getActiveNeeds());
            preparedStatement.setString(18, resident.getSeenItems());
            preparedStatement.setString(19, resident.getSocialTimeTracking());
            preparedStatement.setString(20, resident.getStatus());
            preparedStatement.setString(21, resident.getStatusData());
            preparedStatement.setInt(22, resident.getStatusCount());
            preparedStatement.setInt(23, resident.getHappinessScore());
            preparedStatement.setLong(24, resident.getLastTaxTime());
            preparedStatement.setString(25, resident.getCurrentDemand());
            preparedStatement.setBoolean(26, resident.isDemandCompleted());
            preparedStatement.setLong(27, resident.getLastInteractionTime());
            preparedStatement.setInt(28, resident.getActionFoodCount());
            preparedStatement.setInt(29, resident.getActionRestCount());
            preparedStatement.setInt(30, resident.getActionHealthCount());
            preparedStatement.setInt(31, resident.getActionSocialCount());
            preparedStatement.setInt(32, resident.getActionEntertainmentCount());
            preparedStatement.setInt(33, resident.getActionAdventureCount());
            preparedStatement.setString(34, resident.getActiveNeeds());
            preparedStatement.setString(35, resident.getSeenItems());
            preparedStatement.setString(36, resident.getSocialTimeTracking());
            preparedStatement.setString(37, resident.getStatus());
            preparedStatement.setString(38, resident.getStatusData());
            preparedStatement.setInt(39, resident.getStatusCount());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Speichern des Bewohners: " + e.getMessage());
        }
    }

    public void updateResident(Resident resident) {
        residents.put(resident.getLocationKey(), resident);
        saveResidentToDatabase(resident);
        updateVillagerName(resident);
    }

    /**
     * Updated den Namen des Villagers nur wenn er geladen ist
     */
    private void updateVillagerName(Resident resident) {
        org.bukkit.World world = Bukkit.getWorld(resident.getWorld());
        if (world == null) return;
        
        Location location = new Location(world, resident.getX(), resident.getY(), resident.getZ());
        org.bukkit.Chunk chunk = location.getChunk();
        
        // Nur updaten wenn Chunk geladen ist
        if (!chunk.isLoaded()) return;
        
        // Suche Villager an dieser Location
        for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
            if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                // Prüfe ob es der richtige Villager ist (anhand des Namens)
                if (villager.getCustomName() != null && villager.getCustomName().startsWith("§e§lBewohner von " + resident.getLandName())) {
                    villager.setCustomName(resident.getVillagerName());
                    return;
                }
            }
        }
    }

    public Resident getResidentByLocation(Location location) {
        String locationKey = Resident.createLocationKey(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        return residents.get(locationKey);
    }
    
    public Resident getResidentByLocation(String world, double x, double y, double z) {
        String locationKey = Resident.createLocationKey(world, x, y, z);
        return residents.get(locationKey);
    }

    public boolean isResident(Entity entity) {
        if (!(entity instanceof Villager)) return false;
        if (!entity.getCustomName().startsWith("§e§lBewohner von")) return false;
        Location location = entity.getLocation();
        String locationKey = Resident.createLocationKey(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        return residents.containsKey(locationKey);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = event.getItem().getItemMeta().getDisplayName();
        if (!displayName.equalsIgnoreCase("§4§lBewohner")) {
            return;
        }

        Player player = event.getPlayer();
        Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if (land == null || !land.canBuild(player)) {
            player.sendMessage(Constant.PREFIX + "§7Bitte platziere den Bewohner in deinem Land.");
            return;
        }

        if (event.getItem().getAmount() > 1) {
            org.bukkit.inventory.ItemStack newStack = event.getItem().clone();
            newStack.setAmount(event.getItem().getAmount() - 1);
            player.getInventory().setItemInMainHand(newStack);
        } else {
            player.getInventory().remove(event.getItem());
        }

        spawnNewResident(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Villager)) return;
        if (!isResident(event.getEntity())) return;

        // Bewohner können nicht getötet werden, außer durch Admin beim Sneaken
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            if (damageEvent.getDamager() instanceof Player) {
                Player player = (Player) damageEvent.getDamager();
                if (player.isSneaking()) {
                    Resident resident = getResidentByLocation(event.getEntity().getLocation());
                    if (resident != null) {
                        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(resident.getLandName());
                        if (land != null && land.isOwnerUUID(player.getUniqueId().toString())) {
                            // Admin kann Bewohner entfernen
                            removeResident(resident);
                            event.getEntity().remove();
                            player.sendMessage(Constant.PREFIX + "§7Bewohner entfernt.");
                            return;
                        }
                    }
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) return;
        if (!isResident(event.getRightClicked())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        Resident resident = getResidentByLocation(event.getRightClicked().getLocation());
        if (resident == null) return;

        // Fallback: Name sofort updaten wenn Spieler interagiert (falls Chunk gerade geladen wurde)
        updateVillagerName(resident);

        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(resident.getLandName());
        if (land == null || !land.canBuild(player)) {
            player.sendMessage("§e§lBewohner §7§l| §7Du gehörst nicht zu meinem Land!");
            return;
        }

        // Prüfe ob alle benötigten Offiziere im Umkreis vorhanden sind
        Location residentLocation = getResidentLocation(resident);
        if (residentLocation == null) {
            player.sendMessage("§e§lBewohner §7§l| §cFehler: Bewohner-Location nicht gefunden!");
            return;
        }
        
        if (!HeroCraft.getPlugin().getOfficialManager().hasRequiredOfficialsInRadius(residentLocation, resident.getLandName())) {
            String missing = HeroCraft.getPlugin().getOfficialManager().getMissingOfficialsMessage(residentLocation, resident.getLandName());
            player.sendMessage("§e§lBewohner §7§l| §cIm Umkreis von 10 Blöcken fehlen: §e" + missing);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }

        // Prüfe ob sehr unglücklich - Bewohner zieht weg
        if (resident.getHappinessLevel() <= 1) {
            // Bewohner verschwindet - Entity wird in removeResident entfernt
            removeResident(resident);
            
            // Nachricht an alle Land-Mitglieder
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (land.canBuild(onlinePlayer)) {
                    onlinePlayer.sendMessage(Constant.PREFIX + "§cEin Bewohner ist aus §e" + land.getName() + " §causgezogen.");
                }
            }
            player.sendMessage("§e§lBewohner §7§l| §cDer Bewohner ist zu unglücklich und ist weggezogen.");
            return;
        }

        // Status-basierte Interaktion
        String status = resident.getStatus();
        
        switch (status) {
            case "TAX":
                handleTaxStatus(player, resident, land);
                break;
            case "ROBBED":
                handleRobbedStatus(player, resident, land);
                break;
            case "ACCIDENT":
                handleAccidentStatus(player, resident, land);
                break;
            case "NEED":
            default:
                // Standard: GUI mit Bedürfnissen öffnen
                HeroCraft.getPlugin().getResidentGUI().openResidentGUI(player, resident, land);
                break;
        }
    }
    
    /**
     * Handhabt Steuer-Status: Steuern direkt auszahlen
     */
    private void handleTaxStatus(Player player, Resident resident, Land land) {
        long currentTime = System.currentTimeMillis();
        long lastTaxTime = resident.getLastTaxTime();
        long oneDayInMillis = 86400000L; // 24 Stunden
        boolean canCollectTaxes = (currentTime - lastTaxTime) >= oneDayInMillis;
        
        if (!canCollectTaxes) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBewohner §7§l| §cDie Steuern wurden heute bereits abgeholt!");
            return;
        }
        
        // Prüfe ob sehr unglücklich
        if (resident.getHappinessLevel() <= 1) {
            // Bewohner verschwindet - Entity wird in removeResident entfernt
            removeResident(resident);
            
            // Nachricht an alle Land-Mitglieder
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (land.canBuild(onlinePlayer)) {
                    onlinePlayer.sendMessage(Constant.PREFIX + "§cEin Bewohner ist aus §e" + land.getName() + " §causgezogen.");
                }
            }
            return;
        }
        
        double taxAmount = resident.getTaxAmount();
        if (taxAmount > 0) {
            land.setCoins(land.getCoins() + taxAmount);
            saveLand(land);
            resident.setLastTaxTime(currentTime);
            
            player.sendMessage("§e§lBewohner §7§l| §7Hier sind meine Steuern für heute: §a" + String.format("%.0f", taxAmount) + " Coins");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);
            
            // Status direkt zu NEED ändern (Steuern sind einmalig)
            resident.setStatus("NEED");
            resident.setStatusData("");
            resident.resetStatusInteractionCount();
            updateResident(resident);
        } else {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBewohner §7§l| §cIch bin zu unglücklich, um Steuern zu zahlen!");
        }
    }
    
    /**
     * Handhabt Überfall-Status
     */
    private void handleRobbedStatus(Player player, Resident resident, Land land) {
        String mobType = resident.getStatusData();
        
        if (mobType == null || mobType.isEmpty()) {
            // Noch kein Mob-Typ gesetzt (sollte nicht passieren)
            player.sendMessage("§e§lBewohner §7§l| §cIch wurde überfallen, aber ich weiß nicht mehr, wer es war...");
            return;
        }
        
        // Prüfe ob Spieler das Mob bereits getötet hat (wird über EntityDeathEvent gesetzt)
        if (resident.getStatusData().startsWith("KILLED:")) {
            // Mob wurde getötet, jetzt Coins zahlen
            double cost = resident.getRobbedCost();
            double playerCoins = HeroCraft.getPlugin().coin.getCoins(player);
            
            if (playerCoins < cost) {
                player.sendMessage("§e§lBewohner §7§l| §cDu hast nicht genug Coins! Du brauchst §e" + String.format("%.0f", cost) + " Coins §cfür die Schadensersatz.");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
            
            HeroCraft.getPlugin().coin.removeMoney(player, cost);
            player.sendMessage("§e§lBewohner §7§l| §aVielen Dank! Der Schaden wurde ersetzt. (-" + String.format("%.0f", cost) + " Coins)");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);
            
            // Status direkt zu NEED ändern (Überfall ist einmalig)
            resident.incrementStatusCount();
            resident.setStatus("NEED");
            resident.setStatusData("");
            resident.resetStatusInteractionCount();
            updateResident(resident);
        } else {
            // Zeige Info über Überfall
            String mobName = getMobDisplayName(mobType);
            player.sendMessage("§e§lBewohner §7§l| §cIch wurde von einem §4" + mobName + " §cüberfallen!");
            player.sendMessage("§e§lBewohner §7§l| §7Bitte töte dieses Monster und komme dann wieder!");
            double cost = resident.getRobbedCost();
            player.sendMessage("§e§lBewohner §7§l| §7Danach muss ich §e" + String.format("%.0f", cost) + " Coins §7für den Schadensersatz.");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_HURT, 0.7f, 1.0f);
        }
    }
    
    /**
     * Handhabt Unfall-Status
     */
    private void handleAccidentStatus(Player player, Resident resident, Land land) {
        int requiredApples = resident.getAccidentGoldenAppleCount();
        
        // Prüfe ob Spieler genug OP Goldäpfel hat
        int playerApples = countEnchantedGoldenApples(player);
        
        if (playerApples < requiredApples) {
            double cost = resident.getAccidentCost();
            player.sendMessage("§e§lBewohner §7§l| §cIch hatte einen Unfall und brauche Hilfe!");
            player.sendMessage("§e§lBewohner §7§l| §7Bitte bringe mir §e" + requiredApples + "x OP Goldäpfel §7und zahle §e" + String.format("%.0f", cost) + " Coins §7für Arztkosten.");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_HURT, 0.7f, 1.0f);
            return;
        }
        
        // Prüfe Coins
        double cost = resident.getAccidentCost();
        double playerCoins = HeroCraft.getPlugin().coin.getCoins(player);
        
        if (playerCoins < cost) {
            player.sendMessage("§e§lBewohner §7§l| §cDu hast nicht genug Coins! Du brauchst §e" + String.format("%.0f", cost) + " Coins §cfür die Arztkosten.");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }
        
        // Entferne OP Goldäpfel
        removeEnchantedGoldenApples(player, requiredApples);
        
        // Entferne Coins
        HeroCraft.getPlugin().coin.removeMoney(player, cost);
        
        player.sendMessage("§e§lBewohner §7§l| §aVielen Dank für deine Hilfe! Ich fühle mich schon besser.");
        player.sendMessage("§e§lBewohner §7§l| §7(-" + requiredApples + "x OP Goldäpfel, -" + String.format("%.0f", cost) + " Coins)");
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);
        
        // Status direkt zu NEED ändern (Unfall ist einmalig)
        resident.incrementStatusCount();
        resident.setStatus("NEED");
        resident.setStatusData("");
        resident.resetStatusInteractionCount();
        updateResident(resident);
    }
    
    /**
     * Generiert einen neuen zufälligen Status für einen Resident
     * TAX: Am seltensten (einmal pro Tag)
     * NEED: Am häufigsten (meistens)
     * ROBBED/ACCIDENT: Zufällig
     */
    public void generateNewStatus(Resident resident) {
        long currentTime = System.currentTimeMillis();
        long lastTaxTime = resident.getLastTaxTime();
        long oneDayInMillis = 86400000L;
        boolean canHaveTax = (currentTime - lastTaxTime) >= oneDayInMillis;
        
        java.util.Random random = new java.util.Random();
        
        if (canHaveTax && random.nextInt(10) == 0) {
            // 10% Chance für Steuern (wenn möglich)
            resident.setStatus("TAX");
            resident.setStatusData("");
        } else if (random.nextInt(5) == 0) {
            // 20% Chance für ROBBED oder ACCIDENT
            if (random.nextBoolean()) {
                resident.setStatus("ROBBED");
                // Zufälliger Mob-Typ
                String[] mobTypes = {"ZOMBIE", "SKELETON", "SPIDER", "CREEPER", "ENDERMAN", "WITCH", "PILLAGER"};
                String mobType = mobTypes[random.nextInt(mobTypes.length)];
                resident.setStatusData(mobType);
            } else {
                resident.setStatus("ACCIDENT");
                resident.setStatusData("");
            }
        } else {
            // 70% Chance für Bedürfnisse
            resident.setStatus("NEED");
            resident.setStatusData("");
        }
    }
    
    /**
     * Zählt OP Goldäpfel im Inventar des Spielers
     */
    private int countEnchantedGoldenApples(Player player) {
        int count = 0;
        for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == org.bukkit.Material.ENCHANTED_GOLDEN_APPLE) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    /**
     * Entfernt OP Goldäpfel aus dem Inventar
     */
    private void removeEnchantedGoldenApples(Player player, int amount) {
        int remaining = amount;
        for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == org.bukkit.Material.ENCHANTED_GOLDEN_APPLE) {
                int toRemove = Math.min(remaining, item.getAmount());
                if (item.getAmount() == toRemove) {
                    player.getInventory().remove(item);
                } else {
                    item.setAmount(item.getAmount() - toRemove);
                }
                remaining -= toRemove;
                if (remaining <= 0) break;
            }
        }
    }
    
    /**
     * Gibt den Anzeigenamen für einen Mob-Typ zurück
     */
    private String getMobDisplayName(String mobType) {
        switch (mobType.toUpperCase()) {
            case "ZOMBIE": return "Zombie";
            case "SKELETON": return "Skelett";
            case "SPIDER": return "Spinne";
            case "CREEPER": return "Creeper";
            case "ENDERMAN": return "Enderman";
            case "WITCH": return "Hexe";
            case "PILLAGER": return "Plünderer";
            default: return mobType;
        }
    }
    
    /**
     * Speichert ein Land (Helper-Methode)
     */
    private void saveLand(Land land) {
        HeroCraft.getPlugin().getLandManager().saveLand(land);
    }

    // Cooldown für SOCIAL-Tracking (nur alle 5 Sekunden updaten)
    private HashMap<UUID, Long> socialTrackingCooldown = new HashMap<>();
    private static final long SOCIAL_TRACKING_COOLDOWN_MS = 5000; // 5 Sekunden
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        
        // Prüfe alle Residents auf Überfall-Status
        for (Resident resident : new java.util.ArrayList<>(residents.values())) {
            if (!resident.getStatus().equals("ROBBED")) continue;
            
            String statusData = resident.getStatusData();
            if (statusData == null || statusData.isEmpty() || statusData.startsWith("KILLED:")) continue;
            
            // Prüfe ob das getötete Entity dem gespeicherten Mob-Typ entspricht
            EntityType killedType = event.getEntityType();
            if (killedType.name().equalsIgnoreCase(statusData)) {
                // Mob wurde getötet - markiere als erfüllt
                resident.setStatusData("KILLED:" + statusData);
                updateResident(resident);
                
                // Informiere Spieler
                killer.sendMessage("§e§lBewohner §7§l| §aGut gemacht! Der Übeltäter wurde getötet!");
                killer.sendMessage("§e§lBewohner §7§l| §7Gehe zurück zum Bewohner und zahle den Schadensersatz.");
                killer.playSound(killer.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                break; // Nur einen Resident pro Kill updaten
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Cooldown-Check (nur alle 5 Sekunden prüfen)
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (socialTrackingCooldown.containsKey(playerUUID)) {
            long lastCheck = socialTrackingCooldown.get(playerUUID);
            if (currentTime - lastCheck < SOCIAL_TRACKING_COOLDOWN_MS) {
                return; // Zu früh, überspringe
            }
        }
        socialTrackingCooldown.put(playerUUID, currentTime);
        
        Location playerLocation = player.getLocation();
        
        // Prüfe alle Residents im Radius
        double radius = 10.0; // Radius für "in der Nähe"
        
        for (Resident resident : new java.util.ArrayList<>(residents.values())) {
            Location residentLocation = getResidentLocation(resident);
            if (residentLocation == null) continue;
            
            // Prüfe ob Spieler in der Nähe ist
            if (playerLocation.getWorld().equals(residentLocation.getWorld())) {
                double distance = playerLocation.distance(residentLocation);
                if (distance <= radius) {
                    // Spieler ist in der Nähe - starte/aktualisiere Tracking
                    resident.startSocialTracking(player.getUniqueId());
                    // Nicht jedes Mal speichern - nur im Cache behalten, wird beim nächsten Save gespeichert
                }
            }
        }
    }
    
    /**
     * Gibt die Location des Bewohners zurück
     */
    private Location getResidentLocation(Resident resident) {
        org.bukkit.World world = Bukkit.getWorld(resident.getWorld());
        if (world == null) return null;
        return new Location(world, resident.getX(), resident.getY(), resident.getZ());
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        String worldName = chunk.getWorld().getName();
        
        // Spawne alle Bewohner in diesem Chunk, die noch nicht existieren
        for (Resident resident : residents.values()) {
            if (!resident.getWorld().equals(worldName)) continue;
            
            // Prüfe ob Bewohner in diesem Chunk ist
            int chunkX = (int) Math.floor(resident.getX() / 16.0);
            int chunkZ = (int) Math.floor(resident.getZ() / 16.0);
            
            if (chunk.getX() == chunkX && chunk.getZ() == chunkZ) {
                // Prüfe ob bereits ein Villager an dieser Location existiert
                boolean found = false;
                for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
                    if (entity instanceof Villager) {
                        Villager villager = (Villager) entity;
                        if (villager.getCustomName() != null && villager.getCustomName().startsWith("§e§lBewohner von " + resident.getLandName())) {
                            // Villager existiert bereits - nur Name updaten
                            updateVillagerName(resident);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    // Spawne neuen Villager
                    spawnResidentVillager(resident);
                }
            }
        }
        
        // Prüfe alle Entities im Chunk auf bereits existierende Bewohner (für Name-Update)
        for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Villager)) continue;
            Resident resident = getResidentByLocation(entity.getLocation());
            if (resident != null) {
                // Name sofort updaten wenn Chunk geladen wird
                updateVillagerName(resident);
            }
        }
    }

    private void handleResidentInteraction(Player player, Resident resident, Land land) {
        long currentTime = System.currentTimeMillis();
        long lastTaxTime = resident.getLastTaxTime();
        long oneDayInMillis = 86400000L; // 24 Stunden

        // Prüfe ob neuer Tag (seit letzter Steuer)
        boolean isNewDay = (currentTime - lastTaxTime) >= oneDayInMillis;

        if (isNewDay) {
            // Neuer Tag - Steuern können wieder gezahlt werden
            if (resident.getHappinessLevel() <= 1) {
                // Sehr unglücklich - Bewohner verschwindet - Entity wird in removeResident entfernt
                removeResident(resident);
                
                // Nachricht an alle Land-Mitglieder
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (land.canBuild(onlinePlayer)) {
                        onlinePlayer.sendMessage(Constant.PREFIX + "§cEin Bewohner ist aus §e" + land.getName() + " §causgezogen.");
                    }
                }
                return;
            }

            // Steuern geben
            double taxAmount = resident.getTaxAmount();
            if (taxAmount > 0) {
                land.setCoins(land.getCoins() + taxAmount);
                HeroCraft.getPlugin().getLandManager().saveLand(land);
                resident.setLastTaxTime(currentTime);
                
                player.sendMessage("");
                player.sendMessage("§e§lBewohner §7§l| §7Hier sind meine Steuern für heute: §a" + String.format("%.0f", taxAmount) + " Coins");
                player.sendMessage("");
                
                // Glücklichkeitsstatus aktualisieren (wird beim nächsten Klick besser)
                updateResident(resident);
            }
        } else {
            // Nicht neuer Tag - Forderung anzeigen oder prüfen
            if (resident.getCurrentDemand() == null || resident.isDemandCompleted()) {
                // Neue Forderung generieren
                String newDemand = generateNewDemand(resident, land);
                resident.setCurrentDemand(newDemand);
                resident.setDemandCompleted(false);
                player.sendMessage("");
                player.sendMessage("§e§lBewohner §7§l| §7" + newDemand);
                player.sendMessage("");
            } else {
                // Forderung prüfen
                boolean completed = checkDemandCompletion(resident, land);
                if (completed) {
                    player.sendMessage("");
                    player.sendMessage("§e§lBewohner §7§l| §aVielen Dank! Die Forderung wurde erfüllt.");
                    player.sendMessage("");
                    resident.setDemandCompleted(true);
                    // Score auf 1000 setzen (Sehr zufrieden)
                    resident.setHappinessScore(Resident.SCORE_REWARD_FULFILLED);
                } else {
                    player.sendMessage("");
                    player.sendMessage("§e§lBewohner §7§l| §7" + resident.getCurrentDemand());
                    player.sendMessage("§e§lBewohner §7§l| §cDie Forderung wurde noch nicht erfüllt.");
                    player.sendMessage("");
                }
            }
            
            resident.setLastInteractionTime(currentTime);
            updateResident(resident);
        }
    }

    /**
     * Startet den periodischen Update-Task (alle 15 Minuten)
     */
    private void startPeriodicUpdateTask() {
        // Alle 15 Minuten (900 Sekunden = 18000 Ticks)
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                updateAllResidentScores();
            }
        //}.runTaskTimer(HeroCraft.getPlugin(), 20L * 60 * 15, 20L * 60 * 15); // Start nach 15 Min, dann alle 15 Min
        }.runTaskTimer(HeroCraft.getPlugin(), 20*10, 20*2); // Start nach 15 Min, dann alle 15 Min
    }

    /**
     * Updated alle Bewohner-Scores und deren Namen
     */
    private void updateAllResidentScores() {
        for (Resident resident : new java.util.ArrayList<>(residents.values())) {
            System.out.println(resident.getLandName() + "Weniger zugriedenheit");
            // Score reduzieren
            int currentScore = resident.getHappinessScore();
            int newScore = Math.max(0, currentScore - SCORE_REDUCTION_PER_UPDATE);
            resident.setHappinessScore(newScore);
            
            // In Datenbank speichern (asynchron für Performance)
            Bukkit.getScheduler().runTaskAsynchronously(HeroCraft.getPlugin(), () -> {
                saveResidentToDatabase(resident);
            });
            
            // Namen updaten (nur wenn geladen)
            updateVillagerName(resident);
        }
    }

    private String generateNewDemand(Resident resident, Land land) {
        // Beispiel-Forderung: Infrastruktur-Erweiterung
        // Der Bewohner möchte, dass das Land wächst und mehr Mitglieder bekommt
        return "Die Infrastruktur sollte ausgebaut werden. Bitte erweitere dein Land auf mindestens 3 aktive Mitglieder (inkl. dir selbst).";
    }

    private boolean checkDemandCompletion(Resident resident, Land land) {
        if (resident.getCurrentDemand() == null) return false;
        
        String demand = resident.getCurrentDemand();
        
        // Forderung: Mindestens 3 aktive Mitglieder (Founder + CoFounders + Members)
        if (demand.contains("Infrastruktur") && demand.contains("Mitglieder")) {
            int totalMembers = 1; // Founder zählt mit
            totalMembers += land.getMemberUUIDs().length; // Mitglieder
            totalMembers += land.getCoFounderUUIDs().length; // Co-Founder
            
            // Prüfe ob mindestens 3 aktive Mitglieder vorhanden sind
            return totalMembers >= 3;
        }
        
        return false;
    }

    public void removeResident(Resident resident) {
        residents.remove(resident.getLocationKey());
        
        // Entferne Villager-Entity
        org.bukkit.World world = Bukkit.getWorld(resident.getWorld());
        if (world != null) {
            Location location = new Location(world, resident.getX(), resident.getY(), resident.getZ());
            org.bukkit.Chunk chunk = location.getChunk();
            if (chunk.isLoaded()) {
                for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
                    if (entity instanceof Villager) {
                        Villager villager = (Villager) entity;
                        if (villager.getCustomName() != null && villager.getCustomName().startsWith("§e§lBewohner von " + resident.getLandName())) {
                            entity.remove();
                            break;
                        }
                    }
                }
            }
        }
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `land_residents` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
            preparedStatement.setString(1, resident.getWorld());
            preparedStatement.setDouble(2, resident.getX());
            preparedStatement.setDouble(3, resident.getY());
            preparedStatement.setDouble(4, resident.getZ());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Entfernen des Bewohners: " + e.getMessage());
        }
    }

    /**
     * Entfernt alle Bewohner-Villager beim Server-Stop
     * (Villager werden jetzt beim Chunk-Load gespawnt, daher müssen sie nicht beim Shutdown entfernt werden)
     */
    public void removeAllResidentVillagers() {
        // Nicht mehr nötig - Villager werden beim Chunk-Load gespawnt
        // Beim Shutdown werden persistente Villager automatisch von Minecraft gespeichert
    }
}

