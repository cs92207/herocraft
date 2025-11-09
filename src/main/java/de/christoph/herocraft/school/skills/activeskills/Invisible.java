package de.christoph.herocraft.school.skills.activeskills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.protection.ProtectionListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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

public class Invisible extends ActiveSkill implements Listener {

    public Invisible() {
        super("§4§lUnsichtbarkeit", "Sneake und Rechtsklicke in auf einen Block, um Unsichtbar zu werden", 500, new Location(Bukkit.getWorld("world"), 60, 83, -148), "§7Werde Unsichtbar hinter den Bannern.", 70);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                trainingLocation = new Location(Bukkit.getWorld("world"), 60, 83, -148);
            }
        }, 20*2);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerJoined(event);
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
    public void onPlayerSneak(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!players.containsKey(player))
            return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player))
            return;
        if(!player.isSneaking())
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()) && !trainingPlayers.containsKey(player))
            return;
        if(HeroCraft.getPlugin().getLandManager().isInOtherLand(player))
            return;
        performSkill(player);
    }

    @Override
    public void performSkill(Player player) {
        if(ProtectionListener.isInDangerZone(player.getLocation()) && !trainingPlayers.containsKey(player))
            return;
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            return;
        int seconds = 2 + players.get(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, seconds*20, 500));
        if(trainingPlayers.containsKey(player)) {
            makeTrainingProgress(player);
            return;
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a" + seconds + " Sekunden §eunsichtbar"));
    }

}
