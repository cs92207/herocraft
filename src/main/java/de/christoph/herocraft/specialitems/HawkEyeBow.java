package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.ItemBuilder;
import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class HawkEyeBow implements Listener {

    private HashMap<Player, BowMode> hawkEyePlayers = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lHawk Eyes Bogen"))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        if(event.getAction() != Action.RIGHT_CLICK_AIR)
        if(hawkEyePlayers.containsKey(player))
            chooseNewMode(hawkEyePlayers.get(player), player);
        else {
            chooseNewMode(BowMode.NORMAL, player);
            hawkEyePlayers.put(player, BowMode.NORMAL);
        }
    }

    private void chooseNewMode(BowMode mode, Player player) {
        if(mode == BowMode.NORMAL) {
            hawkEyePlayers.put(player, BowMode.FIRE);
            player.sendMessage(Constant.PREFIX + "§7Dein neuer Modus ist §aFeuer§7.");
        } else if(mode == BowMode.FIRE) {
            hawkEyePlayers.put(player, BowMode.EXPLOSION);
            player.sendMessage(Constant.PREFIX + "§7Dein neuer Modus ist §aExplosion§7.");
        } else if(mode == BowMode.EXPLOSION) {
            hawkEyePlayers.put(player, BowMode.TELEPORT);
            player.sendMessage(Constant.PREFIX + "§7Dein neuer Modus ist §aTeleportation§7.");
        }  else if(mode == BowMode.TELEPORT) {
            hawkEyePlayers.put(player, BowMode.NORMAL);
            player.sendMessage(Constant.PREFIX + "§7Dein neuer Modus ist §aNormal§7.");
        }
    }

    @EventHandler
    public void onHawkEyeShoot(ProjectileLaunchEvent event) {
        if(!(event.getEntity() instanceof Arrow))
            return;
        Arrow arrow = (Arrow) event.getEntity();
        if(!(arrow.getShooter() instanceof Player))
            return;
        Player player = (Player) arrow.getShooter();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lHawk Eyes Bogen"))
            return;
        Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land != null && !land.canBuild(player)) {
            event.setCancelled(true);
            return;
        }
        if(ProtectionListener.isInDangerZone(player.getLocation())) {
            event.setCancelled(true);
            return;
        }
        event.getEntity().setCustomName("HawkEye");
        player.getInventory().addItem(new ItemBuilder(Material.ARROW).build());
        if(!hawkEyePlayers.containsKey(player))
            hawkEyePlayers.put(player, BowMode.NORMAL);
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        Location location = event.getEntity().getLocation();
        if(event.getEntity().getCustomName() == null)
            return;
        if(!event.getEntity().getCustomName().equals("HawkEye"))
            return;
        if(!(event.getEntity().getShooter() instanceof Player))
            return;
        Player player = (Player) event.getEntity().getShooter();
        if(hawkEyePlayers.get(player) == BowMode.NORMAL)
            return;
        if(hawkEyePlayers.get(player) == BowMode.EXPLOSION)
            location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 3, false, false);
        else if(hawkEyePlayers.get(player) == BowMode.TELEPORT) {
            player.teleport(location);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 3);
        } else if(hawkEyePlayers.get(player) == BowMode.FIRE) {
            Material block = event.getEntity().getLocation().getBlock().getType();
            event.getEntity().getLocation().getBlock().setType(Material.FIRE);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        location.getBlock().setType(block);
                    }
                }, 20*5);
            }
        }
    }
