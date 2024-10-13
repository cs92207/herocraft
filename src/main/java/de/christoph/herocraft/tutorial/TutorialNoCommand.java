package de.christoph.herocraft.tutorial;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialNoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        player.sendMessage(Constant.TUTORIAL_PREFIX + "§7Okay, dann will ich dich nicht länger stören!");
        HeroCraft.getPlugin().getConfig().set("Tutorial." + player.getUniqueId().toString(), true);
        HeroCraft.getPlugin().saveConfig();
        if(strings.length == 0)
            TutorialNPCCommand.openSpidermanGUI(player);
        return false;
    }

}
