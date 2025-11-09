package de.christoph.herocraft.dimensions;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDate;
import java.time.Month;

public class SummerDimension extends Dimension {

    public SummerDimension() {
        super(
            "Sommer Dimension",
            "summerDimension",
            "Diese Dimension ist nur im Sommer verfügbar. Baue mit großer Fläche deine Sommer-Residenz",
            new String[]{
                "Nur im Sommer verfügbar",
                "Außerhalb des Sommers Nur Spectator Modus"
            },
            new String[]{  },
            Material.PINK_TULIP
        );
    }

    public static boolean isSummer() {
        Month monat = LocalDate.now().getMonth();
        return monat == Month.MAY
            || monat == Month.JUNE
            || monat == Month.JULY;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getPlayer().getWorld().getName().equalsIgnoreCase(getWorld()))
            return;
        if(!isSummer()) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionEntered(Player player) {
        if(!isSummer()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionLeaved(Player player) {
        if(!isSummer()) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

}
