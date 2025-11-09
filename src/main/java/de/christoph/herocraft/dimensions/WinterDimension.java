package de.christoph.herocraft.dimensions;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDate;
import java.time.Month;

public class WinterDimension extends Dimension {

    public WinterDimension() {
        super(
            "Winter Dimension",
            "winterDimension",
            "Diese Dimension ist nur im Winter verfügbar. Baue mit großer Fläche deine Winter-Residenz",
            new String[]{
                "Nur im Winter verfügbar",
                "Außerhalb des Winters Nur Spectator Modus"
            },
            new String[]{  },
            Material.SNOW_BLOCK
        );
    }

    public static boolean isWinter() {
        Month monat = LocalDate.now().getMonth();
        return monat == Month.DECEMBER
            || monat == Month.JANUARY
            || monat == Month.FEBRUARY;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getPlayer().getWorld().getName().equalsIgnoreCase(getWorld()))
            return;
        if(!isWinter()) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionEntered(Player player) {
        if(!isWinter()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDimensionLeaved(Player player) {
        if(!isWinter()) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

}
