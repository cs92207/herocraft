package de.christoph.herocraft.school.skills.passiveskills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.school.MentorListener;
import de.christoph.herocraft.school.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PassiveSkill extends Skill {

    // PassiveSkills are sills where the player doesn't have to do anything to perform them

    private PotionEffectType effectType;
    private int effectValue; // How strong is the effect

    public PassiveSkill(String name, String description, int firstCosts, PotionEffectType effectType, Location trainingLocation, String trainingDescription, int trainingProgressUntilUpgrade) {
        super(name, description, firstCosts, trainingLocation, trainingDescription, trainingProgressUntilUpgrade);
        this.effectType = effectType;
        this.effectValue = effectValue;
    }

    @Override
    public void onSkillActivated(Player player) {
        super.onSkillActivated(player);
        player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, players.get(player)));
        addPotionEffectToPlayer(player);
    }

    @Override
    public void onSkillUpgraded(Player player) {
        super.onSkillUpgraded(player);
        addPotionEffectToPlayer(player);
    }

    @Override
    public void onSkillLearned(Player player) { // is automaticlly called when a skill got learned
        super.onSkillLearned(player);
        addPotionEffectToPlayer(player);
    }

    private void addPotionEffectToPlayer(Player player) {
        player.removePotionEffect(effectType);
        PotionEffect potionEffect = new PotionEffect(effectType, Integer.MAX_VALUE, players.get(player) - 1, true, false);
        player.addPotionEffect(potionEffect);
    }

    public void onPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(isInDatabase(event.getPlayer())) {
            if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player))
                return;
            if(MentorListener.hasSkillDeActivated(player, getName())) {
                player.removePotionEffect(effectType);
                return;
            }
            HeroCraft.getPlugin().getSkillManager().skills.get(getName()).activateSkill(player, getSkillLevel(player));
        }
    }

    public void onPlayerRespawned(PlayerRespawnEvent event) {
        System.out.println(getName());
        Player player = event.getPlayer();
        players.remove(player);
        if(isInDatabase(event.getPlayer())) {
            if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player))
                return;
            if(MentorListener.hasSkillDeActivated(player, getName()))
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
