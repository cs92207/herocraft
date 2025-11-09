package de.christoph.herocraft.booster;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DropBooster extends Booster {

    public DropBooster() {
        super("Drop-Booster", "drop_booster", Material.ROTTEN_FLESH);
    }

    @EventHandler
    public void onPlayerKillMob(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player)
            return;
        if(event.getEntity().getType() == EntityType.DONKEY || event.getEntity().getType() == EntityType.HORSE)
            return;
        if(!isBoosterActive() || !boosterWorlds.contains(event.getEntity().getWorld()))
            return;
        for (ItemStack drop : event.getDrops()) {
            ItemStack extraDrop = new ItemStack(drop.getType(), drop.getAmount() * 2);
            event.getDrops().add(extraDrop);
        }
    }

    @Override
    public void onBoosterActivate() {

    }

    @Override
    public void onBoosterMultiply() {

    }

    @Override
    public void onBoosterDeactivated() {

    }

    @Override
    public void onPlayerEnterBoosterWorld(Player player) {

    }

    @Override
    public void onPlayerLeaveBoosterWorld(Player player) {

    }

}
