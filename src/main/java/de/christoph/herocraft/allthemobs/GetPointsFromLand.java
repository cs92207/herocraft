package de.christoph.herocraft.allthemobs;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetPointsFromLand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        if(strings.length != 1) {
            commandSender.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/landpunkte <Land>§7.");
            return false;
        }
        String land = strings[0];
        commandSender.sendMessage(Constant.PREFIX + "§7Punkte von " + land + ": §e" + HeroCraft.getPlugin().getConfig().getInt("LandPoints." + land));
        return false;
    }

}
