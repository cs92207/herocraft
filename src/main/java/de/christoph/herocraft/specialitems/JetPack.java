package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class JetPack implements Listener {

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(player.isSneaking())
            return;
        if(player.getInventory().getChestplate() == null)
            return;
        if(!player.getInventory().getChestplate().hasItemMeta())
            return;
        if(!player.getInventory().getChestplate().getItemMeta().hasDisplayName())
            return;
        if(!player.getInventory().getChestplate().getItemMeta().getDisplayName().equals("§4§lJet Pack"))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        Vector vector = player.getLocation().getDirection().multiply(1.90).setY(1.4D);
        player.setVelocity(vector);
        player.getWorld().playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 5);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 3);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(player.getInventory().getChestplate() == null)
            return;
        if(!player.getInventory().getChestplate().hasItemMeta())
            return;
        if(!player.getInventory().getChestplate().getItemMeta().getDisplayName().equals("§4§lJet Pack"))
            return;
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

}
