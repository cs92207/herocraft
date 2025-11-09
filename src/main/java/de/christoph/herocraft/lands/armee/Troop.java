package de.christoph.herocraft.lands.armee;

public abstract class Troop {

    private int id;
    private String name;
    private int level;


    public Troop(int id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

}
