package de.christoph.herocraft.landpresentation;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class VoteForLandCommand implements CommandExecutor {

    public static ArrayList<Player> votingPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `best_land_voting` WHERE `voter` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                player.sendMessage(Constant.PREFIX + "§7Du hast bereits §cgevoted§7. Versuche es nächste Woche nochmal.");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        votingPlayers.add(player);
        player.sendMessage(Constant.PREFIX + "§7Gebe das Land an, für welches du abstimmen willst. §0(Schreibe in den Chat)");
        player.sendMessage("§4Sneake zum abbrechen!");
        return false;
    }

}
