package de.christoph.herocraft.utils;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ChatClickBuilder {
    private String message;
    private String commandToRun;
    private String hoverMessage;

    public ChatClickBuilder(String message, String commandToRun, String hoverMessage) {
        this.message = ChatColor.GREEN + message;
        this.commandToRun = commandToRun;
        this.hoverMessage = hoverMessage;
    }

    public void sendToPlayer(Player player) {
        Bukkit.getScheduler().runTask(HeroCraft.getPlugin(), () -> {
            Bukkit.dispatchCommand((ConsoleCommandSender) Bukkit.getConsoleSender(),
                    "tellraw " + player.getName() + " {\"text\":\"" + this.message + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + this.commandToRun + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[{\"text\":\"" + this.hoverMessage + "\",\"color\":\"green\"}]}}");
        });
    }

}
