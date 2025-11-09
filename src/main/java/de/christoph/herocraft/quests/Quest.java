package de.christoph.herocraft.quests;

public interface Quest {

    String getDescription();
    boolean isComplete();
    int getProgress();
    void setProgress(int progress);
    String getGoal();
    Quest copy();

}
