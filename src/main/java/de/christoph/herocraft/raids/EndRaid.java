package de.christoph.herocraft.raids;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndRaid implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Raid raid = HeroCraft.getPlugin().raidManager.getRaidFromPlayer(player);
        if(raid == null)
            return false;
        raid.finishRaidFailed();
        return false;
    }

}
