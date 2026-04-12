package de.christoph.herocraft.onboarding;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CreateTutorialLandPositionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen!");
            return true;
        }

        Player player = (Player) sender;

        // Nur Admins
        if (!player.hasPermission("admin") && !player.hasPermission("herocraft.admin") && !player.isOp()) {
            player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        // Speichere die aktuelle Position des Spielers als Tutorial-Land-Position
        Location location = player.getLocation();
        FileConfiguration config = HeroCraft.getPlugin().getConfig();

        config.set("TUTORIAL_LAND_POSITION.WORLD", location.getWorld().getName());
        config.set("TUTORIAL_LAND_POSITION.X", location.getX());
        config.set("TUTORIAL_LAND_POSITION.Y", location.getY());
        config.set("TUTORIAL_LAND_POSITION.Z", location.getZ());

        HeroCraft.getPlugin().saveConfig();

        player.sendMessage("§e§lTutorial §7§l| §aPosition für Land-Erstellung gespeichert!");
        player.sendMessage("§e§lTutorial §7§l| §7Koordinaten: §c" + 
            (int)location.getX() + ", " + (int)location.getY() + ", " + (int)location.getZ());

        return true;
    }

}
