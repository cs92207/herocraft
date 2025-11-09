package de.christoph.herocraft.quests;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EatItemQuest implements Quest {
    private final String description;
    private final Material targetFood;
    private final int amount;
    private int eatenCount = 0;
    private boolean completed = false;

    public EatItemQuest(String description, Material targetFood, int amount) {
        this.description = description;
        this.targetFood = targetFood;
        this.amount = amount;
    }

    public void onEat(ItemStack eaten) {
        if (!completed && eaten.getType() == targetFood) {
            setProgress(eatenCount + 1);
            if (eatenCount >= amount) {
                completed = true;
            }
        }
    }

    @Override
    public boolean isComplete() {
        return completed;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getProgress() { return eatenCount; }

    @Override
    public void setProgress(int progress) {
        eatenCount = progress;
    }

    @Override
    public String getGoal() {
        return String.valueOf(amount);
    }

    @Override
    public Quest copy() {
        return new EatItemQuest(description, targetFood, amount);
    }
}
