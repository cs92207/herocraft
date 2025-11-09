package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import io.lumine.mythic.bukkit.utils.adventure.platform.facet.Facet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LandTagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist in keinem §cLand§7.");
            return false;
        }
        if(land.isModeratorUUID(player.getUniqueId().toString()) ||land.isOwnerUUID(player.getUniqueId().toString())) {
            if(strings.length != 1 && strings.length != 2) {
                player.sendMessage(Constant.PREFIX + "§7Bitte benutze §c/landtag <TAG> <FARBE>§7 oder §c/landtag remove§7.");
                player.sendMessage(Constant.PREFIX + "§7Eine Liste mit allen Farben bekommst du mit §c/tagfarben§7");
                return false;
            }
            if(strings[0].equalsIgnoreCase("remove")) {
                if(!hasTag(land.getName())) {
                    player.sendMessage(Constant.PREFIX + "§7Dein Land hat keinen §cTag§7.");
                    return false;
                }
                try {
                    PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `land_suffixes` WHERE `land` = ?");
                    preparedStatement.setString(1, land.getName());
                    preparedStatement.execute();
                    player.sendMessage(Constant.PREFIX + "§7Land Tag §centfernt§7. Alle Mitglieder des Landes müssen neu joinen, damit es geupdated wird.");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
            if(strings.length != 2) {
                player.sendMessage(Constant.PREFIX + "§7Bitte benutze §c/landtag <TAG> <FARBE>§7 oder §c/landtag remove§7.");
                player.sendMessage(Constant.PREFIX + "§7Eine Liste mit allen Farben bekommst du mit §c/tagfarben§7");
                return false;
            }
            String tag = strings[0];
            if(tag.length() > 5) {
                player.sendMessage(Constant.PREFIX + "§7Der Tag deines Landes darf maximal §c5 Zeichen §7lang sein.");
                return false;
            }
            ChatColor color;
            try {
                color = ChatColor.valueOf(strings[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(Constant.PREFIX + "§7Das ist keine gültige Farbe. Benutze §c/tagfarben§7.");
                return false;
            }
            try {
                PreparedStatement preparedStatement;
                if(hasTag(land.getName())) {
                    preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `land_suffixes` SET `suffix` = ?, `color` = ? WHERE `land` = ?");
                    preparedStatement.setString(1, tag);
                    preparedStatement.setString(2, strings[1]);
                    preparedStatement.setString(3, land.getName());
                } else {
                    preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `land_suffixes` (`land`,`suffix`, `color`) VALUES (?,?,?)");
                    preparedStatement.setString(1, land.getName());
                    preparedStatement.setString(2, tag);
                    preparedStatement.setString(3, strings[1]);
                }
                preparedStatement.execute();
                player.sendMessage(Constant.PREFIX + "§7Tag §agesetzt§7. Alle Mitglieder deines Landes müssen neu joinen, damit es geupdated wird.");
            } catch (SQLException e) {
              e.printStackTrace();
            }

        } else  {
            player.sendMessage(Constant.PREFIX + "§7Du der Owner oder die Moderatoren deines Landes können dies tun.");
        }
        return false;
    }

    public boolean hasTag(String land) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_suffixes` WHERE `land` = ?");
            preparedStatement.setString(1, land);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
