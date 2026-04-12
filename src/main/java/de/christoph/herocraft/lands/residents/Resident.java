package de.christoph.herocraft.lands.residents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Resident {
    private String landName;
    private double x;
    private double y;
    private double z;
    private String world;
    private int happinessScore; // Interner Score (0-1000+), wird zu Happiness Level gemappt
    private long lastTaxTime; // Timestamp der letzten Steuer-Zahlung
    private String currentDemand; // Aktuelle Forderung (null wenn keine)
    private boolean demandCompleted; // Ob die Forderung erfüllt wurde
    private long lastInteractionTime; // Letzte Interaktion (für Glücklichkeits-Berechnung)
    
    // Action-Counts für abnehmende Boni (GESAMT, nicht täglich reset)
    private int actionFoodCount; // Gesamt-Anzahl "Nahrung bringen" Aktionen
    private int actionRestCount; // Gesamt-Anzahl "Erholung" Aktionen
    private int actionHealthCount; // Gesamt-Anzahl "Gesundheit" Aktionen
    private int actionSocialCount; // Gesamt-Anzahl "Sozialbedürfnis" Aktionen
    private int actionEntertainmentCount; // Gesamt-Anzahl "Unterhaltung" Aktionen
    private int actionAdventureCount; // Gesamt-Anzahl "Abenteuerlust" Aktionen
    
    // Aktive Bedürfnisse (2 von 6)
    private String activeNeeds; // Komma-getrennt, z.B. "FOOD,REST"
    
    // Für Abenteuerlust: Gesehene Items (Material-Namen als String)
    private String seenItems; // Komma-getrennte Material-Namen
    
    // Für Sozialbedürfnis: Zeit-Tracking pro Spieler (UUID:Timestamp)
    private String socialTimeTracking; // Format: "uuid1:timestamp1,uuid2:timestamp2"
    
    // Status-System
    private String status; // "NEED", "TAX", "ROBBED", "ACCIDENT"
    private String statusData; // Zusätzliche Daten für Status (z.B. Mob-Typ bei ROBBED, Anzahl Goldäpfel bei ACCIDENT)
    private int statusCount; // Wie oft dieser Status bereits erfüllt wurde (für steigende Kosten)
    private int statusInteractionCount; // Zählt wie oft der aktuelle Status erfüllt wurde (für Status-Wechsel)
    private int statusChangeTarget; // Zufälliger Zielwert zwischen 10-15 für Status-Wechsel
    
    // Score-Bereiche für Happiness Level
    public static final int SCORE_SEHR_UNGGLUECKLICH_MAX = 200;  // 0-200 = Sehr unglücklich
    public static final int SCORE_UNGGLUECKLICH_MAX = 400;        // 201-400 = Unglücklich
    public static final int SCORE_NEUTRAL_MAX = 600;              // 401-600 = Neutral
    public static final int SCORE_ZUFRIEDEN_MAX = 800;            // 601-800 = Zufrieden
    public static final int SCORE_SEHR_ZUFRIEDEN_MIN = 801;       // 801+ = Sehr zufrieden
    public static final int SCORE_START = 600;                    // Start bei Neutral (500)
    public static final int SCORE_REWARD_FULFILLED = 1000;        // Score bei Forderungserfüllung

    public Resident(String landName, double x, double y, double z, String world) {
        this.landName = landName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.happinessScore = SCORE_START; // Start bei Neutral-Score
        this.lastTaxTime = 0;
        this.currentDemand = null;
        this.demandCompleted = false;
        this.lastInteractionTime = System.currentTimeMillis();
        this.actionFoodCount = 0;
        this.actionRestCount = 0;
        this.actionHealthCount = 0;
        this.actionSocialCount = 0;
        this.actionEntertainmentCount = 0;
        this.actionAdventureCount = 0;
        this.activeNeeds = generateInitialActiveNeeds();
        this.seenItems = "";
        this.socialTimeTracking = "";
        this.status = "NEED"; // Standard-Status: Bedürfnis
        this.statusData = "";
        this.statusCount = 0;
        this.statusInteractionCount = 0;
        this.statusChangeTarget = generateRandomStatusChangeTarget();
    }
    
    /**
     * Generiert initial 2 zufällige aktive Bedürfnisse
     */
    private static String generateInitialActiveNeeds() {
        String[] allNeeds = {"FOOD", "REST", "HEALTH", "ENTERTAINMENT", "ADVENTURE"};
        java.util.Random random = new java.util.Random();
        int first = random.nextInt(allNeeds.length);
        int second;
        do {
            second = random.nextInt(allNeeds.length);
        } while (second == first);
        return allNeeds[first] + "," + allNeeds[second];
    }

    // Konstruktor für DB-Loading (ohne neue Felder - für Rückwärtskompatibilität)
    public Resident(String landName, double x, double y, double z, String world, 
                    int happinessScore, long lastTaxTime, String currentDemand, boolean demandCompleted, long lastInteractionTime) {
        this(landName, x, y, z, world, happinessScore, lastTaxTime, currentDemand, demandCompleted, 
             lastInteractionTime, 0, 0, 0, 0, 0, 0, null, "", "", "NEED", "", 0);
    }
    
    // Konstruktor für DB-Loading (mit Action-Counts)
    public Resident(String landName, double x, double y, double z, String world, 
                    int happinessScore, long lastTaxTime, String currentDemand, boolean demandCompleted, long lastInteractionTime,
                    int actionFoodCount, int actionRestCount, int actionHealthCount, 
                    int actionSocialCount, int actionEntertainmentCount, int actionAdventureCount,
                    String activeNeeds, String seenItems, String socialTimeTracking) {
        this(landName, x, y, z, world, happinessScore, lastTaxTime, currentDemand, demandCompleted,
             lastInteractionTime, actionFoodCount, actionRestCount, actionHealthCount, actionSocialCount, 
             actionEntertainmentCount, actionAdventureCount, activeNeeds, seenItems, socialTimeTracking,
             "NEED", "", 0);
    }
    
    // Konstruktor für DB-Loading (mit Status-System)
    public Resident(String landName, double x, double y, double z, String world, 
                    int happinessScore, long lastTaxTime, String currentDemand, boolean demandCompleted, long lastInteractionTime,
                    int actionFoodCount, int actionRestCount, int actionHealthCount, 
                    int actionSocialCount, int actionEntertainmentCount, int actionAdventureCount,
                    String activeNeeds, String seenItems, String socialTimeTracking,
                    String status, String statusData, int statusCount) {
        this.landName = landName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.happinessScore = happinessScore;
        this.lastTaxTime = lastTaxTime;
        this.currentDemand = currentDemand;
        this.demandCompleted = demandCompleted;
        this.lastInteractionTime = lastInteractionTime;
        this.actionFoodCount = actionFoodCount;
        this.actionRestCount = actionRestCount;
        this.actionHealthCount = actionHealthCount;
        this.actionSocialCount = actionSocialCount;
        this.actionEntertainmentCount = actionEntertainmentCount;
        this.actionAdventureCount = actionAdventureCount;
        this.activeNeeds = (activeNeeds == null || activeNeeds.isEmpty()) ? generateInitialActiveNeeds() : activeNeeds;
        this.seenItems = (seenItems == null) ? "" : seenItems;
        this.socialTimeTracking = (socialTimeTracking == null) ? "" : socialTimeTracking;
        this.status = (status == null || status.isEmpty()) ? "NEED" : status;
        this.statusData = (statusData == null) ? "" : statusData;
        this.statusCount = statusCount;
        this.statusInteractionCount = 0; // Wird nicht aus DB geladen, immer bei 0 starten
        this.statusChangeTarget = generateRandomStatusChangeTarget(); // Neuer zufälliger Zielwert
    }

    /**
     * Erstellt einen eindeutigen Location-Key für diesen Bewohner
     */
    public String getLocationKey() {
        // Verwende gerundete Werte für bessere Stabilität (auf 0.1 genau)
        return world + ":" + Math.round(x * 10) / 10.0 + ":" + Math.round(y * 10) / 10.0 + ":" + Math.round(z * 10) / 10.0;
    }
    
    public static String createLocationKey(String world, double x, double y, double z) {
        return world + ":" + Math.round(x * 10) / 10.0 + ":" + Math.round(y * 10) / 10.0 + ":" + Math.round(z * 10) / 10.0;
    }

    public String getLandName() {
        return landName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public void setLocation(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public int getHappinessScore() {
        return happinessScore;
    }

    public void setHappinessScore(int happinessScore) {
        // Min 0, kein Max (kann über 1000 gehen)
        if (happinessScore < 0) happinessScore = 0;
        this.happinessScore = happinessScore;
    }

    /**
     * Konvertiert den Score zu einem Happiness Level (1-5)
     */
    public int getHappinessLevel() {
        if (happinessScore <= SCORE_SEHR_UNGGLUECKLICH_MAX) {
            return 1; // Sehr unglücklich
        } else if (happinessScore <= SCORE_UNGGLUECKLICH_MAX) {
            return 2; // Unglücklich
        } else if (happinessScore <= SCORE_NEUTRAL_MAX) {
            return 3; // Neutral
        } else if (happinessScore <= SCORE_ZUFRIEDEN_MAX) {
            return 4; // Zufrieden
        } else {
            return 5; // Sehr zufrieden
        }
    }

    public long getLastTaxTime() {
        return lastTaxTime;
    }

    public void setLastTaxTime(long lastTaxTime) {
        this.lastTaxTime = lastTaxTime;
    }

    public String getCurrentDemand() {
        return currentDemand;
    }

    public void setCurrentDemand(String currentDemand) {
        this.currentDemand = currentDemand;
        this.demandCompleted = false;
    }

    public boolean isDemandCompleted() {
        return demandCompleted;
    }

    public void setDemandCompleted(boolean demandCompleted) {
        this.demandCompleted = demandCompleted;
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }

    public void setLastInteractionTime(long lastInteractionTime) {
        this.lastInteractionTime = lastInteractionTime;
    }

    public String getHappinessStatus() {
        return getHappinessStatus(getHappinessLevel());
    }

    public static String getHappinessStatus(int level) {
        switch (level) {
            case 5:
                return "§aSehr zufrieden";
            case 4:
                return "§aZufrieden";
            case 3:
                return "§eNeutral";
            case 2:
                return "§cUnglücklich";
            case 1:
                return "§4Sehr unglücklich";
            default:
                return "§eNeutral";
        }
    }

    /**
     * Gibt den Namen-String für den Villager zurück (mit Score, Happiness-Status und aktueller Status)
     */
    public String getVillagerName() {
        String statusDisplay = getStatusDisplayName();
        return "§e§l" + landName + " §7[" + getHappinessStatus() + "§7] §8(§7" + happinessScore + "§8) §r§f§l[" + statusDisplay + "§f§l]";
    }
    
    /**
     * Gibt den deutschen Anzeigenamen für den aktuellen Status zurück
     */
    private String getStatusDisplayName() {
        if (status == null) return "§aBedürfnis";
        
        switch (status) {
            case "TAX":
                return "§eSteuerabgaben";
            case "ROBBED":
                return "§cÜberfallen";
            case "ACCIDENT":
                return "§4Unfall";
            case "NEED":
            default:
                return "§aBedürfnis";
        }
    }

    public double getTaxAmount() {
        switch (getHappinessLevel()) {
            case 5:
                return 5000.0; // Sehr zufrieden
            case 4:
                return 4000.0; // Zufrieden
            case 3:
                return 3000.0; // Neutral
            case 2:
                return 2000.0; // Unglücklich
            case 1:
                return 1000.0; // Sehr unglücklich
            default:
                return 3000.0;
        }
    }
    
    // Action-Count Getter/Setter (GESAMT, nicht täglich)
    public int getActionFoodCount() { return actionFoodCount; }
    public void setActionFoodCount(int actionFoodCount) { this.actionFoodCount = actionFoodCount; }
    
    public int getActionRestCount() { return actionRestCount; }
    public void setActionRestCount(int actionRestCount) { this.actionRestCount = actionRestCount; }
    
    public int getActionHealthCount() { return actionHealthCount; }
    public void setActionHealthCount(int actionHealthCount) { this.actionHealthCount = actionHealthCount; }
    
    public int getActionSocialCount() { return actionSocialCount; }
    public void setActionSocialCount(int actionSocialCount) { this.actionSocialCount = actionSocialCount; }
    
    public int getActionEntertainmentCount() { return actionEntertainmentCount; }
    public void setActionEntertainmentCount(int actionEntertainmentCount) { this.actionEntertainmentCount = actionEntertainmentCount; }
    
    public int getActionAdventureCount() { return actionAdventureCount; }
    public void setActionAdventureCount(int actionAdventureCount) { this.actionAdventureCount = actionAdventureCount; }
    
    // Aktive Bedürfnisse
    public String getActiveNeeds() { return activeNeeds; }
    public void setActiveNeeds(String activeNeeds) { this.activeNeeds = activeNeeds; }
    
    /**
     * Prüft ob ein Bedürfnis aktiv ist
     */
    public boolean isNeedActive(String need) {
        if (activeNeeds == null || activeNeeds.isEmpty()) return false;
        return activeNeeds.contains(need);
    }
    
    /**
     * Setzt neue aktive Bedürfnisse (2 zufällige)
     */
    public void generateNewActiveNeeds() {
        String[] allNeeds = {"FOOD", "REST", "HEALTH", "ENTERTAINMENT", "ADVENTURE"};
        java.util.Random random = new java.util.Random();
        int first = random.nextInt(allNeeds.length);
        int second;
        do {
            second = random.nextInt(allNeeds.length);
        } while (second == first);
        this.activeNeeds = allNeeds[first] + "," + allNeeds[second];
    }
    
    // Gesehene Items (für Abenteuerlust)
    public String getSeenItems() { return seenItems; }
    public void setSeenItems(String seenItems) { this.seenItems = seenItems; }
    
    /**
     * Prüft ob ein Item bereits gesehen wurde
     */
    public boolean hasSeenItem(String materialName) {
        if (seenItems == null || seenItems.isEmpty()) return false;
        String[] items = seenItems.split(",");
        for (String item : items) {
            if (item.equals(materialName)) return true;
        }
        return false;
    }
    
    /**
     * Fügt ein Item zu den gesehenen Items hinzu
     */
    public void addSeenItem(String materialName) {
        if (hasSeenItem(materialName)) return;
        if (seenItems == null || seenItems.isEmpty()) {
            seenItems = materialName;
        } else {
            seenItems += "," + materialName;
        }
    }
    
    // Sozialbedürfnis Zeit-Tracking
    public String getSocialTimeTracking() { return socialTimeTracking; }
    public void setSocialTimeTracking(String socialTimeTracking) { this.socialTimeTracking = socialTimeTracking; }
    
    /**
     * Startet das Zeit-Tracking für einen Spieler
     */
    public void startSocialTracking(UUID playerUUID) {
        String uuidStr = playerUUID.toString();
        if (socialTimeTracking == null) socialTimeTracking = "";
        
        // Prüfe ob bereits vorhanden
        if (socialTimeTracking.contains(uuidStr + ":")) {
            // Entferne alten Eintrag
            String[] entries = socialTimeTracking.split(",");
            StringBuilder newTracking = new StringBuilder();
            for (String entry : entries) {
                if (!entry.startsWith(uuidStr + ":")) {
                    if (newTracking.length() > 0) newTracking.append(",");
                    newTracking.append(entry);
                }
            }
            socialTimeTracking = newTracking.toString();
        }
        
        // Füge neuen Eintrag hinzu
        long currentTime = System.currentTimeMillis();
        if (socialTimeTracking.isEmpty()) {
            socialTimeTracking = uuidStr + ":" + currentTime;
        } else {
            socialTimeTracking += "," + uuidStr + ":" + currentTime;
        }
    }
    
    /**
     * Gibt die Zeit zurück, die ein Spieler im Umkreis verbracht hat (in Millisekunden)
     */
    public long getSocialTimeForPlayer(UUID playerUUID) {
        if (socialTimeTracking == null || socialTimeTracking.isEmpty()) return 0;
        String uuidStr = playerUUID.toString();
        String[] entries = socialTimeTracking.split(",");
        for (String entry : entries) {
            if (entry.startsWith(uuidStr + ":")) {
                try {
                    long startTime = Long.parseLong(entry.split(":")[1]);
                    return System.currentTimeMillis() - startTime;
                } catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    /**
     * Berechnet den Glücklichkeitsbonus für eine Aktion basierend auf der Häufigkeit
     * Formel: baseBonus / (1 + count * 0.2)
     */
    public int calculateActionBonus(int baseBonus, int actionCount) {
        return (int) (baseBonus / (1.0 + actionCount * 0.2));
    }
    
    // Status-System Getter/Setter
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStatusData() { return statusData; }
    public void setStatusData(String statusData) { this.statusData = statusData; }
    
    public int getStatusCount() { return statusCount; }
    public void setStatusCount(int statusCount) { this.statusCount = statusCount; }
    
    /**
     * Erhöht den Status-Count um 1
     */
    public void incrementStatusCount() {
        this.statusCount++;
    }
    
    public int getStatusInteractionCount() { return statusInteractionCount; }
    public void setStatusInteractionCount(int count) { this.statusInteractionCount = count; }
    
    /**
     * Generiert einen zufälligen Zielwert zwischen 10 und 15 für Status-Wechsel
     */
    private int generateRandomStatusChangeTarget() {
        java.util.Random random = new java.util.Random();
        return 10 + random.nextInt(6); // 10-15 (10 + 0-5)
    }
    
    /**
     * Erhöht den Status-Interaction-Count um 1
     * Gibt true zurück, wenn der Status gewechselt werden soll (alle 10-15 Mal)
     */
    public boolean incrementStatusInteractionCount() {
        this.statusInteractionCount++;
        return this.statusInteractionCount >= this.statusChangeTarget;
    }
    
    /**
     * Setzt den Status-Interaction-Count zurück (wenn Status gewechselt wurde)
     * Generiert einen neuen zufälligen Zielwert zwischen 10-15
     */
    public void resetStatusInteractionCount() {
        this.statusInteractionCount = 0;
        this.statusChangeTarget = generateRandomStatusChangeTarget();
    }
    
    /**
     * Berechnet die Kosten für Überfall-Status (steigend)
     */
    public double getRobbedCost() {
        return 500.0 + (statusCount * 250.0); // Start: 500, dann +250 pro Mal (gleich wie Unfall)
    }
    
    /**
     * Berechnet die Kosten für Unfall-Status (steigend)
     */
    public double getAccidentCost() {
        return 500.0 + (statusCount * 250.0); // Start: 500, dann +250 pro Mal (deutlich niedriger als Steuern)
    }
    
    /**
     * Berechnet die Anzahl benötigter OP Goldäpfel für Unfall-Status (steigend)
     */
    public int getAccidentGoldenAppleCount() {
        return 1 + statusCount; // Start: 1, dann +1 pro Mal
    }
}

