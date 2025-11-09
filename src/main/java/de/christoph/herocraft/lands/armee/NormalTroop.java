package de.christoph.herocraft.lands.armee;

import org.bukkit.entity.EntityType;

public class NormalTroop extends Troop {

    public String entityType;

    public NormalTroop(int id, String name, int level, String entityType) {
        super(id, name, level);
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

}
