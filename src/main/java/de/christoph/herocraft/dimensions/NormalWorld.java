package de.christoph.herocraft.dimensions;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NormalWorld extends Dimension {

    public NormalWorld() {
        super("Normale Welt",
                "world",
                "Die normale Minecraft Welt, perfekt zum Start",
                new String[]{
                    ""
                },
                new String[]{},
                Material.GRASS_BLOCK);
    }

    @Override
    public void onDimensionEntered(Player player) {

    }

    @Override
    public void onDimensionLeaved(Player player) {

    }

}
