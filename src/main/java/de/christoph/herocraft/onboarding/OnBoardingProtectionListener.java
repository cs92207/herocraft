package de.christoph.herocraft.onboarding;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Dieser Listener blockiert alle Aktionen für Spieler, die sich im Onboarding befinden.
 * Spieler können ONLY Länder erstellen, bis sie das Onboarding abgeschlossen haben.
 */
public class OnBoardingProtectionListener implements Listener {

    private static final String[] ALLOWED_COMMANDS = {
        "/job",
        "/land",
        "/createland",
        "/createlandfast",
        "/befehle",
        "/spawn",
        "/teleporter",
        "/help",
        "/msg",
        "/message",
        "/reply",
        "/r",
        "/ia"
    };

    /**
     * Blockiere Block-Break im Onboarding
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Admins sind vom Onboarding-Schutz ausgenommen
        if (player.hasPermission("admin") || player.hasPermission("herocraft.admin") || player.isOp()) {
            return;
        }
        
        if (isPlayerInOnBoarding(player)) {
            event.setCancelled(true);
            player.sendMessage("§e§lOnboarding §7§l| §cDu kannst während des Onboardings keine Blöcke zerstören!");
        }
    }

    /**
     * Blockiere Block-Place im Onboarding (außer für Land-Item)
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        // Admins sind vom Onboarding-Schutz ausgenommen
        if (player.hasPermission("admin") || player.hasPermission("herocraft.admin") || player.isOp()) {
            return;
        }
        
        if (isPlayerInOnBoarding(player)) {
            // Allow furniture placement (handled by FurniturePlaceSuccessEvent)
            // But block normal block placement
            if (!event.getBlock().getType().name().contains("FARMLAND") && 
                !event.getBlock().getType().name().contains("AIR")) {
                event.setCancelled(true);
                player.sendMessage("§e§lOnboarding §7§l| §cDu kannst während des Onboardings keine Blöcke platzieren!");
            }
        }
    }

    /**
     * Blockiere PVP/PVE im Onboarding
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            
            // Admins sind vom Onboarding-Schutz ausgenommen
            if (player.hasPermission("admin") || player.hasPermission("herocraft.admin") || player.isOp()) {
                return;
            }
            
            if (isPlayerInOnBoarding(player)) {
                event.setCancelled(true);
                player.sendMessage("§e§lOnboarding §7§l| §cDu kannst während des Onboardings nicht kämpfen!");
            }
        }
    }

    /**
     * Blockiere Interaktionen im Onboarding (Türen, Truhen, etc.)
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Admins sind vom Onboarding-Schutz ausgenommen
        if (player.hasPermission("admin") || player.hasPermission("herocraft.admin") || player.isOp()) {
            return;
        }
        
        if (isPlayerInOnBoarding(player)) {
            if (event.getClickedBlock() != null) {
                String blockType = event.getClickedBlock().getType().name();
                // Allow interaction with land creation items, block other interactions
                if (blockType.contains("CHEST") || 
                    blockType.contains("DOOR") || 
                    blockType.contains("LEVER") ||
                    blockType.contains("BUTTON")) {
                    event.setCancelled(true);
                    player.sendMessage("§e§lOnboarding §7§l| §cDu kannst während des Onboardings nicht mit Blöcken interagieren!");
                }
            }
        }
    }

    /**
     * Blockiere die meisten Commands im Onboarding
     */
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        // Admins sind vom Onboarding-Schutz ausgenommen
        if (player.hasPermission("admin") || player.hasPermission("herocraft.admin") || player.isOp()) {
            return;
        }
        
        if (isPlayerInOnBoarding(player)) {
            String command = event.getMessage().toLowerCase().split(" ")[0];
            
            if (!isCommandAllowed(command)) {
                event.setCancelled(true);
                player.sendMessage("§e§lOnboarding §7§l| §cDieser Command ist während des Onboardings nicht verfügbar!");
                player.sendMessage("§e§lOnboarding §7§l| §7Fokussiere dich auf die Erstellung deines Landes.");
            }
        }
    }

    /**
     * Prüfe, ob ein Spieler im Onboarding ist
     */
    private boolean isPlayerInOnBoarding(Player player) {
        return HeroCraft.getPlugin().getOnBoardingManager().isPlayerInOnBoarding(player);
    }

    /**
     * Prüfe, ob ein Command erlaubt ist im Onboarding
     */
    private boolean isCommandAllowed(String command) {
        for (String allowed : ALLOWED_COMMANDS) {
            if (command.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

}
