package de.christoph.herocraft.tutorial;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class TutorialListener implements Listener {

    public static ArrayList<Player> askForGeneralTutorial = new ArrayList<>();
    public static ArrayList<Player> askForMarketTutorial = new ArrayList<>();
    public static ArrayList<Player> askForIKEATutorial = new ArrayList<>();

    public static ArrayList<Player> cooldownPlayers = new ArrayList<>();

    public static boolean isInMarket(Location location) {
        if(!location.getWorld().getName().equalsIgnoreCase("world"))
            return false;
        if(location.getX() < 151 && location.getX() > 85) {
            if(location.getZ() < -161 && location.getZ() > -243) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::tutorial_inv:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lLand Tutorial")) {
            player.closeInventory();
            player.performCommand("starttutorial");
        } else if(displayName.equalsIgnoreCase("§4§lMarkt Tutorial")) {
            player.closeInventory();
            player.performCommand("markttutorial");
        } else if(displayName.equalsIgnoreCase("§4§lDungeons Tutorial")) {
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Comming Soon :)");
        } if(displayName.equalsIgnoreCase("§4§lCustom Items Tutorial")) {
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7In Survival Lands gibt es verschiedene Custom Items. Zum einen kannst du dir verschiedene Craften (§e/rezepte§7). Diese Items haben dann verschiedene Features / Fähigkeiten. Außerdem gibt es unseren §eAnyKea§7. Hier kannst du dir verschiedene Custom Möbel und Dekorationen kaufen. Zudem kannst du auf Survival Lands §eFähigkeiten erlernen und trainieren§7. Dies kannst du beim Mentor tun.");
        }
    }

    public static boolean isInIkea(Location location) {
        if(!location.getWorld().getName().equalsIgnoreCase("world"))
            return false;
        if(location.getX() < 145 && location.getX() > 89) {
            if(location.getZ() < -251 && location.getZ() > -271) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        askForIKEATutorial.remove(event.getPlayer());
        askForGeneralTutorial.remove(event.getPlayer());
        askForMarketTutorial.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(cooldownPlayers.contains(player))
            return;
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        if(!config.contains("Tutorial." + player.getUniqueId().toString()) && !askForGeneralTutorial.contains(player)) {
            if(isInMarket(player.getLocation())) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.sendMessage(Constant.TUTORIAL_PREFIX + "§7Kennst du dich auf SurvivalLands bereits aus, oder soll ich dir etwas erklären?");
                TextComponent textComponent = new TextComponent("§a§l(Etwas erklären)");
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/starttutorial"));
                TextComponent textComponent1 = new TextComponent("§c§l(Nein nicht erklären)");
                textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tutorialnein no"));
                player.spigot().sendMessage(textComponent);
                player.spigot().sendMessage(textComponent1);
                askForGeneralTutorial.add(player);
                cooldownPlayers.add(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        cooldownPlayers.remove(player);
                    }
                }, 20*25);
            }
        } else if(!config.contains("Market." + player.getUniqueId().toString()) && !askForMarketTutorial.contains(player)) {
            if(isInMarket(player.getLocation())) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.sendMessage(Constant.TUTORIAL_PREFIX + "§e§lWillkommen beim Markt!");
                TextComponent textComponent = new TextComponent("§a§l(Etwas erklären)");
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/markttutorial"));
                TextComponent textComponent1 = new TextComponent("§c§l(Nichts erklären)");
                textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marktnein"));
                player.spigot().sendMessage(textComponent);
                player.spigot().sendMessage(textComponent1);
                askForMarketTutorial.add(player);
            }
        } else if(!config.contains("HeroKea." + player.getUniqueId().toString()) && !askForIKEATutorial.contains(player)) {

        }
    }

}
