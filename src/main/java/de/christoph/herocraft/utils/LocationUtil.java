package de.christoph.herocraft.utils;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationUtil {

    public static void saveLocation(String root, HeroCraft plugin, Location location) {
        FileConfiguration config = plugin.getConfig();
        config.set(root + ".World", location.getWorld().getName());
        config.set(root + ".X", location.getX());
        config.set(root + ".Y", location.getY());
        config.set(root + ".Z", location.getZ());
        config.set(root + ".Yaw", location.getYaw());
        config.set(root + ".Pitch", location.getPitch());
        plugin.saveConfig();
    }

    public static void saveLocationWithoutYawAndPitch(String root, HeroCraft plugin, Location location) {
        FileConfiguration config = plugin.getConfig();
        config.set(root + ".World", location.getWorld().getName());
        config.set(root + ".X", location.getX());
        config.set(root + ".Y", location.getY());
        config.set(root + ".Z", location.getZ());
        plugin.saveConfig();
    }

    public static Location getLocation(String root, HeroCraft plugin) {
        FileConfiguration config = plugin.getConfig();
        World world = Bukkit.getWorld(config.getString(root + ".World"));
        double x = config.getDouble(root + ".X");
        double y = config.getDouble(root + ".Y");
        double z = config.getDouble(root + ".Z");
        float yaw = (float) config.getDouble(root + ".Yaw");
        float pitch = (float) config.getDouble(root + ".Pitch");
        Location location = new Location(world, x, y, z, yaw, pitch);
        return location;
    }

    public static Location getLocationWithoutYawAndPitch(String root, HeroCraft plugin) {
        FileConfiguration config = plugin.getConfig();
        World world = Bukkit.getWorld(config.getString(root + ".World"));
        double x = config.getDouble(root + ".X");
        double y = config.getDouble(root + ".Y");
        double z = config.getDouble(root + ".Z");
        Location location = new Location(world, x, y, z);
        return location;
    }

}
