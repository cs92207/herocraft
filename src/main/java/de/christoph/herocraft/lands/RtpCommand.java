package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class RtpCommand implements CommandExecutor {

    public static ArrayList<Player> cooldownPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!strings[0].equalsIgnoreCase("1288"))
            return false;
        if(cooldownPlayers.contains(player)) {
            player.sendMessage(Constant.PREFIX + "§7Warte ein paar Sekunden, bis du dies erneut tun kannst.");
            return false;
        }
        Random random = new Random();
        int x = random.nextInt(1000);
        int z = random.nextInt(1000);
        int y = Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x, 1, z));
        cooldownPlayers.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                cooldownPlayers.remove(player);
            }
        }, 20*10);
        player.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
        return false;
    }

}
