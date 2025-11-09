package de.christoph.herocraft.dimensions;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class End extends Dimension {

    public End() {
        super("Ende",
                "world_the_end",
                "Die Normale Minecraft End Dimension",
                new String[]{
                    "Kann nur über Portal erreicht werden"
                },
                new String[]{
                    "Enderdrache"
                },
                Material.END_STONE
            );
    }

    @Override
    public void onDimensionEntered(Player player) {

    }

    @Override
    public void onDimensionLeaved(Player player) {

    }

}
