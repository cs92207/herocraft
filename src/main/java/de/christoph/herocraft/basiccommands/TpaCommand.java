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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(player)) {
                player.sendMessage(Constant.PREFIX + "§7Das darfst du nicht im Gefängnis. Baue Obsidian ab, oder verlasse dein Land §0(§e/land§0)§7.");
                return false;
            }
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
                        Player target = Bukkit.getPlayer(strings[0]);
                        if(target != null) {
                            if(!tpaPlayers.containsKey(target)) {
                                player.sendMessage(Constant.PREFIX + "§7Du hast eine Anfrage gestellt. Sie läuft in §e120 Sekunden§7 ab.");
                                target.sendMessage(Constant.PREFIX + "§7Der Spieler §e" + player.getName() + "§7 möchte sich zu dir teleportiern. Benutze §e/tpa annehmen §7um dies zu erlauben. Die Anfrage verfällt in §e20 Sekunden§7.");
                                tpaPlayers.put(target, player);
                                int taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                                    @Override
                                    public void run() {
                                        tpaPlayers.remove(target);
                                    }
                                }, 20*120);
                            } else
                                player.sendMessage(Constant.PREFIX + "§7Dieser Spieler hat bereits eine §cAnfrage §7bekommen.");
                        } else
                            player.sendMessage(Constant.PLAYER_NOT_ONLINE);


                }
            } else
                player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/tpa <Spieler> / <annehmen>");
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

}
