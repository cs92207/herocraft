package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class InvseeCommand implements CommandExecutor {

    private static ArrayList<Player> invPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission("herowars.invsee")) {
                if (strings.length == 1) {
                    Player target = Bukkit.getPlayer(strings[0]);
                    if (target != null) {
                        player.openInventory(target.getInventory());
                    }  else
                        player.sendMessage(Constant.PREFIX + "§7Dieser Spieler ist nicht auf dem §cServer§7.");
                } else
                    player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/inventar <Spieler>§7.");
            } else
                player.sendMessage(Constant.NO_PERMISSION);
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }
}
