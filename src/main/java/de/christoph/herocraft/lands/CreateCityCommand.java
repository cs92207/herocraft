package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CreateCityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(HeroCraft.getPlugin().getConfig().contains("City." + player.getUniqueId().toString() + "." + player.getWorld().getName())) {
            player.sendMessage(Constant.PREFIX + "§7Du hast in dieser Dimension bereits eine gratis Stadt §cabgeholt§7. Kaufe eine weitere mit §e/landshop§7.");
            return false;
        }
        if(player.getWorld().getName().equalsIgnoreCase("world_the_end") || player.getWorld().getName().equalsIgnoreCase("hero") || player.getWorld().getName().equalsIgnoreCase("world_nether")) {
            return false;
        }
        HeroCraft.getPlugin().getConfig().set("City." + player.getUniqueId().toString() + "." + player.getWorld().getName(), true);
        HeroCraft.getPlugin().saveConfig();
        int x = new Random().nextInt(1000);
        int z = new Random().nextInt(1000);
        int y = player.getWorld().getHighestBlockYAt(new Location(player.getWorld(), x, 0, z));
        player.teleport(new Location(player.getWorld(), x, y, z));
        player.getInventory().addItem(HeroCraft.getItemsAdderItem("§4§lRathaus"));
        player.sendTitle("§e§lPlatziere Rathaus", "§7Um die Stadt zu erstellen...");
        return false;
    }

}
