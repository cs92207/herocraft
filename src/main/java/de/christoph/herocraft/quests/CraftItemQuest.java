package de.christoph.herocraft.quests;

import org.bukkit.Material;

public class CraftItemQuest implements Quest {

    private final String description;
    private final Material item;
    private final int goal;
    private int current = 0;

    public CraftItemQuest(String description, Material item, int goal) {
        this.description = description;
        this.item = item;
        this.goal = goal;
    }

    public void onCraft(Material crafted) {
        if (crafted == item) {
            setProgress(current + 1);
        }
    }

    public boolean isComplete() { return current >= goal; }
    public String getDescription() { return description; }
    public int getProgress() { return current; }

    @Override
    public void setProgress(int progress) {
        current = progress;
    }
    public String getGoal() { return String.valueOf(goal); }
    public Quest copy() { return new CraftItemQuest(description, item, goal); }

}
