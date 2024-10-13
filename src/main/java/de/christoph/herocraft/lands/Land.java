package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBeacon;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.beans.beancontext.BeanContext;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Land {

    private static final int PARTICLE_DENSITY = 5;

    private String name;
    private String founderUUID;
    private String founderName;
    private String[] coFounderUUIDs;
    private String[] coFounderNames;
    private String[] memberUUIDs;
    private String[] memberNames;
    private double x1;
    private double z1;
    private double x2;
    private double z2;
    private double spawnX;
    private double spawnY;
    private double spawnZ;
    private double coins;
    private int maxBlocks;
    private String[] trusted;

    public Land(String name, String founderUUID, String founderName, String[] coFounderUUIDs, String[] coFounderNames, String[] memberUUIDs, String[] memberNames, double x1, double z1, double x2, double z2, double spawnX, double spawnY, double spawnZ, double coins, int maxBlocks, String[] trusted) {
        this.name = name;
        this.founderUUID = founderUUID;
        this.founderName = founderName;
        this.coFounderUUIDs = coFounderUUIDs;
        this.coFounderNames = coFounderNames;
        this.memberUUIDs = memberUUIDs;
        this.memberNames = memberNames;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.coins = coins;
        this.maxBlocks = maxBlocks;
        this.trusted = trusted;
    }

    public boolean isInLand(Player player) {
        if(founderUUID.equals(player.getUniqueId().toString()))
            return true;
        for(String i : coFounderUUIDs) {
            if(i.equals(player.getUniqueId().toString()))
                return true;
        }
        for(String i : memberUUIDs) {
            if(i.equals(player.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getAllLandNames() {
        ArrayList<String> allLandNames = new ArrayList<>();
        allLandNames.addAll(List.of(memberNames));
        allLandNames.addAll(List.of(coFounderNames));
        allLandNames.add(founderName);
        return allLandNames;
    }

    public boolean canBuild(Player player) {
        if(founderUUID.equals(player.getUniqueId().toString()))
            return true;
        for(String i : coFounderUUIDs) {
            if(i.equals(player.getUniqueId().toString()))
                return true;
        }
        for(String i : memberUUIDs) {
            if(i.equals(player.getUniqueId().toString())) {
                return true;
            }
        }
        for(String i : trusted) {
            if(i.equalsIgnoreCase(player.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isOwner(String name) {
        return founderName.equalsIgnoreCase(name);
    }

    public boolean isOwnerUUID(String uuid) {
        return founderUUID.equalsIgnoreCase(uuid);
    }

    public boolean isModerator(String name) {
        for(String i : coFounderNames) {
            if(i.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public boolean isModeratorUUID(String uuid) {
        for(String i : coFounderUUIDs) {
            if(i.equalsIgnoreCase(uuid))
                return true;
        }
        return false;
    }


    public void showLandBorder(Player player) {
        createLaserEffect(new Location(
                Bukkit.getWorld("world"),
                x1,
                Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x1, 10, z1)),
                z1),
                new Location(
                        Bukkit.getWorld("world"),
                        x1,
                        1000,
                        z1));

        createLaserEffect(new Location(
                        Bukkit.getWorld("world"),
                        x2,
                        Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x2, 10, z2)),
                        z2),
                new Location(
                        Bukkit.getWorld("world"),
                        x2,
                        1000,
                        z2));
        double x3 = x1;
        double z3 = z2;
        double x4 = x2;
        double z4 = z1;
        createLaserEffect(new Location(
                        Bukkit.getWorld("world"),
                        x3,
                        Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x3, 10, z3)),
                        z3),
                new Location(
                        Bukkit.getWorld("world"),
                        x3,
                        1000,
                        z3));
        createLaserEffect(new Location(
                        Bukkit.getWorld("world"),
                        x4,
                        Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x4, 10, z4)),
                        z4),
                new Location(
                        Bukkit.getWorld("world"),
                        x4,
                        1000,
                        z4));
    }

    public void changeSize(int x1, int z1, int x2, int z2) {
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `lands` SET `x1` = ?, `z1` = ?, `x2` = ?, `z2` = ? WHERE `name` = ?");
            preparedStatement.setInt(1, x1);
            preparedStatement.setInt(2, z1);
            preparedStatement.setInt(3, x2);
            preparedStatement.setInt(4, z2);
            preparedStatement.setString(5, name);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createLaserEffect(Location start, Location end) {
        World world = start.getWorld();
        double particleDistance = 0.25;
        Vector direction = end.clone().subtract(start).toVector();
        double length = direction.length();
        direction.normalize();

        int particlesPerTick = 10;
        int totalTicks = (int) Math.ceil(length / particleDistance / particlesPerTick);

        BukkitTask task = new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                for (int i = 0; i < particlesPerTick; i++) {
                    double progress = (double) (tick * particlesPerTick + i) / (totalTicks * particlesPerTick);
                    if (progress < 1.0) {
                        double particleDistancePerTick = particleDistance * particlesPerTick;
                        double currentLength = progress * length;
                        Location location = start.clone().add(direction.clone().multiply(currentLength));
                        location.setY(start.getY() + progress * length);
                        world.spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1));
                    }
                }

                if (++tick >= totalTicks) {
                    cancel();
                }
            }
        }.runTaskTimer(HeroCraft.getPlugin(), 0, 1);

        Bukkit.getScheduler().runTaskLater(HeroCraft.getPlugin(), () -> {
            task.cancel();
        }, 8 * 20);
    }

    public void teleportTo(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.teleport(new Location(Bukkit.getWorld("world"), spawnX, spawnY, spawnZ));
    }

    public void addMember(Player player) {
        List<String> membUUIDs = new ArrayList<>(Arrays.asList(memberUUIDs));
        List<String> membNames = new ArrayList<>(Arrays.asList(memberNames));
        if(membUUIDs.contains(player.getUniqueId().toString()))
            return;
        membUUIDs.add(player.getUniqueId().toString());
        membNames.add(player.getName());
        memberUUIDs = membUUIDs.toArray(new String[0]);
        memberNames = membNames.toArray(new String[0]);
        HeroCraft.getPlugin().getLandManager().saveLand(this);
    }

    public void promotePlayer(String name) {
        List<String> membNames = new ArrayList<>(Arrays.asList(memberNames));
        List<String> membUUIDs = new ArrayList<>(Arrays.asList(memberUUIDs));
        List<String> moderatorUUIDs = new ArrayList<>(Arrays.asList(coFounderUUIDs));
        List<String> moderatorNames = new ArrayList<>(Arrays.asList(coFounderNames));
        if(!membNames.contains(name))
            return;
        int index = membNames.indexOf(name);
        membNames.remove(name);
        String uuid  = "";
        for(String i : membUUIDs) {
            if(membUUIDs.indexOf(i) == index) {
                uuid = i;
            }
        }
        membUUIDs.remove(uuid);
        membNames.remove(name);
        moderatorNames.add(name);
        moderatorUUIDs.add(uuid);
        memberNames = membNames.toArray(new String[0]);
        memberUUIDs = membUUIDs.toArray(new String[0]);
        coFounderNames = moderatorNames.toArray(new String[0]);
        coFounderUUIDs = moderatorUUIDs.toArray(new String[0]);
        HeroCraft.getPlugin().getLandManager().saveLand(this);
    }

    public void degradePlayer(String name) {
        List<String> membNames = new ArrayList<>(Arrays.asList(memberNames));
        List<String> membUUIDs = new ArrayList<>(Arrays.asList(memberUUIDs));
        List<String> moderatorUUIDs = new ArrayList<>(Arrays.asList(coFounderUUIDs));
        List<String> moderatorNames = new ArrayList<>(Arrays.asList(coFounderNames));
        if(!moderatorNames.contains(name))
            return;
        int index = moderatorNames.indexOf(name);
        moderatorNames.remove(name);
        String uuid  = "";
        for(String i : moderatorUUIDs) {
            if(moderatorUUIDs.indexOf(i) == index) {
                uuid = i;
            }
        }
        membUUIDs.add(uuid);
        membNames.add(name);
        moderatorNames.remove(name);
        moderatorUUIDs.remove(uuid);
        memberNames = membNames.toArray(new String[0]);
        memberUUIDs = membUUIDs.toArray(new String[0]);
        coFounderNames = moderatorNames.toArray(new String[0]);
        coFounderUUIDs = moderatorUUIDs.toArray(new String[0]);
        HeroCraft.getPlugin().getLandManager().saveLand(this);
    }

    public void removeMember(String playerName) {
        List<String> moderatorUUIDs = new ArrayList<>(Arrays.asList(coFounderUUIDs));
        List<String> moderatorNames = new ArrayList<>(Arrays.asList(coFounderNames));
        if(!moderatorNames.contains(playerName)) {
            List<String> membUUIDs = new ArrayList<>(Arrays.asList(memberUUIDs));
            List<String> membNames = new ArrayList<>(Arrays.asList(memberNames));
            if(!membNames.contains(playerName))
                return;
            int index = membNames.indexOf(playerName);
            membNames.remove(playerName);
            String uuid  = "";
            for(String i : membUUIDs) {
                if(membUUIDs.indexOf(i) == index) {
                    uuid = i;
                }
            }
            membUUIDs.remove(uuid);
            memberUUIDs = membUUIDs.toArray(new String[0]);
            memberNames = membNames.toArray(new String[0]);
            HeroCraft.getPlugin().getLandManager().saveLand(this);
            return;
        }
        int index = moderatorNames.indexOf(playerName);
        moderatorNames.remove(playerName);
        String uuid  = "";
        for(String i : moderatorUUIDs) {
            if(moderatorUUIDs.indexOf(i) == index) {
                uuid = i;
            }
        }
        moderatorUUIDs.remove(uuid);
        coFounderUUIDs = moderatorUUIDs.toArray(new String[0]);
        coFounderNames = moderatorNames.toArray(new String[0]);
        HeroCraft.getPlugin().getLandManager().saveLand(this);
    }

    public void setSpawnPoint(Location location) {
        spawnX = location.getX();
        spawnY = location.getY();
        spawnZ = location.getZ();
        HeroCraft.getPlugin().getLandManager().saveLand(this);
    }

    public void delete() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `lands` WHERE `name` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            HeroCraft.getPlugin().getLandManager().getAllLands().remove(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCoins(double amount) {
        coins = amount;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `lands` SET `coins` = ? WHERE `name` = ?");
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `lands` SET `max_blocks` = ? WHERE `name` = ?");
            preparedStatement.setInt(1, maxBlocks);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void trustPlayer(Player player) {
        boolean isTrusted = false;
        for(String i : trusted) {
            if(i.equalsIgnoreCase(player.getUniqueId().toString())) {
                isTrusted = true;
            }
        }
        if(isTrusted)
            return;
        List<String> trustedUUIDs = new ArrayList<>(Arrays.asList(trusted));
        trustedUUIDs.add(player.getUniqueId().toString());
        trusted = trustedUUIDs.toArray(new String[0]);
        HeroCraft.getPlugin().getLandManager().saveLand(this);
    }

    public void unTrustPlayer(Player player) {
        boolean isTrusted = false;
        for(String i : trusted) {
            if(i.equalsIgnoreCase(player.getUniqueId().toString())) {
                isTrusted = true;
            }
        }
        if(!isTrusted)
            return;
        List<String> trustedUUIDs = new ArrayList<>(Arrays.asList(trusted));
        trustedUUIDs.remove(player.getUniqueId().toString());
        trusted = trustedUUIDs.toArray(new String[0]);
        HeroCraft.getPlugin().getLandManager().saveLand(this);
    }

    public String getName() {
        return name;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getZ1() {
        return z1;
    }

    public double getZ2() {
        return z2;
    }

    public String getFounderName() {
        return founderName;
    }

    public String getFounderUUID() {
        return founderUUID;
    }

    public String[] getCoFounderNames() {
        return coFounderNames;
    }

    public String[] getCoFounderUUIDs() {
        return coFounderUUIDs;
    }

    public double getSpawnX() {
        return spawnX;
    }

    public double getSpawnY() {
        return spawnY;
    }

    public double getSpawnZ() {
        return spawnZ;
    }

    public double getCoins() {
        return coins;
    }

    public String[] getMemberNames() {
        return memberNames;
    }

    public String[] getMemberUUIDs() {
        return memberUUIDs;
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }

    public String[] getTrusted() {
        return trusted;
    }

}
