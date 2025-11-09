package de.christoph.herocraft.allthemobs;

import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MobListCommand implements CommandExecutor, Listener {

    private final int PAGE_SIZE = 45;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lMob Liste");
        inventory.setItem(19, new ItemBuilder(Material.GRAY_DYE).setDisplayName("§7§lNormale Mobs").build());
        inventory.setItem(22, new ItemBuilder(Material.PURPLE_DYE).setDisplayName("§5§lSeltene Mobs").build());
        inventory.setItem(25, new ItemBuilder(Material.YELLOW_DYE).setDisplayName("§e§lLegendäre Mobs").build());
        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    public void onShowInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        if(event.getCurrentItem() == null)
            return;
        String title = event.getView().getTitle();
        if(title.equalsIgnoreCase("§7§lNormale Mobs") || title.equalsIgnoreCase("§5§lSeltene Mobs") || title.equalsIgnoreCase("§e§lLegendäre Mobs"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerOpenListInventory(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase("§4§lMob Liste"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§7§lNormale Mobs")) {
            Inventory inventory = Bukkit.createInventory(null, 9*6, "§7§lNormale Mobs");
            for (Map.Entry<EntityType, Integer> entry : AllTheMobsManager.normaleMobs.entrySet()) {
                EntityType type = entry.getKey();
                int points = entry.getValue();
                inventory.addItem(new ItemBuilder(Material.PAPER).setDisplayName("§7§l" + type.toString()).setLore("", "§7Punkte: §e" + points).build());
            }
            player.openInventory(inventory);
        } else if(displayName.equalsIgnoreCase("§5§lSeltene Mobs")) {
            Inventory inventory = Bukkit.createInventory(null, 9*6, "§5§lSeltene Mobs");
            for (Map.Entry<EntityType, Integer> entry : AllTheMobsManager.selteneMobs.entrySet()) {
                EntityType type = entry.getKey();
                int points = entry.getValue();
                inventory.addItem(new ItemBuilder(Material.PAPER).setDisplayName("§5§l" + type.toString()).setLore("", "§7Punkte: §e" + points).build());
            }
            player.openInventory(inventory);
        } else if(displayName.equalsIgnoreCase("§e§lLegendäre Mobs")) {
            Inventory inventory = Bukkit.createInventory(null, 9*6, "§e§lLegendäre Mobs");
            for (Map.Entry<EntityType, Integer> entry : AllTheMobsManager.legendaereMobs.entrySet()) {
                EntityType type = entry.getKey();
                int points = entry.getValue();
                inventory.addItem(new ItemBuilder(Material.PAPER).setDisplayName("§e§l" + type.toString()).setLore("", "§7Punkte: §e" + points).build());
            }
            player.openInventory(inventory);
        }
    }

}
