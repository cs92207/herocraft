package de.christoph.herocraft.allthemobs;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class LandMobsCommand implements CommandExecutor, Listener {

    public static HashMap<Player, Integer> pagePlayers = new HashMap<>();
    public static HashMap<Player, String> landPlayers = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(strings.length != 1) {
            player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/landmobs <LAND>§7.");
            return false;
        }
        String land = strings[0];
        openLandMobInventory(land, player, 0);
        return false;
    }

    @EventHandler
    public void onLandMobInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase("§4§lMobs vom Land"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§cZurück")) {
            if(pagePlayers.get(player) == 0) {
                return;
            }
            openLandMobInventory(landPlayers.get(player), player, pagePlayers.get(player) - 1);
        } else if(displayName.equalsIgnoreCase("§cWeiter")) {
            openLandMobInventory(landPlayers.get(player), player, pagePlayers.get(player) + 1);
        }
    }

    public void openLandMobInventory(String land, Player player, int page) {
        pagePlayers.put(player, page);
        landPlayers.put(player, land);
        Inventory inventory = Bukkit.createInventory(null, 9*6, "§4§lMobs vom Land");
        if(!HeroCraft.getPlugin().getConfig().contains("LandKilledEntities_" + land)) {
            player.openInventory(inventory);
            return;
        }
        List<String> landKilledEnities = HeroCraft.getPlugin().getConfig().getStringList("LandKilledEntities_" + land);
        int start = page * 45;
        int end = start + 45;
        int n = 0;
        for(int i = start; i <= end; i++) {
            if(landKilledEnities.size() <= i)
                break;
            String entityType = landKilledEnities.get(i).split("_")[3];
            inventory.addItem(new ItemBuilder(Material.PAPER).setDisplayName("§a§l" + entityType).setLore("", "§7Anzahl: §e" + HeroCraft.getPlugin().getConfig().getInt("LAND_KILLED_" + land + "_" + entityType)).build());
            n++;
        }
        inventory.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("§cZurück").build());
        inventory.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("§cWeiter").build());
        player.openInventory(inventory);
    }

}
