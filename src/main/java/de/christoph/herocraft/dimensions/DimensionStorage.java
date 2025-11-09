package de.christoph.herocraft.dimensions;


import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DimensionStorage implements CommandExecutor, Listener {

    private File storageFile;
    private FileConfiguration storageConfig;
    public static ArrayList<Player> informedPlayers = new ArrayList<>();

    public DimensionStorage() {
        storageFile = new File(HeroCraft.getPlugin().getDataFolder(), "storage.yml");
        if (!storageFile.exists()) {
            HeroCraft.getPlugin().saveResource("storage.yml", false);
        }
        storageConfig = YamlConfiguration.loadConfiguration(storageFile);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if(!informedPlayers.contains(player)) {
                player.sendMessage(Constant.PREFIX + "§7Das Dimensions Lager zu öffnen kostet jedes mal §e" + Constant.DIMENSION_CHEST_PRICE + " Coins§7. §a(Gebe den Befehl erneut ein, um fortzufahren)");
                informedPlayers.add(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        informedPlayers.remove(player);
                    }
                }, 20*120);
                return false;
            }
            if(HeroCraft.getPlugin().coin.getCoins(player) < Constant.DIMENSION_CHEST_PRICE) {
                player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
                return false;
            }
            HeroCraft.getPlugin().coin.removeMoney(player, Constant.DIMENSION_CHEST_PRICE);
            informedPlayers.remove(player);

            // Create a 9-slot inventory
            Inventory storageInventory = Bukkit.createInventory(null, 9, "§e§lDimensions Lager");

            // Load items from the storage file
            String playerUUID = player.getUniqueId().toString();
            if (storageConfig.contains(playerUUID)) {
                for (int i = 0; i < 9; i++) {
                    if (storageConfig.contains(playerUUID + ".slot" + i)) {
                        ItemStack item = storageConfig.getItemStack(playerUUID + ".slot" + i);
                        storageInventory.setItem(i, item);
                    }
                }
            }

            // Open the inventory for the player
            player.openInventory(storageInventory);
            return true;
        }

        commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("§e§lDimensions Lager")) {
            Player player = (Player) event.getPlayer();
            Inventory inventory = event.getInventory();

            // Save the inventory contents to the storage file
            String playerUUID = player.getUniqueId().toString();
            for (int i = 0; i < 9; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null) {
                    storageConfig.set(playerUUID + ".slot" + i, item);
                } else {
                    storageConfig.set(playerUUID + ".slot" + i, null);
                }
            }

            try {
                storageConfig.save(storageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
