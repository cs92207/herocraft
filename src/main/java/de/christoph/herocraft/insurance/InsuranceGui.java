package de.christoph.herocraft.insurance;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.ItemBuilder;
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

public class InsuranceGui implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::versicherungen:");
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(11, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(12, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(19, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(20, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(21, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(28, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(30, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lNeue Versicherung").setCustomModelData(1000).build());
        inventory.setItem(14, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(15, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(23, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(24, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(25, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(32, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        inventory.setItem(34, new ItemBuilder(Material.STONE_AXE).setDisplayName("§4§lDeine Versicherungen").setCustomModelData(1000).build());
        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    public void onInsuranceGUIClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::versicherungen:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lNeue Versicherung")) {
            HeroCraft.getPlugin().getInsuranceManager().openNewInsuranceGUI(player);
        } else if(displayName.equalsIgnoreCase("§4§lDeine Versicherungen")) {
            HeroCraft.getPlugin().getInsuranceManager().openYourInsurancesGUI(player);
        }
    }

}
