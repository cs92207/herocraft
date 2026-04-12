package de.christoph.herocraft.onboarding;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartOnBoardingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen!");
            return true;
        }

        Player player = (Player) sender;

        // Check if player already completed onboarding
        if (HeroCraft.getPlugin().getOnBoardingManager().hasPlayerCompletedOnBoarding(player)) {
            player.sendMessage("§e§lOnboarding §7§l| §cDu hast das Onboarding bereits abgeschlossen!");
            return true;
        }

        // Check if player already has an active onboarding
        if (HeroCraft.getPlugin().getOnBoardingManager().isPlayerInOnBoarding(player)) {
            player.sendMessage("§e§lOnboarding §7§l| §cDu bist bereits im Onboarding!");
            return true;
        }

        // Start the onboarding
        HeroCraft.getPlugin().getOnBoardingManager().startOnBoarding(player);
        return true;
    }

}
