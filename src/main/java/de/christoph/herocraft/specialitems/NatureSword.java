package de.christoph.herocraft.specialitems;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NatureSword implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player))
            return;
        Player player = (Player) event.getDamager();
        if(!player.getInventory().getItemInMainHand().hasItemMeta())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lNatur Schwert"))
            return;
        event.setDamage(event.getDamage() + 8);
    }

}
