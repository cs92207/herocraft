package de.christoph.herocraft.basiccommands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class EnderchestListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(EnderchestCommand.ecPlayer.contains(player)) {
            event.setCancelled(true);
        } else
            return;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if(EnderchestCommand.ecPlayer.contains(event.getPlayer())) {
            EnderchestCommand.ecPlayer.remove(event.getPlayer());
        }
    }

}
