package de.christoph.herocraft.quests;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KillWithWeaponQuest implements Quest {

    private final String description;
    private final EntityType mobType;
    private final Material requiredWeapon;
    private final int goal;
    private int current = 0;

    public KillWithWeaponQuest(String description, EntityType mobType, Material requiredWeapon, int goal) {
        this.description = description;
        this.mobType = mobType;
        this.requiredWeapon = requiredWeapon;
        this.goal = goal;
    }

    public void onKill(EntityType killed, ItemStack weapon) {
        if (killed == mobType && weapon != null && weapon.getType() == requiredWeapon) {
            setProgress(current + 1);
        }
    }

    @Override
    public boolean isComplete() {
        return current >= goal;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getProgress() { return current; }

    @Override
    public void setProgress(int progress) {
        current = progress;

    }

    @Override
    public String getGoal() {
        return String.valueOf(goal);
    }

    @Override
    public Quest copy() {
        return new KillWithWeaponQuest(description, mobType, requiredWeapon, goal);
    }

}
