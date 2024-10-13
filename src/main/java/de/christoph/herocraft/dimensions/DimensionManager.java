package de.christoph.herocraft.dimensions;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class DimensionManager implements Listener {

    private ArrayList<Dimension> dimensions;

    private int taskID;

    public DimensionManager() {
        registerDimensions();
        startTimer();
    }

    private void startTimer() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for(Dimension dimension : dimensions) {
                    dimension.onTick();
                }
            }
        }, 20, 20*10);
    }

    private void registerDimensions() {
        dimensions = new ArrayList<>();
        NormalWorld normalWorld = new NormalWorld();
        Bukkit.getPluginManager().registerEvents(normalWorld, HeroCraft.getPlugin());
        dimensions.add(normalWorld);
        Nether nether = new Nether();
        Bukkit.getPluginManager().registerEvents(nether, HeroCraft.getPlugin());
        dimensions.add(nether);
        End end = new End();
        Bukkit.getPluginManager().registerEvents(end, HeroCraft.getPlugin());
        dimensions.add(end);
        NatureAdventure natureAdventure = new NatureAdventure();
        Bukkit.getPluginManager().registerEvents(natureAdventure, HeroCraft.getPlugin());
        dimensions.add(natureAdventure);
        HotDessert hotDessert = new HotDessert();
        Bukkit.getPluginManager().registerEvents(hotDessert, HeroCraft.getPlugin());
        dimensions.add(hotDessert);
        BlackDessert blackDessert = new BlackDessert();
        Bukkit.getPluginManager().registerEvents(blackDessert, HeroCraft.getPlugin());
        dimensions.add(blackDessert);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::dimensions:"))
            return;
        event.setCancelled(true);
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        String name = displayName.substring(4);
        Dimension dimension = getDimensionByName(name);
        player.closeInventory();
        player.sendMessage(Constant.PREFIX + "§7Du wirst Random in die Dimension §a" + name + "§7 teleportiert.");
        if(dimension.getWorld().equalsIgnoreCase("world_nether")) {
            player.teleport(new Location(Bukkit.getWorld(dimension.getWorld()), 32, 52, 0));
            return;
        }
        if(dimension.getWorld().equalsIgnoreCase("world_the_end")) {
            player.teleport(new Location(Bukkit.getWorld(dimension.getWorld()), 47, 60, 28));
            return;
        }
        Random random = new Random();
        int x = random.nextInt(350);
        int z = random.nextInt(350);
        int y = Bukkit.getWorld(dimension.getWorld()).getHighestBlockYAt(new Location(Bukkit.getWorld(dimension.getWorld()), x, 1, z));
        player.teleport(new Location(Bukkit.getWorld(dimension.getWorld()), x, y, z));
    }

    @Nullable
    public Dimension getDimensionByName(String name) {
        for(Dimension dimension : dimensions) {
            if(dimension.getName().equalsIgnoreCase(name))
                return dimension;
        }
        return null;
    }

    public ArrayList<Dimension> getDimensions() {
        return dimensions;
    }

    public int getTaskID() {
        return taskID;
    }

}
