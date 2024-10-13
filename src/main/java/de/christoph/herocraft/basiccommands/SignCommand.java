package de.christoph.herocraft.basiccommands;

import de.anyblocks.api.AnyBlocksAPI;
import de.anyblocks.api.permission.BuyablePermission;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SignCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(strings.length >= 1) {
                if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
                    boolean canMake = false ;
                    if(player.hasPermission("signs.*")) {
                        canMake = true;
                    }
                    BuyablePermission.Result result = AnyBlocksAPI.getInstance().getBuyablePermissionManager().getSign().usePermissionFeature(player);
                    if(result == BuyablePermission.Result.WORKED_WITH_BOUGHT || result == BuyablePermission.Result.WORKED_WITH_TIME)
                        canMake = true;
                    if(!canMake) {
                        player.sendMessage(Constant.PREFIX + "§7Du hast keine Signs mehr, oder musst warten, bis sie aufgeladen sind.");
                        return false;
                    }
                    String message = "";
                    for(int i = 0; i < strings.length; i++) {
                        message += strings[i] + " ";
                    }
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(message.replace("&", "§"));
                    lore.add("§7signiert von §e§l" + player.getName());
                    ItemStack itemStack = player.getItemInHand();
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setLore(lore);
                    itemStack.setItemMeta(meta);
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
                } else
                    player.sendMessage(Constant.PREFIX + "§7Du hast kein §cItem§7 in deiner Hand.");
            } else
                player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/signieren <Nachricht>");
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
