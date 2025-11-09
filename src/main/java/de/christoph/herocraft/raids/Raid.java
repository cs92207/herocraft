package de.christoph.herocraft.raids;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Raid {

    private Land land;
    private int landRaidLevel;
    private ArrayList<UUID> raidEntities;
    private int killedRaidEntities;
    private int mustKillRaidEntities;
    private int wave;
    private int maxWaves;
    private BossBar bossBar;
    private int deaths;
    private int maxDeaths;

    private String victimEntity;
    private int taskID;

    public static final EntityType[] HOSTILE_DAYLIGHT_SAFE_MOBS = new EntityType[]{
        EntityType.PILLAGER,
        EntityType.RAVAGER,
        EntityType.VINDICATOR
    };

    public Raid(Land land) {
        this.land = land;
        this.raidEntities = new ArrayList<>();
        this.landRaidLevel = loadLandRaidLevel();
        this.wave = 0;
    }

    public ArrayList<Player> getPlayersInRaid() {
        ArrayList<Player> list = new ArrayList<>();
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(land.canBuild((all))) {
                list.add(all);
            }
        }
        return list;
    }

    /*
    * ArrayList<String> names = new ArrayList<>();
        names.addAll(List.of(land.getMemberNames()));
        System.out.println("Member Names");
        for(String i : land.getAllLandNames()) {
            System.out.println(i);
        }
        System.out.println("Member Names");


        names.addAll(List.of(land.getCoFounderNames()));
        System.out.println("Co Names");
        for(String i : land.getCoFounderNames()) {
            System.out.println(i);
        }
        System.out.println("Co Names");


        System.out.println(land.getFounderName());
        System.out.println(land.getName());
        names.add(land.getFounderName());
        ArrayList<Player> playersInRaid = new ArrayList<>();
        for(String i : names) {
            Player mPlayer = Bukkit.getPlayer(i);
            if(mPlayer != null) {
                if(!playersInRaid.contains(mPlayer)) {
                    playersInRaid.add(mPlayer);
                }
            }
        }
        return playersInRaid;*/

    public void start() {
        maxWaves = landRaidLevel * 3;
        for(Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(Constant.PREFIX + "§7Das Land §e§l" + land.getName() + "§7 wird §c§langegriffen§7. Es kann geplündert werden!");
            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        for(Player all : getPlayersInRaid()) {
            all.sendTitle("§e§lAngriff!", "§7Dein Land wird angegriffen!");
            all.playSound(all.getLocation(), Sound.EVENT_RAID_HORN, 1, 1);
        }
        Villager villager = (Villager) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), land.getSpawnX(), land.getSpawnY(), land.getSpawnZ()), EntityType.VILLAGER);
        villager.setCustomName("§4§lOpfer");
        villager.setCustomNameVisible(true);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 500));
        victimEntity = villager.getUniqueId().toString();
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                startNewWave();
            }
        }, 20*4);
    }

    public void killAllRaidEntities() {
        for(UUID i : raidEntities) {
            Entity entity = Bukkit.getEntity(i);
            if(entity instanceof LivingEntity) {
                entity.addScoreboardTag("custom_silent_death");
                ((LivingEntity) entity).setHealth(0);
            }
        }
    }

    public void startWave() {
        deaths = 0;
        maxDeaths = getPlayersInRaid().size() * 2;
        killedRaidEntities = 0;
        killAllRaidEntities();
        double baseGrowth = 1.05; // Wachstum pro Stufe/Welle (leicht exponentiell)
        mustKillRaidEntities = (int) Math.round(
                Math.pow(baseGrowth, landRaidLevel + wave) * (landRaidLevel + wave)
        );
        mustKillRaidEntities += 5;
        if(mustKillRaidEntities > 750) {
            mustKillRaidEntities = 750;
        }
        setBossBar();
        for(Player all : getPlayersInRaid()) {
            all.sendTitle("§e§lWelle " + wave, "§7Töte §e" + mustKillRaidEntities);
            all.playSound(all.getLocation(), Sound.EVENT_RAID_HORN, 1, 1);
        }
        //startRaidScheduler();
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for(int i = -(mustKillRaidEntities); i < mustKillRaidEntities; i++) {
                    spawnWaveMob();
                }
            }
        }, 20*5);
    }

    /*public void startRaidScheduler() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(raidEntities != null && victimEntity != null && !victimEntity.isEmpty()) {
                    for(UUID all : raidEntities) {
                        Entity entity = Bukkit.getEntity(all);
                        if(entity != null && entity instanceof Pillager) {
                            System.out.println("test4" + "_" + entity.getLocation().getX() + "_" + entity.getLocation().getZ());
                            ((Pillager) entity).getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
                            ((Pillager) entity).setTarget((LivingEntity) Bukkit.getEntity(UUID.fromString(victimEntity)));
                            //entity.setVelocity(entity.getVelocity().multiply(5).setY(1));
                        }
                        if(entity != null && entity instanceof Vindicator) {
                            System.out.println("test4" + "_" + entity.getLocation().getX() + "_" + entity.getLocation().getZ());
                            ((Vindicator) entity).getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
                            ((Vindicator) entity).setTarget((LivingEntity) Bukkit.getEntity(UUID.fromString(victimEntity)));
                            entity.setVelocity(entity.getVelocity().multiply(5).setY(1));
                        }
                        if(entity != null && entity instanceof Ravager) {
                            System.out.println("test4" + "_" + entity.getLocation().getX() + "_" + entity.getLocation().getZ());
                            ((Ravager) entity).getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
                            ((Ravager) entity).setTarget((LivingEntity) Bukkit.getEntity(UUID.fromString(victimEntity)));
                            entity.setVelocity(entity.getVelocity().multiply(5).setY(1));
                        }
                        if(entity == null) {
                            continue;
                        }
                        Location baseLoc = entity.getLocation().clone();

                        // Liste der Offsets in X- und Z-Richtung (nur direkt angrenzende)
                        int[][] offsets = {
                                { 1, 0 },  // +X
                                {-1, 0 },  // -X
                                { 0, 1 },  // +Z
                                { 0,-1 }   // -Z
                        };

                        for (int[] offset : offsets) {
                            Location blockLoc = baseLoc.clone().add(offset[0], 1, offset[1]); // Block auf Bodenhöhe
                            Location blockLoc2 = baseLoc.clone().add(offset[0], 2, offset[1]); // Block auf Bodenhöhe

                            Block block = blockLoc.getBlock();
                            Block block2 = blockLoc2.getBlock();

                            if (block.getType() != Material.AIR && block.getType().isSolid()) {
                                Material originalType = block.getType();
                                BlockData originalData = block.getBlockData();

                                // Block entfernen
                                block.setType(Material.AIR);

                                // Nach 5 Sekunden wiederherstellen
                                Bukkit.getScheduler().runTaskLater(HeroCraft.getPlugin(), () -> {
                                    block.setType(originalType);
                                    block.setBlockData(originalData);
                                }, 100L);
                            }

                            if (block2.getType() != Material.AIR && block2.getType().isSolid()) {
                                Material originalType = block2.getType();
                                BlockData originalData = block2.getBlockData();

                                // Block entfernen
                                block2.setType(Material.AIR);

                                // Nach 5 Sekunden wiederherstellen
                                Bukkit.getScheduler().runTaskLater(HeroCraft.getPlugin(), () -> {
                                    block2.setType(originalType);
                                    block2.setBlockData(originalData);
                                }, 100L);
                            }
                        }
                    }
                }
            }
        }, 20*3, 20*3);
    }*/

    public void startNewWave() {
        wave++;
        if(wave >= maxWaves) {
            finishRaidSuccessfully();
            return;
        }
        startWave();
    }

    public void finishRaidFailed() {
        Bukkit.getScheduler().cancelTask(taskID);
        bossBar.removeAll();
        Entity entity = Bukkit.getEntity(UUID.fromString(victimEntity));
        if(entity != null && entity instanceof LivingEntity) {
            ((LivingEntity) entity).setHealth(0);
        }
        killAllRaidEntities();
        for(Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(Constant.PREFIX + "§7Das Land §e§l" + land.getName() + "§7 hat seinen Angriff §cnicht überstanden§7.");
            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        for(Player all : getPlayersInRaid()) {
            all.sendTitle("§c§lAngriff Fehlgeschlagen!", "§7Dein Land hat den Angriff nicht überstanden!");
            all.playSound(all.getLocation(), Sound.EVENT_RAID_HORN, 1, 1);
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage(Constant.PREFIX + "§0[§c-§0] §c" + (Constant.LAND_COINS_PER_RAID_FAILURE * landRaidLevel) + " Coins §7(Für dein Land)");
        }
        double landCoins = land.getCoins();
        landCoins = landCoins - (Constant.LAND_COINS_PER_RAID_FAILURE * landRaidLevel);
        if(landCoins < 0) {
            landCoins = 0;
        }
        land.setCoins(landCoins);
        HeroCraft.getPlugin().raidManager.getRaids().remove(this);
    }

    public void finishRaidSuccessfully() {
        Bukkit.getScheduler().cancelTask(taskID);
        addLandRaidLevel();
        bossBar.removeAll();
        killAllRaidEntities();
        for(Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(Constant.PREFIX + "§7Das Land §e§l" + land.getName() + "§7 hat seinen Angriff §aüberstanden§7.");
            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        for(Player all : getPlayersInRaid()) {
            all.sendTitle("§a§lAngriff Geschafft!", "§7Dein Land hat den Angriff überstanden!");
            all.playSound(all.getLocation(), Sound.EVENT_RAID_HORN, 1, 1);
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage("");
            all.sendMessage(Constant.PREFIX + "§0[§a+§0] " + (Constant.LAND_COINS_PER_RAID_SUCCESS * landRaidLevel) + " Coins §7(Für dein Land)");
            all.sendMessage(Constant.PREFIX + "§0[§a+§0] " + (Constant.ARMEE_COINS_PER_RAID_SUCCESS * landRaidLevel) + " Armee Coins §7(Für dein Land)");
        }
        land.setArmeeCoins(land.getArmeeCoins() + (Constant.ARMEE_COINS_PER_RAID_SUCCESS * landRaidLevel));
        land.setCoins(land.getCoins() + (Constant.LAND_COINS_PER_RAID_SUCCESS * landRaidLevel));
        HeroCraft.getPlugin().raidManager.getRaids().remove(this);
    }

    public void killedRaidEntity(Entity entity, int entityLevel) {
        spawnWaveMob();
        double armeeCoins = Constant.LAND_COINS_PER_RAID_MOB + entityLevel;
        land.setArmeeCoins(land.getArmeeCoins() + armeeCoins);
        for(Player i : getPlayersInRaid()) {
            i.sendMessage(Constant.PREFIX + "§0[§a+§0] §a" + armeeCoins + " Armee Coins §7(Für dein Land)");
        }
        raidEntities.remove(entity);
        killedRaidEntities++;
        setBossBar();
        if(killedRaidEntities >= mustKillRaidEntities) {
            startNewWave();
        }
    }

    public void onRaidPlayerLeave(Player player) {
        if(getPlayersInRaid().size() <= 0) {
            finishRaidFailed();
        }
    }

    public void deathFromRaidPlayer(Player player) {
        setBossBar();
        deaths++;
        if(deaths >= maxDeaths) {
            finishRaidFailed();
        }
    }

    public void setBossBar() {
        if(bossBar != null) {
            bossBar.removeAll();
        }
        bossBar = Bukkit.createBossBar("§e§lWelle " + wave + "/" + maxWaves + " §0(§c" + killedRaidEntities + "§0/§c" + mustKillRaidEntities + "§0) §7§l| §e§lTode: " + deaths + "/" + maxDeaths, BarColor.YELLOW, BarStyle.SOLID);
        for(Player all : getPlayersInRaid()) {
            bossBar.addPlayer(all);
        }
    }

    private void spawnWaveMob() {

        double landDistanceX = land.getX1() - land.getX2();
        if(landDistanceX < 0) {
            landDistanceX = landDistanceX * (-1);
        }

        double landDistanceZ = land.getX1() - land.getX2();
        if(landDistanceZ < 0) {
            landDistanceZ = landDistanceZ * (-1);
        }

        landDistanceZ /= 2;
        landDistanceX /= 2;

        landDistanceZ += 20;
        landDistanceX += 20;

        if(landDistanceZ > 100) {
            landDistanceZ = 100;
        }
        if(landDistanceX > 100) {
            landDistanceX = 100;
        }

        Random random = new Random();
        double x = land.getSpawnX();
        double z = land.getSpawnZ();
        int bool = random.nextInt(10);

        double addX = random.nextDouble(landDistanceX);
        if(bool > 5) {
            addX = addX * (-1);
        }

        double addZ = random.nextDouble(landDistanceZ);
        if(bool < 5) {
            addZ = addZ * (-1);
        }

        x += addX;
        z += addZ;

        /*World world = Bukkit.getWorld("world");
        if (world == null) return;
        double y = world.getHighestBlockYAt(new Location(world, x, 0, z)) + 1;
        Entity activeMob = spawnMythicMob(new Location(world, x, y, z));
        if(activeMob != null) {
            raidEntities.add(activeMob.getUniqueId());
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ((LivingEntity) activeMob).damage(0.5, getPlayersInRaid().get(0));
            }
        }, 20*3);*/


        World world = Bukkit.getWorld("world");
        if (world == null) return;

        double y = world.getHighestBlockYAt(new Location(world, x, 0, z)) + 1;

        int level = random.nextInt(mustKillRaidEntities);

        Entity entity = world.spawnEntity(
                new Location(world, x, y, z),
                getRandomHostileDaylightMob()
        );
        entity.setCustomName("§e§lTruppe §0§c§lLvl. " + level);
        entity.setCustomNameVisible(true);
        entity.setGlowing(true);

        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setMaxHealth(level * 3 + 1);
            ((LivingEntity) entity).setHealth(level * 3);
        }

        raidEntities.add(entity.getUniqueId());
    }


    /*public Entity spawnMythicMob(Location spawnLocation) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("pillager").orElse(null);
        if(mob != null){
            // spawns mob
            ActiveMob knight = mob.spawn(BukkitAdapter.adapt(spawnLocation),1);
            knight.setTarget((AbstractEntity) Bukkit.getEntity(UUID.fromString(victimEntity)));
            return knight;
        }
        return null;
    }*/

    public Entity spawnMythicMob(Location spawnLocation) {
        spawnLocation.getWorld().getChunkAt(spawnLocation).load();
        Entity pillager1 = Bukkit.getWorld("world").spawnEntity(spawnLocation, getRandomHostileDaylightMob());
        if(!(pillager1 instanceof LivingEntity))
            return pillager1;
        LivingEntity pillager = (LivingEntity) pillager1;
        pillager.setRemoveWhenFarAway(false);
        pillager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
        if(pillager instanceof Pillager)
            ((Pillager) pillager).setTarget((LivingEntity) Bukkit.getEntity(UUID.fromString(victimEntity)));
        if(pillager instanceof Ravager) {
            ((Ravager) pillager).setTarget((LivingEntity) Bukkit.getEntity(UUID.fromString(victimEntity)));
        }
        if(pillager instanceof Vindicator)
            ((Vindicator) pillager).setTarget((LivingEntity) Bukkit.getEntity(UUID.fromString(victimEntity)));
        int level = new Random().nextInt(mustKillRaidEntities);
        pillager.setCustomName("§e§lTruppe §0§c§lLvl. " + level);
        pillager.setCustomNameVisible(true);
        pillager.setGlowing(true);
        pillager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        return pillager;
    }



    public static EntityType getRandomHostileDaylightMob() {
        Random random = new Random();
        return HOSTILE_DAYLIGHT_SAFE_MOBS[random.nextInt(HOSTILE_DAYLIGHT_SAFE_MOBS.length)];
    }

    private int loadLandRaidLevel() {
        try {
            if(!isInLandRaidLevel()) {
                return 1;
            }
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_raid_levels` WHERE `land` = ?");
            preparedStatement.setString(1, land.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean isInLandRaidLevel() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_raid_levels` WHERE `land` = ?");
            preparedStatement.setString(1, land.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addLandRaidLevel() {
        try {
            PreparedStatement preparedStatement;
            if(isInLandRaidLevel()) {
                preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `land_raid_levels` SET `level` = ? WHERE `land` = ?");
                preparedStatement.setInt(1, getLandRaidLevel() + 1);
                preparedStatement.setString(2, land.getName());
            } else {
                preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `land_raid_levels` (`land`,`level`) VALUES (?,?)");
                preparedStatement.setString(1, land.getName());
                preparedStatement.setInt(2, 2);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UUID> getRaidEntities() {
        return raidEntities;
    }

    public int getLandRaidLevel() {
        return landRaidLevel;
    }

    public Land getLand() {
        return land;
    }

    public String getVictimEntity() {
        return victimEntity;
    }

}
