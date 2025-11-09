package de.christoph.herocraft.lands.roles;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LandRole {

    private String name;
    private String description;
    private String land;

    private ArrayList<String> players;
    private ArrayList<LandPermission> permissions;

    public LandRole(String name, String description, String land, ArrayList<String> players, ArrayList<LandPermission> permissions) {
        this.name = name;
        this.description = description;
        this.land = land;
        this.players = players;
        this.permissions = permissions;
    }

    public boolean isPlayerMember(Player player) {
        return (players.contains(player.getUniqueId().toString()));
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ArrayList<LandPermission> getPermissions() {
        return permissions;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public String getLand() {
        return land;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermissions(ArrayList<LandPermission> permissions) {
        this.permissions = permissions;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

}
