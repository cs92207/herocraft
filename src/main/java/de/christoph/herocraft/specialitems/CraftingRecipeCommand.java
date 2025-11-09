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
    static ItemStack darkStick;
    static ItemStack natureSword;
    static ItemStack sandstorm;
    static ItemStack scaleStick;

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
                case "§4§lDunkler Stab":
                    darkStick = customStack.getItemStack();
                    break;
                case "§4§lNatur Schwert":
                    natureSword = customStack.getItemStack();
                    break;
                case "§4§lSandsturm":
                    sandstorm = customStack.getItemStack();
                    break;
                case "§4§lScale Waffe":
                    scaleStick = customStack.getItemStack();
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
        inventory.setItem(9, mjolnir);
        inventory.setItem(10, hawkEyesBow);
        inventory.setItem(11, jetPack);
        inventory.setItem(12, netShooter);
        inventory.setItem(13, pistol);
        inventory.setItem(14, capsShield);
        inventory.setItem(18, sandstorm);
        inventory.setItem(27, natureSword);
        inventory.setItem(36, darkStick);
        inventory.setItem(37, scaleStick);
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
            } else if(displayName.equalsIgnoreCase("§4§lDunkler Stab")) {
                inventory.setItem(12, new ItemStack(Material.PURPLE_STAINED_GLASS));
                inventory.setItem(13, new ItemStack(Material.PURPLE_STAINED_GLASS));
                inventory.setItem(21, getItemsAdderItem("§4§lDunkles Herz"));
                inventory.setItem(22, new ItemStack(Material.PURPLE_STAINED_GLASS));
                inventory.setItem(29, new ItemStack(Material.STICK));
            } else if(displayName.equalsIgnoreCase("§4§lScale Waffe")) {
                inventory.setItem(11, new ItemStack(Material.NETHERITE_INGOT));
                inventory.setItem(12, getItemsAdderItem("§4§lDunkler Stab"));
                inventory.setItem(13, new ItemStack(Material.NETHERITE_INGOT));
                inventory.setItem(20, getItemsAdderItem("§4§lDunkler Stab"));
                inventory.setItem(21, new ItemStack(Material.HEAVY_CORE));
                inventory.setItem(22, getItemsAdderItem("§4§lDunkler Stab"));
                inventory.setItem(29, new ItemStack(Material.NETHERITE_INGOT));
                inventory.setItem(30, getItemsAdderItem("§4§lDunkler Stab"));
                inventory.setItem(31, new ItemStack(Material.NETHERITE_INGOT));
            } else if(displayName.equalsIgnoreCase("§4§lSandsturm")) {
                inventory.setItem(12, new ItemStack(Material.PHANTOM_MEMBRANE));
                inventory.setItem(20, new ItemStack(Material.PHANTOM_MEMBRANE));
                inventory.setItem(22, new ItemStack(Material.PHANTOM_MEMBRANE));
                inventory.setItem(30, new ItemStack(Material.PHANTOM_MEMBRANE));
                inventory.setItem(21, getItemsAdderItem("§4§lWüsten Herz"));
            } else if(displayName.equalsIgnoreCase("§4§lNatur Schwert")) {
                inventory.setItem(12, new ItemStack(Material.RED_TULIP));
                inventory.setItem(13, new ItemStack(Material.ALLIUM));
                inventory.setItem(22, new ItemStack(Material.CORNFLOWER));
                inventory.setItem(29, new ItemStack(Material.STICK));
                inventory.setItem(21, getItemsAdderItem("§4§lDark Crystal"));
            }
            player.openInventory(inventory);
        }
    }

    private ItemStack getItemsAdderItem(String name) {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase(name)) {
                itemStack = i.getItemStack();
            }
        }
        return itemStack;
    }

}
