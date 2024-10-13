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

public class ArmoursmithCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::ruestungsschmied:");
            inventory.setItem(19, new ItemBuilder(Material.IRON_BOOTS).setAmount(1).setLore("", "§7Preis: §e" + Constant.I_BOOTS_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(20, new ItemBuilder(Material.IRON_LEGGINGS).setAmount(1).setLore("", "§7Preis: §e" + Constant.I_LEGGINS_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(21, new ItemBuilder(Material.IRON_CHESTPLATE).setAmount(1).setLore("", "§7Preis: §e" + Constant.I_CHEST_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(22, new ItemBuilder(Material.IRON_HELMET).setAmount(1).setLore("", "§7Preis: §e" + Constant.I_HELMET_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(23, new ItemBuilder(Material.DIAMOND_BOOTS).setAmount(1).setLore("", "§7Preis: §e" + Constant.D_BOOTS_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(24, new ItemBuilder(Material.DIAMOND_LEGGINGS).setAmount(1).setLore("", "§7Preis: §e" + Constant.D_LEGGINS_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(25, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setAmount(1).setLore("", "§7Preis: §e" + Constant.D_CHEST_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(29, new ItemBuilder(Material.DIAMOND_HELMET).setAmount(1).setLore("", "§7Preis: §e" + Constant.D_HELMET_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(30, new ItemBuilder(Material.NETHERITE_BOOTS).setAmount(1).setLore("", "§7Preis: §e" + Constant.N_BOOTS_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(31, new ItemBuilder(Material.NETHERITE_LEGGINGS).setAmount(1).setLore("", "§7Preis: §e" + Constant.N_LEGGINS_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(32, new ItemBuilder(Material.NETHERITE_CHESTPLATE).setAmount(1).setLore("", "§7Preis: §e" + Constant.N_CHEST_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(33, new ItemBuilder(Material.NETHERITE_HELMET).setAmount(1).setLore("", "§7Preis: §e" + Constant.N_HELMET_PRICE + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            player.openInventory(inventory);
        } else
            sender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
