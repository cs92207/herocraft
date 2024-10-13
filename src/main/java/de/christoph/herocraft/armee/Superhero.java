package de.christoph.herocraft.armee;

import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Superhero {

    // private int id;
    // private String name;

    // speed
    // strongness
    // jumpheight
    // regeneration
    // resistence

    // ability: invisibility, forceJump, fire (Feuerkugeln verschießen), teleportation (Zum Gegner hin), levetation (Gegner fliegen lassen), Blindness (für Gegner)


    private int id;
    private String name;
    private EntityType entityType;
    private ArrayList<PotionEffectType> skills;
    private ArrayList<Integer> amplifiers;
    private Rarity rarity;

    public Superhero(int id, String name, EntityType entityType, ArrayList<PotionEffectType> skills, ArrayList<Integer> amplifiers, Rarity rarity) {
        this.id = id;
        this.name = name;
        this.entityType = entityType;
        this.skills = skills;
        this.amplifiers = amplifiers;
        this.rarity = rarity;
    }

    public static ItemStack getSuperheroIcon(Superhero superhero) {
        Color color;
        if(superhero.getRarity() == Rarity.NORMAL)
            color = Color.GRAY;
        else if(superhero.getRarity() == Rarity.RARE) {
            color = Color.GREEN;
        } else if(superhero.getRarity() == Rarity.EPIC)  {
            color = Color.PURPLE;
        } else {
            color = Color.YELLOW;
        }
        String skills = "";
        int i = 0;
        for(PotionEffectType potionEffectType : superhero.getSkills()) {
            skills += potionEffectType.getName() + " (" + superhero.getAmplifiers().get(i) + "), ";
            i++;
        }
        return new ItemBuilder(Material.PAPER)
                .setDisplayName("§4§l" + superhero.getName())
                .setLore("", color + superhero.getRarity().name(), "", "§eFähigkeiten:", skills)
                .build();
    }

    public ArrayList<Integer> getAmplifiers() {
        return amplifiers;
    }

    public ArrayList<PotionEffectType> getSkills() {
        return skills;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
