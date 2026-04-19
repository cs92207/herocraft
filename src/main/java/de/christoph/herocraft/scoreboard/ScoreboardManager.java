package de.christoph.herocraft.scoreboard;

import de.anyblocks.api.AnyBlocksAPI;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class ScoreboardManager {

    public static void setScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("asde", "asde");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("♛");
        //objective.setDisplayName("§7§l● §e§lHeroCraft §7§l●");
        objective.getScore("                 ").setScore(15);
        objective.getScore("\uD83D\uDE12 §0| §7Coins:").setScore(14);
        objective.getScore("§e  " + HeroCraft.getPlugin().coin.getCoins(player)).setScore(13);
        objective.getScore("              ").setScore(12);
        objective.getScore("\uD83D\uDE12 §0| §7AnyCoins:").setScore(11);
        objective.getScore("§e  " + AnyBlocksAPI.getInstance().getAnyCoinsAPI().getAnyCoins(player.getUniqueId().toString() + "  ")).setScore(10);
        objective.getScore("             ").setScore(9);
        objective.getScore("\uD83D\uDCBE §0| §7Tode:").setScore(8);
        objective.getScore("§e  " + player.getStatistic(Statistic.DEATHS)).setScore(7);
        objective.getScore("").setScore(6);
        objective.getScore("✈ §0| §7Land:").setScore(5);
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null) {
            objective.getScore("§e  -").setScore(4);
        } else {
            objective.getScore("§e  " + land.getName()).setScore(4);
        }
        objective.getScore("      ").setScore(3);
        player.setScoreboard(scoreboard);
    }

}
