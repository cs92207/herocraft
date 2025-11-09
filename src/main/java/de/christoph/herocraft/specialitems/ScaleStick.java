package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ScaleStick implements Listener {

    public static HashMap<Player, Double> scaleSize = new HashMap<>();
    public static ArrayList<Player> cooldownPlayers = new ArrayList<>();


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getInventory().getItemInMainHand().hasItemMeta())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        String displayName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        if(!displayName.equalsIgnoreCase("§4§lScale Waffe"))
            return;
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if(cooldownPlayers.contains(player)) {
                player.sendMessage(Constant.PREFIX + "§7Du hast noch §cCooldown§7.");
                return;
            }
            double scale = 1.4;
            if(scaleSize.containsKey(player)) {
                scale = scaleSize.get(player);
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "entitysize player " + player.getName() + " " + scale);
            cooldownPlayers.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "entitysize player " + player.getName() + " 1");
                }
            }, 20*10);

            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    cooldownPlayers.remove(player);
                }
            }, 20*20);

        } else if(event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "entitysize player " + player.getName() + " " + 1);
        } else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(!scaleSize.containsKey(player)) {
                scaleSize.put(player, 1.4);
                player.sendMessage(Constant.PREFIX + "§7Dein Scale beträgt nun §a1.4");
                return;
            }
            scaleSize.put(player, scaleSize.get(player) + 0.2);
            player.sendMessage(Constant.PREFIX + "§7Dein Scale beträgt nun §a" + Math.round(scaleSize.get(player) * 10.0) / 10.0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "entitysize player " + player.getName() + " 1");
                }
            }, 20*10);
        } else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if(!scaleSize.containsKey(player)) {
                scaleSize.put(player, 0.6);
                player.sendMessage(Constant.PREFIX + "§7Dein Scale beträgt nun §a0.6");
                return;
            }
            scaleSize.put(player, scaleSize.get(player) - 0.2);
            player.sendMessage(Constant.PREFIX + "§7Dein Scale beträgt nun §a" + Math.round(scaleSize.get(player) * 10.0) / 10.0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "entitysize player " + player.getName() + " 1");
                }
            }, 20*10);
        }
    }

}
