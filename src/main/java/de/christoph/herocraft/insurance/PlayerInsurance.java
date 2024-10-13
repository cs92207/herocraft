package de.christoph.herocraft.insurance;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class PlayerInsurance {

    private Player player;
    private String insuranceName;
    private Date needPaied;

    public PlayerInsurance(Player player, String insuranceName, Date needPaied) {
        this.player = player;
        this.insuranceName = insuranceName;
        this.needPaied = needPaied;
    }

    public void refresh() {
        Insurance insurance = getInsurance();
        if(insurance == null)
            return;
        if(HeroCraft.getPlugin().coin.getCoins(player) < insurance.getCost()) {
            player.sendMessage(Constant.PREFIX + "§7Du hast nicht genug §cCoins §7um die Versicherung zu verlängern.");
            return;
        }
        needPaied = Date.valueOf(needPaied.toLocalDate().plusDays(2));
        save();
        HeroCraft.getPlugin().coin.removeMoney(player, insurance.getCost());
        player.sendMessage(Constant.PREFIX + "§7Du hast die Versicherung §a" + insuranceName + " §7Verlängert.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1 ,1);
    }

    public void checkForExpire() {
        if(needPaied.toLocalDate().isBefore(LocalDate.now())) {
            try {
                deleteInsurance();
                player.sendMessage(Constant.PREFIX + "§7Deine Versicherung §c" + insuranceName + " §7ist abgelaufen, während du weg warst. Schließe sie erneut ab, wenn du willst.");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if(needPaied.equals(Date.valueOf(LocalDate.now()))) {
            player.sendMessage(Constant.PREFIX + "§7Deine Versicherung §c" + insuranceName + "§7 läuft heute ab §e§lVerlängere sie!");
        }
    }

    public void save() {
        ArrayList<PlayerInsurance> playerInsurances = HeroCraft.getPlugin().getInsuranceManager().getInsurencePlayers().get(player);
        playerInsurances.removeIf(i -> i.getInsuranceName().equalsIgnoreCase(insuranceName));
        try {
            if(hasAlready()) {
                deleteInsurance();
            }
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `insurance` (`uuid`,`insuranceName`,`needPaied`) VALUES (?,?,?)");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, insuranceName);
            preparedStatement.setDate(3, needPaied);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        playerInsurances.add(this);
        HeroCraft.getPlugin().getInsuranceManager().getInsurencePlayers().put(player, playerInsurances);
    }

    public boolean hasAlready() throws SQLException {
        PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `insurance` WHERE `uuid` = ?");
        preparedStatement.setString(1, player.getUniqueId().toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if(resultSet.getString("insuranceName").equalsIgnoreCase(insuranceName))
                return true;
        }
        return false;
    }

    public void deleteInsurance() throws SQLException {
        PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `insurance` WHERE `uuid` = ? AND `insuranceName` = ?");
        preparedStatement.setString(1, player.getUniqueId().toString());
        preparedStatement.setString(2, insuranceName);
        preparedStatement.execute();
        ArrayList<PlayerInsurance> playerInsurances = HeroCraft.getPlugin().getInsuranceManager().getInsurencePlayers().get(player);
        playerInsurances.removeIf(playerInsurance -> playerInsurance.getInsuranceName().equalsIgnoreCase(insuranceName));
        HeroCraft.getPlugin().getInsuranceManager().getInsurencePlayers().put(player, playerInsurances);
    }

    @Nullable
    private Insurance getInsurance() {
        for(Insurance insurance : HeroCraft.getPlugin().getInsuranceManager().getInsurances()) {
            if(insurance.getName().equalsIgnoreCase(insuranceName))
                return insurance;
        }
        return null;
    }

    public Date getNeedPaied() {
        return needPaied;
    }

    public Player getPlayer() {
        return player;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

}
