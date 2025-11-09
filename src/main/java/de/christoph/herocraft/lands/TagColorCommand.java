package de.christoph.herocraft.lands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TagColorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        StringBuilder builder = new StringBuilder();

        for (ChatColor color : ChatColor.values()) {
            // Filtere ggf. nur Farben, keine Formatierungen wie BOLD, UNDERLINE etc.
            if (color.isColor()) {
                builder.append(color) // fügt die Farbcodierung hinzu
                        .append(color.name().toLowerCase()) // fügt den Farbnamen hinzu
                        .append(" "); // Leerzeichen zwischen Farben
            }
        }

        String message = builder.toString();
        commandSender.sendMessage(Constant.PREFIX + message);
        return false;
    }

}
