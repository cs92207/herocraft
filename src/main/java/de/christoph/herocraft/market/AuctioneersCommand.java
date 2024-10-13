package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AuctioneersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::auctioneer_main:");
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(11, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(12, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(19, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(20, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(21, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(28, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());
        inventory.setItem(30, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDeine Angebote").build());

        inventory.setItem(14, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(15, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(23, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(24, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(25, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(32, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        inventory.setItem(34, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAlle Angebote").build());
        player.openInventory(inventory);
        return false;
    }

}
