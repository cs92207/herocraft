package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FreeAnyCoinsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        player.sendMessage(Constant.PREFIX + "§7Schaue dir eine kurze Werbung an und erhalte §e§lJeden Tag kostenlose AnyCoins§7! Folge einfach dem Link - §cDanke für deine Unterstützung <3");
        TextComponent text = new TextComponent("§0(§e§lKlick§0)");
        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://pauen-it.de/anyblocks-rewards/"));
        player.spigot().sendMessage(text);
        return false;
    }

}
