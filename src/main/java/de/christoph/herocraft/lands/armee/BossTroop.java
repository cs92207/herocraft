package de.christoph.herocraft.lands.armee;

public class BossTroop extends Troop {

    private String mythicName;

    public BossTroop(int id, String name, int level, String mythicName) {
        super(id, name, level);
        this.mythicName = mythicName;
    }

    public String getMythicName() {
        return mythicName;
    }

}
