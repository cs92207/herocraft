package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SemiUnTrustCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem Land.");
            return false;
        }
        if(land.isModeratorUUID(player.getUniqueId().toString()) || land.isOwnerUUID(player.getUniqueId().toString())) {
            if(strings.length != 1) {
                player.sendMessage(Constant.PREFIX + "§7Bitte benutze §c/semiuntrust <Spieler>§7.");
                return false;
            }
            Player target = Bukkit.getPlayer(strings[0]);
            if(target == null) {
                player.sendMessage(Constant.PREFIX + "§7Dieser Spieler ist nicht §cauf SurvivalLands§7.");
                return false;
            }
            land.unSemiTrustPlayer(target);
            player.sendMessage(Constant.PREFIX + "§7Spieler §cnicht mehr gesemitrusted§7.");
        } else {
            player.sendMessage(Constant.PREFIX + "§7Du musst Moderator oder Owner deines Landes sein.");
        }
        return false;
    }

}
