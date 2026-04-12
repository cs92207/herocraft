package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.lands.province.Province;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import de.christoph.herocraft.protection.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Mjölnir implements Listener {

    public static ArrayList<Player> mjoelnirPlayers = new ArrayList<Player>();
    public static ArrayList<Player> mjoelnirThrowAwayPlayers = new ArrayList<>();

    @EventHandler
    public void onMjölnirInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getItemInHand().hasItemMeta())
            return;
        if(!player.getItemInHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals("§4§lMjölnir"))
            return;
        if(ProtectionListener.isInDangerZone(player.getLocation()))
            return;
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            if(land != null && !land.canBuild(player))
                return;
            Province province = ProvinceManager.getProvinceAtLocation(player.getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
            if(province != null) {
                if(!province.canBuild(player)) {
                    return;
                }
            }
            player.getWorld().strikeLightning(event.getClickedBlock().getLocation());
        } else if(event.getAction() == Action.RIGHT_CLICK_AIR) {
            if(player.isGliding()) {
                player.sendMessage(Constant.PREFIX + "§7Dies darfst du nicht, wenn du fliegst.");
                return;
            }
            Vector vector = event.getPlayer().getLocation().getDirection().multiply(5).setY(0.8D);
            event.getPlayer().setVelocity(vector);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 3);
        } else if(event.getAction() == Action.LEFT_CLICK_AIR) {
            Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            Province province = ProvinceManager.getProvinceAtLocation(player.getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
            if(land != null && !land.canBuild(player))
                return;
            if(province != null && !province.canBuild(player))
                return;
            mjölnirThrow(player);
        }
    }

    private void mjölnirThrow(Player player) {
        if(!mjoelnirPlayers.contains(player)) {
            Snowball projectile = player.launchProjectile(Snowball.class);
            projectile.setVelocity(projectile.getVelocity().multiply(2.5));
            projectile.setCustomName("Mjölnir");
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 3);
            player.getInventory().clear(player.getInventory().getHeldItemSlot());
            mjoelnirThrowAwayPlayers.add(player);
            mjoelnirThrowAwayPlayers.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    mjoelnirThrowAwayPlayers.remove(player);
                }
            }, 20*3);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if(mjoelnirPlayers.contains(player)) {
                        mjoelnirPlayers.remove(player);
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.IRON_AXE).setDisplayName("§4§lMjölnir").setLore("", "§eRechtsklick auf Block §7» Blitz erzeugen", "§eRechtsklick in die Luft §7» Fliegen", "§eLinksklick §7» werfen").setCustomModelData(1000).build());
                    }
                }
            }, 20*10);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 3);
        }
    }

    @EventHandler
    public void onMjölnirHit(ProjectileHitEvent event) {
        if(event.getEntity().getCustomName() == null)
            return;
        if(!event.getEntity().getCustomName().equals("Mjölnir"))
            return;
        Player player = (Player) event.getEntity().getShooter();
        if(event.getEntity().getLocation().getBlock() != null) {
            Location location = event.getEntity().getLocation();
            location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 2, false, false);
        }
        if(mjoelnirThrowAwayPlayers.contains(player)) {
            if(player.getInventory().getItemInMainHand() != null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.getInventory().addItem(HeroCraft.getItemsAdderItem("§4§lMjölnir"));
            } else {
                player.getInventory().addItem(HeroCraft.getItemsAdderItem("§4§lMjölnir"));
            }
            mjoelnirThrowAwayPlayers.remove(player);
        }
    }
}
