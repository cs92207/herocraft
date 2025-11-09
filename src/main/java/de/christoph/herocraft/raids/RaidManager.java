package de.christoph.herocraft.raids;

import de.christoph.herocraft.lands.Land;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaidManager implements Listener {

    private ArrayList<Raid> raids;

    public RaidManager() {
        this.raids = new ArrayList<>();
    }

    public void startRaid(Land land) {
        Raid raid = new Raid(land);
        raids.add(raid);
        raid.start();
    }

    public boolean isLandInRaid(Land land) {
        for(Raid i : raids) {
            if(i.getLand().getName().equalsIgnoreCase(land.getName()))
                return true;
        }
        return false;
    }

    public boolean isPlayerInRaid(Player player) {
        for(Raid i : raids) {
            if(i.getPlayersInRaid().contains(player)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Villager))
            return;
        Villager villager = (Villager) event.getEntity();
        if(villager.getCustomName() == null)
            return;
        if(!villager.getCustomName().contains("§4§lOpfer"))
            return;
        Raid raid = null;
        for(Raid i : raids) {
            if(i.getVictimEntity().equalsIgnoreCase(villager.getUniqueId().toString())) {
                raid = i;
            }
        }
        if(raid == null)
            return;
        raid.finishRaidFailed();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity().getCustomName() == null)
            return;
        if(event.getEntity().getCustomName().contains("§e§lTruppe") || event.getEntity().getCustomName().contains("§4§lMonster")) {
            if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent event) {
        Entity entity = event.getEntity();
        if(entity.getCustomName() == null)
            return;
        Bukkit.getLogger().info("Entity " + entity.getType() + " wurde entfernt: " + entity.getCustomName() + "_" + event.getCause());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Raid raid = getRaidFromPlayer(player);
        if(raid == null)
            return;
        raid.onRaidPlayerLeave(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(!isPlayerInRaid(player)) {
            return;
        }
        getRaidFromPlayer(player).deathFromRaidPlayer(player);
        List<ItemStack> drops = event.getDrops();
        if (drops.size() < 2) return;
        Collections.shuffle(drops);
        int toRemove = drops.size() / 2;
        for (int i = 0; i < toRemove; i++) {
            drops.remove(0);
        }
    }

    public Raid getRaidFromPlayer(Player player) {
        for(Raid i : raids) {
            if(i.getPlayersInRaid().contains(player)) {
                return i;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerKilledRaidEntity(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        String name = entity.getCustomName();
        if(name == null)
            return;
        if(!name.contains("§e§lTruppe"))
            return;
        if (event.getEntity().getScoreboardTags().contains("custom_silent_death")) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            return;
        }
        Pattern pattern = Pattern.compile("Lvl\\.\\s*(\\d+)");
        Matcher matcher = pattern.matcher(name);
        int level = 0;
        if (matcher.find()) {
            level = Integer.parseInt(matcher.group(1));
        }
        for(Raid raid : raids) {
            for(UUID i : raid.getRaidEntities()) {
                if(i.equals(entity.getUniqueId())) {
                    raid.killedRaidEntity(entity, level);
                }
            }
        }
    }

    @EventHandler
    public void onRaidEntityHitPlayer(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if(damager.getCustomName() == null) {
            return;
        }
        String name = damager.getCustomName();
        if(!name.contains("§e§lTruppe"))
            return;
        boolean isRaidEntity = false;
        for(Raid raid : raids) {
            for(UUID i : raid.getRaidEntities()) {
                if(i.equals(damager.getUniqueId())) {
                    isRaidEntity = true;
                    break;
                }
            }
        }
        if(!isRaidEntity)
            return;
        Pattern pattern = Pattern.compile("Lvl\\.\\s*(\\d+)");
        Matcher matcher = pattern.matcher(name);
        int level = 0;
        if (matcher.find()) {
            level = Integer.parseInt(matcher.group(1));
            System.out.println("Extrahiertes Level: " + level);
        }
        event.setDamage(event.getDamage() + ((double) level / 2));
    }

    public ArrayList<Raid> getRaids() {
        return raids;
    }

}
