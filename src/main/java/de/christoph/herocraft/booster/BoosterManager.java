package de.christoph.herocraft.booster;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class BoosterManager implements Listener, CommandExecutor {

    private ArrayList<Booster> allBoosters;

    public BoosterManager() {
        this.allBoosters = new ArrayList<>();
        loadBoosters();
    }

    private void loadBoosters() {
        XPBooster xpBooster = new XPBooster();
        Bukkit.getPluginManager().registerEvents(xpBooster, HeroCraft.getPlugin());
        allBoosters.add(xpBooster);
        CoinsBooster coinsBooster = new CoinsBooster();
        Bukkit.getPluginManager().registerEvents(coinsBooster, HeroCraft.getPlugin());
        allBoosters.add(coinsBooster);
        JumpBooster jumpBooster = new JumpBooster();
        Bukkit.getPluginManager().registerEvents(jumpBooster, HeroCraft.getPlugin());
        allBoosters.add(jumpBooster);
        SpeedBooster speedBooster = new SpeedBooster();
        Bukkit.getPluginManager().registerEvents(speedBooster, HeroCraft.getPlugin());
        allBoosters.add(speedBooster);
        BreakBooster breakBooster = new BreakBooster();
        Bukkit.getPluginManager().registerEvents(breakBooster, HeroCraft.getPlugin());
        allBoosters.add(breakBooster);
        DropBooster dropBooster = new DropBooster();
        Bukkit.getPluginManager().registerEvents(dropBooster, HeroCraft.getPlugin());
        allBoosters.add(dropBooster);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 9 * 5, ":offset_-16::booster:");
        for(Booster all : allBoosters) {
            inventory.addItem(all.getBoosterIcon(player));
        }
        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::booster:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        Booster booster = null;
        for(Booster allBooster : allBoosters) {
            if(allBooster.getBoosterIcon(player).getItemMeta().getDisplayName().equalsIgnoreCase(displayName)) {
                booster = allBooster;
            }
        }
        if(booster == null)
            return;
        booster.activateBooster(player);
    }

    public ArrayList<Booster> getAllBoosters() {
        return allBoosters;
    }

}
