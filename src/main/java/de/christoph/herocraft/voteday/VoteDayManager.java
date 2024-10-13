package de.christoph.herocraft.voteday;

import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VoteDayManager {

    private VoteDay currentVoteDay;

    public VoteDayManager() {
        this.currentVoteDay = null;
    }

    public void startVoteDay(Player player) {
        long time = Bukkit.getWorld("world").getTime();
        if(time % 24000 < 12000) {
            player.sendMessage(Constant.PREFIX + "§7Es kann nur in der §cNacht §7eine VoteDay-Abstimmung gestartet werden.");
            return;
        }
        if(isInVoteDay()) {
            player.sendMessage(Constant.PREFIX + "§7Es läuft bereits eine §cVoteDay Abstimmung§7.");
            return;
        }
        currentVoteDay = new VoteDay(player);
    }

    public void endVoteDay() {
        currentVoteDay = null;
    }

    public boolean isInVoteDay() {
        return this.currentVoteDay != null;
    }

    public VoteDay getCurrentVoteDay() {
        return currentVoteDay;
    }

}
