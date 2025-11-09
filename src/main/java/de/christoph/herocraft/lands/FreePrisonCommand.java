package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FreePrisonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem §cLand§7.");
            return false;
        }
        if(!land.isOwnerUUID(player.getUniqueId().toString()) && !land.isModeratorUUID(player.getUniqueId().toString())) {
            player.sendMessage(Constant.PREFIX + "§7Das kann nur der §cAdmin des Landes§7.");
            return false;
        }
        if(strings.length != 1) {
            player.sendMessage(Constant.PLAYER_NOT_ONLINE);
            return false;
        }
        Player target = Bukkit.getPlayer(strings[0]);
        if(target == null) {
            player.sendMessage(Constant.PLAYER_NOT_ONLINE);
            return false;
        }
        Land inLand = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(target);
        if(!land.getName().equals(inLand.getName())) {
            player.sendMessage(Constant.PREFIX + "§7Dieser Spieler ist nicht in deinem §cLand§7.");
            return false;
        }
        if(!HeroCraft.getPlugin().prisonManager.prisonPlayers.containsKey(target)) {
            player.sendMessage(Constant.PREFIX + "§7Dieser Spieler ist nicht im §cGefängnis§7.");
            return false;
        }
        HeroCraft.getPlugin().prisonManager.prisonPlayers.get(target).setObsidianAmount(0);
        HeroCraft.getPlugin().prisonManager.prisonPlayers.remove(target);
        target.teleport(new Location(Bukkit.getWorld("world"), land.getSpawnX(), land.getSpawnY(), land.getSpawnZ(), (float) land.getSpawnYaw(), (float) land.getSpawnPitch()));
        target.sendTitle("§a§lBefreit!", "§7Du bist nun nicht mehr im Gefängnis");
        player.sendMessage(Constant.PREFIX + "§7Der Spieler wurde frühzeitig wegen guter Führung §aentlassen§7.");
        return false;
    }

}
