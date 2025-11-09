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
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Jump extends PassiveSkill implements Listener {

    private ArrayList<Player> trainingWaitingPlayers;

    public Jump() {
        super("§4§lSprungkraft", "§7Bekomme einen Sprungboost", 80, PotionEffectType.JUMP_BOOST,   new Location(Bukkit.getWorld("world"), 54, 84, -150), "§7Springe auf der Fläche.", 150);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                trainingLocation = new Location(Bukkit.getWorld("world"), 54, 84, -150);
            }
        }, 20*2);
        trainingWaitingPlayers = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!trainingPlayers.containsKey(player))
            return;
        if(player.isOnGround())
            return;
        if(trainingWaitingPlayers.contains(player))
            return;
        makeTrainingProgress(player);
        trainingWaitingPlayers.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                trainingWaitingPlayers.remove(player);
            }
        }, 20);
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

}
