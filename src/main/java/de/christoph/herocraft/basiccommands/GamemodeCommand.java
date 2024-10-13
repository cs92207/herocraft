package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(strings.length == 1) {
                if(strings[0].equalsIgnoreCase("0")) {
                    if(player.hasPermission("gamemode.survival")) {
                        player.sendMessage(Constant.PREFIX + "§7Du bist nun im §aÜberleben Modus§7.");
                        player.setGameMode(GameMode.SURVIVAL);
                    } else
                        player.sendMessage(Constant.NO_PERMISSION);
                } else if(strings[0].equalsIgnoreCase("1")) {
                    if(player.hasPermission("gamemode.creative")) {
                        player.sendMessage(Constant.PREFIX + "§7Du bist nun im §aKreativ Modus§7.");
                        player.setGameMode(GameMode.CREATIVE);
                    } else
                        player.sendMessage(Constant.NO_PERMISSION);
                } else if(strings[0].equalsIgnoreCase("2")) {
                    if(player.hasPermission("gamemode.adventure")) {
                        player.sendMessage(Constant.PREFIX + "§7Du bist nun im §aAbenteuer Modus§7.");
                        player.setGameMode(GameMode.ADVENTURE);
                    } else
                        player.sendMessage(Constant.NO_PERMISSION);
                } else if(strings[0].equalsIgnoreCase("3")) {
                    if(player.hasPermission("gamemode.spectator")) {
                        player.sendMessage(Constant.PREFIX + "§7Du bist nun im §aZuschauer Modus§7.");
                        player.setGameMode(GameMode.SPECTATOR);
                    } else
                        player.sendMessage(Constant.NO_PERMISSION);
                }
            } else
                player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/gm <0/1/2/3>§7.");
        }
        return false;
    }

}
