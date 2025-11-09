package de.christoph.herocraft.markethall;

import de.christoph.herocraft.market.MarketCommand;
import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class MarketShop implements Listener {

    private String guiName;
    private ArrayList<MarketItem> marketItems;
    private boolean sellable;

    public MarketShop(String guiName, boolean sellable, MarketItem... marketItems) {
        this.guiName = guiName;
        this.marketItems = new ArrayList<>();
        this.marketItems.addAll(List.of(marketItems));
        this.sellable = sellable;
    }

    public void openShopMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, guiName);
        for(MarketItem i : marketItems) {
            inventory.addItem(i.getIcon());
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onShopInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(guiName))
            return;
        event.setCancelled(true);
        MarketItem marketItem = null;
        for(MarketItem i : marketItems) {
            if(i.getMaterial() == event.getCurrentItem().getType()) {
                marketItem = i;
            }
        }
        if(marketItem == null)
            return;
        if(event.getAction() == InventoryAction.PICKUP_HALF) {
            openSellInventory(marketItem, player);
        } else {
            marketItem.buyItem(player);
        }
    }

    public void openSellInventory(MarketItem marketItem, Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::sellitem:");
        inventory.setItem(19, new ItemStack(marketItem.getMaterial(), 1));
        inventory.setItem(21, new ItemStack(marketItem.getMaterial(), 16));
        inventory.setItem(23, new ItemStack(marketItem.getMaterial(), 32));
        inventory.setItem(25, new ItemStack(marketItem.getMaterial(), 64));
        player.openInventory(inventory);
    }

    @EventHandler
    public void onPlayerSellInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::sellitem:"))
            return;
        if(!sellable)
            return;
        MarketItem marketItem = null;
        for(MarketItem i : marketItems) {
            if(i.getMaterial() == event.getCurrentItem().getType()) {
                marketItem = i;
            }
        }
        if(marketItem == null)
            return;
        int sellAmount = event.getCurrentItem().getAmount();
        marketItem.sellItems(player, sellAmount);
    }

    public ArrayList<MarketItem> getMarketItems() {
        return marketItems;
    }

    public String getGuiName() {
        return guiName;
    }

}
