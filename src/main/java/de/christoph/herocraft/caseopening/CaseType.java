package de.christoph.herocraft.caseopening;

public enum CaseType {

    NORMAL(
            "normal",
            "caseinventory.normal",
            "survivalland_cases",
            "survivalland_cases_progresses",
            "SurvivalLands Kiste",
            "SurvivalLands Kisten"
    ),
    PREMIUM(
        "premium",
        "caseinventory.premium",
        "survivallands_cases_premium",
        "survivallands_cases_premium_progresses",
        "Plus Kiste",
        "Plus Kisten"
    );

    private static final String PLUS_CASE_DISPLAY_NAME = "§4§lKiste Plus";

    private final String configKey;
    private final String configPath;
    private final String chestTable;
    private final String progressTable;
    private final String singularDisplayName;
    private final String pluralDisplayName;

    CaseType(String configKey, String configPath, String chestTable, String progressTable, String singularDisplayName, String pluralDisplayName) {
        this.configKey = configKey;
        this.configPath = configPath;
        this.chestTable = chestTable;
        this.progressTable = progressTable;
        this.singularDisplayName = singularDisplayName;
        this.pluralDisplayName = pluralDisplayName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getChestTable() {
        return chestTable;
    }

    public String getProgressTable() {
        return progressTable;
    }

    public String getSingularDisplayName() {
        return singularDisplayName;
    }

    public String getPluralDisplayName() {
        return pluralDisplayName;
    }

    public static CaseType fromArgument(String argument) {
        if (argument.equalsIgnoreCase("plus")) {
            return PREMIUM;
        }
        for (CaseType type : values()) {
            if (type.configKey.equalsIgnoreCase(argument)) {
                return type;
            }
        }
        return null;
    }

    public static CaseType fromFurnitureDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        if (displayName.equalsIgnoreCase(PLUS_CASE_DISPLAY_NAME)) {
            return PREMIUM;
        }
        if (displayName.equalsIgnoreCase("§4§lInventar") || displayName.equalsIgnoreCase("§4§lMobiles Kisten Gewinnspiel")) {
            return NORMAL;
        }
        return null;
    }
}