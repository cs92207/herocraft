package de.christoph.herocraft.lands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LandInvitationDenyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!Goverment.invitedPlayers.containsKey(player))
            return false;
        Goverment.invitedPlayers.remove(player);
        player.sendMessage(Constant.PREFIX + "§7Einladung abgelehnt.");
        return false;
    }

}
