package de.christoph.herocraft.school;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.school.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class MentorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equals(":offset_-16::skills:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a§lSkills aktivieren")) {
            HeroCraft.getPlugin().getSkillManager().setSkillsActive(player, true);
            player.kickPlayer("§a§lDeine Skills wurden aktiviert");
            return;
        } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lSkills deaktivieren")) {
            HeroCraft.getPlugin().getSkillManager().setSkillsActive(player, false);
            player.getActivePotionEffects().clear();
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.kickPlayer("§c§lDeine Skills wurden deaktiviert");
            return;
        }
        learn(player, event.getCurrentItem().getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if(event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase("hero")) {
            event.getEntity().spigot().respawn();
            event.getEntity().teleport(new Location(Bukkit.getWorld("world"), 172.55, 132, -216.5));
        }
    }
    private void learn(Player player, String skill) {
        Skill currentSkill = null;
        for(Map.Entry<String, Skill> entry : HeroCraft.getPlugin().getSkillManager().skills.entrySet()) {
            if(entry.getValue().getName().equalsIgnoreCase(skill))
                currentSkill = entry.getValue();
        }
        if(currentSkill == null)
            return;
        if(currentSkill.players.containsKey(player)) {
            currentSkill.startTraining(player);
        } else {
            currentSkill.learnSkill(player);
        }
    }

}
