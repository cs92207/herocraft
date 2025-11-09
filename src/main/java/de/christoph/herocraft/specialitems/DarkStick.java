package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.UUID;

public class DarkStick implements Listener {

    private final HashMap<UUID, Spell> playerSpells = new HashMap<>();
    private final HashMap<UUID, Long> explosionCooldowns = new HashMap<>();
    private final HeroCraft plugin;

    public DarkStick(HeroCraft plugin) {
        this.plugin = plugin;
    }

    // Enum for Spells
    private enum Spell {
        CURSE,
        EXPLOSION,
        LEVITATION
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerSpells.put(event.getPlayer().getUniqueId(), Spell.CURSE);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerSpells.remove(event.getPlayer().getUniqueId());
        explosionCooldowns.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        if (item == null || item.getType() != Material.STICK || !item.getItemMeta().hasDisplayName() ||
                !item.getItemMeta().getDisplayName().equalsIgnoreCase("§4§lDunkler Stab")) {
            return;
        }

        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                cycleSpell(player);
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                castSpell(player);
                break;
            default:
                break;
        }
    }

    private void cycleSpell(Player player) {
        UUID playerId = player.getUniqueId();
        Spell currentSpell = playerSpells.getOrDefault(playerId, Spell.CURSE);
        Spell nextSpell;

        switch (currentSpell) {
            case CURSE:
                nextSpell = Spell.EXPLOSION;
                break;
            case EXPLOSION:
                nextSpell = Spell.LEVITATION;
                break;
            case LEVITATION:
            default:
                nextSpell = Spell.CURSE;
                break;
        }

        playerSpells.put(playerId, nextSpell);
        player.sendMessage(Constant.PREFIX + "§7Zauber: §a" + nextSpell.name());
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }

    private void castSpell(Player player) {
        Spell spell = playerSpells.getOrDefault(player.getUniqueId(), Spell.CURSE);
        switch (spell) {
            case CURSE:
                castCurse(player);
                break;
            case EXPLOSION:
                castExplosion(player);
                break;
            case LEVITATION:
                castLevitation(player);
                break;
        }
    }

    private void castCurse(Player player) {
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setMetadata("magic_curse", new FixedMetadataValue(plugin, true));
        snowball.setGravity(false);
        snowball.setCustomNameVisible(false);
        snowball.setCustomName("Magic Curse");
        snowball.setVelocity(snowball.getVelocity().multiply(1.5));
        snowball.setMetadata("damage", new FixedMetadataValue(plugin, 10.0));

        player.playSound(player.getLocation(), Sound.ENTITY_WITCH_THROW, 1, 1);

        // Partikel alle 1 Tick anzeigen
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!snowball.isDead() && snowball.isValid()) {
                Location loc = snowball.getLocation();
                loc.getWorld().spawnParticle(Particle.WITCH, loc, 10, 0.2, 0.2, 0.2, 0);
            }
        }, 0L, 1L);

        // Schneeball nach 5 Sekunden entfernen
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!snowball.isDead() && snowball.isValid()) {
                snowball.remove();
            }
        }, 20*8); // 100 Ticks = 5 Sekunden
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getHitEntity() == null)
            return;
        if(!(event.getEntity() instanceof Snowball)) {
            return;
        }
        Snowball snowball = (Snowball) event.getEntity();
        if(snowball.getShooter() instanceof Player) {
            Player shooter = (Player) snowball.getShooter();
            Land land = LandManager.getLandAtLocation(event.getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            if(land != null) {
                if(!land.canBuild(shooter)) {
                    event.setCancelled(true);
                    return;
                }
            }
            Province province = ProvinceManager.getProvinceAtLocation(event.getEntity().getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
            if(province != null) {
                if(!province.canBuild(shooter)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if(snowball.getCustomName().equalsIgnoreCase("Magic Curse")) {
            ((LivingEntity)event.getHitEntity()).damage(5);
        }
    }

    private void castExplosion(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (explosionCooldowns.containsKey(playerId) && currentTime - explosionCooldowns.get(playerId) < 2000) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
            return;
        }

        explosionCooldowns.put(playerId, currentTime);

        Location target = player.getTargetBlock(null, 50).getLocation();
        target.getWorld().createExplosion(target, 0F, false, false);
        target.getWorld().spawnParticle(Particle.EXPLOSION, target, 1);
        player.playSound(target, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

        for (Entity entity : target.getWorld().getNearbyEntities(target, 5, 5, 5)) {
            if (entity instanceof Player && entity != player) {
                ((Player) entity).damage(15.0, player);
            }
        }
    }

    private void castLevitation(Player player) {
        player.setVelocity(new Vector(0, 1, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1));
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
    }

}
