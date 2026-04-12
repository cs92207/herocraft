package de.christoph.herocraft.onboarding;

import de.christoph.herocraft.HeroCraft;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;

public class OnBoardingListener implements Listener {

    /**
     * Wenn der Spieler eine Furniture platziert (Land-Erstellungs-Item),
     * prüfen wir, ob dieser im Onboarding ist.
     */
    @EventHandler
    public void onFurniturePlaced(FurniturePlaceSuccessEvent event) {
        Player player = event.getPlayer();
        OnBoarding onBoarding = HeroCraft.getPlugin().getOnBoardingManager().getOnBoardingForPlayer(player);

        if (onBoarding == null) return;
        if (onBoarding.getCurrentStep() != OnBoardingStep.CREATE_LAND) return;

        // Let the land creation happen first, then complete the onboarding
        // We'll use a delayed task to do this after the land is created
        HeroCraft.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
            HeroCraft.getPlugin(),
            () -> {
                // Check if land was created
                if (HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player) != null) {
                    HeroCraft.getPlugin().getOnBoardingManager().completeOnBoarding(player);
                }
            },
            10L // Run after 0.5 seconds
        );
    }

    /**
     * Wenn ein Spieler dem Server beitritt
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {/*
        Player player = event.getPlayer();

        // Check if player is new (first join)
        if (!player.hasPlayedBefore()) {
            // Start onboarding for new players
            HeroCraft.getPlugin().getOnBoardingManager().startOnBoarding(player);
        } else {
            // Load the onboarding from config if exists
            HeroCraft.getPlugin().getOnBoardingManager().loadOnBoardingForPlayer(player);
        }

        // Start navigation task if player is in CREATE_LAND step
        startNavigationTask(player);*/
    }

    /**
     * Zeige Navigation an, wenn Spieler sich bewegt
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        OnBoarding onBoarding = HeroCraft.getPlugin().getOnBoardingManager().getOnBoardingForPlayer(player);

        if (onBoarding == null || onBoarding.getCurrentStep() != OnBoardingStep.CREATE_LAND) {
            return;
        }

        // Nur wenn der Block sich ändert (nicht bei jeder Bewegung)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        showNavigation(player);
    }

    /**
     * Wenn ein Spieler stirbt
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        OnBoarding onBoarding = HeroCraft.getPlugin().getOnBoardingManager().getOnBoardingForPlayer(player);

        if (onBoarding == null) return;

        // Send reminder message
        HeroCraft.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
            HeroCraft.getPlugin(),
            () -> {
                if (player.isOnline()) {
                    player.sendMessage("§e§lOnboarding §7§l| §cDu bist gestorben!");
                    onBoarding.sendCurrentStepMessage(player);
                }
            },
            5L // Delay, damit der Respawn vollständig ist
        );
    }

    /**
     * Wenn ein Spieler den Server verlässt
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Save the onboarding to config
        HeroCraft.getPlugin().getOnBoardingManager().saveOnBoardingOnLogout(player);
    }

    /**
     * Starte einen Task, der regelmäßig die Navigation aktualisiert
     */
    private void startNavigationTask(Player player) {
        HeroCraft.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(
            HeroCraft.getPlugin(),
            new Runnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        // Spieler ist offline, stoppe den Task
                        HeroCraft.getPlugin().getServer().getScheduler().cancelTask(HeroCraft.getPlugin().getServer().getScheduler().runTaskTimer(HeroCraft.getPlugin(), () -> {}, 0, 20).getTaskId());
                        return;
                    }

                    OnBoarding onBoarding = HeroCraft.getPlugin().getOnBoardingManager().getOnBoardingForPlayer(player);
                    if (onBoarding == null || onBoarding.getCurrentStep() != OnBoardingStep.CREATE_LAND) {
                        return;
                    }

                    showNavigation(player);
                }
            },
            0, // Initial delay
            20 // Repeat every second (20 ticks)
        );
    }

    /**
     * Zeige die Navigation zur Tutorial-Position an
     */
    private void showNavigation(Player player) {
        Location tutorialLocation = getTutorialLandLocation();

        if (tutorialLocation == null) {
            // Position nicht gesetzt
            player.sendMessage("§c[System] Tutorial-Position nicht konfiguriert!");
            return;
        }

        Location playerLocation = player.getLocation();
        double distance = playerLocation.distance(tutorialLocation);

        // Wenn zu weit weg, zeige spawn Hinweis
        if (distance > 500) {
            TextComponent actionBar = new TextComponent("§c§l⚠ Zu weit weg! Nutze §l/spawn §r§c§lum näher zu kommen.");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBar);
            return;
        }

        // Berechne Richtung
        String direction = getDirection(playerLocation, tutorialLocation);
        
        // Formatiere Distance
        String distanceStr;
        if (distance < 50) {
            distanceStr = "§a✓ Du bist nah genug!";
        } else if (distance < 100) {
            distanceStr = "§e≈ " + (int)distance + "m";
        } else {
            distanceStr = "§c→ " + (int)distance + "m";
        }

        // Sende ActionBar mit Navigation
        TextComponent actionBar = new TextComponent("§7Land-Erstellungs-NPC: " + direction + " §7| " + distanceStr);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBar);
    }

    /**
     * Berechne die Richtung zwischen zwei Positionen
     */
    private String getDirection(Location from, Location to) {
        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();

        double angle = Math.atan2(deltaZ, deltaX);
        angle = Math.toDegrees(angle) + 90; // Anpassung für Minecraft-Koordinaten

        if (angle < 0) angle += 360;

        if (angle < 22.5 || angle >= 337.5) {
            return "§c↑ Norden";
        } else if (angle < 67.5) {
            return "§c↗ Nordosten";
        } else if (angle < 112.5) {
            return "§c→ Osten";
        } else if (angle < 157.5) {
            return "§c↘ Südosten";
        } else if (angle < 202.5) {
            return "§c↓ Süden";
        } else if (angle < 247.5) {
            return "§c↙ Südwesten";
        } else if (angle < 292.5) {
            return "§c← Westen";
        } else {
            return "§c↖ Nordwesten";
        }
    }

    /**
     * Hole die gespeicherte Tutorial-Land-Position aus der Config
     */
    private Location getTutorialLandLocation() {
        org.bukkit.configuration.file.FileConfiguration config = HeroCraft.getPlugin().getConfig();

        if (!config.contains("TUTORIAL_LAND_POSITION")) {
            return null;
        }

        String world = config.getString("TUTORIAL_LAND_POSITION.WORLD");
        if (world == null) return null;

        org.bukkit.World bukkitWorld = org.bukkit.Bukkit.getWorld(world);
        if (bukkitWorld == null) return null;

        double x = config.getDouble("TUTORIAL_LAND_POSITION.X");
        double y = config.getDouble("TUTORIAL_LAND_POSITION.Y");
        double z = config.getDouble("TUTORIAL_LAND_POSITION.Z");

        return new Location(bukkitWorld, x, y, z);
    }

}
