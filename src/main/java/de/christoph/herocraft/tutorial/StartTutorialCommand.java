package de.christoph.herocraft.tutorial;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartTutorialCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        for(int i = 0; i < 50; i++)
            player.sendMessage("");
        player.sendMessage(Constant.TUTORIAL_PREFIX + "§7Okay, kein Problem. Deine ersten Schritte auf HeroCraft:");
        player.sendMessage("");
        player.sendMessage("§e1. §7Grundequipment farmen");
        player.sendMessage("");
        player.sendMessage("§e2. §7Eigenes Land claimen, oder Land beitreten (§e/land§7)");
        player.sendMessage("");
        player.sendMessage("§e3. §7Land aufbauen und Spaß haben ;)");
        player.sendMessage("");
        player.sendMessage("§e§lBonus: §7Besuche Orte wie den Markt oder unsere Farmwelt um mehr Features zu entdecken. Alles wird dir beim spielen erklärt werden!");
        player.sendMessage("§aZudem kannst du mich jederzeit für Hilfe anklicken!");
        player.sendMessage("");
        TextComponent textComponent = new TextComponent("§a§l(Mehr Infos)");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/startmehrinfos"));
        player.spigot().sendMessage(textComponent);
        HeroCraft.getPlugin().getConfig().set("Tutorial." + player.getUniqueId().toString(), true);
        HeroCraft.getPlugin().saveConfig();
        return false;
    }

}
