package de.christoph.herocraft.quests;

import org.bukkit.Location;
import org.bukkit.block.Biome;

public class FindBiomeQuest implements Quest {

    private final String description;
    private final Biome targetBiome;
    private boolean completed = false;

    public FindBiomeQuest(String description, Biome biome) {
        this.description = description;
        this.targetBiome = biome;
    }

    public void onMove(Location location) {
        if (location.getBlock().getBiome() == targetBiome) {
            completed = true;
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

    @Override
    public int getProgress() {
        return completed ? 1 : 0;
    }

    @Override
    public void setProgress(int progress) {
        if(progress == 1) {
            completed = true;
        } else {
            completed = false;
        }
    }

    @Override
    public String getGoal() {
        return "1";
    }

    @Override
    public Quest copy() {
        return new FindBiomeQuest(description, targetBiome);
    }

}
