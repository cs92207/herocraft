package de.christoph.herocraft.booster;

import com.google.errorprone.annotations.ForOverride;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Booster implements Listener {

    public static final int BOOSTER_TIME = 15; // time in minutes

    protected List<World> boosterWorlds;

    protected String name;
    protected String databaseID;
    protected Material iconMaterial;

    protected int multiply;
    protected int taskID;

    public Booster(String name, String databaseID, Material iconMaterial) {
        this.name = name;
        this.databaseID = databaseID;
        this.multiply = 0;
        this.iconMaterial = iconMaterial;
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                loadBoosterWorlds();
            }
        }, 20*3);
    }

    public boolean isBoosterActive() {
        return multiply > 0;
    }

    private void loadBoosterWorlds() {
        boosterWorlds = new ArrayList<>();
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        if(!config.contains("boosterWorlds")) {
            List<String> mBoosterWorlds = new ArrayList<>();
            mBoosterWorlds.add("world");
            config.set("boosterWorlds", mBoosterWorlds);
            HeroCraft.getPlugin().saveConfig();
        }
        List<String> mBoosterWorlds = config.getStringList("boosterWorlds");
        for(String i : mBoosterWorlds) {
            boosterWorlds.add(Bukkit.getWorld(i));
        }
    }

    public ItemStack getBoosterIcon(Player player) {
        return new ItemBuilder(iconMaterial)
                .setDisplayName("§4§l" + name)
                .setLore("", "§7Booster: §e" + getBoostersFromPlayer(player))
                .build();
    }

    public void activateBooster(Player activator) {
        if(getBoostersFromPlayer(activator) <= 0) {
            activator.sendMessage(Constant.PREFIX + "§7Du hast keine §c" + name + " §7mehr.");
            return;
        }
        if(!boosterWorlds.contains(activator.getWorld())) {
            activator.sendMessage(Constant.PREFIX + "§7In dieser Welt gibt es keine §cBooster§7.");
            return;
        }
        setBoostersForPlayer(activator, getBoostersFromPlayer(activator) - 1);
        if(!isBoosterActive()) {
            onBoosterActivate();
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    multiply--;
                    if(!isBoosterActive()) {
                        onBoosterDeactivated();
                        for(Player all : getBoosterPlayers()) {
                            all.playSound(all.getLocation(), Sound.ENTITY_WOLF_DEATH, 1, 1);
                            all.sendMessage("");
                            all.sendMessage("§0-------");
                            all.sendMessage("");
                            all.sendMessage("§7Der Booster §c§l" + name + "§7 ist §cabgelaufen§7.");
                            all.sendMessage("");
                            all.sendMessage("§0-------");
                            all.sendMessage("");
                        }
                        Bukkit.getScheduler().cancelTask(taskID);
                    } else {
                        onBoosterMultiplyEnded();
                        for(Player all : getBoosterPlayers()) {
                            all.sendMessage("");
                            all.sendMessage("§0-------");
                            all.sendMessage("");
                            all.sendMessage("§7Der Booster-Multiplikator §c§l" + name + "§7 ist §cabgelaufen§7.");
                            all.sendMessage("§7Multiplikator: §e§lx" + multiply);
                            all.sendMessage("");
                            all.sendMessage("§0-------");
                            all.sendMessage("");
                        }
                    }
                }
            }, 20*60*BOOSTER_TIME, 20*60*BOOSTER_TIME);
        } else {
            onBoosterMultiply();
        }
        multiply++;
        for(Player all : getBoosterPlayers()) {
            all.playSound(all.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            all.sendMessage("");
            all.sendMessage("§0-------");
            all.sendMessage("");
            all.sendMessage("§7Der Spieler §e" + activator.getName() + " §7hat den §a§l" + name + " §7 für " + BOOSTER_TIME + " Minuten aktiviert.");
            all.sendMessage("§7Multiplikator: §e§lx" + multiply);
            all.sendMessage("");
            all.sendMessage("§0-------");
            all.sendMessage("");
        }
    }

    @ForOverride
    public void onBoosterMultiplyEnded() {  }

    public int getBoostersFromPlayer(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT * FROM `" + databaseID + "` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setBoostersForPlayer(Player player, int amount) {
        try {
            PreparedStatement preparedStatement;
            if(isInDatabase(player)) {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("UPDATE `" + databaseID + "` SET `amount` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("INSERT INTO `" + databaseID + "` (`uuid`, `amount`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, amount);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isInDatabase(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT * FROM `" + databaseID + "` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Player> getBoosterPlayers() {
        ArrayList<Player> boosterPlayers = new ArrayList<>();
        for(World world : boosterWorlds) {
            boosterPlayers.addAll(world.getPlayers());
        }
        return boosterPlayers;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if(boosterWorlds.contains(world) && isBoosterActive()) {
            onPlayerEnterBoosterWorld(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if(boosterWorlds.contains(world) && isBoosterActive()) {
            onPlayerLeaveBoosterWorld(player);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();
        if(!isBoosterActive())
            return;
        if(boosterWorlds.contains(from) && boosterWorlds.contains(to)) {
            return;
        }
        if(boosterWorlds.contains(from)) {
            onPlayerLeaveBoosterWorld(player);
            return;
        }
        if(boosterWorlds.contains(to)) {
            onPlayerEnterBoosterWorld(player);
        }
    }

    public abstract void onBoosterActivate();

    public abstract void onBoosterMultiply();

    public abstract void onBoosterDeactivated();

    public abstract void onPlayerEnterBoosterWorld(Player player);

    public abstract void onPlayerLeaveBoosterWorld(Player player);


    public String getName() {
        return name;
    }

    public int getMultiply() {
        return multiply;
    }

}
