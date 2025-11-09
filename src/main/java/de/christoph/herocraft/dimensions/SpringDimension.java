package de.christoph.herocraft.dimensions;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDate;
import java.time.Month;

public class SpringDimension extends Dimension {

    public SpringDimension() {
        super(
            "Frühling Dimension",
            "springDimension",
            "Diese Dimension ist nur im Frühling verfügbar. Baue mit großer Fläche deine Frühling-Residenz",
            new String[]{
                "Nur im Frühling verfügbar",
                "Außerhalb des Frühlings Nur Spectator Modus"
            },
            new String[]{  },
            Material.PINK_TULIP
        );
    }

    public static boolean isSpring() {
        Month monat = LocalDate.now().getMonth();
        return monat == Month.MARCH
            || monat == Month.APRIL
            || monat == Month.MAY;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getPlayer().getWorld().getName().equalsIgnoreCase(getWorld()))
            return;
        if(!isSpring()) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionEntered(Player player) {
        if(!isSpring()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionLeaved(Player player) {
        if(!isSpring()) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

}
