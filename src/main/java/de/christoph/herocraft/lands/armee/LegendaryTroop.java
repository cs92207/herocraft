package de.christoph.herocraft.lands.armee;

public class LegendaryTroop extends Troop {

    private String mythicName;

    public LegendaryTroop(int id, String name, int level, String mythicName) {
        super(id, name, level);
        this.mythicName = mythicName;
    }

    public String getMythicName() {
        return mythicName;
    }

}
