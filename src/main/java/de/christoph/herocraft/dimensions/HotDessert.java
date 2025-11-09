package de.christoph.herocraft.dimensions;

import de.christoph.herocraft.HeroCraft;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class HotDessert extends Dimension {

    public HashMap<Player, Integer> thirstScala = new HashMap<>();
    public HashMap<Player, Integer> tiredScala = new HashMap<>();
    public HashMap<Player, Integer> foodScala = new HashMap<>();
    public HashMap<Player, Integer> hotScala = new HashMap<>();
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

    @EventHandler
    public void onPlayerDrinkWater(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.POTION && item.getItemMeta() != null) {
            addThirstScala(player, 20);
        }
    }

    public HotDessert() {
        super("Heiße Wüste",
            "dessert",
            "Eine heiße Wüste mit schweren Überlebungsbedingungen \n für beste Herausforderung",
                new String[]{
                    "Durst",
                    "Stärkere Monster",
                    "Wenige Ressourcen",
                    "Erschöpfung",
                    "Ausgewogene Ernährung",
                    "Überhitzung",
                    "Langsamere Heilung"
                },
                new String[]{
                    "Skorpion",
                    "Mumie",
                    "BOSS: Sandman"
                },
                Material.SAND
            );
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
        String hot;

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

        if(hotScala.get(player) < 20) {
            if(hotScala.get(player) <= 5) {
                player.damage(2);
            }
            hot = "§cÜberhitzung";
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        } else if(hotScala.get(player) >= 90) {
            hot = "§aÜberhitzung";
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.WEAKNESS);
        } else {
            hot = "§7Überhitzung";
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }

        sendActionBar(player, thirst + " §e(" + thirstScala.get(player) + "/100) §0 | " + tired + " §e(" + tiredScala.get(player) + "/100) §0| " + food + " §e(" + foodScala.get(player) + "/100) §0| " + hot + " §e(" + hotScala.get(player) + "/100)");
    }

    @Override
    public void onDimensionEntered(Player player) {
        if(!HeroCraft.getPlugin().getConfig().contains("HotDessert.Thirst." + player.getUniqueId().toString())) {
            hotScala.put(player, 50);
            thirstScala.put(player, 50);
            tiredScala.put(player, 50);
            foodScala.put(player, 50);
        } else {
            hotScala.put(player, HeroCraft.getPlugin().getConfig().getInt("HotDessert.Hot." + player.getUniqueId().toString()));
            thirstScala.put(player, HeroCraft.getPlugin().getConfig().getInt("HotDessert.Thirst." + player.getUniqueId().toString()));
            tiredScala.put(player, HeroCraft.getPlugin().getConfig().getInt("HotDessert.Tired." + player.getUniqueId().toString()));
            foodScala.put(player, HeroCraft.getPlugin().getConfig().getInt("HotDessert.Food." + player.getUniqueId().toString()));
        }
    }

    @Override
    public void onDimensionLeaved(Player player) {
        HeroCraft.getPlugin().getConfig().set("HotDessert.Hot." + player.getUniqueId().toString(), hotScala.get(player));
        HeroCraft.getPlugin().getConfig().set("HotDessert.Tired." + player.getUniqueId().toString(), tiredScala.get(player));
        HeroCraft.getPlugin().getConfig().set("HotDessert.Food." + player.getUniqueId().toString(), foodScala.get(player));
        HeroCraft.getPlugin().getConfig().set("HotDessert.Thirst." + player.getUniqueId().toString(), thirstScala.get(player));
        HeroCraft.getPlugin().saveConfig();
        hotScala.remove(player);
        tiredScala.remove(player);
        foodScala.remove(player);
        thirstScala.remove(player);
        blocksPlayers.remove(player);
    }

    @Override
    public void onTick() {
        for(Player player : getDimensionPlayers()) {
            setBossBarsForPlayer(player);
            //if(isPlayerAFK(player))
            // continue;
            checkForTired(player);
            checkForThirst(player);
            checkForHotRemove(player);
            checkForHotAddNaturally(player);
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

    public void checkForHotRemove(Player player) {
        if(isNight(player.getWorld()))
            return;
        if(isOnColdBlock(player))
            return;
        if(isInWater(player))
            return;
        removeHotScala(player, 1);
    }

    public void checkForHotAddNaturally(Player player) {
        if(isOnColdBlock(player)) {
            addHotScala(player, 8);
        }
        if(isInWater(player)) {
            addHotScala(player, 4);
        }
    }

    public boolean isOnColdBlock(Player player) {
        Location location = player.getLocation();
        Material blockType = location.getBlock().getType();
        return COLD_BLOCKS.contains(blockType);
    }

    public boolean isInWater(Player player) {
        Location location = player.getLocation();
        Material blockType = location.getBlock().getType();
        return blockType == Material.WATER || blockType == Material.KELP || blockType == Material.SEAGRASS
                || blockType == Material.BUBBLE_COLUMN || blockType == Material.KELP_PLANT;
    }

    public boolean isNight(World world) {
        if (world == null) {
            return false;
        }
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
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

    private void addHotScala(Player player, int amount) {
        if(hotScala.containsKey(player)) {
            if(amount < 100) {
                if((hotScala.get(player) + amount) < 100)
                    hotScala.put(player, hotScala.get(player) + amount);
                else
                    hotScala.put(player, 100);
            }
        }
    }

    private void removeHotScala(Player player, int amount) {
        if(hotScala.containsKey(player)) {
            if(amount > 0) {
                if(hotScala.get(player) > amount)
                    hotScala.put(player, hotScala.get(player) - amount);
                else
                    hotScala.put(player, 0);
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
    public void onSandManMobRitual(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lIce Cream")) {
                if(player.getInventory().getItemInMainHand().getAmount() == 1) {
                    player.getInventory().setItemInMainHand(null);
                } else {
                    int amount = player.getInventory().getItemInMainHand().getAmount();
                    amount--;
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    itemStack.setAmount(amount);
                    player.getInventory().setItemInMainHand(itemStack);
                }
                addHotScala(player, 30);
            }
        }
        if(!player.getInventory().getItemInMainHand().getType().equals(Material.WITHER_ROSE))
            return;
        if(event.getClickedBlock() == null)
            return;
        if(!event.getClickedBlock().getType().equals(Material.BEACON))
            return;
        if(!player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, 1, 0)).getType().equals(Material.CARVED_PUMPKIN))
            return;
        if(!player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(1, 0, 0)).getType().equals(Material.SOUL_SAND) && !player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, 0, 1)).getType().equals(Material.SOUL_SAND))
            return;
        if(!player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(-1, 0, 0)).getType().equals(Material.SOUL_SAND) && !player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, 0, -1)).getType().equals(Material.SOUL_SAND))
            return;
        if(!player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, -1, 0)).getType().equals(Material.SUSPICIOUS_SAND))
          return;
        ItemStack flesh = player.getInventory().getItemInMainHand();
        if(flesh.getAmount() > 1) {
            flesh.setAmount(flesh.getAmount() - 1);
            player.getInventory().remove(player.getInventory().getItemInMainHand());
            player.getInventory().setItemInMainHand(flesh);
        } else {
            player.getInventory().remove(player.getInventory().getItemInMainHand());
        }
        event.setCancelled(true);
        player.getWorld().strikeLightning(event.getClickedBlock().getLocation());
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation()).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, 1, 0)).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(1, 0, 0)).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(-1, 0, 0)).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, 0, 1)).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, 0, -1)).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, -1, 0)).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(1, 1, 0)).breakNaturally(null);
        player.getWorld().getBlockAt(event.getClickedBlock().getLocation().add(0, 1, 1)).breakNaturally(null);
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("sandman").orElse(null);
        mob.spawn(BukkitAdapter.adapt(event.getClickedBlock().getLocation().add(0, 10, 0)),1);
    }

    @EventHandler
    public void onBossDamageCauseFireAspect(EntityDamageEvent event) {
        if(!event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) && !event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) && !event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            return;
        Entity entity = event.getEntity();
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if(activeMob.getName().equalsIgnoreCase("§fSandmann")) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onCustomMobsDrop(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if(activeMob.getName().equalsIgnoreCase("§fMumie")) {
                int random = new Random().nextInt(10);
                if(random >= 5) {
                    entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.WITHER_ROSE));
                    entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.SOUL_SAND));
                } else {
                    entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.ROTTEN_FLESH));
                }
            } else if(activeMob.getName().equalsIgnoreCase("§fSkorpion")) {
                entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.SPIDER_EYE));
            } else if(activeMob.getName().equalsIgnoreCase("§fSandmann")) {
                entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), HeroCraft.getItemsAdderItem("§4§lWüsten Herz"));
            }
        });
    }

    @EventHandler
    public void onPlayerMoveBoss(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        for(Entity entity : player.getNearbyEntities(5, 5, 5)) {
            Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
            optActiveMob.ifPresent(activeMob -> {
                if(activeMob.getName().equalsIgnoreCase("§fSandmann")) {
                    triggerDamage(player, entity);
                }
            });
        }
        for(Entity entity : player.getNearbyEntities(15, 15, 15)) {
            Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
            optActiveMob.ifPresent(activeMob -> {
                if(activeMob.getName().equalsIgnoreCase("§fSandmann")) {
                    triggerRangedAttack(player, entity);
                }
            });
        }
    }

    private void triggerRangedAttack(Player player, Entity entity) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for(Entity i : player.getNearbyEntities(15, 15, 15)) {
                    Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(i.getUniqueId());
                    optActiveMob.ifPresent(activeMob -> {
                        if(activeMob.getName().equalsIgnoreCase("§fSandmann")) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 20));
                        } else {
                            player.removePotionEffect(PotionEffectType.BLINDNESS);
                        }
                    });
                }
            }
        }, 20*2);
    }

    private void triggerDamage(Player player, Entity entity) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for(Entity i : player.getNearbyEntities(6, 6, 6)) {
                    Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(i.getUniqueId());
                    optActiveMob.ifPresent(activeMob -> {
                        if(activeMob.getName().equalsIgnoreCase("§fSandmann")) {
                            player.damage(3);
                        }
                    });
                }
            }
        }, 20*2);
    }

}
