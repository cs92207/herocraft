package de.christoph.herocraft.lands.roles;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public enum LandPermission {
    
    BUILD("Bauen", "Mitglied kann im Land bauen."),
    BREAK("Abbauen", "Mitglied kann im Land abbauen."),
    PVP("PVP", "Mitglied kann im Land andere Spieler angreifen."),
    PVE_UNFRIENDLY("Feindliche Mobs bekämpfen", "Mitglied kann im Land andere feindliche Entitäten angreifen."),
    PVE_FRIENDLY("Friedliche Mobs bekämpfen", "Mitglied kann im Land andere friedliche Entitäten angreifen."),
    OPEN_DOOR("Türen benutzen", "Mitglied kann im Land Türen öffnen"),
    OPEN_CHESTS("Kisten benutzen", "Mitglied kann im Land Kisten benutzen"),
    INTERACT("Interagieren", "Mitglied kann im Land mit sämmtlichen Blöcken interagieren."),
    ADMIN_ACCESS("Admin Zugriff", "Mitglied kann auf das Admin Menü im Land zugreifen.");

    private LandPermission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
