package de.christoph.herocraft.basiccommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;

public class CommandsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }


        Player player = (Player) commandSender;


        player.sendMessage("");
        player.sendMessage("§7-- §e§lAnyBlocks §7--");
        player.sendMessage("");
        player.sendMessage("§e/coins §7- Liste alle Coins Befehle auf");
        player.sendMessage("§e/ec §7- Öffne deine Endertruhe");
        player.sendMessage("§e/tpa §7- Versende eine tpa");
        player.sendMessage("");
        player.sendMessage("§7-- §e§lAnyBlocks §7--");
        player.sendMessage("");
        return false;
    }

}
