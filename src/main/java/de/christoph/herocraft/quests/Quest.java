package de.christoph.herocraft.quests;

public interface Quest {
    String getDescription();

    boolean isComplete();

    int getProgress();

    void setProgress(int paramInt);

    String getGoal();

    Quest copy();
}


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\quests\Quest.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */