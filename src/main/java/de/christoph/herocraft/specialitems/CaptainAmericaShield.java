package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.ItemBuilder;
import de.christoph.herocraft.protection.ProtectionListener;
//import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
//import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;

public class CaptainAmericaShield implements Listener {
    public static ArrayList<Player> captainAmericaWaitPlayers = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lCaptain Americas Schild"))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(projectile.getVelocity().multiply(2.5));
        projectile.setCustomName("Shield");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 3);
        player.getInventory().clear(player.getInventory().getHeldItemSlot());
        captainAmericaWaitPlayers.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(captainAmericaWaitPlayers.contains(player)) {
                    captainAmericaWaitPlayers.remove(player);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.SHULKER_SHELL).setDisplayName("§4§lCaptain Americas Schild").setLore("", "§eLinksklick §7» werfen", "§7Du bekommst weniger Schaden").setCustomModelData(1000).build());
                }
            }
        }, 20*10);
    }

    @EventHandler
    public void onShield(ProjectileHitEvent event) {
        if(event.getEntity().getCustomName() == null)
            return;
        if(!event.getEntity().getCustomName().equals("Shield"))
            return;
        Player player = (Player) event.getEntity().getShooter();
        if(event.getEntity().getLocation().getBlock() != null) {
            Location location = event.getEntity().getLocation();
            location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 1, false, false);
        }
        for(Entity i : event.getEntity().getNearbyEntities(3, 3, 3)) {
            if(i.equals(player))
                continue;
            if(i instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) i;
                livingEntity.damage(3);
            }
        }
        if(captainAmericaWaitPlayers.contains(player)) {
            if(player.getInventory().getItemInMainHand() != null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.getInventory().addItem(new ItemBuilder(Material.SHULKER_SHELL).setDisplayName("§4§lCaptain Americas Schild").setLore("", "§eLinksklick §7» werfen", "§7Du bekommst weniger Schaden").setCustomModelData(1000).build());
            } else {
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.SHULKER_SHELL).setDisplayName("§4§lCaptain Americas Schild").setLore("", "§eLinksklick §7» werfen", "§7Du bekommst weniger Schaden").setCustomModelData(1000).build());
            }
            captainAmericaWaitPlayers.remove(player);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lCaptain Americas Schild"))
            return;
        if(event.getDamage() - 4 < 0)
            event.setDamage(0);
        else
            event.setDamage(event.getDamage() - 4);
    }

}
