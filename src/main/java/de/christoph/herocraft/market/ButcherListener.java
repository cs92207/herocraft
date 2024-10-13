package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ButcherListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::schlachter:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(event.getCurrentItem().getType().equals(Material.BLUE_STAINED_GLASS_PANE))
            return;
        if(event.getCurrentItem().getType().equals(Material.PORKCHOP)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.PORKCHOP_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.PORKCHOP_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.BEEF)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.BEEF_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.BEEF_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.CHICKEN)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.CHICKEN_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.CHICKEN_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.RABBIT)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.RABBIT_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.RABBIT_PRICE, player);
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.MUTTON)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.MUTTON_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.MUTTON_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.COOKED_PORKCHOP)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COOKED_SELL_PORKCHOP_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COOKED_PORKCHOP_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.COOKED_BEEF)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COOKED_SELL_BEEF_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COOKED_BEEF_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.COOKED_CHICKEN)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COOKED_SELL_CHICKEN_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COOKED_CHICKEN_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.COOKED_RABBIT)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COOKED_SELL_RABBIT_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COOKED_RABBIT_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.COOKED_MUTTON)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.COOKED_SELL_MUTTON_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.COOKED_MUTTON_PRICE, player);
            }

        }
    }

}
