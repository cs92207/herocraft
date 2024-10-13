package de.christoph.herocraft.landpresentation;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.LocationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPresentationHoloLocationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!player.hasPermission("anyblocks.admin"))
            return false;
        if(strings.length != 1) {
            player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/setpresentationlocation <BEST|RICHEST>");
            return false;
        }
        if(strings[0].equalsIgnoreCase("best")) {
            LocationUtil.saveLocation("BestLandHoloLocation", HeroCraft.getPlugin(), player.getLocation());
            player.sendMessage(Constant.PREFIX + "§7Best Location gesetzt.");
        } else if(strings[0].equalsIgnoreCase("richest")) {
            LocationUtil.saveLocation("RichestLandHoloLocation", HeroCraft.getPlugin(), player.getLocation());
            player.sendMessage(Constant.PREFIX + "§7Richest Location gesetzt.");
        } else {
            player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/setpresentationlocation <BEST|RICHEST>");
        }
        return false;
    }

}
