package de.christoph.herocraft.lands.quests;

import de.christoph.herocraft.quests.DailyQuest;
import de.christoph.herocraft.quests.Quest;

public class LandQuestGiver {

    private final String landName;
    private final double x;
    private final double y;
    private final double z;
    private final String world;
    private Quest currentQuest;
    private int progress;

    public LandQuestGiver(String landName, double x, double y, double z, String world, String questDescription, int progress) {
        this.landName = landName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.progress = progress;
        if (questDescription != null && !questDescription.trim().isEmpty()) {
            this.currentQuest = DailyQuest.getQuestByDescription(questDescription);
            if (this.currentQuest != null) {
                this.currentQuest.setProgress(progress);
            }
        }
    }

    public String getLandName() {
        return landName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public Quest getCurrentQuest() {
        return currentQuest;
    }

    public String getQuestDescription() {
        return currentQuest == null ? "" : currentQuest.getDescription();
    }

    public int getProgress() {
        return progress;
    }

    public void setQuest(Quest quest) {
        currentQuest = quest;
        progress = 0;
        if (currentQuest != null) {
            currentQuest.setProgress(0);
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (currentQuest != null) {
            currentQuest.setProgress(progress);
        }
    }
}