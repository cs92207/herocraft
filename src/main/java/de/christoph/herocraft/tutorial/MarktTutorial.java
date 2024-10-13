package de.christoph.herocraft.tutorial;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarktTutorial implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        for(int i = 0; i < 50; i++) {
            player.sendMessage("");
        }
        player.sendMessage(Constant.TUTORIAL_PREFIX + "§7Am Markt kannst du verschiedene Items kaufen und sogar bei einigen Ständen auch verkaufen. (§eMit rechtsklick kaufen, mit linksklick verkaufen§7)");
        player.sendMessage("");
        player.sendMessage("§7Zudem haben wir unser Möbelhaus §eHeroKea§7. Hier kannst du custom Mobel / Items und Dekorationen kaufen. Schau gerne mal vorbei!");
        HeroCraft.getPlugin().getConfig().set("Market." + player.getUniqueId().toString(), true);
        HeroCraft.getPlugin().saveConfig();
        return false;
    }

}
