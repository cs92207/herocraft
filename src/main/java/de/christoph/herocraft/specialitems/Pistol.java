package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class Pistol implements Listener {

    public static HashMap<Player, Integer> pistolPlayers = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!pistolPlayers.containsKey(event.getPlayer()))
            pistolPlayers.put(event.getPlayer(), 10);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lPistole"))
            return;
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(pistolPlayers.containsKey(player)) {
                reload(player);
            }
            return;
        }
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land != null) {
            if(!land.canBuild(player)) {
                player.sendMessage(Constant.PREFIX + "§7Du kannst in anderen Ländern nicht schießen.");
                return;
            }
        }
        Province province = ProvinceManager.getProvinceAtLocation(player.getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        if(province != null) {
            if(!province.canBuild(player)) {
                return;
            }
        }
        if(pistolPlayers.containsKey(player)) {
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            arrow.setVelocity(arrow.getVelocity().multiply(2));
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.3F, 1);
            pistolPlayers.put(player, pistolPlayers.get(player) - 1);
            if(pistolPlayers.get(player) == 0) {
                pistolPlayers.remove(player);
                reload(player);
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§eNoch §a" + pistolPlayers.get(player) + " §eSchuss"));
            }
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 3);
        }
    }

    private void reload(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§eWird Nachgeladen..."));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 3, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 3, 1);
                pistolPlayers.put(player, 10);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§eNachgeladen"));
            }
        }, 20 * 5);
    }

}
