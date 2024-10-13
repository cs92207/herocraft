package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import net.minecraft.world.item.ItemAir;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class LandShop implements CommandExecutor, Listener {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::land_shop:");
        ItemStack goverment = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
                goverment = i.getItemStack();
            }
        }
        ItemMeta itemMeta = goverment.getItemMeta();
        ArrayList<String> lore = new ArrayList();
        lore.add("");
        lore.add("§7Preis: " + "§e" + Constant.GOVERMENT_PRICE);
        itemMeta.setLore(lore);
        goverment.setItemMeta(itemMeta);
        inventory.addItem(goverment);
        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::land_shop:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        String diplayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(diplayName.equalsIgnoreCase("§4§lRegierungsgebäude")) {
            if(HeroCraft.getPlugin().coin.getCoins(player) >= Constant.GOVERMENT_PRICE) {
                ItemStack goverment = null;
                for(CustomStack i : ItemsAdder.getAllItems()) {
                    if(i.getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
                        goverment = i.getItemStack();
                    }
                }
                player.getInventory().addItem(goverment);
                HeroCraft.getPlugin().coin.removeMoney(player, Constant.GOVERMENT_PRICE);
                player.sendMessage(Constant.PREFIX + "§7Erfolgreich gekauft.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 3);
                player.closeInventory();
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            }
        }
    }

}
