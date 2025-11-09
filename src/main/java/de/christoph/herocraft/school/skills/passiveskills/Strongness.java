package de.christoph.herocraft.school.skills.passiveskills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

public class Strongness extends PassiveSkill implements Listener {

    public Strongness() {
        super("§4§lStärke", "Werde Stärker", 30, PotionEffectType.STRENGTH, new Location(Bukkit.getWorld("world"), 66, 77, -132), "§7Trainiere mit den Boxssäcken", 200);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                trainingLocation = new Location(Bukkit.getWorld("world"), 66, 77, -132);
            }
        }, 20*2);
    }

    @Override
    public void onSkillLearned(Player player) {
        super.onSkillLearned(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerJoined(event);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        onPlayerQuit(event);
    }

    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        onPlayerRespawned(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block block = event.getClickedBlock();
        if(block == null)
            return;
        if(block.getType() != Material.RED_CONCRETE)
            return;
        if(ProtectionListener.isInDangerZone(block.getLocation()))
            leaveTraining(event.getPlayer());
    }


    // training
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if(block == null)
            return;
        if(!trainingPlayers.containsKey(player))
            return;
        if(block.getLocation().getX() == 66 && block.getLocation().getZ() == -131) {
            makeTrainingProgress(player);
        }
    }

}
