package de.christoph.herocraft.jobs;

public enum JobType {
    HOLZFAELLER("Holzfäller"),
    MINENARBEITER("Minenarbeiter"),
    FARMER("Farmer"),
    SCHLACHTER("Schlachter"),
    LANDSCHAFTSBAUER("Landschaftsbauer");

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static JobType fromString(String name) {
        for (JobType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.displayName.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}