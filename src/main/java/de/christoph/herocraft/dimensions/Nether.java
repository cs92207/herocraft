package de.christoph.herocraft.dimensions;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Nether extends Dimension {

    public Nether() {
        super("Hölle",
            "world_nether",
            "Die normale Minecraft Nether Welt",
            new String[]{
                "Kann nur über Portale erreicht werden"
            },
            new String[]{

            },
                Material.NETHERRACK);
    }

    @Override
    public void onDimensionEntered(Player player) {

    }

    @Override
    public void onDimensionLeaved(Player player) {

    }

}
