package de.christoph.herocraft.quests;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CatchFishQuest implements Quest {

    private final String description;
    private final Material fishType;
    private final int goal;
    private int current = 0;

    public CatchFishQuest(String description, Material fishType, int goal) {
        this.description = description;
        this.fishType = fishType;
        this.goal = goal;
    }

    public void onFishCaught(ItemStack caughtItem) {
        if (caughtItem.getType() == fishType) {
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
        return new CatchFishQuest(description, fishType, goal);
    }
}
