package de.christoph.herocraft.challenges;

import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChangeChallengeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("herowars.admin")) {
            commandSender.sendMessage("§e§lHeroWars §7§l| §7Dazu hast du keine §cRechte§7.");
            return false;
        }
        ProtectionListener.createChallenge();
        commandSender.sendMessage("§e§lHeroWars §7§l| §7Challenge geändert!");
        return false;
    }

}
