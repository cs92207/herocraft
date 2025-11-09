package de.christoph.herocraft.prison;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PrisonManager implements Listener {

    public HashMap<Player, Prison> prisonPlayers;

    public PrisonManager() {
        this.prisonPlayers = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(event.getPlayer());
        if(land == null)
            return;
        if(!HeroCraft.getPlugin().getConfig().contains("Prison." + player.getUniqueId().toString() + "." + land.getName())) {
            return;
        }
        if(HeroCraft.getPlugin().getConfig().getInt("Prison." + player.getUniqueId().toString() + "." + land.getName()) <= 0) {
            return;
        }
        prisonPlayers.put(player, new Prison(
            player.getUniqueId().toString(),
            land.getName(),
            HeroCraft.getPlugin().getConfig().getInt("Prison." + player.getUniqueId().toString() + "." + land.getName())
        ));
        player.teleport(new Location(Bukkit.getWorld("world"), land.getPrisonSpawnX(), land.getPrisonSpawnY(), land.getPrisonSpawnZ()));
        player.sendTitle("§4§lGefängnis", "§7Baue Obsidian Blöcke ab");
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!prisonPlayers.containsKey(player)) {
            return;
        }
        event.setDamage(0);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(!prisonPlayers.containsKey(player)) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(Constant.PREFIX + "§7Du darfst im Gefängnis nur §e§lObsidian §7abbauen. Baue noch §c" + prisonPlayers.get(player).getObsidianAmount() + " Obsidian §7ab, oder verlasse dein Land §0(§e/land§0)§7.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(!prisonPlayers.containsKey(player)) {
            return;
        }
        if(event.getBlock().getType().equals(Material.OBSIDIAN)) {
            if(!player.getInventory().getItemInMainHand().hasItemMeta() || !player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() || !player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lGefängnis Werkzeug")) {
                event.setCancelled(true);
                player.sendMessage(Constant.PREFIX + "§7Du musst das Obsidian mit deinem §4§lGefängnis Werkzeug §7abbauen.");
                return;
            }
            prisonPlayers.get(player).setObsidianAmount(prisonPlayers.get(player).getObsidianAmount() - 1);
            if(prisonPlayers.get(player).getObsidianAmount() <= 0) {
                Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
                player.teleport(new Location(Bukkit.getWorld("world"), land.getSpawnX(), land.getSpawnY(), land.getSpawnZ(), (float) land.getSpawnYaw(), (float) land.getSpawnPitch()));
                player.sendTitle("§a§lBefreit!", "§7Du bist nun nicht mehr im Gefängnis");
                prisonPlayers.remove(player);
                return;
            }
            player.sendMessage(Constant.PREFIX + "§7Noch §e" + prisonPlayers.get(player).getObsidianAmount() + " Obsidian§7.");
        } else {
            event.setCancelled(true);
            player.sendMessage(Constant.PREFIX + "§7Du darfst im Gefängnis nur §e§lObsidian §7abbauen. Baue noch §c" + prisonPlayers.get(player).getObsidianAmount() + " Obsidian §7ab, oder verlasse dein Land §0(§e/land§0)§7.");
        }
    }

    public void putInPrison(Player target, Land land, int obsidian) {
        target.teleport(land.getPrisonSpawnPoint());
        target.sendTitle("§e§lGefängnis Strafe", "§7Baue §c" + obsidian + " Obsidian ab");
        target.sendMessage(Constant.PREFIX + "§7Du wurdest von deinem Land ins Gefängnis gesteckt. Baue entweder §c" + obsidian + " Obsidian §7ab, oder §cverlasse dein Land §0(§e/land§0)§7.");
        target.getInventory().addItem(new ItemBuilder(Material.GOLDEN_PICKAXE).setDisplayName("§4§lGefängnis Werkzeug").build());
        prisonPlayers.put(target, new Prison(
            target.getUniqueId().toString(),
            land.getName(),
            obsidian
        ));
        prisonPlayers.get(target).saveInConfig();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(prisonPlayers.containsKey(event.getPlayer())) {
            prisonPlayers.get(event.getPlayer()).saveInConfig();
            prisonPlayers.remove(event.getPlayer());
        }
    }

}
