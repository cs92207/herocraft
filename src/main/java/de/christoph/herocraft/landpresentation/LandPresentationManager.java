package de.christoph.herocraft.landpresentation;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.LocationUtil;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.plugin.HolographicDisplays;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LandPresentationManager implements Listener {

    private String bestLand;
    private String richestLand;
    private double richestLandValue;

    public Hologram bestHolo;
    public Hologram richestHolo;

    public LandPresentationManager() {
        if(HeroCraft.getPlugin().getConfig().contains("BestLandHoloLocation.World")) {
            loadBestVotedLand();
            loadRichestLand();
        }
    }

    private void loadBestVotedLand() {
        bestLand = HeroCraft.getPlugin().getConfig().getString("BestLand");
        Location bestLandHoloLocation = LocationUtil.getLocation("BestLandHoloLocation", HeroCraft.getPlugin());
        HolographicDisplaysAPI holoAPI = HolographicDisplaysAPI.get(HeroCraft.getPlugin());
        if(bestHolo != null) {
            bestHolo.delete();
            bestHolo = null;
        }
        bestHolo = holoAPI.createHologram(bestLandHoloLocation);
        bestHolo.getLines().appendText("§e§l" + bestLand);
    }

    private void loadRichestLand() {
        richestLand = HeroCraft.getPlugin().getConfig().getString("RichestLand");
        richestLandValue = HeroCraft.getPlugin().getConfig().getDouble("RichestValue");
        Location richestHoloLocation = LocationUtil.getLocation("RichestLandHoloLocation", HeroCraft.getPlugin());
        if(richestHolo != null) {
            richestHolo.delete();
            richestHolo = null;
        }
        HolographicDisplaysAPI holoAPI = HolographicDisplaysAPI.get(HeroCraft.getPlugin());
        richestHolo = holoAPI.createHologram(richestHoloLocation);
        richestHolo.getLines().appendText("§e§l" + richestLand);
        richestHolo.getLines().appendText("");
        richestHolo.getLines().appendText("§a" + richestLandValue + " Coins");

    }

    @EventHandler
    public void checkForNewPresentationLands(PlayerJoinEvent event) {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        String dateString = config.getString("LastLandPresentationChecked");
        if(dateString == null) {
            setNewPresentationLand();
            return;
        }
        LocalDate lastSet = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        if(isOlderThanOneWeek(lastSet)) {
            setNewPresentationLand();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!VoteForLandCommand.votingPlayers.contains(player))
            return;
        event.setCancelled(true);
        player.sendMessage(Constant.PREFIX + "§7Wird abgestimmt...");
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), () -> {
            try {
                PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `lands` WHERE `name` = ?");
                preparedStatement.setString(1, event.getMessage());
                ResultSet resultSet = preparedStatement.executeQuery();
                if(!resultSet.next()) {
                    player.sendMessage(Constant.PREFIX + "§7Dieses Land existiert nicht. Versuche es erneut.");
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `best_land_voting` (`voted_land`,`voter`) VALUES (?,?)");
                preparedStatement.setString(1, event.getMessage());
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.execute();
                player.sendMessage(Constant.PREFIX + "§7Du hast §aabgestimmt§7.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 20);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if(VoteForLandCommand.votingPlayers.contains(event.getPlayer())) {
            event.getPlayer().sendMessage(Constant.PREFIX + "§4Vorgang abgebrochen");
            VoteForLandCommand.votingPlayers.remove(event.getPlayer());
        }
    }

    private void setNewPresentationLand() {
        LocalDate now = LocalDate.now();
        HeroCraft.getPlugin().getConfig().set("LastLandPresentationChecked", now.toString());
        HeroCraft.getPlugin().saveConfig();
        setNewBestVoted();
        setNewRichest();
    }

    private void setNewBestVoted() {
        HashMap<String, Integer> votes = new HashMap<>();
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `best_land_voting`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String landName = resultSet.getString("voted_land");
                if(!votes.containsKey(landName)) {
                    votes.put(landName, 1);
                } else {
                    votes.put(landName, votes.get(landName) + 1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String best = "ZapfenStocher";
        int bestValue = 0;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if(value > bestValue) {
                best = key;
                bestValue = value;
            }
        }
        bestLand = best;
        HeroCraft.getPlugin().getConfig().set("BestLand", bestLand);
        HeroCraft.getPlugin().saveConfig();
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `best_land_voting`");
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadBestVotedLand();
    }

    private void setNewRichest() {
        String richest;
        double richestValue;
        try {
            richest = "";
            richestValue = 0;
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `lands`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                double coind = resultSet.getDouble("coins");
                if (coind > richestValue) {
                    richestValue = coind;
                    richest = resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        richestLand = richest;
        richestLandValue = richestValue;
        HeroCraft.getPlugin().getConfig().set("RichestLand", richestLand);
        HeroCraft.getPlugin().getConfig().set("RichestValue", richestLandValue);
        HeroCraft.getPlugin().saveConfig();
        loadRichestLand();
    }

    public static boolean isOlderThanOneWeek(LocalDate dateToCheck) {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minus(1, ChronoUnit.WEEKS);
        return dateToCheck.isBefore(oneWeekAgo);
    }

}
