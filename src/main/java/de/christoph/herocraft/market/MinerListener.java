package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MinerListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::bergarbeiter:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(event.getCurrentItem().getType().equals(Material.BLUE_STAINED_GLASS_PANE))
            return;
        if(event.getCurrentItem().getType().equals(Material.IRON_INGOT)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.I_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.I_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.REDSTONE)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.R_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.R_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.GOLD_INGOT)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.G_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.G_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.LAPIS_LAZULI)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.L_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.L_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.DIAMOND)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.D_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.D_PRICE, player);
            }

        }
        else if(event.getCurrentItem().getType().equals(Material.EMERALD)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.E_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.E_PRICE, player);
            }
        }
        else if(event.getCurrentItem().getType().equals(Material.NETHERITE_INGOT)) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) {
                MarketCommand.sellItem(event.getCurrentItem(), Constant.N_SELL_PRICE, player);
            } else {
                MarketCommand.buyItem(event.getCurrentItem(), Constant.N_PRICE, player);
            }

        }
    }

}
