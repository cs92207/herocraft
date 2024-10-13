package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.utils.ItemBuilder;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftingRecipeCommand implements CommandExecutor, Listener {

    static ItemStack mjolnir;
    static ItemStack hawkEyesBow;
    static ItemStack jetPack;
    static ItemStack netShooter;
    static ItemStack pistol;
    static ItemStack capsShield;

    public static void loadStacks() {
        for(CustomStack customStack : ItemsAdder.getAllItems()) {
            switch (customStack.getDisplayName()) {
                case "§4§lMjölnir":
                    mjolnir = customStack.getItemStack();
                    break;
                case "§4§lJet Pack":
                    jetPack = customStack.getItemStack();
                    break;
                case "§4§lSpidermans Netzshooter":
                    netShooter = customStack.getItemStack();
                    break;
                case "§4§lPistole":
                    pistol = customStack.getItemStack();
                    break;
                case "§4§lCaptain Americas Schild":
                    capsShield = customStack.getItemStack();
                    break;
            }
        }
        hawkEyesBow = new ItemBuilder(Material.BOW).setDisplayName("§4§lHawk Eyes Bogen").setLore("", "§eRechtsklick §7» Pfeilart auswählen").build();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::recipes_inventory:");
        inventory.addItem(mjolnir);
        inventory.addItem(hawkEyesBow);
        inventory.addItem(jetPack);
        inventory.addItem(netShooter);
        inventory.addItem(pistol);
        inventory.addItem(capsShield);
        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    public void onRecipesInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        if(event.getCurrentItem() == null)
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getTitle().equalsIgnoreCase(":offset_-16::recipes_inventory:") || event.getView().getTitle().equalsIgnoreCase(":offset_-16::recipe_inventory:")) {
            event.setCancelled(true);
        }
        if(event.getView().getTitle().equalsIgnoreCase(":offset_-16::recipes_inventory:")) {
            if(!event.getCurrentItem().hasItemMeta())
                return;
            if(!event.getCurrentItem().getItemMeta().hasDisplayName())
                return;
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::recipe_inventory:");
            inventory.setItem(24, event.getCurrentItem());
            if(displayName.equalsIgnoreCase("§4§lMjölnir")) {
                inventory.setItem(11, new ItemStack(Material.STONE));
                inventory.setItem(12, new ItemStack(Material.NETHER_STAR));
                inventory.setItem(13, new ItemStack(Material.STONE));
                inventory.setItem(21, new ItemStack(Material.BEACON));
                inventory.setItem(30, new ItemStack(Material.SPRUCE_PLANKS));
            } else if(displayName.equalsIgnoreCase("§4§lHawk Eyes Bogen")) {
                inventory.setItem(11, new ItemStack(Material.STRING));
                inventory.setItem(12, new ItemStack(Material.STICK));
                inventory.setItem(20, new ItemStack(Material.STRING));
                inventory.setItem(22, new ItemStack(Material.DIAMOND_BLOCK));
                inventory.setItem(22, new ItemStack(Material.STICK));
                inventory.setItem(29, new ItemStack(Material.STRING));
                inventory.setItem(30, new ItemStack(Material.STICK));
            } else if(displayName.equalsIgnoreCase("§4§lJet Pack")) {
                inventory.setItem(11, new ItemStack(Material.STONE));
                inventory.setItem(13, new ItemStack(Material.STONE));
                inventory.setItem(20, new ItemStack(Material.STONE));
                inventory.setItem(21, new ItemStack(Material.BEACON));
                inventory.setItem(22, new ItemStack(Material.STONE));
                inventory.setItem(29, new ItemStack(Material.REDSTONE));
                inventory.setItem(31, new ItemStack(Material.REDSTONE));
            } else if(displayName.equalsIgnoreCase("§4§lSpidermans Netzshooter")) {
                inventory.setItem(20, new ItemStack(Material.NETHER_STAR));
                inventory.setItem(21, new ItemStack(Material.REDSTONE));
                inventory.setItem(22, new ItemStack(Material.STRING));
            } else if(displayName.equalsIgnoreCase("§4§lPistole")) {
                inventory.setItem(20, new ItemStack(Material.STONE));
                inventory.setItem(21, new ItemStack(Material.STONE));
                inventory.setItem(22, new ItemStack(Material.STONE));
                inventory.setItem(29, new ItemStack(Material.SPRUCE_PLANKS));
                inventory.setItem(30, new ItemStack(Material.DIAMOND_BLOCK));
            } else if(displayName.equalsIgnoreCase("§4§lCaptain Americas Schild")) {
                inventory.setItem(11, new ItemStack(Material.RED_DYE));
                inventory.setItem(12, new ItemStack(Material.BLUE_DYE));
                inventory.setItem(13, new ItemStack(Material.RED_DYE));
                inventory.setItem(20, new ItemStack(Material.BLUE_DYE));
                inventory.setItem(21, new ItemStack(Material.NETHER_STAR));
                inventory.setItem(22, new ItemStack(Material.BLUE_DYE));
                inventory.setItem(29, new ItemStack(Material.RED_DYE));
                inventory.setItem(30, new ItemStack(Material.BLUE_DYE));
                inventory.setItem(31, new ItemStack(Material.RED_DYE));
            }
            player.openInventory(inventory);
        }
    }

}
