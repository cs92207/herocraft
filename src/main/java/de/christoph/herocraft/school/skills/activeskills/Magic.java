package de.christoph.herocraft.school.skills.activeskills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Magic extends ActiveSkill implements Listener {

    private ArrayList<Player> magicedPlayers = new ArrayList<>();

    public Magic() {
        super("§4§lHexerei", "Klicke Gegner an und verhexe sie", 1000, new Location(Bukkit.getWorld("hero"), -655, 67, -269, 177.5F, 1.3F), "§7Studiere das Zauberbuch", 200);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                trainingLocation = new Location(Bukkit.getWorld("hero"), -655, 67, -279, 177.5F, 1.3F);
            }
        }, 20*2);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerJoined(event);
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
        leaveTraining(event.getPlayer());
    }

    @EventHandler
    public void onMagic(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(!trainingPlayers.containsKey(event.getPlayer()))
            return;
        if(ProtectionListener.isInDangerZone(event.getPlayer().getLocation())) {
            return;
        }
        if(HeroCraft.getPlugin().getLandManager().isInOtherLand(event.getPlayer()))
            return;
        if(event.getClickedBlock() == null)
            return;
        if(!event.getClickedBlock().getType().equals(Material.ENCHANTING_TABLE))
            return;
        event.setCancelled(true);
        makeTrainingProgress(event.getPlayer());
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
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(!(event.getRightClicked() instanceof Player))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        Player clicked = (Player) event.getRightClicked();
        if(!players.containsKey(player))
            return;
        if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player))
            return;
        if(magicedPlayers.contains(clicked))
            return;
        magicedPlayers.add(clicked);
        clicked.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 500));
        clicked.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 500));
        for(int i = 0; i < 400; i++)
            clicked.getLocation().getWorld().playEffect(new Location(clicked.getWorld(), clicked.getLocation().getX(), clicked.getLocation().getY() + 2, clicked.getLocation().getZ()), Effect.REDSTONE_TORCH_BURNOUT, 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                magicedPlayers.remove(clicked);
                for(int i = 0; i < 400; i++)
                    clicked.getLocation().getWorld().playEffect(new Location(clicked.getWorld(), clicked.getLocation().getX(), clicked.getLocation().getY() + 2, clicked.getLocation().getZ()), Effect.REDSTONE_TORCH_BURNOUT, 5);
            }
        }, 3L * players.get(player));
    }

    @Override
    public void performSkill(Player player) {  }
}
