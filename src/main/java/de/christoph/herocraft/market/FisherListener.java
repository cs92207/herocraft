package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FisherListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::fisher:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(event.getCurrentItem().getType().equals(Material.BLUE_STAINED_GLASS_PANE))
            return;
        if(event.getCurrentItem().getType().equals(Material.COD)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COD_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COD_PRICE, player);
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.SALMON)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.SALMON_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.SALMON_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.TROPICAL_FISH)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.TROPICAL_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.TROPICAL_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.COOKED_COD)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COOKED_SELL_COD_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COOKED_COD_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.COOKED_SALMON)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COOKED_SELL_SALMON_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COOKED_SALMON_PRICE, player);
            }

        }
    }

}
