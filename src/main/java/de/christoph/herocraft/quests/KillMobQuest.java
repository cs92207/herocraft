package de.christoph.herocraft.quests;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.entity.EntityType;

public class KillMobQuest implements Quest {

    private final String description;
    private final EntityType type;
    private final int goal;
    private int current = 0;

    public KillMobQuest(String description, EntityType type, int goal) {
        this.description = description;
        this.type = type;
        this.goal = goal;
    }

    public void onKill(EntityType killed) {
        if (killed == type) {
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
    public Quest copy() { return new KillMobQuest(description, type, goal); }

}
