package de.christoph.herocraft.lands.province;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Province {

    private String land;
    private String name;
    private double x1;
    private double z1;
    private double x2;
    private double z2;
    private String world;

    public Province(String land, String name, double x1, double z1, double x2, double z2, String world) {
        this.land = land;
        this.name = name;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.world = world;
    }

    public boolean canBuild(Player player) {
        Land landOb = HeroCraft.getPlugin().getLandManager().getLandByName(land);
        return landOb.canBuild(player);
    }

    public void teleportTo(Player player) {
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);
        double centerX = (minX + maxX) / 2;
        double centerZ = (minZ + maxZ) / 2;
        double y = Bukkit.getWorld(world).getHighestBlockYAt(new Location(Bukkit.getWorld(world), centerX, 1, centerZ));
        player.sendTitle("§e§lReise startet..", "§7Zur Stadt §a" + name);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                player.teleport(new Location(Bukkit.getWorld(world), centerX, y, centerZ));
            }
        }, 20*3);
    }

    public String getName() {
        return name;
    }

    public String getLand() {
        return land;
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

    public String getWorld() {
        return world;
    }

    public void setName(String name) {
        this.name = name;
    }

}
