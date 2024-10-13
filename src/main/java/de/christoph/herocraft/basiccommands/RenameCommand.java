package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission("herowars.rename")) {
                if(strings.length >= 1) {
                    if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
                        String message = "";
                        for(int i = 0; i < strings.length; i++) {
                            message += strings[i] + " ";
                        }
                        ItemStack itemStack = player.getItemInHand();
                        ItemMeta meta = itemStack.getItemMeta();
                        meta.setDisplayName(message.replace("&", "§"));
                        itemStack.setItemMeta(meta);
                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
                    } else
                        player.sendMessage(Constant.PREFIX + "§7Du hast kein §cItem§7 in deiner Hand.");
                } else
                    player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/rename <Nachricht>");
            } else
                player.sendMessage(Constant.NO_PERMISSION);
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}

