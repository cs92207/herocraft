package de.christoph.herocraft.teleporter;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Teleporter implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Das darfst du nicht im Gefängnis. Baue Obsidian ab, oder verlasse dein Land §0(§e/land§0)§7.");
            return false;
        }
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::teleporter:");
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand Management").build());
        inventory.setItem(12, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lTrainingsarena").build());
        inventory.setItem(14, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lVersicherungen").build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lRezepte").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMarkt").build());
        inventory.setItem(31, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDimensionen").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMöbelhaus").build());
        player.openInventory(inventory);
        return false;
    }

    public static ItemStack getTeleporterItem() {
        ItemStack itemStack = null;
        for(CustomStack customStack : ItemsAdder.getAllItems()) {
            if(customStack.getDisplayName().contains("§4§lNavigator")) {
                itemStack = customStack.getItemStack();
            }
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§4§lTeleporter");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Rechtsklicke, um das Teleporter");
        lore.add("§7Menü zu öffnen");
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onTeleporterItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getInventory().getItemInMainHand().hasItemMeta())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        String displayName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        if(!displayName.equalsIgnoreCase("§4§lTeleporter"))
            return;
        event.setCancelled(true);
        if(HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Das darfst du nicht im Gefängnis. Baue Obsidian ab, oder verlasse dein Land §0(§e/land§0)§7.");
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::teleporter:");
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand Management").build());
        inventory.setItem(12, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lTrainingsarena").build());
        inventory.setItem(14, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lVersicherungen").build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lRezepte").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMarkt").build());
        inventory.setItem(31, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDimensionen").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMöbelhaus").build());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::teleporter:"))
            return;
        event.setCancelled(true);
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Das darfst du nicht im Gefängnis. Baue Obsidian ab, oder verlasse dein Land §0(§e/land§0)§7.");
            return;
        }
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lLand Management")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 137.5, 82.5, -146.5D, 180F, 3.5F));
            HeroCraft.getPlugin().getStatisticsManager().markTeleporterUsed(player.getUniqueId());
        } else if(displayName.equalsIgnoreCase("§4§lTrainingsarena")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 71.5, 77.5, -144.5, 90F, 1.2F));
            HeroCraft.getPlugin().getStatisticsManager().markTeleporterUsed(player.getUniqueId());
        } else if(displayName.equalsIgnoreCase("§4§lVersicherungen")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 96.5, 87.5, -217.5D, 180F, 3.5F));
            HeroCraft.getPlugin().getStatisticsManager().markTeleporterUsed(player.getUniqueId());
        } else if(displayName.equalsIgnoreCase("§4§lRezepte")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 175, 62.5, -165.5, 179F, 6.7F));
            HeroCraft.getPlugin().getStatisticsManager().markTeleporterUsed(player.getUniqueId());
        } else if(displayName.equalsIgnoreCase("§4§lMarkt")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 191, 69.5, -226, -91.1F, -0.9F));
            HeroCraft.getPlugin().getStatisticsManager().markTeleporterUsed(player.getUniqueId());
        } else if(displayName.equalsIgnoreCase("§4§lDimensionen")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 175, 78.5, -173.8, 0.4F, 0.7F));
            HeroCraft.getPlugin().getStatisticsManager().markTeleporterUsed(player.getUniqueId());
        } else if(displayName.equalsIgnoreCase("§4§lMöbelhaus")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.teleport(new Location(Bukkit.getWorld("world"), 161.5, 80.5, -258.5, 180, -0.2F));
            HeroCraft.getPlugin().getStatisticsManager().markTeleporterUsed(player.getUniqueId());
        }
    }


}
