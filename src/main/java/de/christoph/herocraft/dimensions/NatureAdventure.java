package de.christoph.herocraft.dimensions;

import de.christoph.herocraft.HeroCraft;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class NatureAdventure extends Dimension {


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

    public static final Set<Material> YUMMY_FOODS = new HashSet<>(Arrays.asList(
       Material.CAKE,
       Material.PUMPKIN_PIE,
       Material.COOKIE,
       Material.HONEY_BOTTLE,
       Material.SWEET_BERRIES
    ));

    public static final Set<Material> UNYUMMY_FOODS = new HashSet<>(Arrays.asList(
            Material.ROTTEN_FLESH,
            Material.SPIDER_EYE,
            Material.POISONOUS_POTATO,
            Material.PUFFERFISH,
            Material.RABBIT,
            Material.CHICKEN,
            Material.BEEF,
            Material.MUTTON,
            Material.COD,
            Material.SALMON
    ));

    public static final Set<Material> NICE_BLOCKS = new HashSet<>(Arrays.asList(
            Material.WHITE_WOOL,
            Material.BLACK_WOOL,
            Material.BLUE_WOOL,
            Material.CYAN_WOOL,
            Material.BROWN_WOOL,
            Material.GRAY_WOOL,
            Material.LIGHT_BLUE_WOOL,
            Material.LIGHT_GRAY_WOOL,
            Material.MAGENTA_WOOL,
            Material.LIME_WOOL,
            Material.ORANGE_WOOL,
            Material.PINK_WOOL,
            Material.PURPLE_WOOL,
            Material.YELLOW_WOOL,
            Material.RED_WOOL
    ));

    public static final Set<Material> UNNICE_BLOCKS = new HashSet<>(Arrays.asList(
            Material.STONE,
            Material.COBBLED_DEEPSLATE,
            Material.DEEPSLATE,
            Material.NETHERRACK,
            Material.NETHER_BRICK,
            Material.STONE_BRICKS,
            Material.BRICK_WALL,
            Material.COBBLESTONE,
            Material.COBBLED_DEEPSLATE_SLAB,
            Material.COBBLED_DEEPSLATE_STAIRS,
            Material.STONE_SLAB,
            Material.STONE_STAIRS,
            Material.DEEPSLATE_BRICKS,
            Material.DEEPSLATE_BRICK_SLAB,
            Material.DEEPSLATE_BRICK_STAIRS,
            Material.NETHER_BRICK_SLAB,
            Material.NETHER_BRICK_STAIRS,
            Material.BRICK_SLAB,
            Material.BRICK_WALL,
            Material.COBBLESTONE_SLAB,
            Material.COBBLESTONE_STAIRS
    ));

    public HashMap<Player, Integer> blocksPlayers = new HashMap<>();

    public HashMap<Player, Integer> niceScala = new HashMap<>();
    // - Biom, + Wolle, - Stein, + Schlafen, + Süßes Essen, - Gesundes Essen, - Monster, + Friedliche Tiere, - Abbauen, - Schaden, - Dunkel, - Laufen, + Zug oder Boot fahren

    public HashMap<Player, Integer> tiredScala = new HashMap<>();
    // - Wüsten Biom, - Laufen, -Bauen/Abbauen/Kämpfen, - Schwimmen, - Springen, + AFK stehen, + Zug fahren, + Elytra fliegen, + Schlafen

    public HashMap<Player, Integer> foodScala = new HashMap<>();
    // - Honig, - Süßbeeren, - Rohes, - Giftige Kartoffeln, - Keks, - Seetank, - Verottetes Fleisch, - Kuchen, - Spinnenauge, + Wasser, + Milch, + Suppen, + Gebratenes, + Brot, + Melone, + Apfel, + Ofenkartoffel, + Karotte

    public NatureAdventure() {
        super("Natur Wunder",
                "nature",
                "Spannende und verbesserte Natur mit verschiedenen Biomen",
                new String[]{
                    "Wenig freie Fläche",
                    "Wohlfühl Skala",
                    "Erschöpfung",
                    "Ausgewogene Ernährung"
                },
                new String[]{
                    "Geist",
                    "Zombie Mutant",
                    "BOSS: Stein Golem"
                },
                Material.ROSE_BUSH
            );
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            removeNiceScala(player, (int)Math.round(event.getDamage()));
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Material food = event.getItem().getType();
        if (YUMMY_FOODS.contains(food)) {
            addNiceScala(event.getPlayer(), 15);
        } else if (UNYUMMY_FOODS.contains(food)) {
            removeNiceScala(event.getPlayer(), 3);
        }

        if(HEALTHY_FOODS.contains(food)) {
            addFoodScala(event.getPlayer(), 15);
        } else if(UNHEALTHY_FOODS.contains(food)) {
            removeFoodScala(event.getPlayer(), 15);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        removeNiceScala((Player) event.getEntity(), 1);
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedLeaveEvent event) {
        if(tiredScala.containsKey(event.getPlayer())) {
            tiredScala.put(event.getPlayer(), 100);
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

    @Override
    public void onDimensionEntered(Player player) {
        if(!HeroCraft.getPlugin().getConfig().contains("NatureAdventure.Nice." + player.getUniqueId().toString())) {
            niceScala.put(player, 50);
            tiredScala.put(player, 50);
            foodScala.put(player, 50);
        } else {
            niceScala.put(player, HeroCraft.getPlugin().getConfig().getInt("NatureAdventure.Nice." + player.getUniqueId().toString()));
            tiredScala.put(player, HeroCraft.getPlugin().getConfig().getInt("NatureAdventure.Tired." + player.getUniqueId().toString()));
            foodScala.put(player, HeroCraft.getPlugin().getConfig().getInt("NatureAdventure.Food." + player.getUniqueId().toString()));
        }
        setBossBarsForPlayer(player);
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

    private void addNiceScala(Player player, int amount) {
        if(niceScala.containsKey(player)) {
            if(amount < 100) {
                if((niceScala.get(player) + amount) < 100)
                    niceScala.put(player, niceScala.get(player) + amount);
                else
                    niceScala.put(player, 100);
            }
        }
    }

    private void addTiredScala(Player player, int amount) {
        if(tiredScala.containsKey(player)) {
            if(amount < 100) {
                if(tiredScala.get(player) > amount)
                    tiredScala.put(player, tiredScala.get(player) - amount);
                else
                    tiredScala.put(player, 0);
            }
        }
    }

    private void removeNiceScala(Player player, int amount) {
        if(niceScala.containsKey(player)) {
            if(amount > 0) {
                if(niceScala.get(player) > amount)
                    niceScala.put(player, niceScala.get(player) - amount);
                else
                    niceScala.put(player, 0);
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


    @Override
    public void onTick() {
        for(Player player : getDimensionPlayers()) {
            //if(isPlayerAFK(player))
               // continue;
            checkForNice(player);
            checkForTired(player);
            setBossBarsForPlayer(player);
        }
    }

    @Override
    public void onScoreboardTick() {
        for(Player player : getDimensionPlayers()) {
            setBossBarsForPlayer(player);
        }
    }

    public void checkForTired(Player player) {
        Biome biome = player.getWorld().getBiome(player.getLocation());
        if(biome.equals(Biome.DESERT)) {
            removeTiredScala(player, 1);
        }
        if(player.isSprinting()) {
            if(!isPlayerRiding(player))
                removeNiceScala(player, 1);
        }
        if(player.isSwimming()) {
            removeTiredScala(player, 2);
        }
        if(player.isSleeping()) {
            removeTiredScala(player, 8);
        }
        if(isPlayerRiding(player)) {
            addNiceScala(player, 5);
        }
    }

    public void checkForNice(Player player) {
        Biome biome = player.getWorld().getBiome(player.getLocation());
        if(biome.equals(Biome.DESERT) || biome.toString().contains("SNOWY")) {
            removeNiceScala(player, 1);
        }
        if(UNNICE_BLOCKS.contains(player.getLocation().getBlock().getType())) {
            removeNiceScala(player, 1);
        } else if(NICE_BLOCKS.contains(player.getLocation().getBlock().getType())) {
            addNiceScala(player, 12);
        }
        for(Entity i : player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5)) {
            if(i instanceof Monster) {
                removeNiceScala(player, 1);
            } else if(i instanceof Animals) {
                addNiceScala(player, 12);
            }
        }
        if(isInDarkEnvironment(player)) {
            removeNiceScala(player, 1);
        }
        if(player.isSprinting()) {
            if(!isPlayerRiding(player))
                removeNiceScala(player, 1);
        }
        if(isPlayerRiding(player)) {
            addNiceScala(player, 8);
        }
        if(player.isSwimming()) {
            addNiceScala(player, 8);
        }
    }

    public static boolean isPlayerRiding(Player player) {
        Entity vehicle = player.getVehicle();

        if (vehicle == null) {
            return false;
        }

        switch (vehicle.getType()) {
            case MINECART:
            case HORSE:
            case BOAT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPlayerAFK(Player player) {
        long lastActive = player.getLastPlayed();
        long currentTime = System.currentTimeMillis();

        return (currentTime - lastActive) > 3 * 60 * 1000;
    }

    public boolean isInDarkEnvironment(Player player) {
        Location location = player.getLocation();
        Block block = location.getBlock();
        int lightLevel = block.getLightLevel();
        int darknessThreshold = 8;
        return lightLevel < darknessThreshold;
    }

    private void setBossBarsForPlayer(Player player) {
        String nice;
        String tired;
        String food;
        if(niceScala.get(player) < 20) {
            if(niceScala.get(player) <= 5) {
                player.damage(2);
            }
            nice = "§cWohlfühl Skala";
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.STRENGTH);
        } else if(niceScala.get(player) >= 90) {
            nice = "§aWohlfühl Skala";
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.WEAKNESS);
        } else {
            nice = "§7Wohlfühl Skala";
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.removePotionEffect(PotionEffectType.STRENGTH);
        }
        if(tiredScala.get(player) < 20) {
            if(tiredScala.get(player) <= 5) {
                player.damage(6);
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
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        }
        if(foodScala.get(player) < 20) {
            if(foodScala.get(player) <= 5) {
                player.damage(6);
            }
            food = "§cErnährung";
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        } else if(foodScala.get(player) >= 90) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*60*3, 3));
            player.removePotionEffect(PotionEffectType.POISON);
            food = "§aErnährung";
        } else {
            player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
            player.removePotionEffect(PotionEffectType.POISON);
            food = "§7Ernährung";
        }
        sendActionBar(player, nice + " (§e" + niceScala.get(player) + "/100) §0 | " + tired + " (§e" + tiredScala.get(player) + "/100) §0| " + food + " (§e" + foodScala.get(player) + "/100)");
    }

    @Override
    public void onDimensionLeaved(Player player) {
        HeroCraft.getPlugin().getConfig().set("NatureAdventure.Nice." + player.getUniqueId().toString(), niceScala.get(player));
        HeroCraft.getPlugin().getConfig().set("NatureAdventure.Tired." + player.getUniqueId().toString(), tiredScala.get(player));
        HeroCraft.getPlugin().getConfig().set("NatureAdventure.Food." + player.getUniqueId().toString(), foodScala.get(player));
        HeroCraft.getPlugin().saveConfig();
        niceScala.remove(player);
        tiredScala.remove(player);
        foodScala.remove(player);
        blocksPlayers.remove(player);
    }

    @EventHandler
    public void onCustomMobsDrop(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if(activeMob.getName().equalsIgnoreCase("§fForestMonster")) {
                int random = new Random().nextInt(10);
                if(random >= 5) {
                    entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), HeroCraft.getItemsAdderItem("§4§lWald Ei"));
                } else {
                    entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.OAK_SAPLING));
                }
            } else if(activeMob.getName().equalsIgnoreCase("§fGeist")) {
                entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.PHANTOM_MEMBRANE));
            } else if(activeMob.getName().equalsIgnoreCase("§fStoneGolem")) {
                entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), HeroCraft.getItemsAdderItem("§4§lDark Crystal"));
            }
        });
    }

    @EventHandler
    public void onStoneGolemRitual(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        new BukkitRunnable(){
            private int duration = 100;
            @Override
            public void run(){
                if (duration > 0 && item.isValid()){
                    if (item.getLocation().getBlock().getType() == Material.WATER_CAULDRON){
                        Block block = item.getLocation().getBlock();
                        Player player = event.getPlayer();
                        if (block.getType() != Material.WATER_CAULDRON) {
                            return;
                        }
                        if(!item.getItemStack().hasItemMeta())
                            return;
                        if(!item.getItemStack().getItemMeta().hasDisplayName())
                            return;
                        if(!item.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lWald Ei"))
                            return;
                        if(!player.getWorld().getBlockAt(block.getLocation().add(0, -1, 0)).getType().equals(Material.DIAMOND_BLOCK))
                            return;
                        if(!player.getWorld().getBlockAt(block.getLocation().add(1, -1, 0)).getType().equals(Material.COBBLESTONE))
                            return;
                        if(!player.getWorld().getBlockAt(block.getLocation().add(-1, -1, 0)).getType().equals(Material.COBBLESTONE))
                            return;
                        if(!player.getWorld().getBlockAt(block.getLocation().add(0, -1, 1)).getType().equals(Material.COBBLESTONE))
                            return;
                        if(!player.getWorld().getBlockAt(block.getLocation().add(0, -1, -1)).getType().equals(Material.COBBLESTONE))
                            return;
                        item.remove();
                        spawnCauldronParticles(block.getLocation(), Particle.LARGE_SMOKE, 20);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                spawnCauldronParticles(block.getLocation(), Particle.LARGE_SMOKE, 20);
                            }
                        }, 20);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                spawnCauldronParticles(block.getLocation(), Particle.LARGE_SMOKE, 20);
                            }
                        }, 20*2);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                player.getWorld().getBlockAt(block.getLocation().add(0, -1, 0)).setType(Material.STONE);
                                player.getWorld().strikeLightning(block.getLocation());
                                MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("stonegolem").orElse(null);
                                mob.spawn(BukkitAdapter.adapt(block.getLocation().add(0, 10, 0)),1);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                                    @Override
                                    public void run() {
                                        MythicMob mob1 = MythicBukkit.inst().getMobManager().getMythicMob("geist").orElse(null);
                                        mob1.spawn(BukkitAdapter.adapt(player.getLocation().add(0, 5, 0)),1);
                                        mob1.spawn(BukkitAdapter.adapt(player.getLocation().add(0, 5, 0)),1);
                                        mob1.spawn(BukkitAdapter.adapt(player.getLocation().add(0, 5, 0)),1);
                                        mob1.spawn(BukkitAdapter.adapt(player.getLocation().add(0, 5, 0)),1);
                                        mob1.spawn(BukkitAdapter.adapt(player.getLocation().add(0, 5, 0)),1);
                                        player.getWorld().strikeLightning(player.getLocation());
                                    }
                                }, 20*4);
                            }
                        }, 20*3);
                        cancel();
                    }
                    duration--;
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(HeroCraft.getPlugin(), 1L, 1L);
    }

    @EventHandler
    public void onBossDamageCauseFireAspect(EntityDamageEvent event) {
        if(!event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) && !event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) && !event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            return;
        Entity entity = event.getEntity();
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        optActiveMob.ifPresent(activeMob -> {
            if(activeMob.getName().equalsIgnoreCase("§fStoneGolem")) {
                event.setCancelled(true);
            }
        });
    }

    public void spawnCauldronParticles(Location cauldronLocation, Particle particleType, int count) {
        World world = cauldronLocation.getWorld();
        if (world == null) return;
        double x = cauldronLocation.getX() + 0.5;
        double y = cauldronLocation.getY() + 1.0;
        double z = cauldronLocation.getZ() + 0.5;
        world.spawnParticle(particleType, x, y, z, count, 0.3, 0.3, 0.3, 0.01);
    }


}
