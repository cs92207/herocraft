package de.christoph.herocraft.lands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LandInvitationAcceptCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!Goverment.invitedPlayers.containsKey(player))
            return false;
        Land land = Goverment.invitedPlayers.get(player);
        land.addMember(player);
        player.sendMessage(Constant.PREFIX + "§7Du bist nun Teil des Landes §e§l" + land.getName() + ".");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        Goverment.invitedPlayers.remove(player);
        for(Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(Constant.PREFIX + "§e" + player.getName() + "§7 ist dem Land §e" + land.getName() + "§7 beigetreten.");
        }
        return false;
    }

}
