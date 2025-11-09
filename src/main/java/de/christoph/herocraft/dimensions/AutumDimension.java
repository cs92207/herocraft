package de.christoph.herocraft.dimensions;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDate;
import java.time.Month;

public class AutumDimension extends Dimension {

    public AutumDimension() {
        super(
            "Herbst Dimension",
            "autumDimension",
            "Diese Dimension ist nur im Herbst verfügbar. Baue mit großer Fläche deine Herbst-Residenz",
            new String[]{
                "Nur im Herbst verfügbar",
                "Außerhalb des Herbstes Nur Spectator Modus"
            },
            new String[]{  },
            Material.RED_MUSHROOM
        );
    }

    public static boolean isAutum() {
        Month monat = LocalDate.now().getMonth();
        return monat == Month.SEPTEMBER
            || monat == Month.OCTOBER
            || monat == Month.NOVEMBER;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getPlayer().getWorld().getName().equalsIgnoreCase(getWorld()))
            return;
        if(!isAutum()) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionEntered(Player player) {
        if(!isAutum()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionLeaved(Player player) {
        if(!isAutum()) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

}
