package de.christoph.herocraft.school.skills.passiveskills;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

public class Speed extends PassiveSkill implements Listener {

    public Speed() {
        super("§4§lGeschwindigkeit", "Werde schneller", 40, PotionEffectType.SPEED, new Location(Bukkit.getWorld("hero"), -671, 67, -268, 178.0F, 1.6F), "§7Laufe auf dem Laufband", 150);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                trainingLocation = new Location(Bukkit.getWorld("hero"), -671, 67, -268, 178.0F, 1.6F);
            }
        }, 20*2);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().getLocation().getBlock().getLocation().getX() == -672 && event.getPlayer().getLocation().getBlock().getLocation().getZ() == -272) {
            if(trainingPlayers.containsKey(event.getPlayer())) {
                makeTrainingProgress(event.getPlayer());
                event.getPlayer().teleport(new Location(Bukkit.getWorld("hero"), -671.446, 67.5D, -269.416, 177.8F, 0.6F));
            }

        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerJoined(event);
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
        leaveTraining(event.getPlayer());
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
