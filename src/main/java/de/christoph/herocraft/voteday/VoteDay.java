package de.christoph.herocraft.voteday;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class VoteDay {

    private Player sender;
    private ArrayList<Player> yesPlayers;
    private ArrayList<Player> noPlayers;
    private int taskID;
    private int seconds;

    public VoteDay(Player sender) {
        seconds = Constant.VOTE_DAY_TIME;
        this.sender = sender;
        yesPlayers = new ArrayList<>();
        noPlayers = new ArrayList<>();
        for(Player all : Bukkit.getWorld("world").getPlayers()) {
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("§0--- §e§lVote Day §0---");
            all.sendMessage("");
            all.sendMessage("§7Der Spieler §e§l" + sender.getName() + "§7 würde gerne auf Tag stellen. Stimme ab, ob die Zeit geändert werden soll.");
            all.sendMessage("§a§l/tagja");
            all.sendMessage("§4§l/tagnein");
            all.sendMessage("");
            all.sendMessage("§0--- §e§lVote Day §0---");
            all.sendMessage("");
            all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        }
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                switch (seconds) {
                    case 0:
                        sendResult();
                        break;
                    case 30: case 20: case 10: case 5: case 3: case 2: case 1:
                        for(Player all : Bukkit.getWorld("world").getPlayers()) {
                            all.sendMessage(Constant.PREFIX + "§7Die §aVoteDay-Abstimmung §7endet in §e" + seconds + " Sekunde(n)§7.");
                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                        break;
                }
                seconds--;
            }
        }, 20, 20);
    }

    private void sendResult() {
        Bukkit.getScheduler().cancelTask(taskID);
        if(yesPlayers.size() > noPlayers.size()) {
            // Switch to day
            Bukkit.getWorld("world").setTime(0);
            for(Player all : Bukkit.getWorld("world").getPlayers()) {
                all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1 ,1);
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("§0--- §e§lVote Day §0---");
                all.sendMessage("");
                all.sendMessage("§7Die VoteDay-Abstimmung ging folgendermaßen aus:");
                all.sendMessage("§a§lJa-Stimmen: §e" + yesPlayers.size());
                all.sendMessage("§4§lNein-Stimmen: §e" + noPlayers.size());
                all.sendMessage("");
                all.sendMessage("§7Die Zeit wurde also auf §aTag §7gestellt.");
                all.sendMessage("");
                all.sendMessage("§0--- §e§lVote Day §0---");
            }
        } else {
            // Don´t change anything
            for(Player all : Bukkit.getWorld("world").getPlayers()) {
                all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1 ,1);
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("");
                all.sendMessage("§0--- §e§lVote Day §0---");
                all.sendMessage("");
                all.sendMessage("§7Die VoteDay-Abstimmung ging folgendermaßen aus:");
                all.sendMessage("§a§lJa-Stimmen: §e" + yesPlayers.size());
                all.sendMessage("§4§lNein-Stimmen: §e" + noPlayers.size());
                all.sendMessage("");
                all.sendMessage("§7Die Zeit wurde also §cnicht§7 auf Tag gestellt.");
                all.sendMessage("");
                all.sendMessage("§0--- §e§lVote Day §0---");
            }
        }
        HeroCraft.getPlugin().getVoteDayManager().endVoteDay();
    }

    public void sayYes(Player player) {
        if(yesPlayers.contains(player) || noPlayers.contains(player)) {
            player.sendMessage(Constant.PREFIX + "§7Du hast bereits §cabgestimmt§7.");
            return;
        }
        yesPlayers.add(player);
        player.sendMessage(Constant.PREFIX + "§7Du hast für §aJa §7gestimmt.");
    }

    public void sayNo(Player player) {
        if(yesPlayers.contains(player) || noPlayers.contains(player)) {
            player.sendMessage(Constant.PREFIX + "§7Du hast bereits §cabgestimmt§7.");
            return;
        }
        noPlayers.add(player);
        player.sendMessage(Constant.PREFIX + "§7Du hast für §cNein §7gestimmt.");
    }

    public Player getSender() {
        return sender;
    }

    public ArrayList<Player> getNoPlayers() {
        return noPlayers;
    }

    public ArrayList<Player> getYesPlayers() {
        return yesPlayers;
    }

}
