package de.christoph.herocraft.home;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if(HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Das darfst du nicht im Gefängnis. Baue Obsidian ab, oder verlasse dein Land §0(§e/land§0)§7.");
            return false;
        }
        if(strings.length == 1) {
            if(strings[0].equalsIgnoreCase("list")) {
                ArrayList<Home> playerHomes = HeroCraft.getPlugin().getHomeManager().getPlayerHomes().get(player);
                player.sendMessage(Constant.PREFIX + "§7Deine Homes:");
                for(Home home : playerHomes) {
                    player.sendMessage("§e" + home.getName());
                }
            } else {
                String homeName = strings[0];
                ArrayList<Home> playerHomes = HeroCraft.getPlugin().getHomeManager().getPlayerHomes().get(player);
                for(Home home : playerHomes) {
                    if(home.getName().equalsIgnoreCase(homeName)) {
                        home.teleport();
                        player.sendMessage(Constant.PREFIX + "§7Du wurdest zum Home §a" + homeName + "§7 teleportiert.");
                        return false;
                    }
                }
                player.sendMessage(Constant.PREFIX + "§7Du hast kein Home mit dem Namen §c" + homeName + "§7.");
            }
            return false;
        }
        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("create")) {
                ArrayList<Home> playerHomes = HeroCraft.getPlugin().getHomeManager().getPlayerHomes().get(player);
                if(playerHomes.size() >= 3) {
                    player.sendMessage(Constant.PREFIX + "§7Du kannst maximal 3 Homes haben.");
                    return false;
                }
                String homeName = strings[1];
                if(homeName.length() > 10) {
                    player.sendMessage(Constant.PREFIX + "§7Der Name darf maximal 10 Zeichen haben.");
                    return false;
                }
                for(Home home : playerHomes) {
                    if(home.getName().equalsIgnoreCase(homeName)) {
                        player.sendMessage(Constant.PREFIX + "§7Du hast bereits ein Home mit diesem §cNamen§7.");
                        return false;
                    }
                }
                Home home = new Home(player, homeName, player.getLocation());
                home.save();
                player.sendMessage(Constant.PREFIX + "§7Du hast ein Home §aerstellt§7.");
            } else if(strings[0].equalsIgnoreCase("delete")) {
                String homeName = strings[1];
                ArrayList<Home> playerHomes = HeroCraft.getPlugin().getHomeManager().getPlayerHomes().get(player);
                for(Home home : playerHomes) {
                    if(home.getName().equalsIgnoreCase(homeName)) {
                        home.delete();
                        player.sendMessage(Constant.PREFIX + "§7Du hast das §agelöscht§7.");
                        return false;
                    }
                }
                player.sendMessage(Constant.PREFIX + "§7Du hast kein Home §c" + homeName + "§7.");
            }
            return false;
        }
        player.sendMessage(Constant.PREFIX + "§7Bitte benutze:");
        player.sendMessage("§e/home <Name> §7Zum Home teleportieren");
        player.sendMessage("§e/home create <Name> §7Home erstellen");
        player.sendMessage("§e/home delete <Name> §7Home löschen");
        player.sendMessage("§e/home list §7Homes anzeigen");
        return false;
    }

}
