package de.christoph.herocraft.basiccommands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BroadcastCommand implements CommandExecutor {

    private static final String CONFIG_PATH = "Broadcast";
    private final HeroCraft plugin;
    private final Random random = new Random();
    private BukkitTask broadcastTask;
    private List<String> messages = Collections.emptyList();
    private int minMinutes;
    private int maxMinutes;
    private int nextMessageIndex;

    public BroadcastCommand(HeroCraft plugin) {
        this.plugin = plugin;
    }

    public void start() {
        loadFromConfig();
        restartTask();
    }

    public void shutdown() {
        cancelTask();
    }

    public void reloadBroadcasts(CommandSender sender) {
        plugin.reloadConfig();
        loadFromConfig();
        restartTask();
        if (sender != null) {
            sender.sendMessage(Constant.PREFIX + "§7Broadcasts wurden §aneu geladen§7.");
        }
    }

    private void loadFromConfig() {
        applyDefaults();
        plugin.saveConfig();

        FileConfiguration config = plugin.getConfig();
        minMinutes = Math.max(1, config.getInt(CONFIG_PATH + ".MinMinutes", 5));
        maxMinutes = Math.max(minMinutes, config.getInt(CONFIG_PATH + ".MaxMinutes", 10));

        List<String> configuredMessages = config.getStringList(CONFIG_PATH + ".Messages");
        List<String> sanitizedMessages = new ArrayList<>();
        for (String message : configuredMessages) {
            if (message == null) {
                continue;
            }
            String trimmedMessage = message.trim();
            if (trimmedMessage.isEmpty()) {
                continue;
            }
            sanitizedMessages.add(trimmedMessage.replace("&", "§"));
        }
        messages = sanitizedMessages;
        if (nextMessageIndex >= messages.size()) {
            nextMessageIndex = 0;
        }
    }

    private void applyDefaults() {
        FileConfiguration config = plugin.getConfig();
        config.addDefault(CONFIG_PATH + ".MinMinutes", 5);
        config.addDefault(CONFIG_PATH + ".MaxMinutes", 10);
        List<String> defaultMessages = new ArrayList<>();
        defaultMessages.add("&e&lAnyBlocks &7&l| &7Willkommen auf dem Server.");
        defaultMessages.add("&e&lAnyBlocks &7&l| &7Nutze &e/befehle &7für eine Übersicht.");
        defaultMessages.add("&e&lAnyBlocks &7&l| &7Bearbeite die Nachrichten in der Config und lade sie mit &e/broadcast reload &7neu.");
        config.addDefault(CONFIG_PATH + ".Messages", defaultMessages);
        config.options().copyDefaults(true);
    }

    private void restartTask() {
        cancelTask();
        if (messages.isEmpty()) {
            plugin.getLogger().warning("Keine Broadcast-Nachrichten konfiguriert. Der Broadcast-Timer bleibt deaktiviert.");
            return;
        }
        scheduleNextBroadcast();
    }

    private void scheduleNextBroadcast() {
        long delayTicks = 20L * 60L * getRandomMinutes();
        broadcastTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!messages.isEmpty()) {
                Bukkit.broadcastMessage(messages.get(nextMessageIndex));
                nextMessageIndex++;
                if (nextMessageIndex >= messages.size()) {
                    nextMessageIndex = 0;
                }
            }
            scheduleNextBroadcast();
        }, delayTicks);
    }

    private int getRandomMinutes() {
        if (maxMinutes <= minMinutes) {
            return minMinutes;
        }
        return random.nextInt(maxMinutes - minMinutes + 1) + minMinutes;
    }

    private void cancelTask() {
        if (broadcastTask != null) {
            broadcastTask.cancel();
            broadcastTask = null;
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("herowars.broadcast.reload")) {
                commandSender.sendMessage(Constant.NO_PERMISSION);
                return true;
            }
            reloadBroadcasts(commandSender);
            return true;
        }

        commandSender.sendMessage(Constant.PREFIX + "§7Benutze §e/broadcast reload§7.");
        return true;
    }
}