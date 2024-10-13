package de.christoph.herocraft.voteday;

import de.anyblocks.api.AnyBlocksAPI;
import de.anyblocks.api.permission.BuyablePermission;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartVoteDayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        boolean canDo = false;
        if(player.hasPermission("votedays.*")) {
            canDo = true;
        }
        BuyablePermission.Result result = AnyBlocksAPI.getInstance().getBuyablePermissionManager().getVoteDay().usePermissionFeature(player);
        if(result == BuyablePermission.Result.WORKED_WITH_BOUGHT || result == BuyablePermission.Result.WORKED_WITH_TIME)
            canDo = true;
        if(!canDo) {
            player.sendMessage(Constant.PREFIX + "§7Du hast keine VoteDays oder musst warten, bis sie aufgeladen sind.");
            return false;
        }
        HeroCraft.getPlugin().getVoteDayManager().startVoteDay(player);
        return false;
    }

}
