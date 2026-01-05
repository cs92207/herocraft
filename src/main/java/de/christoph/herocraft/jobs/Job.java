package de.christoph.herocraft.jobs;

import java.util.UUID;

public class Job {
    private UUID uuid;
    private JobType jobType;
    private int level;
    private double xp;
    private double xpForNextLevel;

    public Job(UUID uuid, JobType jobType, int level, double xp) {
        this.uuid = uuid;
        this.jobType = jobType;
        this.level = level;
        this.xp = xp;
        this.xpForNextLevel = calculateXPForNextLevel(level);
    }

    public UUID getUuid() {
        return uuid;
    }

    public JobType getJobType() {
        return jobType;
    }

    public int getLevel() {
        return level;
    }

    public double getXp() {
        return xp;
    }

    public double getXpForNextLevel() {
        return xpForNextLevel;
    }

    public void addXp(double amount) {
        this.xp += amount;
        while (this.xp >= this.xpForNextLevel) {
            this.xp -= this.xpForNextLevel;
            this.level++;
            this.xpForNextLevel = calculateXPForNextLevel(this.level);
        }
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public void setLevel(int level) {
        this.level = level;
        this.xpForNextLevel = calculateXPForNextLevel(level);
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    private double calculateXPForNextLevel(int currentLevel) {
        // Deutlich mehr XP benötigt: 200 * (level + 1) * 2.0 - deutlich langsameres Leveln
        return 200 * (currentLevel + 1) * 2.0;
    }

    /**
     * Berechnet den Coins-Multiplikator basierend auf dem Level
     * Level 1 = 1.0x, Level 2 = 1.05x, Level 3 = 1.10x, etc.
     * Begrenzt, damit maximal 5 Coins erreicht werden können
     */
    public double getCoinsMultiplier() {
        // Langsamere Steigerung: nur 0.05 pro Level statt 0.1
        return 1.0 + (level - 1) * 0.05;
    }
}

