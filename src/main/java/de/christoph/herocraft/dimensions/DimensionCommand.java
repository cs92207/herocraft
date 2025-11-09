package de.christoph.herocraft.dimensions;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DimensionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Das darfst du nicht im Gefängnis. Baue Obsidian ab, oder verlasse dein Land §0(§e/land§0)§7.");
            return false;
        }
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::dimensions:");
        for(Dimension dimension : HeroCraft.getPlugin().getDimensionManager().getDimensions()) {
            inventory.addItem(dimension.getIcon());
        }
        player.openInventory(inventory);
        return false;
    }

}
