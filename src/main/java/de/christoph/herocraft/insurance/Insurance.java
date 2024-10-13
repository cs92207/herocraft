package de.christoph.herocraft.insurance;

import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Date;

public abstract class Insurance {

    private String name;
    private String description;
    private double cost;
    private Material iconMaterial;

    public Insurance(String name, String description, double cost, Material iconMaterial) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.iconMaterial = iconMaterial;
    }

    public ItemStack getIcon() {
        return new ItemBuilder(iconMaterial)
                .setDisplayName("§4§l" + name)
                .setLore("", "§7" + description, "", "§7Kosten / 2 Tage: §e" + cost)
                .build();
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public Material getIconMaterial() {
        return iconMaterial;
    }

}
