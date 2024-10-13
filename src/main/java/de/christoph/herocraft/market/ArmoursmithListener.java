package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ArmoursmithListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::ruestungsschmied:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(event.getCurrentItem().getType().equals(Material.BLUE_STAINED_GLASS_PANE))
            return;
        if(event.getCurrentItem().getType().equals(Material.IRON_BOOTS)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.I_BOOTS_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.IRON_LEGGINGS)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.I_LEGGINS_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.IRON_CHESTPLATE)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.I_CHEST_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.IRON_HELMET)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.I_HELMET_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.DIAMOND_BOOTS)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.D_BOOTS_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.DIAMOND_LEGGINGS)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.D_LEGGINS_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.DIAMOND_CHESTPLATE)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.D_CHEST_PRICE, player);
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.DIAMOND_HELMET)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.D_HELMET_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.NETHERITE_BOOTS)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.N_BOOTS_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.NETHERITE_LEGGINGS)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.N_LEGGINS_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.NETHERITE_CHESTPLATE)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.N_CHEST_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.NETHERITE_HELMET)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.N_HELMET_PRICE, player);
            }

        }
    }

}
