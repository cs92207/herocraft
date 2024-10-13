package de.christoph.herocraft.dimensions;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlackDessert extends Dimension {

    public BlackDessert() {
        super("Schwarze Wüste",
            "blackDessert",
            "Eine Wüste bestehend aus schwarzen Stein und schweren Überlebensverhältnissen. Perfekt für eine neue Herausforderung und neue Baustile",
            new String[]{
                "Durst",
                "Stärkere Monster",
                "Wenige Ressourcen",
                "Erschöpfung",
                "Ausgewogene Ernährung",
                "Überhitzung",
                "Langsamere Heilung",
            },
                Material.BLACKSTONE
        );
    }

    @Override
    public void onDimensionEntered(Player player) {

    }

    @Override
    public void onDimensionLeaved(Player player) {

    }

}
