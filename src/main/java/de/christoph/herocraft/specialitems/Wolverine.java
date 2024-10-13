package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Wolverine implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player))
            return;
        Player player = (Player) event.getDamager();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lWolverins Krallen"))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        event.setDamage(event.getDamage() + 4);
    }

}
