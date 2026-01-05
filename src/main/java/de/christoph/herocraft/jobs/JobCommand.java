package de.christoph.herocraft.jobs;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constant.NO_PLAYER);
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // GUI öffnen
            HeroCraft.getPlugin().getJobGUI().openJobGUI(player);
            return true;
        }

        if (args.length == 1) {
            String jobName = args[0];
            JobType jobType = JobType.fromString(jobName);

            if (jobType == null) {
                player.sendMessage(Constant.PREFIX + "§7Ungültiger Job. Verfügbare Jobs: §aHolzfäller, Minenarbeiter, Farmer, Schlachter, Landschaftsbauer");
                return true;
            }

            JobManager jobManager = HeroCraft.getPlugin().getJobManager();
            
            if (jobManager.hasJob(player)) {
                Job currentJob = jobManager.getJob(player);
                if (currentJob.getJobType() == jobType) {
                    player.sendMessage(Constant.PREFIX + "§7Du hast bereits den Job §a" + jobType.getDisplayName() + "§7.");
                    return true;
                }
            }

            jobManager.setJob(player, jobType);
            player.sendMessage(Constant.PREFIX + "§7Du hast den Job §a" + jobType.getDisplayName() + "§7 angenommen!");
            return true;
        }

        player.sendMessage(Constant.PREFIX + "§7Verwende: §a/job [Holzfäller|Minenarbeiter|Farmer|Schlachter|Landschaftsbauer]");
        return true;
    }
}

