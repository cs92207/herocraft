package de.christoph.herocraft.lands.officials;

public class Official {
    private String landName;
    private double x;
    private double y;
    private double z;
    private String world;
    private String type; // "FIREFIGHTER", "POLICE", "DOCTOR"
    private long lastSalaryTime; // Timestamp der letzten Gehaltszahlung
    private int salaryCount; // Wie oft Gehalt gezahlt wurde (für steigende Kosten)
    
    // Score-Bereiche für Happiness Level
    public static final double BASE_SALARY = 1000.0; // Basis-Gehalt
    public static final double SALARY_INCREASE = 500.0; // Erhöhung pro Mal
    
    public Official(String landName, double x, double y, double z, String world, String type) {
        this.landName = landName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.type = type;
        this.lastSalaryTime = System.currentTimeMillis();
        this.salaryCount = 0;
    }
    
    // Konstruktor für DB-Loading
    public Official(String landName, double x, double y, double z, String world, String type, long lastSalaryTime, int salaryCount) {
        this.landName = landName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.type = type;
        this.lastSalaryTime = lastSalaryTime;
        this.salaryCount = salaryCount;
    }
    
    /**
     * Gibt einen eindeutigen Location-Key zurück (für HashMap-Schlüssel)
     */
    public String getLocationKey() {
        // Verwende gerundete Werte für bessere Stabilität (auf 0.1 genau)
        return world + ":" + Math.round(x * 10) / 10.0 + ":" + Math.round(y * 10) / 10.0 + ":" + Math.round(z * 10) / 10.0;
    }
    
    /**
     * Erstellt einen Location-Key aus Koordinaten
     */
    public static String createLocationKey(String world, double x, double y, double z) {
        return world + ":" + Math.round(x * 10) / 10.0 + ":" + Math.round(y * 10) / 10.0 + ":" + Math.round(z * 10) / 10.0;
    }
    
    // Getter/Setter
    public String getLandName() { return landName; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public String getWorld() { return world; }
    public String getType() { return type; }
    public long getLastSalaryTime() { return lastSalaryTime; }
    public void setLastSalaryTime(long lastSalaryTime) { this.lastSalaryTime = lastSalaryTime; }
    public int getSalaryCount() { return salaryCount; }
    public void setSalaryCount(int salaryCount) { this.salaryCount = salaryCount; }
    public void incrementSalaryCount() { this.salaryCount++; }
    
    /**
     * Berechnet das aktuelle Gehalt (steigend)
     */
    public double getCurrentSalary() {
        return BASE_SALARY + (salaryCount * SALARY_INCREASE);
    }
    
    /**
     * Prüft ob das Gehalt überfällig ist (mehr als 10 Tage - dann wird der Beamte arbeitslos)
     */
    public boolean isSalaryOverdue() {
        long currentTime = System.currentTimeMillis();
        long tenDaysInMillis = 10 * 86400000L; // 10 Tage (3 Tage Puffer nach 7 Tagen Bezahlungsfrist)
        return (currentTime - lastSalaryTime) > tenDaysInMillis;
    }
    
    /**
     * Prüft ob das Gehalt in den letzten 7 Tagen bereits gezahlt wurde
     */
    public boolean isSalaryPaidWithin7Days() {
        // Wenn noch nie bezahlt wurde, dann nicht bezahlt
        if (salaryCount == 0 || lastSalaryTime == 0) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long sevenDaysInMillis = 7 * 86400000L; // 7 Tage
        return (currentTime - lastSalaryTime) < sevenDaysInMillis;
    }
    
    /**
     * Prüft ob das Gehalt heute bereits gezahlt wurde (für Rückwärtskompatibilität - wird zu isSalaryPaidWithin7Days)
     * @deprecated Verwende isSalaryPaidWithin7Days()
     */
    @Deprecated
    public boolean isSalaryPaidToday() {
        return isSalaryPaidWithin7Days();
    }
    
    /**
     * Gibt den Namen für den Villager zurück
     */
    public String getVillagerName() {
        String typeDisplay = getTypeDisplayName();
        if (isSalaryOverdue()) {
            // Rot: Arbeitslos (mehr als 10 Tage nicht bezahlt)
            return "§c§l" + typeDisplay + " §4§l[Arbeitslos]";
        } else if (isSalaryPaidWithin7Days()) {
            // Grün: Bezahlt (in den letzten 7 Tagen bezahlt)
            return "§a§l" + typeDisplay + " §2§l[bezahlt]";
        } else {
            // Orange: Bezahlung fällig (mehr als 7 Tage vergangen, aber weniger als 10 Tage)
            return "§6§l" + typeDisplay + " §e§l[Bezahlung]";
        }
    }
    
    /**
     * Gibt den deutschen Anzeigenamen für den Typ zurück
     */
    private String getTypeDisplayName() {
        switch (type) {
            case "FIREFIGHTER":
                return "Feuerwehrmann";
            case "POLICE":
                return "Polizist";
            case "DOCTOR":
                return "Notarzt";
            default:
                return "Beamter";
        }
    }
}


