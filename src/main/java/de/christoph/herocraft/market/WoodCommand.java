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

public class WoodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::holzfaeller:");
            inventory.setItem(19, new ItemBuilder(Material.OAK_LOG).setAmount(16).setLore("", "§7Preis: §e" + Constant.OAK_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(20, new ItemBuilder(Material.SPRUCE_LOG).setAmount(16).setLore("", "§7Preis: §e" + Constant.SPRUCE_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(21, new ItemBuilder(Material.BIRCH_LOG).setAmount(16).setLore("", "§7Preis: §e" + Constant.BIRCH_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(22, new ItemBuilder(Material.JUNGLE_LOG).setAmount(16).setLore("", "§7Preis: §e" + Constant.JUNGLE_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(23, new ItemBuilder(Material.ACACIA_LOG).setAmount(16).setLore("", "§7Preis: §e" + Constant.ACACIA_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(24, new ItemBuilder(Material.DARK_OAK_LOG).setAmount(16).setLore("", "§7Preis: §e" + Constant.DARK_OAK_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            inventory.setItem(25, new ItemBuilder(Material.MANGROVE_LOG).setAmount(16).setLore("", "§7Preis: §e" + Constant.MANGROVE_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "§7(§eRechtsklick§7) - Verkaufen").build());
            player.openInventory(inventory);
        } else
            sender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
