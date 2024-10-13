package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class TpaCommand implements CommandExecutor {

    public static HashMap<Player, Player> tpaPlayers = new HashMap<>(); // <Angefragter, Sender>
    private static ArrayList<Player> informedPlayer = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(strings.length == 1) {
                if(strings[0].equalsIgnoreCase("annehmen") || strings[0].equalsIgnoreCase("accept")) {
                    if(tpaPlayers.containsKey(player)) {
                        if(tpaPlayers.get(player) != null) {
                            tpaPlayers.get(player).teleport(player);
                        } else {
                            player.sendMessage(Constant.PREFIX + "§7Der Spieler, der dir eine Anfrage gesendet hat ist nun Offline.");
                            tpaPlayers.remove(player);
                        }
                    } else
                        player.sendMessage(Constant.PREFIX + "§7Du hast keine offene §cTeleportations Anfrage§7.");
                } else {
                    if(informedPlayer.contains(player)) {
                        if(HeroCraft.getPlugin().coin.getCoins(player) >= Constant.TPA_PRICE) {
                            informedPlayer.remove(player);
                            HeroCraft.getPlugin().coin.removeMoney(player, Constant.TPA_PRICE);
                            Player target = Bukkit.getPlayer(strings[0]);
                            if(target != null) {
                                if(!tpaPlayers.containsKey(target)) {
                                    player.sendMessage(Constant.PREFIX + "§7Du hast eine Anfrage gestellt. Sie läuft in §e20 Sekunden§7 ab.");
                                    target.sendMessage(Constant.PREFIX + "§7Der Spieler §e" + player.getName() + "§7 möchte sich zu dir teleportiern. Benutze §e/tpa annehmen §7um dies zu erlauben. Die Anfrage verfällt in §e20 Sekunden§7.");
                                    tpaPlayers.put(target, player);
                                    int taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                                        @Override
                                        public void run() {
                                            tpaPlayers.remove(target);
                                        }
                                    }, 20*20);
                                } else
                                    player.sendMessage(Constant.PREFIX + "§7Dieser Spieler hat bereits eine §cAnfrage §7bekommen.");
                            } else
                                player.sendMessage(Constant.PLAYER_NOT_ONLINE);
                        } else {
                            informedPlayer.remove(player);
                            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
                        }
                    } else {
                        informedPlayer.add(player);
                        player.sendMessage(Constant.PREFIX + "§7Dies wird dich §a" + Constant.TPA_PRICE + "§7 kosten. Wenn du dies trotzdem tun möchtest, §agebe den Befehl erneut ein§7.");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                informedPlayer.remove(player);
                            }
                        }, 20*6);
                    }
                }
            } else
                player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/tpa <Spieler> / <annehmen>");
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
