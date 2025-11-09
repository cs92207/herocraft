package de.christoph.herocraft.dimensions;

import de.anyblocks.api.AnyBlocksAPI;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.ItemBuilder;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class BlackDessert extends Dimension {

    // Dark Zombie Natural Spawnen gucken
    // Danach: Dark Shadow modelieren und einfügen (Rezepte und Drops erst nach allen Models)

    public HashMap<Player, Integer> thirstScala = new HashMap<>();
    public HashMap<Player, Integer> tiredScala = new HashMap<>();
    public HashMap<Player, Integer> foodScala = new HashMap<>();
    public HashMap<Player, Integer> blocksPlayers = new HashMap<>();

    private static final EnumSet<Material> COLD_BLOCKS = EnumSet.of(
            Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE, Material.SNOW, Material.SNOW_BLOCK, Material.POWDER_SNOW
    );

    private static final Set<Material> HEALTHY_FOODS = new HashSet<>(Arrays.asList(
            Material.APPLE,
            Material.GOLDEN_APPLE,
            Material.CARROT,
            Material.POTATO,
            Material.BAKED_POTATO,
            Material.BEETROOT,
            Material.BEETROOT_SOUP,
            Material.MUSHROOM_STEW,
            Material.MELON_SLICE,
            Material.COOKED_CHICKEN,
            Material.COOKED_BEEF,
            Material.COOKED_MUTTON,
            Material.COOKED_RABBIT,
            Material.COOKED_SALMON,
            Material.COOKED_COD,
            Material.BREAD
    ));

    // Set of unhealthy foods
    private static final Set<Material> UNHEALTHY_FOODS = new HashSet<>(Arrays.asList(
            Material.ROTTEN_FLESH,
            Material.SPIDER_EYE,
            Material.POISONOUS_POTATO,
            Material.PUFFERFISH,
            Material.CAKE,
            Material.POISONOUS_POTATO,
            Material.PUMPKIN_PIE,
            Material.COOKIE,
            Material.HONEY_BOTTLE,
            Material.SWEET_BERRIES,
            Material.RABBIT,
            Material.CHICKEN,
            Material.BEEF,
            Material.MUTTON,
            Material.COD,
            Material.SALMON
    ));

    public BlackDessert() {
        super("Schwarze Wüste",
            "blackDessert",
            "Eine Wüste bestehend aus schwarzen Stein und schweren Überlebensverhältnissen. Perfekt für eine neue Herausforderung und neue Baustile",
            new String[]{
                "Durst",
                "Stärkere Monster",
                "Wenige Ressourcen",
                "Erschöpfung",
                "Ausgewogene Ernährung",
                "Langsamere Heilung",
            },
            new String[]{
                "Dark Zombie",
                "Dark Shadow",
                "BOSS: Dark Golem",
            },
            Material.BLACKSTONE
        );
    }

    @EventHandler
    public void onPlayerDrinkWater(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.POTION && item.getItemMeta() != null) {
            addThirstScala(player, 20);
        }
    }

    @EventHandler
    public void onPlayerResistence(EntityRegainHealthEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            event.setAmount(event.getAmount() - 0.25);
        }
    }

    private void setBossBarsForPlayer(Player player) {
        String thirst;
        String tired;
        String food;

        if(thirstScala.get(player) < 20) {
            if(thirstScala.get(player) <= 5) {
                player.damage(2);
            }
            thirst = "§cDurst";
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.LUCK);
        } else if(thirstScala.get(player) >= 90) {
            thirst = "§aDurst";
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.WEAKNESS);
        } else {
            thirst = "§7Durst";
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.removePotionEffect(PotionEffectType.LUCK);
        }

        if(tiredScala.get(player) < 20) {
            if(tiredScala.get(player) <= 5) {
                player.damage(2);
            }
            tired = "§cErschöpfung";
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.SPEED);
        } else if(tiredScala.get(player) >= 90) {
            tired = "§aErschöpfung";
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        } else {
            tired = "§7Erschöpfung";
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            player.removePotionEffect(PotionEffectType.SPEED);
        }

        if(foodScala.get(player) < 20) {
            if(foodScala.get(player) <= 5) {
                player.damage(2);
            }
            food = "§cErnährung";
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        } else if(foodScala.get(player) >= 90) {
            food = "§aErnährung";
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.POISON);
        } else {
            food = "§7Ernährung";
            player.removePotionEffect(PotionEffectType.POISON);
            player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        }

        sendActionBar(player, thirst + " §e(" + thirstScala.get(player) + "/100) §0 | " + tired + " §e(" + tiredScala.get(player) + "/100) §0| " + food + " §e(" + foodScala.get(player) + "/100)");
    }

    @Override
    public void onDimensionEntered(Player player) {
        if(!HeroCraft.getPlugin().getConfig().contains("BlackDessert.Thirst." + player.getUniqueId().toString())) {
            thirstScala.put(player, 50);
            tiredScala.put(player, 50);
            foodScala.put(player, 50);
        } else {
            thirstScala.put(player, HeroCraft.getPlugin().getConfig().getInt("BlackDessert.Thirst." + player.getUniqueId().toString()));
            tiredScala.put(player, HeroCraft.getPlugin().getConfig().getInt("BlackDessert.Tired." + player.getUniqueId().toString()));
            foodScala.put(player, HeroCraft.getPlugin().getConfig().getInt("BlackDessert.Food." + player.getUniqueId().toString()));
        }
    }

    @Override
    public void onDimensionLeaved(Player player) {
        HeroCraft.getPlugin().getConfig().set("BlackDessert.Tired." + player.getUniqueId().toString(), tiredScala.get(player));
        HeroCraft.getPlugin().getConfig().set("BlackDessert.Food." + player.getUniqueId().toString(), foodScala.get(player));
        HeroCraft.getPlugin().getConfig().set("BlackDessert.Thirst." + player.getUniqueId().toString(), thirstScala.get(player));
        HeroCraft.getPlugin().saveConfig();
        tiredScala.remove(player);
        foodScala.remove(player);
        thirstScala.remove(player);
        blocksPlayers.remove(player);
    }

    @Override
    public void onTick() {
        for(Player player : getDimensionPlayers()) {
            //if(isPlayerAFK(player))
            // continue;
            checkForTired(player);
            checkForThirst(player);
        }
    }

    @Override
    public void onScoreboardTick() {
        for(Player player : getDimensionPlayers()) {
            setBossBarsForPlayer(player);
        }
    }

    public void checkForThirst(Player player) {
        removeThirstScala(player, 1);
    }

    public void checkForTired(Player player) {
        Biome biome = player.getWorld().getBiome(player.getLocation());
        if(player.isSprinting()) {
            removeTiredScala(player, 1);
        }
        if(player.isSwimming()) {
            removeTiredScala(player, 2);
        }
        if(player.isSleeping()) {
            removeTiredScala(player, 8);
        }
    }

    @EventHandler
    public void onPlayerMoveBoss(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        for(Entity entity : player.getNearbyEntities(5, 5, 5)) {
            Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
            optActiveMob.ifPresent(activeMob -> {
                if(activeMob.getName().equalsIgnoreCase("§fDarkGolem")) {
                    triggerKnockback(player, entity);
                }
            });
        }
        for(Entity entity : player.getNearbyEntities(15, 15, 15)) {
            Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
            optActiveMob.ifPresent(activeMob -> {
                if(activeMob.getName().equalsIgnoreCase("§fDarkGolem")) {
                    triggerRangedAttack(player, entity);
                }
            });
        }
    }

    private void triggerKnockback(Player player, Entity boss) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for(Entity i : player.getNearbyEntities(15, 15, 15)) {
                    Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(i.getUniqueId());
                    optActiveMob.ifPresent(activeMob -> {
                        if(activeMob.getName().equalsIgnoreCase("§fDarkGolem")) {
                            Vector direction = player.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize();
                            direction.multiply(1.5).setY(1);
                            player.setVelocity(direction);

                            // Play a sound and send a message
                            World world = boss.getWorld();
                            world.playSound(boss.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 1.0f);
                        }
                    });
                }
            }
        }, 20*2);
    }

    private void triggerRangedAttack(Player player, Entity boss) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for(Entity i : player.getNearbyEntities(15, 15, 15)) {
                    Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(i.getUniqueId());
                    optActiveMob.ifPresent(activeMob -> {
                        if(activeMob.getName().equalsIgnoreCase("§fDarkGolem")) {
                            player.damage(5);
                        }
                    });
                }
            }
        }, 20*2);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(blocksPlayers.containsKey(event.getPlayer())) {
            blocksPlayers.put(event.getPlayer(), blocksPlayers.get(event.getPlayer()) + 1);
            if(blocksPlayers.get(event.getPlayer()) > 10) {
                removeTiredScala(event.getPlayer(), 2);
                blocksPlayers.remove(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(blocksPlayers.containsKey(event.getPlayer())) {
            blocksPlayers.put(event.getPlayer(), blocksPlayers.get(event.getPlayer()) + 1);
            if(blocksPlayers.get(event.getPlayer()) > 10) {
                removeTiredScala(event.getPlayer(), 2);
                blocksPlayers.remove(event.getPlayer());
            }
        }
    }

    private void removeTiredScala(Player player, int amount) {
        if(tiredScala.containsKey(player)) {
            if(amount > 0) {
                if(tiredScala.get(player) > amount)
                    tiredScala.put(player, tiredScala.get(player) - amount);
                else
                    tiredScala.put(player, 0);
            }
        }
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedLeaveEvent event) {
        if(tiredScala.containsKey(event.getPlayer())) {
            tiredScala.put(event.getPlayer(), 100);
        }
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && item.getItemMeta() != null) {
            if(HEALTHY_FOODS.contains(item.getType())) {
                addFoodScala(player, 15);
            } else if(UNHEALTHY_FOODS.contains(item.getType())) {
                removeFoodScala(player, 15);
            }
        }
    }

    private void addTiredScala(Player player, int amount) {
        if(tiredScala.containsKey(player)) {
            if(amount < 100) {
                if((tiredScala.get(player) + amount) < 100)
                    tiredScala.put(player, tiredScala.get(player) + amount);
                else
                    tiredScala.put(player, 100);
            }
        }
    }

    private void addFoodScala(Player player, int amount) {
        if(foodScala.containsKey(player)) {
            if(amount < 100) {
                if((foodScala.get(player) + amount) < 100)
                    foodScala.put(player, foodScala.get(player) + amount);
                else
                    foodScala.put(player, 100);
            }
        }
    }

    private void removeFoodScala(Player player, int amount) {
        if(foodScala.containsKey(player)) {
            if(amount > 0) {
                if(foodScala.get(player) > amount)
                    foodScala.put(player, foodScala.get(player) - amount);
                else
                    foodScala.put(player, 0);
            }
        }
    }

    private void addThirstScala(Player player, int amount) {
        if(thirstScala.containsKey(player)) {
            if(amount < 100) {
                if((thirstScala.get(player) + amount) < 100)
                    thirstScala.put(player, thirstScala.get(player) + amount);
                else
                    thirstScala.put(player, 100);
            }
        }
    }

    private void removeThirstScala(Player player, int amount) {
        if(thirstScala.containsKey(player)) {
            if(amount > 0) {
                if(thirstScala.get(player) > amount)
                    thirstScala.put(player, thirstScala.get(player) - amount);
                else
                    thirstScala.put(player, 0);
            }
        }
    }

    @EventHandler
    public void onCustomMobsDrop(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if(activeMob.getName().equalsIgnoreCase("§fBlackZombie")) {
                int random = new Random().nextInt(10);
                if(random >= 5) {
                    entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), HeroCraft.getItemsAdderItem("§4§lBlutendes Auge"));
                } else {
                    entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.COAL));
                }
            } else if(activeMob.getName().equalsIgnoreCase("§fDarkShadow")) {
                entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.PHANTOM_MEMBRANE));
            } else if(activeMob.getName().equalsIgnoreCase("§fDarkGolem")) {
                entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), HeroCraft.getItemsAdderItem("§4§lDunkles Herz"));
            }
        });
    }

    @EventHandler
    public void onBossSpawnRitual(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if(!player.getWorld().getName().equalsIgnoreCase("blackDessert"))
            return;
        if(!player.getInventory().getItemInMainHand().hasItemMeta())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lBlutendes Auge"))
            return;
        if(!isEntityOnBlock(entity, Material.NETHERITE_BLOCK))
            return;
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if(activeMob.getName().equalsIgnoreCase("§fBlackZombie")) {
                ItemStack eyes = player.getInventory().getItemInMainHand();
                if(eyes.getAmount() > 1) {
                    eyes.setAmount(eyes.getAmount() - 1);
                    player.getInventory().remove(player.getInventory().getItemInMainHand());
                    player.getInventory().setItemInMainHand(eyes);
                } else {
                    player.getInventory().remove(player.getInventory().getItemInMainHand());
                }
                entity.getLocation().getWorld().strikeLightning(entity.getLocation());
                MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("darkgolem").orElse(null);
                mob.spawn(BukkitAdapter.adapt(entity.getLocation().add(0, 10, 0)),1);
                ((LivingEntity) entity).setHealth(0);
            }
        });
    }

    @EventHandler
    public void onBossDamageCauseFireAspect(EntityDamageEvent event) {
        if(!event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) && !event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) && !event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            return;
        Entity entity = event.getEntity();
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if(activeMob.getName().equalsIgnoreCase("§fDarkGolem")) {
                event.setCancelled(true);
            }
        });
    }

    public boolean isEntityOnBlock(Entity entity, Material blockMaterial) {
        Location entityLocation = entity.getLocation();
        Location blockBelowLocation = entityLocation.clone().subtract(0, 1, 0);
        Block blockBelow = blockBelowLocation.getBlock();
        return blockBelow.getType() == blockMaterial;
    }

}
