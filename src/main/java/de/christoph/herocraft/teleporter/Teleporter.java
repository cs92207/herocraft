package de.christoph.herocraft.teleporter;

import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class Teleporter implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::teleporter:");
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand Management").build());
        inventory.setItem(12, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lTrainingsarena").build());
        inventory.setItem(14, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lVersicherungen").build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lSchmiede").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMarkt").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMöbelhaus").build());
        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::teleporter:"))
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lLand Management")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 159.5D, 128D, -234.3D, -135F, 3.3F));
        } else if(displayName.equalsIgnoreCase("§4§lTrainingsarena")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("hero"), -697.5D, 68D, -250, 0.4F, 0.1F));
        } else if(displayName.equalsIgnoreCase("§4§lVersicherungen")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 136D, 128D, -224.4, -90.1F, 8.3F));
        } else if(displayName.equalsIgnoreCase("§4§lSchmiede")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 179, 128, -177, -90.1F, 3.3F));
        } else if(displayName.equalsIgnoreCase("§4§lMarkt")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 154, 128, -216, 90.1F, 3.1F));
        } else if(displayName.equalsIgnoreCase("§4§lMöbelhaus")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 112, 128, -243, 178F, 1.8F));
        }
    }


}
