package de.christoph.herocraft.caseopening;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        if(strings.length != 1) {
            player.sendMessage(Constant.PREFIX + "§7Nutze §c/setcasewinnings <normal|premium>§7.");
            return false;
        }
        CaseType caseType = CaseType.fromArgument(strings[0]);
        if(caseType == null) {
            player.sendMessage(Constant.PREFIX + "§7Unbekannter Kistentyp. Nutze §cnormal §7oder §cpremium§7.");
            return false;
        }
        FileConfiguration config = plugin.getConfig();
        List<ItemStack> currentItems = loadInventory(caseType);
        ItemStack[] inventoryContents = player.getInventory().getContents();
        currentItems.addAll(Arrays.asList(inventoryContents));
        int n = 0;
        for (ItemStack i : currentItems) {
            config.set(caseType.getConfigPath() + ".slot" + n, i);
            n++;
        }
        config.set(caseType.getConfigPath() + ".max_chest_items", n);
        plugin.saveConfig();
        player.sendMessage(Constant.PREFIX + "§7" + caseType.getSingularDisplayName() + " gespeichert.");
        return false;
    }

    public static List<ItemStack> loadInventory() {
        return loadInventory(CaseType.NORMAL);
    }

    public static List<ItemStack> loadInventory(CaseType caseType) {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();

        int maxItems = config.getInt(caseType.getConfigPath() + ".max_chest_items");
        List<ItemStack> inventoryContents = new ArrayList<>();
        for (int i = 0; i < maxItems; i++) {
            ItemStack item = config.getItemStack(caseType.getConfigPath() + ".slot" + i);
            if (item != null) {
                inventoryContents.add(item);
            }
        }

        return inventoryContents;
    }

}
