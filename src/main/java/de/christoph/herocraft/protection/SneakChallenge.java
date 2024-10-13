package de.christoph.herocraft.protection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class SneakChallenge implements Listener, CommandExecutor {

    public static boolean isInChallenge = false;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!player.getName().equalsIgnoreCase("ZapfenLama"))
            return false;
        if(isInChallenge) {
            isInChallenge = false;
            player.setMaxHealth(20);
            player.setHealth(20);
        } else {
            isInChallenge = true;
            player.setHealth(1);
            player.setMaxHealth(1);
        }
        return false;
    }

}
