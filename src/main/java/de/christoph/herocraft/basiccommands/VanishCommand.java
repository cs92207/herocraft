package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class VanishCommand implements CommandExecutor {

    public static ArrayList<Player> vanishPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission("herowars.vanish")) {
                if(vanishPlayers.contains(player)) {
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        all.showPlayer(player);
                    }
                    player.sendMessage(Constant.PREFIX + "§7Du bist nun nicht mehr §cunsichtbar§7.");
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        all.sendMessage("§e§lAnyBlocks §7§l| §7" + player.getName() + " hat SurvivalLands §abetreten§7.");
                    }
                    vanishPlayers.remove(player);
                } else {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if(!all.hasPermission("herowars.vanish.show"))
                                all.hidePlayer(player);
                        }
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            all.sendMessage("§e§lAnyBlocks §7§l| §7" + player.getName() + " hat SurvivalLands §cverlassen§7.");
                        }
                        player.sendMessage(Constant.PREFIX + "§7Du bist nun §aunsichtbar§7.");
                        vanishPlayers.add(player);
                }
            } else
                player.sendMessage(Constant.NO_PERMISSION);
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
