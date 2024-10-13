package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.swing.*;

public class WoodListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::holzfaeller:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(event.getCurrentItem().getType().equals(Material.BLUE_STAINED_GLASS_PANE))
            return;
        if(event.getCurrentItem().getType().equals(Material.OAK_LOG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.OAK_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.OAK_PRICE, player);
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.SPRUCE_LOG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.SPRUCE_SELL__PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.SPRUCE_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.BIRCH_LOG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.BIRCH_SELL__PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.BIRCH_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.JUNGLE_LOG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.JUNGLE_SELL__PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.JUNGLE_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.ACACIA_LOG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.ACACIA_SELL__PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.ACACIA_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.DARK_OAK_LOG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.DARK_OAK_SELL__PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.DARK_OAK_PRICE, player);
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.MANGROVE_LOG)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.MANGROVE_SELL__PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.MANGROVE_PRICE, player);
            }

        }
    }

}
