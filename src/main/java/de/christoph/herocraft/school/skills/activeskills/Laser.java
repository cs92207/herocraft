package de.christoph.herocraft.school.skills.activeskills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Laser extends ActiveSkill implements Listener {

    public static ArrayList<Player> laserPlayers = new ArrayList<Player>();

    public Laser() {
        super("§4§lLaser", "Sneake und schlage in die Luft, um Laser zu verschießen", 250, new Location(Bukkit.getWorld("world"), -67, 83, -163), "§7Schieße auf das Ziel", 80);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                trainingLocation = new Location(Bukkit.getWorld("world"), -67, 83, -163);
            }
        }, 20*2);
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
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() != Action.LEFT_CLICK_AIR)
            return;
        if(!player.isSneaking())
            return;
        performSkill(player);
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
    public void onLaserHit(ProjectileHitEvent event) {
        if(event.getEntity().getCustomName() == null)
            return;
        if(!event.getEntity().getCustomName().equals("Laser"))
            return;
        if(!(event.getEntity().getShooter() instanceof Player))
            return;
        Player player = (Player) event.getEntity().getShooter();
        if(event.getHitEntity() != null) {
            Vector vector = event.getEntity().getLocation().getDirection().multiply(players.get(player)).setY(0.5D);
            event.getHitEntity().setVelocity(vector);
            if(event.getHitEntity() instanceof Player) {
                ((Player)event.getHitEntity()).damage(players.get(player));
            }
            createLaserEffect(player.getLocation(), event.getHitEntity().getLocation());
            return;
        }
        if(trainingPlayers.containsKey(player)) {
            makeTrainingProgress(player);
        }
        createLaserEffect(player.getLocation(), event.getEntity().getLocation());
    }

    public void createLaserEffect(Location start, Location end) {
        World world = start.getWorld();
        double particleDistance = 0.25; // Distance between particle
        Vector direction = end.clone().subtract(start).toVector();
        double length = direction.length();
        direction.normalize();
        for (double i = 0; i < length; i += particleDistance) {
            Location location = start.clone().add(direction.clone().multiply(i));
            location.setY(location.getY() + 1);
            world.spawnParticle(Particle.DUST, location, 1, new Particle.DustOptions(Color.fromRGB(0, 0, 255), 1));
        }
    }

    @Override
    public void performSkill(Player player) {
        if(!players.containsKey(player))
            return;
        if(!player.getItemInHand().getType().equals(Material.AIR))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()) && !trainingPlayers.containsKey(player))
            return;
        if(HeroCraft.getPlugin().getLandManager().isInOtherLand(player))
            return;
        if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player))
            return;
        if(!laserPlayers.contains(player)) {
            Snowball arrow = player.launchProjectile(Snowball.class);
            arrow.setCustomName("Laser");
            arrow.setVisibleByDefault(false);
            arrow.setCustomNameVisible(false);
            if(trainingPlayers.containsKey(player))
                arrow.setVelocity(arrow.getVelocity().multiply(3));
            else
                arrow.setVelocity(arrow.getVelocity().multiply(7));
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 3);
            laserPlayers.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    laserPlayers.remove(player);
                }
            }, 20*3);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 3);
        }
    }

}
