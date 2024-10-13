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

public class ButcherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::schlachter:");
            inventory.setItem(19, new ItemBuilder(Material.PORKCHOP).setAmount(10).setLore("", "§7Preis: §e" + Constant.PORKCHOP_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(20, new ItemBuilder(Material.BEEF).setAmount(10).setLore("", "§7Preis: §e" + Constant.BEEF_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(21, new ItemBuilder(Material.CHICKEN).setAmount(10).setLore("", "§7Preis: §e" + Constant.CHICKEN_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(22, new ItemBuilder(Material.RABBIT).setAmount(10).setLore("", "§7Preis: §e" + Constant.RABBIT_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(23, new ItemBuilder(Material.MUTTON).setAmount(10).setLore("", "§7Preis: §e" + Constant.MUTTON_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(24, new ItemBuilder(Material.COOKED_PORKCHOP).setAmount(10).setLore("", "§7Preis: §e" + Constant.COOKED_PORKCHOP_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(25, new ItemBuilder(Material.COOKED_BEEF).setAmount(10).setLore("", "§7Preis: §e" + Constant.COOKED_BEEF_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(30, new ItemBuilder(Material.COOKED_CHICKEN).setAmount(10).setLore("", "§7Preis: §e" + Constant.COOKED_CHICKEN_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(31, new ItemBuilder(Material.COOKED_RABBIT).setAmount(10).setLore("", "§7Preis: §e" + Constant.COOKED_RABBIT_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(32, new ItemBuilder(Material.COOKED_MUTTON).setAmount(10).setLore("", "§7Preis: §e" + Constant.COOKED_MUTTON_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            player.openInventory(inventory);
        } else
            sender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
