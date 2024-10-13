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

public class FurnitureCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::furniture_shop:");
        inventory.addItem(getOfferItem("§4Red wooden sofa left", "§4§lRotes Sofa links", 100));
        inventory.addItem(getOfferItem("§4Red wooden sofa right", "§4§lRotes Sofa rechts", 100));
        inventory.addItem(getOfferItem("§4Red Oak coffee table", "§4§lRoter Eichenkaffetisch", 70));
        inventory.addItem(getOfferItem("§4Red oak lamp", "§4§lRote Eichenlampe", 40));
        inventory.addItem(getOfferItem("§fWhite wooden sofa left", "§4§lWeißes Sofa links", 100));
        inventory.addItem(getOfferItem("§fWhite wooden sofa right", "§4§lWeißes Sofa rechts", 100));
        inventory.addItem(getOfferItem("§fSpruce coffee table", "§4§lWeißer Kaffeetisch", 70));
        inventory.addItem(getOfferItem("§fSpruce lamp", "§4§lWeiße Lampe", 40));
        inventory.addItem(getOfferItem("§3Cyan wooden sofa left", "§4§lTürkises Sofa links", 100));
        inventory.addItem(getOfferItem("§3Cyan wooden sofa right", "§4§lTürkises Sofa rechts", 100));
        inventory.addItem(getOfferItem("§3Jungle coffee table", "§4§lTürkiser Kaffeetisch", 70));
        inventory.addItem(getOfferItem("§3Jungle cyan lamp", "§4§lTürkise Lampe", 40));
        inventory.addItem(getOfferItem("§6Gray wooden sofa left", "§4§lGraues Sofa links", 100));
        inventory.addItem(getOfferItem("§6Gray wooden sofa right", "§4§lGraues Sofa rechts", 100));
        inventory.addItem(getOfferItem("§6Acacia coffee table", "§4§lGrauer Kaffeetisch", 70));
        inventory.addItem(getOfferItem("§6Gray Acacia lamp", "§4§lGraue Lampe", 40));
        inventory.addItem(getOfferItem("§7§lIron sofa left", "§4§lEisen Sofa links", 150));
        inventory.addItem(getOfferItem("§7§lIron sofa right", "§4§lEisen Sofa rechts", 150));
        inventory.addItem(getOfferItem("§7§lIron coffee table", "§4§lEisen Kaffeetisch", 120));
        inventory.addItem(getOfferItem("§7§lIron  lamp", "§4§lEisen Lampe", 90));
        inventory.addItem(getOfferItem("§5Amethyst sofa left", "§4§lAmethyst Sofa links", 200));
        inventory.addItem(getOfferItem("§5Amethyst sofa right", "§4§lAmethyst Sofa rechts", 200));
        inventory.addItem(getOfferItem("§5Amethyst coffee table", "§4§lAmethyst Kaffeetisch", 170));
        inventory.addItem(getOfferItem("§5Amethyst lamp", "§4§lAmethyst Lampe", 140));
        inventory.addItem(getOfferItem("§fPark Bench", "§4§lPark Bank", 100));
        inventory.addItem(getOfferItem("§fDark Park Bench", "§4§lDunkle Park Bank", 100));
        inventory.addItem(getOfferItem("§fSimple Park Bench", "§4§lEinfache Park Bank", 65));
        inventory.addItem(getOfferItem("§fSimple Dark Park Bench", "§4§lEinfache Dunkle Park Bank", 65));
        inventory.addItem(getOfferItem("§fWood Park Bench", "§4§lHolz Park Bank", 110));
        inventory.addItem(getOfferItem("§fDark Wood Park Bench", "§4§lDunkle Holz Park Bank", 110));
        inventory.addItem(getOfferItem("§fPark Chair", "§4§lStuhl", 50));
        inventory.addItem(getOfferItem("§fDark Park Chair", "§4§lDunkler Stuhl", 50));
        inventory.addItem(getOfferItem("§fWood Park Chair", "§4§lHolz Stuhl", 60));
        inventory.addItem(getOfferItem("§fDark Wood Park Chair", "§4§lDunkler Holz Stuhl", 60));
        inventory.addItem(getOfferItem("§fPark Table", "§4§lTisch", 70));
        inventory.addItem(getOfferItem("§fDark Park Table", "§4§lDunkler Tisch", 70));
        inventory.addItem(getOfferItem("§fGlass Park Table", "§4§lGlasstisch", 80));
        inventory.addItem(getOfferItem("§fSingle Park Table", "§4§lEinfacher Tisch", 40));
        inventory.addItem(getOfferItem("§fSingle Dark Park Table", "§4§lEinfacher dunkler Tisch", 40));
        inventory.addItem(getOfferItem("§fSingle Glass Park Table", "§4§lEinfacher Glasstisch", 50));
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
