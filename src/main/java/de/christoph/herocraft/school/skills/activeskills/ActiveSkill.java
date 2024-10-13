package de.christoph.herocraft.school.skills.activeskills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.school.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public abstract class ActiveSkill extends Skill {

    public ActiveSkill(String name, String description, int firstCosts, Location trainingLocation, String trainingDescription, int trainingProgressUntilUpgrade) {
        super(name, description, firstCosts, trainingLocation, trainingDescription, trainingProgressUntilUpgrade);
    }

    public abstract void performSkill(Player player); // is called in current skill event (for example in laser)

    public void onPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(isInDatabase(player)) {
            if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player))
                return;
            HeroCraft.getPlugin().getSkillManager().skills.get(getName()).activateSkill(player, getSkillLevel(player));
        }
    }

    public void onPlayerRespawned(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if(isInDatabase(player)) {
            if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player))
                return;
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    HeroCraft.getPlugin().getSkillManager().skills.get(getName()).activateSkill(player, getSkillLevel(player));
                }
            }, 20);
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        if(HeroCraft.getPlugin().getSkillManager().skills.containsKey(event.getPlayer())) {
            saveInConfig(event.getPlayer());
        }
    }

}
