package de.christoph.herocraft.dungeons;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Locale;

public class DungeonType {

    private int id;
    private String name;
    private Location location;
    private ArrayList<Entity> enemies;

    public DungeonType(int id, String name, Location location, ArrayList<Entity> enemies) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.enemies = enemies;
    }

    public ArrayList<Entity> getEnemies() {
        return enemies;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

}
