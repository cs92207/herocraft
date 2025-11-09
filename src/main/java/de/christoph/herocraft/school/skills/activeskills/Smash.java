package de.christoph.herocraft.school.skills.activeskills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Smash extends ActiveSkill implements Listener {

    private ArrayList<Player> trainingWaitingPlayers;
    private ArrayList<Player> smashPlayers = new ArrayList<>();
    private ArrayList<Player> possibles = new ArrayList<>();
    private ArrayList<Player> cooldownPlayers = new ArrayList<>();

    public Smash() {
        super("§4§lSmash-Sprung", "Sneake 2mal und mache einen mächtigen Sprung", 150, new Location(Bukkit.getWorld("hero"), -655, 87 , -212, 178.4F, -0.6F), "§7Springe auf der Fläche", 200);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                 trainingLocation = new Location(Bukkit.getWorld("hero"), -655, 87 , -212, 178.4F, -0.6F);
            }
        });
        trainingWaitingPlayers = new ArrayList<>();
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
    public void onPlayerLeave(PlayerQuitEvent event) {
        onPlayerQuit(event);
    }

    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        onPlayerRespawned(event);
    }

    @EventHandler
    public void onPlayerTryFlying(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(player.isSneaking())
            return;
        if(player.getGameMode() != GameMode.SURVIVAL)
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation())) {
            return;
        }
        if(!players.containsKey(player))
            return;
        if(HeroCraft.getPlugin().getLandManager().isInOtherLand(player))
            return;
        if(HeroCraft.getPlugin().getConfig().contains("DSkill." + player.getName()) && HeroCraft.getPlugin().getConfig().getBoolean("DSkill." + player.getName()))
            return;
        if(cooldownPlayers.contains(player))
            return;
        if(!possibles.contains(player)) {
            possibles.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    possibles.remove(player);
                }
            }, 25);
            return;
        }
        if(smashPlayers.contains(player))
            return;
        Vector vector = event.getPlayer().getLocation().getDirection().multiply(0.3).setY(2D);
        event.getPlayer().setVelocity(vector);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 3);
        smashPlayers.add(player);
        cooldownPlayers.add(player);
        player.setHealth(20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                cooldownPlayers.remove(player);
            }
        }, 20*10);
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!players.containsKey(player))
            return;
        if(!smashPlayers.contains(player))
            return;
        if(!player.isOnGround())
            return;
        Location location = player.getLocation();
        player.setHealth(20);
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 3, false, false);
        smashPlayers.remove(player);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!players.containsKey(player))
            return;
        if(!smashPlayers.contains(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(HeroCraft.getPlugin().getConfig().contains("Skill." + event.getPlayer().getUniqueId().toString() + getName()))
            event.getPlayer().setAllowFlight(true);
        onPlayerJoined(event);
    }

    @Override
    public void onSkillLearned(Player player) {
        super.onSkillLearned(player);
        player.setAllowFlight(true);
    }

    @Override
    public void performSkill(Player player) {  }

}
