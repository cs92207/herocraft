package de.christoph.herocraft.lands.residents;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ResidentGUI implements Listener {

    private static final String GUI_TITLE = ":offset_-16::daily_task:";
    
    // Slots im GUI (9x5 = 45 Slots)
    private static final int SLOT_HAPPINESS = 4; // Oben, Mitte (für Progressbar)
    private static final int SLOT_TAXES = 22; // Mitte
    
    // Aktionen (Reihe 2 und 3)
    private static final int SLOT_FOOD = 10;        // Nahrung bringen
    private static final int SLOT_REST = 11;        // Erholung ermöglichen
    private static final int SLOT_HEALTH = 12;      // Gesundheit ermöglichen
    private static final int SLOT_SOCIAL = 13;      // Sozialbedürfnis befriedigen
    private static final int SLOT_ENTERTAINMENT = 14; // Unterhaltung bringen
    private static final int SLOT_ADVENTURE = 15;   // Abenteuerlust befriedigen
    private static final int SLOT_COMMING_SOON_1 = 16; // Coming Soon 1
    private static final int SLOT_COMMING_SOON_2 = 17; // Coming Soon 2

    // Temporäre Speicherung für GUI-Interaktionen
    private static HashMap<Player, Resident> openGUIs = new HashMap<>();

    /**
     * Öffnet das GUI für einen Bewohner
     */
    public void openResidentGUI(Player player, Resident resident, Land land) {
        // Speichere den Resident für diesen Spieler
        openGUIs.put(player, resident);
        Inventory inventory = Bukkit.createInventory(null, 9 * 5, GUI_TITLE);

        // Glücklichkeits-Progressbar
        ItemStack happinessItem = createHappinessProgressItem(resident);
        inventory.setItem(SLOT_HAPPINESS, happinessItem);

        // Steuern-Button entfernt (wird jetzt direkt bei Interaktion behandelt)

        // Alle 6 Aktionen (nur aktive Bedürfnisse werden angezeigt)
        String activeNeeds = resident.getActiveNeeds();
        
        // Nur aktive Bedürfnisse anzeigen, inaktive als grau/roter Farbstoff
        inventory.setItem(SLOT_FOOD, createActionItem(player, resident, "FOOD", activeNeeds.contains("FOOD")));
        inventory.setItem(SLOT_REST, createActionItem(player, resident, "REST", activeNeeds.contains("REST")));
        inventory.setItem(SLOT_HEALTH, createActionItem(player, resident, "HEALTH", activeNeeds.contains("HEALTH")));
        inventory.setItem(SLOT_SOCIAL, createActionItem(player, resident, "SOCIAL", false)); // SOCIAL entfernt - immer inaktiv
        inventory.setItem(SLOT_ENTERTAINMENT, createActionItem(player, resident, "ENTERTAINMENT", activeNeeds.contains("ENTERTAINMENT")));
        inventory.setItem(SLOT_ADVENTURE, createActionItem(player, resident, "ADVENTURE", activeNeeds.contains("ADVENTURE")));
        
        // Coming Soon Barrier-Items (ohne Funktion)
        inventory.setItem(SLOT_COMMING_SOON_1, createComingSoonItem());

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.5f, 1.0f);
    }

    /**
     * Erstellt das Steuern-Item
     */
    private ItemStack createTaxItem(Resident resident) {
        long currentTime = System.currentTimeMillis();
        long lastTaxTime = resident.getLastTaxTime();
        long oneDayInMillis = 86400000L; // 24 Stunden
        boolean canCollectTaxes = (currentTime - lastTaxTime) >= oneDayInMillis;

        List<String> lore = new ArrayList<>();
        lore.add("");
        
        if (canCollectTaxes) {
            double taxAmount = resident.getTaxAmount();
            if (taxAmount > 0) {
                lore.add("§7Status: §aVerfügbar");
                lore.add("§7Betrag: §a" + String.format("%.0f", taxAmount) + " Coins");
                lore.add("");
                lore.add("§eKlicke, um die Steuern");
                lore.add("§eabzuholen!");
                
                return new ItemBuilder(Material.GOLD_INGOT)
                        .setDisplayName("§a§lSteuern abholen")
                        .setLore((ArrayList<String>) lore)
                        .build();
            } else {
                lore.add("§7Status: §cNicht verfügbar");
                lore.add("§7Grund: Bewohner ist sehr unglücklich");
                lore.add("");
                lore.add("§cKümmere dich um den Bewohner!");
                
                return new ItemBuilder(Material.BARRIER)
                        .setDisplayName("§c§lSteuern abholen")
                        .setLore((ArrayList<String>) lore)
                        .build();
            }
        } else {
            // Berechne verbleibende Zeit
            long timeRemaining = oneDayInMillis - (currentTime - lastTaxTime);
            long hoursRemaining = timeRemaining / (1000 * 60 * 60);
            long minutesRemaining = (timeRemaining % (1000 * 60 * 60)) / (1000 * 60);
            
            lore.add("§7Status: §cNicht verfügbar");
            lore.add("§7Verbleibende Zeit: §e" + hoursRemaining + "h " + minutesRemaining + "m");
            lore.add("");
            lore.add("§cSteuern wurden heute bereits");
            lore.add("§cabgeholt!");
            
            return new ItemBuilder(Material.GOLD_NUGGET)
                    .setDisplayName("§7§lSteuern abholen")
                    .setLore((ArrayList<String>) lore)
                    .build();
        }
    }

    /**
     * Erstellt das Glücklichkeits-Progressbar Item
     */
    private ItemStack createHappinessProgressItem(Resident resident) {
        int currentScore = resident.getHappinessScore();
        int happinessLevel = resident.getHappinessLevel();
        String happinessStatus = Resident.getHappinessStatus(happinessLevel);
        
        // Bestimme Score-Bereich der aktuellen Stufe
        int minScore, maxScore, scoreInRange, rangeSize;
        
        switch (happinessLevel) {
            case 1: // Sehr unglücklich
                minScore = 0;
                maxScore = Resident.SCORE_SEHR_UNGGLUECKLICH_MAX;
                break;
            case 2: // Unglücklich
                minScore = Resident.SCORE_SEHR_UNGGLUECKLICH_MAX + 1;
                maxScore = Resident.SCORE_UNGGLUECKLICH_MAX;
                break;
            case 3: // Neutral
                minScore = Resident.SCORE_UNGGLUECKLICH_MAX + 1;
                maxScore = Resident.SCORE_NEUTRAL_MAX;
                break;
            case 4: // Zufrieden
                minScore = Resident.SCORE_NEUTRAL_MAX + 1;
                maxScore = Resident.SCORE_ZUFRIEDEN_MAX;
                break;
            case 5: // Sehr zufrieden
                minScore = Resident.SCORE_ZUFRIEDEN_MAX + 1;
                maxScore = 1000; // Obergrenze für Anzeige (kann höher sein)
                break;
            default:
                minScore = 0;
                maxScore = 200;
        }
        
        rangeSize = maxScore - minScore;
        scoreInRange = currentScore - minScore;
        
        // Berechne Progress (0-100%)
        double progressPercent;
        int scoreToNextLevel;
        
        if (happinessLevel >= 5) {
            // Bei Level 5 (max) zeigen wir einfach den Score
            progressPercent = 100.0;
            scoreToNextLevel = 0; // Keine nächste Stufe
        } else {
            progressPercent = (double) scoreInRange / rangeSize * 100.0;
            if (progressPercent > 100.0) progressPercent = 100.0;
            scoreToNextLevel = maxScore - currentScore;
        }
        
        // Erstelle visuelle Progressbar (20 Zeichen)
        int filled = (int) (progressPercent / 100.0 * 20);
        int empty = 20 - filled;
        StringBuilder progressBarBuilder = new StringBuilder();
        progressBarBuilder.append("§a");
        for (int i = 0; i < filled; i++) {
            progressBarBuilder.append("█");
        }
        progressBarBuilder.append("§7");
        for (int i = 0; i < empty; i++) {
            progressBarBuilder.append("█");
        }
        String progressBar = progressBarBuilder.toString();
        
        // Material basierend auf Level
        Material material;
        switch (happinessLevel) {
            case 5:
                material = Material.GOLD_INGOT;
                break;
            case 4:
                material = Material.EMERALD;
                break;
            case 3:
                material = Material.COPPER_INGOT;
                break;
            case 2:
                material = Material.IRON_INGOT;
                break;
            default:
                material = Material.COAL;
        }
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Status: " + happinessStatus);
        lore.add("§7Score: §e" + currentScore + " §7/ §e" + maxScore);
        lore.add("");
        lore.add("§7Fortschritt zur nächsten Stufe:");
        lore.add(progressBar + " §7" + String.format("%.1f", progressPercent) + "%");
        lore.add("");
        
        if (happinessLevel >= 5) {
            lore.add("§a§lMaximales Level erreicht!");
        } else {
            lore.add("§7Noch §e" + scoreToNextLevel + " §7Score bis zur nächsten Stufe");
        }
        lore.add("");
        lore.add("§7Kümmere dich um den Bewohner,");
        lore.add("§7um seine Glücklichkeit zu steigern!");
        
        return new ItemBuilder(material)
                .setDisplayName("§e§lGlücklichkeit: " + happinessStatus)
                .setLore((ArrayList<String>) lore)
                .build();
    }

    /**
     * Erstellt das "Essen bringen" Item
     */
    private ItemStack createFeedItem(Player player) {
        boolean hasBread = player.getInventory().contains(Material.BREAD, 1);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Kosten: §e1x Brot");
        lore.add("§7Belohnung: §a+10 Glücklichkeit");
        lore.add("");
        
        if (hasBread) {
            lore.add("§aKlicke, um dem Bewohner");
            lore.add("§aEssen zu bringen!");
            
            return new ItemBuilder(Material.BREAD)
                    .setDisplayName("§a§lEssen bringen")
                    .setLore((ArrayList<String>) lore)
                    .build();
        } else {
            lore.add("§cDu hast kein Brot!");
            
            return new ItemBuilder(Material.BARRIER)
                    .setDisplayName("§c§lEssen bringen")
                    .setLore((ArrayList<String>) lore)
                    .build();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) return;
        
        // Finde den Resident für diesen Spieler
        Resident resident = openGUIs.get(player);
        if (resident == null) return;
        
        String displayName = clickedItem.getItemMeta().getDisplayName();
        int slot = event.getSlot();
        
        // Alle Aktionen (nur wenn aktiv)
        String activeNeeds = resident.getActiveNeeds();
        if (slot == SLOT_FOOD && displayName.contains("Nahrung")) {
            if (activeNeeds.contains("FOOD")) {
                handleAction(player, resident, "FOOD");
                return; // GUI wird in handleAction geschlossen
            }
            refreshGUI(player, resident);
            return;
        }
        if (slot == SLOT_REST && displayName.contains("Erholung")) {
            if (activeNeeds.contains("REST")) {
                handleAction(player, resident, "REST");
                return; // GUI wird in handleAction geschlossen
            }
            refreshGUI(player, resident);
            return;
        }
        if (slot == SLOT_HEALTH && displayName.contains("Gesundheit")) {
            if (activeNeeds.contains("HEALTH")) {
                handleAction(player, resident, "HEALTH");
                return; // GUI wird in handleAction geschlossen
            }
            refreshGUI(player, resident);
            return;
        }
        if (slot == SLOT_SOCIAL && displayName.contains("Sozialbedürfnis")) {
            // SOCIAL-Bedürfnis wurde entfernt - keine Aktion möglich
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            refreshGUI(player, resident);
            return;
        }
        if (slot == SLOT_ENTERTAINMENT && displayName.contains("Unterhaltung")) {
            if (activeNeeds.contains("ENTERTAINMENT")) {
                handleAction(player, resident, "ENTERTAINMENT");
                return; // GUI wird in handleAction geschlossen
            }
            refreshGUI(player, resident);
            return;
        }
        if (slot == SLOT_ADVENTURE && displayName.contains("Abenteuerlust")) {
            if (activeNeeds.contains("ADVENTURE")) {
                handleAction(player, resident, "ADVENTURE");
                return; // GUI wird in handleAction geschlossen
            }
            refreshGUI(player, resident);
            return;
        }
    }
    
    /**
     * Handhabt das Abholen der Steuern
     */
    private void handleTaxCollection(Player player, Resident resident) {
        long currentTime = System.currentTimeMillis();
        long lastTaxTime = resident.getLastTaxTime();
        long oneDayInMillis = 86400000L; // 24 Stunden
        boolean canCollectTaxes = (currentTime - lastTaxTime) >= oneDayInMillis;
        
        if (!canCollectTaxes) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBewohner §7§l| §cDie Steuern wurden heute bereits abgeholt!");
            return;
        }
        
        // Prüfe ob sehr unglücklich
        if (resident.getHappinessLevel() <= 1) {
            // Bewohner verschwindet - Entity wird in removeResident entfernt
            // Bewohner entfernen (über ResidentManager)
            HeroCraft.getPlugin().getResidentManager().removeResident(resident);
            openGUIs.remove(player);
            player.closeInventory();
            
            // Nachricht an alle Land-Mitglieder
            Land land = HeroCraft.getPlugin().getLandManager().getLandByName(resident.getLandName());
            if (land != null) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (land.canBuild(onlinePlayer)) {
                        onlinePlayer.sendMessage(Constant.PREFIX + "§cEin Bewohner ist aus §e" + land.getName() + " §causgezogen.");
                    }
                }
            }
            return;
        }
        
        double taxAmount = resident.getTaxAmount();
        if (taxAmount > 0) {
            Land land = HeroCraft.getPlugin().getLandManager().getLandByName(resident.getLandName());
            if (land != null) {
                land.setCoins(land.getCoins() + taxAmount);
                HeroCraft.getPlugin().getLandManager().saveLand(land);
                resident.setLastTaxTime(currentTime);
                
                player.sendMessage("§e§lBewohner §7§l| §7Hier sind meine Steuern für heute: §a" + String.format("%.0f", taxAmount) + " Coins");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);
                
                // Aktualisiere Resident
                HeroCraft.getPlugin().getResidentManager().updateResident(resident);
            }
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBewohner §7§l| §cIch bin zu unglücklich, um Steuern zu zahlen!");
        }
    }
    
    /**
     * Handhabt die "Essen bringen" Aktion
     */
    private void handleFeedAction(Player player, Resident resident) {
        // Prüfe ob Spieler Brot hat
        if (!player.getInventory().contains(Material.BREAD, 1)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBewohner §7§l| §cDu hast kein Brot!");
            return;
        }
        
        // Entferne 1x Brot
        HashMap<Integer, ItemStack> breadItems = (HashMap<Integer, ItemStack>) player.getInventory().all(Material.BREAD);
        for (ItemStack bread : breadItems.values()) {
            if (bread.getAmount() > 0) {
                if (bread.getAmount() == 1) {
                    player.getInventory().remove(bread);
                } else {
                    bread.setAmount(bread.getAmount() - 1);
                }
                break;
            }
        }
        
        // Erhöhe Glücklichkeit um 10
        int currentScore = resident.getHappinessScore();
        resident.setHappinessScore(currentScore + 10);
        
        player.sendMessage("§e§lBewohner §7§l| §aVielen Dank für das Essen! (+10 Glücklichkeit)");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        
        // Aktualisiere Resident
        HeroCraft.getPlugin().getResidentManager().updateResident(resident);
    }
    
    /**
     * Erstellt ein Action-Item für eine Aktion (neue Version mit active-Parameter)
     */
    private ItemStack createActionItem(Player player, Resident resident, String actionType, boolean isActive) {
        int actionCount;
        Material itemMaterial;
        String displayName;
        int baseBonus;
        
        switch (actionType) {
            case "FOOD":
                actionCount = resident.getActionFoodCount();
                itemMaterial = Material.BREAD;
                displayName = "§a§lNahrung bringen";
                baseBonus = 20;
                break;
            case "REST":
                actionCount = resident.getActionRestCount();
                itemMaterial = Material.RED_BED;
                displayName = "§b§lErholung ermöglichen";
                baseBonus = 18;
                break;
            case "HEALTH":
                actionCount = resident.getActionHealthCount();
                itemMaterial = Material.POTION;
                displayName = "§c§lGesundheit ermöglichen";
                baseBonus = 25;
                break;
            case "SOCIAL":
                actionCount = resident.getActionSocialCount();
                itemMaterial = Material.EMERALD;
                displayName = "§d§lSozialbedürfnis befriedigen";
                baseBonus = 15;
                break;
            case "ENTERTAINMENT":
                actionCount = resident.getActionEntertainmentCount();
                itemMaterial = Material.MUSIC_DISC_CAT;
                displayName = "§e§lUnterhaltung bringen";
                baseBonus = 12;
                break;
            case "ADVENTURE":
                actionCount = resident.getActionAdventureCount();
                itemMaterial = Material.COMPASS;
                displayName = "§6§lAbenteuerlust befriedigen";
                baseBonus = 22;
                break;
            default:
                actionCount = 0;
                itemMaterial = Material.BARRIER;
                displayName = "§c§lUnbekannt";
                baseBonus = 0;
        }
        
        // Wenn nicht aktiv, zeige als inaktiv an (roter Farbstoff)
        if (!isActive) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Dieses Bedürfnis ist derzeit");
            lore.add("§7nicht aktiv.");
            lore.add("");
            lore.add("§7Aktiv: §cNein");
            
            return new ItemBuilder(Material.RED_DYE)
                    .setDisplayName("§7§l" + displayName.replace("§a§l", "").replace("§b§l", "").replace("§c§l", "").replace("§d§l", "").replace("§e§l", "").replace("§6§l", ""))
                    .setLore((ArrayList<String>) lore)
                    .build();
        }
        
        // Vereinfachte Bedingungsprüfung (für jetzt - später können wir komplexe Bedingungen hinzufügen)
        boolean canPerform = checkActionConditions(player, resident, actionType);
        
        // Wenn aktiv aber nicht erfüllbar, zeige als grauer Farbstoff
        if (!canPerform) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Dieses Bedürfnis ist aktiv");
            lore.add("§7und muss erfüllt werden!");
            lore.add("");
            lore.add("§7Status: §aAktiv");
            
            return new ItemBuilder(Material.GRAY_DYE)
                    .setDisplayName("§7§l" + displayName.replace("§a§l", "").replace("§b§l", "").replace("§c§l", "").replace("§d§l", "").replace("§e§l", "").replace("§6§l", ""))
                    .setLore((ArrayList<String>) lore)
                    .build();
        }
        
        int currentBonus = resident.calculateActionBonus(baseBonus, actionCount);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Status: §aAktiv");
        lore.add("§7Aktueller Bonus: §a+" + currentBonus + " Glücklichkeit");
        lore.add("§7Basis-Bonus: §e+" + baseBonus + " Glücklichkeit");
        lore.add("§7Gesamt verwendet: §e" + actionCount + "x");
        lore.add("");
        
        if (!canPerform) {
            lore.add("§cBedingungen nicht erfüllt!");
            // Spezifische Fehlermeldung basierend auf Aktion
            switch (actionType) {
                case "FOOD":
                    lore.add("§7Du brauchst Essen im Inventar");
                    break;
                case "HEALTH":
                    lore.add("§7Du brauchst Heilungstränke");
                    break;
                case "REST":
                    lore.add("§7Du brauchst 1 Bett + 2 Kerzen");
                    lore.add("§7im Umkreis des Bewohners");
                    break;
                case "ENTERTAINMENT":
                    lore.add("§7Eine Jukebox muss in der Nähe");
                    lore.add("§7Musik abspielen");
                    break;
                case "ADVENTURE":
                    lore.add("§7Erlaubte Items:");
                    lore.add("§7- Eisen Spitzhacke");
                    lore.add("§7- Eisen Axt");
                    lore.add("§7- Eisen Schaufel");
                    lore.add("§7- Eisen Hacke");
                    lore.add("§7- Eisen Schwert");
                    lore.add("§7- Eisen Helm");
                    lore.add("§7- Eisen Brustharnisch");
                    lore.add("§7- Eisen Beinschienen");
                    lore.add("§7- Eisen Stiefel");
                    lore.add("§7(Der Bewohner muss diese bereits kennen)");
                    break;
                default:
                    lore.add("§7Überprüfe die Anforderungen");
            }
            
            return new ItemBuilder(Material.RED_DYE)
                    .setDisplayName(displayName)
                    .setLore((ArrayList<String>) lore)
                    .build();
        }
        
        if (actionCount > 0) {
            int nextBonus = resident.calculateActionBonus(baseBonus, actionCount + 1);
            lore.add("§7Nächster Bonus: §a+" + nextBonus + " Glücklichkeit");
            lore.add("");
        }
        lore.add("§aKlicke, um diese Aktion");
        lore.add("§aauszuführen!");
        
        return new ItemBuilder(itemMaterial)
                .setDisplayName(displayName)
                .setLore((ArrayList<String>) lore)
                .build();
    }
    
    /**
     * Prüft ob die Bedingungen für eine Aktion erfüllt sind
     */
    private boolean checkActionConditions(Player player, Resident resident, String actionType) {
        Location residentLocation = getResidentLocation(resident);
        if (residentLocation == null) return false;
        
        switch (actionType) {
            case "FOOD":
                // Prüfe ob Spieler irgendein Essen hat
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType().isEdible()) {
                        return true;
                    }
                }
                return false;
            case "HEALTH":
                // Prüfe auf Healing Potions (nur Heiltränke)
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.POTION) {
                        // Vereinfacht: Alle Potions akzeptiert (könnte später präziser sein)
                        return true;
                    }
                }
                return false;
            case "REST":
                return checkRestConditions(residentLocation);
            case "SOCIAL":
                return checkSocialConditions(resident);
            case "ENTERTAINMENT":
                return checkEntertainmentConditions(residentLocation);
            case "ADVENTURE":
                return checkAdventureConditions(player, resident);
            default:
                return false;
        }
    }
    
    /**
     * Gibt die Location des Bewohners zurück
     */
    private Location getResidentLocation(Resident resident) {
        World world = Bukkit.getWorld(resident.getWorld());
        if (world == null) return null;
        return new Location(world, resident.getX(), resident.getY(), resident.getZ());
    }
    
    /**
     * Prüft Bedingungen für Erholung: Bett vorhanden, 2 Kerzen im Umkreis
     */
    private boolean checkRestConditions(Location location) {
        // Prüfe Umkreis (Radius 200 Blöcke) nach Bett und Kerzen
        int radius = 200;
        int bedCount = 0;
        int candleCount = 0;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block checkBlock = location.clone().add(x, y, z).getBlock();
                    Material type = checkBlock.getType();
                    
                    // Prüfe auf Bett (alle Bett-Varianten)
                    if (type.toString().contains("BED") && !type.toString().equals("BEDROCK")) {
                        bedCount++;
                    }
                    
                    // Prüfe auf Kerzen (alle Kerzen-Varianten)
                    if (type.toString().contains("CANDLE")) {
                        candleCount++;
                    }
                }
            }
        }
        
        return bedCount >= 1 && candleCount >= 2;
    }
    
    /**
     * Prüft Bedingungen für Sozialbedürfnis: Ein Villager muss im Umkreis sein
     */
    private boolean checkSocialConditions(Resident resident) {
        Location residentLocation = getResidentLocation(resident);
        if (residentLocation == null) return false;
        
        World world = residentLocation.getWorld();
        if (world == null) return false;
        
        double radius = 10.0; // Radius in Blöcken
        
        // Suche nach Villagern im Umkreis (einschließlich Husk für Beamte)
        for (Entity entity : world.getNearbyEntities(residentLocation, radius, radius, radius)) {
            if (entity instanceof Villager || entity instanceof Husk) {
                // Prüfe ob es nicht der Bewohner selbst ist
                if (entity instanceof Villager) {
                    Villager villager = (Villager) entity;
                    // Prüfe ob es der Bewohner selbst ist (gleiche Location)
                    Location entityLoc = villager.getLocation();
                    if (Math.abs(entityLoc.getX() - resident.getX()) < 0.1 &&
                        Math.abs(entityLoc.getY() - resident.getY()) < 0.1 &&
                        Math.abs(entityLoc.getZ() - resident.getZ()) < 0.1) {
                        continue; // Das ist der Bewohner selbst, überspringe
                    }
                }
                // Ein anderer Villager/Husk wurde gefunden
                return true;
            }
        }
        
        return false; // Kein Villager in der Nähe gefunden
    }
    
    /**
     * Prüft Bedingungen für Unterhaltung: Jukebox muss in der Nähe laufen
     */
    private boolean checkEntertainmentConditions(Location location) {
        int radius = 200;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block checkBlock = location.clone().add(x, y, z).getBlock();
                    if (checkBlock.getType() == Material.JUKEBOX) {
                        // Prüfe ob Jukebox läuft (hat eine Music Disc)
                        Jukebox jukebox = (Jukebox) checkBlock.getState();
                        if (jukebox.getPlaying() != null) {
                            return true; // Jukebox spielt Musik
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Prüft Bedingungen für Abenteuerlust: Spieler muss ein Eisenwerkzeug oder Rüstung haben, das der Bewohner bereits gesehen hat
     */
    private boolean checkAdventureConditions(Player player, Resident resident) {
        // Erlaubte Items: Eisen-Tools und Rüstungen
        Set<Material> allowedItems = new HashSet<>(Arrays.asList(
            Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, 
            Material.IRON_HOE, Material.IRON_SWORD,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, 
            Material.IRON_BOOTS
        ));
        
        // Prüfe Inventar nach Eisen-Tools/Rüstungen, die der Bewohner bereits gesehen hat
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                Material material = item.getType();
                String materialName = material.toString();
                
                // Prüfe ob Item erlaubt ist (Eisen-Tool oder Rüstung)
                if (!allowedItems.contains(material)) {
                    continue;
                }
                
                // Prüfe ob Bewohner dieses Item bereits gesehen hat
                if (resident.hasSeenItem(materialName)) {
                    return true; // Item gefunden, das bereits gesehen wurde
                }
            }
        }
        
        return false;
    }
    
    /**
     * Bestimmt den Bonus für Nahrung basierend auf der Qualität
     */
    private int getFoodBonus(Material foodType) {
        // Basis-Bonus für alle Nahrung
        int baseBonus = 15;
        
        // Bonus-Anpassungen basierend auf Nahrungsqualität
        if (foodType == Material.GOLDEN_APPLE) {
            return baseBonus + 15; // Sehr gute Nahrung
        } else if (foodType == Material.COOKED_BEEF || foodType == Material.COOKED_PORKCHOP || 
                   foodType == Material.COOKED_MUTTON || foodType == Material.COOKED_CHICKEN ||
                   foodType == Material.COOKED_RABBIT || foodType == Material.COOKED_SALMON ||
                   foodType == Material.COOKED_COD) {
            return baseBonus + 10; // Gute Nahrung (gekochtes Fleisch/Fisch)
        } else if (foodType == Material.BREAD || foodType == Material.BAKED_POTATO ||
                   foodType == Material.CARROT || foodType == Material.BEETROOT ||
                   foodType == Material.APPLE || foodType == Material.MELON_SLICE) {
            return baseBonus + 5; // Normale Nahrung
        } else if (foodType.isEdible()) {
            return baseBonus; // Andere essbare Items
        }
        
        return baseBonus;
    }
    
    /**
     * Zerstört Bett und Kerzen im Umkreis (für Erholung)
     */
    private void destroyRestBlocks(Location location) {
        int radius = 200;
        int bedsDestroyed = 0;
        int candlesDestroyed = 0;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    Material type = block.getType();
                    
                    // Zerstöre Bett (nur 1)
                    if (bedsDestroyed < 1 && type.toString().contains("BED") && !type.toString().equals("BEDROCK")) {
                        block.setType(Material.AIR);
                        bedsDestroyed++;
                    }
                    
                    // Zerstöre Kerzen (nur 2)
                    if (candlesDestroyed < 2 && type.toString().contains("CANDLE")) {
                        block.setType(Material.AIR);
                        candlesDestroyed++;
                    }
                }
            }
        }
    }
    
    /**
     * Erstellt ein "Coming Soon" Barrier-Item
     */
    private ItemStack createComingSoonItem() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Comming Soon");
        lore.add("");
        
        return new ItemBuilder(Material.BARRIER)
                .setDisplayName("§8§lComming Soon")
                .setLore((ArrayList<String>) lore)
                .build();
    }

    /**
     * Handhabt eine Aktion (neue vereinfachte Version)
     */
    private void handleAction(Player player, Resident resident, String actionType) {
        // Prüfe ob Bedingungen erfüllt sind
        if (!checkActionConditions(player, resident, actionType)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBewohner §7§l| §cDie Bedingungen für diese Aktion sind nicht erfüllt!");
            return;
        }
        
        int baseBonus = 0;
        
        // Entferne Item/Prüfe Bedingungen und bestimme Basis-Bonus
        Location residentLocation = getResidentLocation(resident);
        switch (actionType) {
            case "FOOD":
                // Nahrung: Entferne erstes essbares Item, bestimme Bonus basierend auf Qualität
                ItemStack foodItem = null;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType().isEdible()) {
                        foodItem = item;
                        baseBonus = getFoodBonus(item.getType());
                        if (item.getAmount() == 1) {
                            player.getInventory().remove(item);
                        } else {
                            item.setAmount(item.getAmount() - 1);
                        }
                        break;
                    }
                }
                break;
            case "HEALTH":
                baseBonus = 25;
                // Gold Apfel oder Potion werden NICHT entfernt (nur für Prüfung benötigt)
                break;
            case "REST":
                baseBonus = 18;
                // Bett und Kerzen werden NICHT zerstört (nur für Prüfung benötigt)
                break;
            case "SOCIAL":
                baseBonus = 15;
                // Entferne Tracking-Eintrag nach erfolgreicher Aktion
                // (Wird später im Resident gespeichert)
                break;
            case "ENTERTAINMENT":
                baseBonus = 12;
                // Jukebox wird nicht mehr zerstört
                break;
            case "ADVENTURE":
                baseBonus = 22;
                // Item wird NICHT entfernt, aber als gesehen markiert
                if (residentLocation != null) {
                    // Erlaubte Items: Eisen-Tools und Rüstungen
                    Set<Material> allowedItems = new HashSet<>(Arrays.asList(
                        Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, 
                        Material.IRON_HOE, Material.IRON_SWORD,
                        Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, 
                        Material.IRON_BOOTS
                    ));
                    
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.getType() != Material.AIR) {
                            Material material = item.getType();
                            String materialName = material.toString();
                            
                            // Prüfe ob Item erlaubt ist
                            if (!allowedItems.contains(material)) continue;
                            // Prüfe ob bereits gesehen
                            if (resident.hasSeenItem(materialName)) {
                                // Item gefunden, das bereits gesehen wurde - als gesehen markieren, NICHT entfernen
                                resident.addSeenItem(materialName);
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                baseBonus = 0;
        }
        
        // Erhöhe Action-Count (Gesamt-Count)
        int actionCount = 0;
        switch (actionType) {
            case "FOOD":
                actionCount = resident.getActionFoodCount();
                resident.setActionFoodCount(actionCount + 1);
                break;
            case "REST":
                actionCount = resident.getActionRestCount();
                resident.setActionRestCount(actionCount + 1);
                break;
            case "HEALTH":
                actionCount = resident.getActionHealthCount();
                resident.setActionHealthCount(actionCount + 1);
                break;
            case "SOCIAL":
                actionCount = resident.getActionSocialCount();
                resident.setActionSocialCount(actionCount + 1);
                break;
            case "ENTERTAINMENT":
                actionCount = resident.getActionEntertainmentCount();
                resident.setActionEntertainmentCount(actionCount + 1);
                break;
            case "ADVENTURE":
                actionCount = resident.getActionAdventureCount();
                resident.setActionAdventureCount(actionCount + 1);
                break;
        }
        
        // Berechne Bonus (mit dem neuen Count)
        int bonus = resident.calculateActionBonus(baseBonus, actionCount + 1);
        
        // Erhöhe Glücklichkeit
        int currentScore = resident.getHappinessScore();
        resident.setHappinessScore(currentScore + bonus);
        
        // Verschiedene Nachrichten je nach Aktion
        String message;
        switch (actionType) {
            case "FOOD":
                message = "§aVielen Dank für das Essen! (+" + bonus + " Glücklichkeit)";
                break;
            case "REST":
                message = "§bAh, endlich etwas Erholung! (+" + bonus + " Glücklichkeit)";
                break;
            case "HEALTH":
                message = "§cDanke für die Gesundheit! (+" + bonus + " Glücklichkeit)";
                break;
            case "SOCIAL":
                message = "§dDas Geschenk freut mich sehr! (+" + bonus + " Glücklichkeit)";
                break;
            case "ENTERTAINMENT":
                message = "§eDie Musik gefällt mir! (+" + bonus + " Glücklichkeit)";
                break;
            case "ADVENTURE":
                message = "§6Endlich etwas Abenteuer! (+" + bonus + " Glücklichkeit)";
                break;
            default:
                message = "§aAktion ausgeführt! (+" + bonus + " Glücklichkeit)";
        }
        
        player.sendMessage("§e§lBewohner §7§l| " + message);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        
        // Aktualisiere Resident und generiere neue aktive Bedürfnisse
        // (Tracking wird für SOCIAL nicht mehr benötigt, da es jetzt nur auf Villager-Präsenz basiert)
        
        resident.generateNewActiveNeeds();
        
        // Status ändern (nur alle 10-15 Mal, wenn Status "NEED" ist)
        if (resident.getStatus().equals("NEED") && resident.incrementStatusInteractionCount()) {
            HeroCraft.getPlugin().getResidentManager().generateNewStatus(resident);
            resident.resetStatusInteractionCount();
        }
        
        HeroCraft.getPlugin().getResidentManager().updateResident(resident);
        // Name sofort aktualisieren nach Status-Änderung, damit der neue Status sofort sichtbar ist
        HeroCraft.getPlugin().getResidentManager().updateVillagerNameImmediate(resident);
        
        // GUI schließen nach erfolgreicher Aktion
        player.closeInventory();
    }
    
    /**
     * Alte handleAction-Methode (wird nicht mehr verwendet, kann später entfernt werden)
     */
    @Deprecated
    private void handleActionOld(Player player, Resident resident, String actionType, Material requiredMaterial, String materialName, int baseBonus) {
        // Prüfe ob Spieler das Item hat
        if (!player.getInventory().contains(requiredMaterial, 1)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            player.sendMessage("§e§lBewohner §7§l| §cDu hast kein " + materialName + "!");
            return;
        }
        
        // Entferne 1x Item
        HashMap<Integer, ItemStack> items = (HashMap<Integer, ItemStack>) player.getInventory().all(requiredMaterial);
        for (ItemStack item : items.values()) {
            if (item.getAmount() > 0) {
                if (item.getAmount() == 1) {
                    player.getInventory().remove(item);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
                break;
            }
        }
        
        // Erhöhe Action-Count (Gesamt-Count, kein Reset)
        int actionCount = 0;
        switch (actionType) {
            case "FOOD":
                actionCount = resident.getActionFoodCount();
                resident.setActionFoodCount(actionCount + 1);
                break;
            case "REST":
                actionCount = resident.getActionRestCount();
                resident.setActionRestCount(actionCount + 1);
                break;
            case "HEALTH":
                actionCount = resident.getActionHealthCount();
                resident.setActionHealthCount(actionCount + 1);
                break;
            case "SOCIAL":
                actionCount = resident.getActionSocialCount();
                resident.setActionSocialCount(actionCount + 1);
                break;
            case "ENTERTAINMENT":
                actionCount = resident.getActionEntertainmentCount();
                resident.setActionEntertainmentCount(actionCount + 1);
                break;
            case "ADVENTURE":
                actionCount = resident.getActionAdventureCount();
                resident.setActionAdventureCount(actionCount + 1);
                break;
        }
        
        // Berechne Bonus (mit dem neuen Count, daher +1)
        int bonus = resident.calculateActionBonus(baseBonus, actionCount + 1);
        
        // Erhöhe Glücklichkeit
        int currentScore = resident.getHappinessScore();
        resident.setHappinessScore(currentScore + bonus);
        
        // Verschiedene Nachrichten je nach Aktion
        String message;
        switch (actionType) {
            case "FOOD":
                message = "§aVielen Dank für das Essen! (+" + bonus + " Glücklichkeit)";
                break;
            case "REST":
                message = "§bAh, endlich etwas Erholung! (+" + bonus + " Glücklichkeit)";
                break;
            case "HEALTH":
                message = "§cDanke für die Gesundheit! (+" + bonus + " Glücklichkeit)";
                break;
            case "SOCIAL":
                message = "§dDas Geschenk freut mich sehr! (+" + bonus + " Glücklichkeit)";
                break;
            case "ENTERTAINMENT":
                message = "§eDie Musik gefällt mir! (+" + bonus + " Glücklichkeit)";
                break;
            case "ADVENTURE":
                message = "§6Endlich etwas Abenteuer! (+" + bonus + " Glücklichkeit)";
                break;
            default:
                message = "§aAktion ausgeführt! (+" + bonus + " Glücklichkeit)";
        }
        
        player.sendMessage("§e§lBewohner §7§l| " + message);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        
        // Aktualisiere Resident
        HeroCraft.getPlugin().getResidentManager().updateResident(resident);
    }
    
    /**
     * Aktualisiert das GUI für den Spieler
     */
    private void refreshGUI(Player player, Resident resident) {
        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(resident.getLandName());
        if (land != null) {
            openResidentGUI(player, resident, land);
        }
    }

    /**
     * Entfernt den Spieler aus der HashMap, wenn das GUI geschlossen wird
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        
        Player player = (Player) event.getPlayer();
        openGUIs.remove(player);
    }
}

