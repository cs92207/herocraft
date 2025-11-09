package de.christoph.herocraft.prison;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetPrisonSpawnPointCommand implements CommandExecutor {

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
        if(!land.isOwnerUUID(player.getUniqueId().toString())) {
            player.sendMessage(Constant.PREFIX + "§7Das kann nur der §cAdmin des Landes§7.");
            return false;
        }
        Land landIn = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(landIn == null || !landIn.getName().equals(land.getName())) {
            player.sendMessage(Constant.PREFIX + "§7Setze den Gefängnis Spawn innerhalb deines Landes.");
            return false;
        }
        land.setPrisonSpawnPoint(player.getLocation());
        player.sendMessage(Constant.PREFIX + "§7Gefängnis Spawnpoint §agesetzt§7.");
        return false;
    }

}
