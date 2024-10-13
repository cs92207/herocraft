package de.christoph.herocraft.basiccommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;

public class ChatClearCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender.hasPermission("herowars.chatclear")) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if(all.hasPermission("herowars.chatclear")) {
                    all.sendMessage("§e§lDer Chat wurde von §a§l" + commandSender.getName() + " §e§lgeleert.");
                } else {
                    for(int i = 0; i < 150; i++)
                        all.sendMessage("");
                    all.sendMessage("§e§lDer Chat wurde von §a§l" + commandSender.getName() + " §e§lgeleert.");
                }
            }
        }
        return false;
    }

}
