package de.christoph.herocraft.dimensions;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HotDessert extends Dimension {

    public HotDessert() {
        super("Heiße Wüste",
            "dessert",
            "Eine heiße Wüste mit schweren Überlebungsbedingungen für beste Herausforderung",
                new String[]{
                    "Durst",
                    "Stärkere Monster",
                    "Wenige Ressourcen",
                    "Erschöpfung",
                    "Ausgewogene Ernährung",
                    "Überhitzung",
                    "Langsamere Heilung"
                },
                Material.SAND
            );
    }

    @Override
    public void onDimensionEntered(Player player) {

    }

    @Override
    public void onDimensionLeaved(Player player) {

    }

}
