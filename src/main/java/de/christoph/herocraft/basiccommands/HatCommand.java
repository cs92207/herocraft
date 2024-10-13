package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission("herowars.hat")) {
                if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
                    if(player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getType().equals(Material.AIR)) {
                        player.getInventory().setHelmet(player.getItemInHand());
                        player.sendMessage(Constant.PREFIX + "§7Das Item aus deiner Hand ist nun auf deinem Kopf.");
                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    } else
                        player.sendMessage(Constant.PREFIX + "§7Du hast bereits etwas auf dem §cKopf§7.");
                } else
                    player.sendMessage(Constant.PREFIX + "§7Bitte nehme ein Item in die Hand.");
            } else
                player.sendMessage(Constant.PREFIX + "§7Dazu brauchst du den §cVIP Rang§7.");
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
