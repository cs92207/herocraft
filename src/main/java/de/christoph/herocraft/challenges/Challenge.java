package de.christoph.herocraft.challenges;

public class Challenge {

    private String name;
    private String description;

    public Challenge(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
}
