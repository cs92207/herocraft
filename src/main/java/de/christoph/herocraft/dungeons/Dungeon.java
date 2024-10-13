package de.christoph.herocraft.dungeons;

public class Dungeon {

    private int dungeonType;
    private int x;
    private int y;
    private int z;
    private String ownedLand;
    private String placedMobs;

    public Dungeon(int dungeonType, int x, int y, int z, String ownedLand, String placedMobs) {
        this.dungeonType = dungeonType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ownedLand = ownedLand;
        this.placedMobs = placedMobs;
    }

    public void play() {

    }

    public void generate() {

    }

    public void delete() {

    }

    public void save() {

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDungeonType() {
        return dungeonType;
    }

    public String getOwnedLand() {
        return ownedLand;
    }

    public String getPlacedMobs() {
        return placedMobs;
    }

}
