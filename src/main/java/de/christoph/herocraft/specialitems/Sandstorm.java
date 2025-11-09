package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Particle;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Sandstorm implements Listener {

    private final Set<UUID> cooldownPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land != null) {
            if(!land.canBuild(player)) {
                return;
            }
        }

        return;

        // Check if the item is the renamed Sandstorm item
        /*if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals("§4§lSandsturm")) {
                event.setCancelled(true);

                if (cooldownPlayers.contains(player.getUniqueId())) {
                    player.sendMessage(Constant.PREFIX + "§7Warte einen moment.");
                    return;
                }

                // Remove the item from the player's inventory
                player.getInventory().setItemInMainHand(null);

                // Trigger the sandstorm
                triggerSandstorm(player);

                // Give the item back after the sandstorm with a cooldown
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().addItem(createSandstormItem());
                        cooldownPlayers.remove(player.getUniqueId());
                    }
                }.runTaskLater(HeroCraft.getPlugin(), 20 * 10); // 10 seconds delay

                // Add the player to cooldown
                cooldownPlayers.add(player.getUniqueId());

                // Remove cooldown after 5 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cooldownPlayers.remove(player.getUniqueId());
                    }
                }.runTaskLater(HeroCraft.getPlugin(), 20 * 5); // 5 seconds delay
            }
        }*/
    }

    private void triggerSandstorm(Player player) {

        // Damage nearby players and show particles
        new BukkitRunnable() {
            int duration = 5; // Sandstorm duration in seconds

            @Override
            public void run() {
                if (duration <= 0) {
                    cancel();
                    player.sendMessage(Constant.PREFIX + "§7Sandsturm geendet.");
                    return;
                }

                // Show particles around the player
                for (int i = 0; i < 10; i++) {
                    double angle = Math.toRadians(i * 36); // Spread blocks evenly in a circle
                    double x = Math.cos(angle) * 4; // Adjusted radius to 4
                    double z = Math.sin(angle) * 4;

                    FallingBlock sandBlock = player.getWorld().spawnFallingBlock(player.getLocation().add(x, 2, z), Material.SAND.createBlockData());
                    sandBlock.setVelocity(new Vector(-z * 0.5, 0.5, x * 0.5)); // Set velocity to spin the block
                    sandBlock.setDropItem(false); // Prevent dropping items
                }

                // Damage nearby entities
                player.getWorld().getEntities().stream()
                        .filter(entity -> entity != player && entity.getLocation().distance(player.getLocation()) <= 6) // Adjusted radius to 6
                        .forEach(entity -> {
                            ((LivingEntity) entity).damage(2); // Damage entities (2 health points)
                            if (entity instanceof Player) {
                                ((Player) entity).sendMessage(Constant.PREFIX + "§7Du bist in einem Sandsturm!");
                            }
                        });

                duration--;
            }
        }.runTaskTimer(HeroCraft.getPlugin(), 0, 20); // Runs every second
    }

    private ItemStack createSandstormItem() {
        return getItemsAdderItem("§4§lSandsturm");
    }

    private ItemStack getItemsAdderItem(String name) {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase(name)) {
                itemStack = i.getItemStack();
            }
        }
        return itemStack;
    }

}
