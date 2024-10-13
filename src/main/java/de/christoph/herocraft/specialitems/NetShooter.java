package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class NetShooter implements Listener {

    private static HashMap<Player, Location> spidermanSwing = new HashMap<>();
    private static ArrayList<Block> webs = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lSpidermans Netzshooter"))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        event.setCancelled(true);
        if(event.getAction() == Action.RIGHT_CLICK_AIR) {
            swing(player);
        } else if(event.getAction() == Action.LEFT_CLICK_AIR)
            shoot(player);
    }

    private void shoot(Player player) {
        Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land != null && !land.canBuild(player))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setCustomName("Net");
        player.playSound(player.getLocation(), Sound.ENTITY_SPIDER_DEATH, 1, 3);
    }

    private void swing(Player player) {
        if(player.isGliding()) {
            player.sendMessage(Constant.PREFIX + "§7Dies kannst du nicht tun, wenn du fliegst.");
            return;
        }
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setCustomName("NetSwing");
        projectile.setVelocity(projectile.getVelocity().multiply(3.5));
        spidermanSwing.put(player, player.getLocation());
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                spidermanSwing.remove(player);
            }
        }, 20*5);
    }

    @EventHandler
    public void onSwing(ProjectileHitEvent event) {
        if(event.getEntity().getCustomName() == null)
            return;
        if(!event.getEntity().getCustomName().equals("NetSwing"))
            return;
        Player player = (Player) event.getEntity().getShooter();
        if(player.isGliding())
            return;
        //Vector vector = player.getLocation().getDirection().multiply(4).setY(2);
        Vector vector = getDirectionBetweenLocations(spidermanSwing.get(player), event.getEntity().getLocation());
        vector.setY(vector.getY() / 8);
        //Vector vector = player.getLocation().getDirection().multiply(3).setY(1.0D);
        player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 5);
        player.playSound(player.getLocation(), Sound.ENTITY_SPIDER_DEATH, 1, 3);
        player.setVelocity(vector);
    }

    Vector getDirectionBetweenLocations(Location start, Location end) {
        Vector from = start.toVector();
        Vector to = end.toVector();
        return to.subtract(from);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(player.getItemInHand().getItemMeta().getDisplayName() != "§4§lSpidermans Netzshooter")
            return;
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(2);
        }
    }

    @EventHandler
    public void onSpiderManShooted(ProjectileHitEvent event) {
        if(event.getEntity().getCustomName() == null)
            return;
        if(!event.getEntity().getCustomName().equals("Net"))
            return;
        Material block = event.getEntity().getLocation().getBlock().getType();
        Location location = event.getEntity().getLocation();
        event.getEntity().getLocation().getBlock().setType(Material.COBWEB);
        Block block1 = location.getBlock();
        webs.add(block1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                location.getBlock().setType(block);
                webs.remove(block1);
            }
        }, 20*5);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        if(webs.contains(event.getBlock()))
            event.setCancelled(true);
    }

}
