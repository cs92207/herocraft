package de.christoph.herocraft.onboarding;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OnBoarding {

    /*

    1. Land erstellen

    2. Ersten Bewohner einziehen lassen (und Bedürfnis erfüllen)

    3. Zum Markt gehen + Startitems kaufen

    4. Erste Challenge machen

    5. Job annehmen

    6. Navigator benutzen für coole Orte


     */


    private String playerUUID;
    private OnBoardingStep currentStep;

    public OnBoarding(String playerUUID, OnBoardingStep currentStep) {
        this.playerUUID = playerUUID;
        this.currentStep = currentStep;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public OnBoardingStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(OnBoardingStep step) {
        this.currentStep = step;
        saveInConfig();
    }

    public void completeOnBoarding() {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        config.set(playerUUID + "_ON_BOARDING_COMPLETED", true);
        HeroCraft.getPlugin().saveConfig();
    }

    public boolean isOnBoardingCompleted() {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        return config.getBoolean(playerUUID + "_ON_BOARDING_COMPLETED", false);
    }

    public void saveInConfig() {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        
        // Ensure entry in list
        List<String> onBoardings;
        if(config.contains("ON_BOARDINGS")) {
            onBoardings = config.getStringList("ON_BOARDINGS");
        } else {
            onBoardings = new ArrayList<>();
        }
        
        String key = playerUUID + "_ON_BOARDING";
        if(!onBoardings.contains(key)) {
            onBoardings.add(key);
        }
        
        config.set("ON_BOARDINGS", onBoardings);
        config.set(key, currentStep.getId());
        HeroCraft.getPlugin().saveConfig();
    }

    public void sendCurrentStepMessage(Player player) {
        switch (currentStep) {
            case CREATE_LAND:
                player.sendMessage("§e§lOnboarding §7§l| §7Willkommen zum Onboarding!");
                player.sendMessage("§e§lOnboarding §7§l| §7Dein erster Schritt: §cErstelle ein Land§7!");
                player.sendMessage("§e§lOnboarding §7§l| §7Zum Land erstellen brauchst du ein §cLand-Erstellungs-Item§7.");
                break;
            case COMPLETED:
                player.sendMessage("§e§lOnboarding §7§l| §aGratuliert! Du hast das Onboarding abgeschlossen!");
                break;
        }
    }

}
