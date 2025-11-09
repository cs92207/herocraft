package de.christoph.herocraft.prison;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import org.bukkit.configuration.file.FileConfiguration;

public class Prison {

    private String playerUUID;
    private String land;
    private int obsidianAmount;

    public Prison(String playerUUID, String land, int obsidianAmount) {
        this.playerUUID = playerUUID;
        this.land = land;
        this.obsidianAmount = obsidianAmount;
    }

    public void saveInConfig() {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        config.set("Prison." + playerUUID + "." + land, obsidianAmount);
        HeroCraft.getPlugin().saveConfig();
    }

    public String getLand() {
        return land;
    }

    public int getObsidianAmount() {
        return obsidianAmount;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public void setObsidianAmount(int obsidianAmount) {
        this.obsidianAmount = obsidianAmount;
        saveInConfig();
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

}
