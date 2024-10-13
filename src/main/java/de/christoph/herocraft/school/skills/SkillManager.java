package de.christoph.herocraft.school.skills;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.school.skills.activeskills.Invisible;
import de.christoph.herocraft.school.skills.activeskills.Laser;
import de.christoph.herocraft.school.skills.activeskills.Magic;
import de.christoph.herocraft.school.skills.activeskills.Smash;
import de.christoph.herocraft.school.skills.passiveskills.Jump;
import de.christoph.herocraft.school.skills.passiveskills.Speed;
import de.christoph.herocraft.school.skills.passiveskills.Strongness;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SkillManager {

    public HashMap<String, Skill> skills; // Contains every Skill

    public SkillManager() {
        this.skills = new HashMap<>();
        registerPassiveSkills();
        registerActiveSkills();
    }

    private void registerActiveSkills() {
        Laser laser = new Laser();
        Bukkit.getPluginManager().registerEvents(laser, HeroCraft.getPlugin());
        skills.put(laser.getName(), laser);
        Invisible invisible = new Invisible();
        Bukkit.getPluginManager().registerEvents(invisible, HeroCraft.getPlugin());
        skills.put(invisible.getName(), invisible);
        Magic magic = new Magic();
        Bukkit.getPluginManager().registerEvents(magic, HeroCraft.getPlugin());
        skills.put(magic.getName(), magic);
        Smash smash = new Smash();
        Bukkit.getPluginManager().registerEvents(smash, HeroCraft.getPlugin());
        skills.put(smash.getName(), smash);
    }

    private void registerPassiveSkills() {
        Strongness strongness = new Strongness();
        Bukkit.getPluginManager().registerEvents(strongness, HeroCraft.getPlugin());
        skills.put(strongness.getName(), strongness);
        Speed speed = new Speed();
        Bukkit.getPluginManager().registerEvents(speed, HeroCraft.getPlugin());
        skills.put(speed.getName(), speed);
        Jump jump = new Jump();
        Bukkit.getPluginManager().registerEvents(jump, HeroCraft.getPlugin());
        skills.put(jump.getName(), jump);
    }

    public void setSkillsActive(Player player, boolean active) {
        PreparedStatement preparedStatement;
        try {
            if(isInActiveDatabase(player)) {
                preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `unactiveskills` SET `value` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, active ? 0 : 1);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `unactiveskills` (`uuid`, `value`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, active ? 0 : 1);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isSkillsActive(Player player) {
        if(!isInActiveDatabase(player))
            return true;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `unactiveskills` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("value") == 0 ? true : false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean isInActiveDatabase(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `unactiveskills` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}
