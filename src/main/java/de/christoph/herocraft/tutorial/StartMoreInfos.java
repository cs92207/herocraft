package de.christoph.herocraft.tutorial;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartMoreInfos implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        for(int i = 0; i < 50; i++) {
            player.sendMessage("");
        }
        player.sendMessage(Constant.TUTORIAL_PREFIX + "§7Hier ein paar weitere Infos:");
        player.sendMessage("");
        player.sendMessage("§7Grundsätzlich ist HeroCraft ein §eFreebuild Server§7. Das heißt, du kannst dir überall deine eigene Base bauen, Coins erwirtschaften und dein Imperium errichten. Wir haben jedoch noch ein §eLand System §7(§e/land§7). Hiermit kannst du dein eigenes Land claimen und mit anderen Spielern ein Land aufbauen. Zudem bist du dann anführer einer Superhelden-Armee.");
        player.sendMessage("");
        player.sendMessage("§7Es gibt noch viele weitere Features. Diese wirst du im Laufe des Spiels kennenlernen! Viel Spaß!");
        return false;
    }

}
