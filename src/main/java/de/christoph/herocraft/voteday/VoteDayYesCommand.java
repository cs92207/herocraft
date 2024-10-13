package de.christoph.herocraft.voteday;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteDayYesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if((commandSender instanceof Player)) {
            Player player = (Player) commandSender;
            if(!HeroCraft.getPlugin().getVoteDayManager().isInVoteDay()) {
                player.sendMessage(Constant.PREFIX + "§7Es läuft keine §cAbstimmung§7.");
                return false;
            }
            HeroCraft.getPlugin().getVoteDayManager().getCurrentVoteDay().sayYes(player);
        }
        return false;
    }

}
