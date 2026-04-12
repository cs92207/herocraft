package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

public class FarmworldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        int x = new Random().nextInt(3500);
        int z = new Random().nextInt(3500);
        int y = Bukkit.getWorld("farmworld").getHighestBlockYAt(x, z);
        y++;
        player.teleport(new Location(Bukkit.getWorld("farmworld"), x, y, z));
        player.sendMessage(Constant.PREFIX + "§7Du wurdest §azufällig §7in die Farmwelt teleportiert.");
        return false;
    }

}
