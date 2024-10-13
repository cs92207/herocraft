package de.christoph.herocraft.afksystem;

import de.christoph.herocraft.utils.Constant;
import jdk.jfr.Enabled;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class AdminAFK implements CommandExecutor, Listener {

    public static ArrayList<Player> adminAFKPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!player.hasPermission("anyblocks.adminafk"))
            return false;
        if(adminAFKPlayers.contains(player)) {
            adminAFKPlayers.remove(player);
            player.sendMessage(Constant.PREFIX + "§7Admin-AFK §causgeschaltet§7.");
        } else {
            adminAFKPlayers.add(player);
            player.sendMessage(Constant.PREFIX + "§7Admin-AFK §aaktiviert§7.");
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for(Player a : adminAFKPlayers) {
            a.playSound(a.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(!event.isCancelled()) {
            for(Player a : adminAFKPlayers) {
                a.playSound(a.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }
    }

}
