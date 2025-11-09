package de.christoph.herocraft.quests;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TradeVillagerQuest implements Quest {

    private final String description;
    private final Material requiredItem;
    private final int amount;
    private int tradedAmount = 0;
    private boolean completed = false;

    public TradeVillagerQuest(String description, Material requiredItem, int amount) {
        this.description = description;
        this.requiredItem = requiredItem;
        this.amount = amount;
    }

    public void onTrade(ItemStack result) {
        if (!completed && result.getType() == requiredItem) {
            setProgress(tradedAmount + result.getAmount());
            if (tradedAmount >= amount) {
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

    public int getProgress() { return tradedAmount; }

    @Override
    public void setProgress(int progress) {
        tradedAmount = progress;
    }

    @Override
    public String getGoal() {
        return String.valueOf(amount);
    }

    @Override
    public Quest copy() {
        return new TradeVillagerQuest(description, requiredItem, amount);
    }
}
