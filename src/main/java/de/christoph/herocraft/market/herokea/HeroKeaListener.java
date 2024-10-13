package de.christoph.herocraft.market.herokea;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HeroKeaListener implements Listener {

    // TODO: Einzelne Möbel kaufen
    // TODO: Leere flächen mit Möbeln füllen
    // TODO: Kauf Inventare


    @EventHandler
    public void onSignSubmit(SignChangeEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("herowars.herocraft.shopsign"))
            return;
        if(!event.getLine(0).equalsIgnoreCase("[AnyKea]"))
            return;
        event.setLine(2, event.getLine(2).replace("&", "§"));
        event.setLine(3, event.getLine(3).replace("&", "§"));
        event.setLine(0, "§7[§e§lAnyKea§7]");
    }

    @EventHandler
    public void onShopMenuClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase("§4§lAnyKea"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(!event.getCurrentItem().getItemMeta().hasLore())
            return;
        String priceText = event.getCurrentItem().getItemMeta().getLore().get(1);
        priceText = priceText.substring(3);
        String numbersOnly = priceText.replaceAll("[^0-9]", "");
        int price = Integer.parseInt(numbersOnly);
        System.out.println(numbersOnly);
        System.out.println(price);
        if(HeroCraft.getPlugin().coin.getCoins(player) < price) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            return;
        }
        HeroCraft.getPlugin().coin.removeMoney(player, price);
        player.sendMessage(Constant.PREFIX + "§7Du hast das Item §agekauft§7.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        ItemStack itemStack = event.getCurrentItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(new ArrayList<>());
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null)
            return;
        if(!(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getClickedBlock().getState();
        if(!sign.getLine(0).equalsIgnoreCase("§7[§e§lAnyKea§7]"))
            return;
        if(sign.getLine(2).equalsIgnoreCase("§fStühle")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("Chair")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.CHAIR_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
            return;
        } else if(sign.getLine(2).equalsIgnoreCase("§fBänke")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("bench") || customStack.getDisplayName().contains("Bench")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.BANK_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
            return;
        } else if(sign.getLine(2).equalsIgnoreCase("§fSofas")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("sofa") || customStack.getDisplayName().contains("Sofa")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.SOFA_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
            return;
        } else if(sign.getLine(2).equalsIgnoreCase("§fTische")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("table") || customStack.getDisplayName().contains("Table")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.TABLE_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        } else if(sign.getLine(2).equalsIgnoreCase("§fCovers")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("cover")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.COVER_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        } else if(sign.getLine(2).equalsIgnoreCase("§fVerkaufsboxen")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("sellbox")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.SELLBOX_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        } else if(sign.getLine(2).equalsIgnoreCase("§fKassen")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("Register")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.CHECKOUT_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        } else if(sign.getLine(2).equalsIgnoreCase("§fBlumentöpfe")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("Pot") || customStack.getDisplayName().contains("pot")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.FLOWER_POD_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        } else if(sign.getLine(2).equalsIgnoreCase("§fFenster Blumen")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("window-side") || customStack.getDisplayName().contains("hanging plante")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.WINDOW_FLOWER_POD_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        } else if(sign.getLine(2).equalsIgnoreCase("§fLampen")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("lamp") || customStack.getDisplayName().contains("Lamp")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.LAMP_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        } else if(sign.getLine(2).equalsIgnoreCase("§fHölzer")) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lAnyKea");
            for(CustomStack customStack : ItemsAdder.getAllItems()) {
                if(customStack.getDisplayName().contains("§foak") || customStack.getDisplayName().contains("§fspruce_log")|| customStack.getDisplayName().contains("§fbirch_log")|| customStack.getDisplayName().contains("§facacia_log")|| customStack.getDisplayName().contains("§fdark_oak")|| customStack.getDisplayName().contains("§fjungle_log")) {
                    ItemStack itemStack = customStack.getItemStack();
                    ItemMeta itemMeta = customStack.getItemStack().getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preis: §e" + Constant.WOOD_PRICE + " Coins");
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.addItem(itemStack);
                }
            }
            player.openInventory(inventory);
        }
        double price = Double.parseDouble(sign.getLine(1));
        String itemName = sign.getLine(2);
        itemName = itemName + " " + sign.getLine(3).substring(2);
        if(HeroCraft.getPlugin().coin.getCoins(player) < price) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            return;
        }
        HeroCraft.getPlugin().coin.removeMoney(player, price);
        for(CustomStack customStack : ItemsAdder.getAllItems()) {
            if(customStack.getDisplayName().equalsIgnoreCase(itemName)) {
                ItemStack itemStack = customStack.getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§4§lMöbel");
                itemMeta.setLore(new ArrayList<>());
                itemStack.setItemMeta(itemMeta);
                player.getInventory().addItem(itemStack);
            }
        }
    }

}
