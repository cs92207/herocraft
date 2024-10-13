package de.christoph.herocraft.scoreboard;

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
        objective.getScore("                 ").setScore(10);
        objective.getScore("\uD83D\uDE12 §0| §7Coins:").setScore(9);
        objective.getScore("§e  " + HeroCraft.getPlugin().coin.getCoins(player)).setScore(8);
        objective.getScore("             ").setScore(7);
        objective.getScore("\uD83D\uDCBE §0| §7Tode:").setScore(6);
        objective.getScore("§e  " + player.getStatistic(Statistic.DEATHS)).setScore(5);
        objective.getScore("").setScore(4);
        objective.getScore("✈ §0| §7Land:").setScore(3);
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null) {
            objective.getScore("§e  -").setScore(2);
        } else {
            objective.getScore("§e  " + land.getName()).setScore(2);
        }
        objective.getScore("      ").setScore(1);
        player.setScoreboard(scoreboard);
    }

}
