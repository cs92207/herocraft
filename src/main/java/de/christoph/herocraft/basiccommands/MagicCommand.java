package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MagicCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission("herowars.enchant")) {
                if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
                    ItemStack itemStack = player.getItemInHand();
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if(strings.length == 2) {
                        if(strings[0].equalsIgnoreCase("luck")) {
                            itemMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, Integer.parseInt(strings[1]), true);
                            itemStack.setItemMeta(itemMeta);
                            player.sendMessage(Constant.PREFIX + "§7Das Item aus deiner Hand wurde §averzaubert§7.");
                            player.getInventory().clear(player.getInventory().getHeldItemSlot());
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
                        } else if(strings[0].equalsIgnoreCase("sharpness")) {
                            itemMeta.addEnchant(Enchantment.SHARPNESS, Integer.parseInt(strings[1]), true);
                            itemStack.setItemMeta(itemMeta);
                            player.sendMessage(Constant.PREFIX + "§7Das Item aus deiner Hand wurde §averzaubert§7.");
                            player.getInventory().clear(player.getInventory().getHeldItemSlot());
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
                        } else if(strings[0].equalsIgnoreCase("efficiency")) {
                            itemMeta.addEnchant(Enchantment.EFFICIENCY, Integer.parseInt(strings[1]), true);
                            itemStack.setItemMeta(itemMeta);
                            player.sendMessage(Constant.PREFIX + "§7Das Item aus deiner Hand wurde §averzaubert§7.");
                            player.getInventory().clear(player.getInventory().getHeldItemSlot());
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
                        } else
                            player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige §cVerzauberung§7.");
                    } else
                        player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/verzaubern <Verzauberung> <Stärke>");
                } else
                    player.sendMessage(Constant.PREFIX + "§7Bitte nehme ein §cItem §7in die Hand.");
            } else
                player.sendMessage(Constant.NO_PLAYER);
        }
        return false;
    }

}
