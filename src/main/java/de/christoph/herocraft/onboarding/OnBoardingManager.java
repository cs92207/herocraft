package de.christoph.herocraft.onboarding;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnBoardingManager {

    private final ArrayList<OnBoarding> onBoardings;

    public OnBoardingManager() {
        this.onBoardings = new ArrayList<>();
    }

    /**
     * Prüft, ob der Spieler aktiv ein Onboarding hat
     */
    public boolean isPlayerInOnBoarding(Player player) {
        return getOnBoardingForPlayer(player) != null;
    }

    /**
     * Prüft, ob der Spieler das Onboarding abgeschlossen hat
     */
    public boolean hasPlayerCompletedOnBoarding(Player player) {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        return config.getBoolean(player.getUniqueId().toString() + "_ON_BOARDING_COMPLETED", false);
    }

    /**
     * Gibt das aktive Onboarding für einen Spieler zurück
     */
    public OnBoarding getOnBoardingForPlayer(Player player) {
        for (OnBoarding onBoarding : onBoardings) {
            if (onBoarding.getPlayerUUID().equals(player.getUniqueId().toString())) {
                return onBoarding;
            }
        }
        return null;
    }

    /**
     * Startet das Onboarding für einen Spieler
     */
    public OnBoarding startOnBoarding(Player player) {
        // Check if player hasn't already completed onboarding
        if (hasPlayerCompletedOnBoarding(player)) {
            return null;
        }

        // Check if player already has an active onboarding
        OnBoarding existing = getOnBoardingForPlayer(player);
        if (existing != null) {
            return existing;
        }

        // Create and register new onboarding
        OnBoarding onBoarding = new OnBoarding(player.getUniqueId().toString(), OnBoardingStep.CREATE_LAND);
        onBoarding.saveInConfig();
        onBoardings.add(onBoarding);
        onBoarding.sendCurrentStepMessage(player);
        return onBoarding;
    }

    /**
     * Lädt das Onboarding aus der Config für einen Spieler
     */
    public void loadOnBoardingForPlayer(Player player) {
        // Skip if already in onboarding
        if (isPlayerInOnBoarding(player)) {
            return;
        }

        // Skip if already completed
        if (hasPlayerCompletedOnBoarding(player)) {
            return;
        }

        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        String key = player.getUniqueId().toString() + "_ON_BOARDING";

        if (!config.contains(key)) {
            // Start new onboarding
            startOnBoarding(player);
            return;
        }

        // Load existing onboarding from config
        int stepId = config.getInt(key);
        OnBoardingStep step = OnBoardingStep.fromId(stepId);
        OnBoarding onBoarding = new OnBoarding(player.getUniqueId().toString(), step);
        onBoardings.add(onBoarding);
        onBoarding.sendCurrentStepMessage(player);
    }

    /**
     * Beendet das Onboarding für einen Spieler
     */
    public void completeOnBoarding(Player player) {
        OnBoarding onBoarding = getOnBoardingForPlayer(player);
        if (onBoarding != null) {
            onBoarding.completeOnBoarding();
            onBoardings.remove(onBoarding);
            player.sendMessage("§e§lOnboarding §7§l| §aGratuliert! Du hast das Onboarding abgeschlossen!");
        }
    }

    /**
     * Speichert das aktive Onboarding beim Ausloggen
     */
    public void saveOnBoardingOnLogout(Player player) {
        OnBoarding onBoarding = getOnBoardingForPlayer(player);
        if (onBoarding != null) {
            onBoarding.saveInConfig();
        }
    }

    /**
     * Entfernt die aktive Onboarding-Session (ohne zu speichern)
     */
    public void removeOnBoardingSession(Player player) {
        OnBoarding onBoarding = getOnBoardingForPlayer(player);
        if (onBoarding != null) {
            onBoardings.remove(onBoarding);
        }
    }

    public ArrayList<OnBoarding> getAllOnBoardings() {
        return onBoardings;
    }

}
