package de.christoph.herocraft.booster;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class XPBooster extends Booster {

    public XPBooster() {
        super("XP-Booster", "xp_booster", Material.EXPERIENCE_BOTTLE);
    }

    @Override
    public void onBoosterActivate() {  }

    @Override
    public void onBoosterMultiply() {  }

    @Override
    public void onBoosterDeactivated() {  }

    @Override
    public void onPlayerEnterBoosterWorld(Player player) {  }

    @Override
    public void onPlayerLeaveBoosterWorld(Player player) {  }

    @EventHandler
    public void onXP(PlayerExpChangeEvent event) {
        if(!isBoosterActive() || !boosterWorlds.contains(event.getPlayer().getWorld()))
            return;
        event.setAmount(event.getAmount() * 2);
    }

}