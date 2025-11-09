package de.christoph.herocraft.raids;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class KillAllTroopsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission("anyblocks.admin"))
            return false;
        for (Entity entity : Bukkit.getWorld("world").getEntities()) {
            if (entity.getCustomName() != null && entity.getCustomName().contains("§e§lTruppe §0§c§lLvl. ")) {
                entity.remove();
            }
        }
        return false;
    }

}
