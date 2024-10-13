package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SpawnCommand implements CommandExecutor {

    public static ArrayList<Player> spawnPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            spawnPlayers.add(player);
            player.sendMessage(Constant.PREFIX + "§7Teleportation in §a3 Sekunden§7.");
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if(spawnPlayers.contains(player)) {
                        player.teleport(new Location(Bukkit.getWorld("world"), 172.55, 132, -216.5));
                        spawnPlayers.remove(player);
                    }
                }
            }, 20*3);
        }
        return false;
    }

}
