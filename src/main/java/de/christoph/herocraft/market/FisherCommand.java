package de.christoph.herocraft.market;

import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FisherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::fisher:");
            inventory.setItem(20, new ItemBuilder(Material.COD).setAmount(10).setLore("", "§7Preis: §e" + Constant.COD_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(21, new ItemBuilder(Material.SALMON).setAmount(10).setLore("", "§7Preis: §e" + Constant.SALMON_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(22, new ItemBuilder(Material.TROPICAL_FISH).setAmount(10).setLore("", "§7Preis: §e" + Constant.TROPICAL_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(23, new ItemBuilder(Material.COOKED_COD).setAmount(10).setLore("", "§7Preis: §e" + Constant.COOKED_COD_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(24, new ItemBuilder(Material.COOKED_SALMON).setAmount(10).setLore("", "§7Preis: §e" + Constant.COOKED_SALMON_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            player.openInventory(inventory);
        } else
            sender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
