package de.christoph.herocraft.quests;

import org.bukkit.Material;

public class BreakBlockQuest implements Quest {

    private final String description;
    private final Material block;
    private final int goal;
    private int current = 0;

    public BreakBlockQuest(String description, Material block, int goal) {
        this.description = description;
        this.block = block;
        this.goal = goal;
    }

    public void onBreak(Material broken) {
        if (broken == block) {
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
    public Quest copy() { return new BreakBlockQuest(description, block, goal); }

}
