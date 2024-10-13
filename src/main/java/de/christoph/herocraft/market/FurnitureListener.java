package de.christoph.herocraft.market;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FurnitureListener implements Listener {

    @EventHandler
    public void onFurnitureInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::furniture_shop:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        String priceLine = event.getCurrentItem().getItemMeta().getLore().get(1);
        String priceString = priceLine.substring(11);
        double price = Double.parseDouble(priceString);
        if(HeroCraft.getPlugin().coin.getCoins(player) < price) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            return;
        }
        HeroCraft.getPlugin().coin.removeMoney(player, price);
        player.getInventory().addItem(event.getCurrentItem());
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.sendMessage(Constant.PREFIX + "§7Erfolgreich gekauft.");
    }

}
