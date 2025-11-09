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
import org.jetbrains.annotations.NotNull;

public class SpecialItemsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::all_offers:");
            inventory.setItem(21, new ItemBuilder(Material.BUDDING_AMETHYST).setAmount(1).setLore("", "§7Preis: §e" + Constant.BUDDING_AMETHYIST + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(22, new ItemBuilder(Material.VILLAGER_SPAWN_EGG).setAmount(1).setLore("", "§7Preis: §e" + Constant.VILLAGER_SPAWN_EGG + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());
            inventory.setItem(23, new ItemBuilder(Material.TOTEM_OF_UNDYING).setAmount(1).setLore("", "§7Preis: §e" + Constant.TOTEM + " Coins", "", "§7(§eLinksklick§7) - Kaufen", "").build());

            player.openInventory(inventory);
        } else
            sender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
