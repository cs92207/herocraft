package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

public class DuckStick implements Listener {

    public static ArrayList<Player> cooldownPlayers = new ArrayList<>();

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getHitEntity() == null)
            return;
        if(!(event.getEntity() instanceof Snowball)) {
            return;
        }
        Snowball snowball = (Snowball) event.getEntity();
        if(snowball.getCustomName().equalsIgnoreCase("Duck Curse")) {
            if(ProtectionListener.isInDangerZone(event.getHitEntity().getLocation()))
                return;
            if(snowball.getShooter() instanceof Player) {
                Player shooter = (Player) snowball.getShooter();
                Land land = LandManager.getLandAtLocation(snowball.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
                if(land != null) {
                    if(!land.canBuild(shooter)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            ((LivingEntity)event.getHitEntity()).damage(260);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getInventory().getItemInMainHand().hasItemMeta())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lDuck Stick"))
            return;
        if(ProtectionListener.isInDangerZone(event.getPlayer().getLocation()))
            return;
        castCurse(player);
    }

    private void castCurse(Player player) {
        if(cooldownPlayers.contains(player)) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
            return;
        }
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_ANGRY, 1, 1);
        HeroCraft plugin = HeroCraft.getPlugin();
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setMetadata("duck_curse", new FixedMetadataValue(plugin, true));
        snowball.setGravity(false);
        snowball.setCustomNameVisible(false);
        snowball.setCustomName("Duck Curse");
        snowball.setVelocity(snowball.getVelocity().multiply(1.5));
        snowball.setMetadata("damage", new FixedMetadataValue(plugin, 10.0));
        snowball.setVisibleByDefault(true);

        player.playSound(player.getLocation(), Sound.ENTITY_WITCH_THROW, 1, 1);

        cooldownPlayers.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                cooldownPlayers.remove(player);
            }
        }, 20*3);

        // Partikel alle 1 Tick anzeigen
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!snowball.isDead() && snowball.isValid()) {
                Location loc = snowball.getLocation();
                loc.getWorld().spawnParticle(Particle.SONIC_BOOM, loc, 10, 0.2, 0.2, 0.2, 0);
            }
        }, 0L, 1L);

        // Schneeball nach 5 Sekunden entfernen
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!snowball.isDead() && snowball.isValid()) {
                snowball.remove();
            }
        }, 20*8); // 100 Ticks = 5 Sekunden
    }

}
