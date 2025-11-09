package de.christoph.herocraft.caseopening;

import com.sun.tools.jconsole.JConsoleContext;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetCaseOpeningCommand implements CommandExecutor {

    private HeroCraft plugin;

    public SetCaseOpeningCommand(HeroCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if(!player.hasPermission("anyblocks.admin")) {
            return false;
        }
        FileConfiguration config = plugin.getConfig();
        List<ItemStack> currentItems = loadInventory();
        ItemStack[] inventoryContents = player.getInventory().getContents();
        currentItems.addAll(Arrays.asList(inventoryContents));
        int n = 0;
        for (ItemStack i : currentItems) {
            config.set("caseinventory.slot" + n, i);
            n++;
        }
        config.set("max_chest_items", n);
        plugin.saveConfig();
        player.sendMessage(Constant.PREFIX + "§7Kiste gespeichert");
        return false;
    }

    public static List<ItemStack> loadInventory() {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();

        int maxItems = config.getInt("max_chest_items");
        List<ItemStack> inventoryContents = new ArrayList<>();
        for (int i = 0; i < maxItems; i++) {
            ItemStack item = config.getItemStack("caseinventory.slot" + i);
            if (item != null) {
                inventoryContents.add(item);
            }
        }

        return inventoryContents;
    }

}
