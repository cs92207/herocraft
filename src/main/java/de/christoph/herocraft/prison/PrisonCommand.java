package de.christoph.herocraft.prison;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrisonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem §cLand§7.");
            return false;
        }
        if(!land.isModeratorUUID(player.getUniqueId().toString()) && !land.isOwnerUUID(player.getUniqueId().toString())) {
            player.sendMessage(Constant.PREFIX + "§7Das kann nur der §cAdmin des Landes§7.");
            return false;
        }
        if(strings.length != 2) {
            player.sendMessage(Constant.PREFIX + "§7Bitte benutze §e/prison <SPIELER> <OBSIDIAN>§7.");
            return false;
        }
        Player target = Bukkit.getPlayer(strings[0]);
        if(target == null) {
            player.sendMessage(Constant.PLAYER_NOT_ONLINE);
            return false;
        }
        int obsidian;
        try {
            obsidian = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Constant.PREFIX + "§7Das ist eine ungültige anzahl an Obsidian. Benutze §e/prison <SPIELER> <OBSIDIAN>§7.");
            return false;
        }
        Land targetLand = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(target);
        if(targetLand == null || !targetLand.getName().equals(land.getName())) {
            player.sendMessage(Constant.PREFIX + "§7Dieser Spieler ist nicht Mitglied deines §cLandes§7.");
            return false;
        }
        if(targetLand.isOwnerUUID(target.getUniqueId().toString())) {
            player.sendMessage(Constant.PREFIX + "§7Du kannst den §cInhaber des Landes §7nicht ins §cGefängnis§7 stecken.");
            return false;
        }
        if(HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(target)) {
            player.sendMessage(Constant.PREFIX + "§7Dieser Spieler ist bereits im §cGefängnis§7.");
            return false;
        }
        if(land.getPrisonSpawnX() == 0) {
            player.sendMessage(Constant.PREFIX + "§7Setze erst den §cGefängnis Spawn§7. §0(§e/setprisonspawn§0)§7.");
            return false;
        }
        HeroCraft.getPlugin().prisonManager.putInPrison(target, land, obsidian);
        player.sendMessage(Constant.PREFIX + "§7Der Spieler ist nun im Gefängnis und muss §a" + obsidian + " abbauen§7. Benutze §e/freeprison <SPIELER>§7 um ihn vorzeitig zu befreien.");
        return false;
    }

}
