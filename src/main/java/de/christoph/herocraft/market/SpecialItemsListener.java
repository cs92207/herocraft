package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SpecialItemsListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::all_offers:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(event.getCurrentItem().getType().equals(Material.BLUE_STAINED_GLASS_PANE))
            return;
        if(event.getCurrentItem().getType().equals(Material.TOTEM_OF_UNDYING)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.TOTEM, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.VILLAGER_SPAWN_EGG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.VILLAGER_SPAWN_EGG, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.BUDDING_AMETHYST)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.BUDDING_AMETHYIST, player);
            }

        }
    }

}
