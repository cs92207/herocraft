package de.christoph.herocraft.market;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class DecorationShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::decoration_shop:");
        inventory.addItem(getOfferItem("§fTrash Can", "§4§lMülltonne", 80));
        inventory.addItem(getOfferItem("§fTraffic Jam", "§4§lPilone", 30));
        inventory.addItem(getOfferItem("§fTraffic Fence", "§4§lAbsperrung", 45));
        inventory.addItem(getOfferItem("§fTraffic Sign", "§4§lEinbahnstraßen Schild", 30));
        inventory.addItem(getOfferItem("§fTraffic Sign 2", "§4§lWeiterfahren Schild", 30));
        player.openInventory(inventory);
        return false;
    }

    public ItemStack getOfferItem(String furnitureName, String displayName, double price) {
        for(CustomStack customStack : ItemsAdder.getAllItems()) {
            if(customStack.getDisplayName().equalsIgnoreCase(furnitureName)) {
                ItemStack itemStack = customStack.getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(displayName);
                ArrayList<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("§7Preis: §e" + price);
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }
        }
        return null;
    }

}
