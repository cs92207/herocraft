package de.christoph.herocraft.school;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.market.MarketCommand;
import de.christoph.herocraft.school.skills.Skill;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffectType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MentorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equals(":offset_-16::skills:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a§lSkills aktivieren")) {
            HeroCraft.getPlugin().getSkillManager().setSkillsActive(player, true);
            player.kickPlayer("§a§lDeine Skills wurden aktiviert §7(Joine neu, damit die Änderung übernommen wird)");
            return;
        } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lSkills deaktivieren")) {
            HeroCraft.getPlugin().getSkillManager().setSkillsActive(player, false);
            player.getActivePotionEffects().clear();
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);
            player.removePotionEffect(PotionEffectType.RESISTANCE);
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.STRENGTH);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.kickPlayer("§c§lDeine Skills wurden deaktiviert §7(Joine neu, damit die Änderung übernommen wird)");
            return;
        }
        if(event.getAction() == InventoryAction.PICKUP_HALF) {
            try {
                if(hasSkillDeActivated(player, event.getCurrentItem().getItemMeta().getDisplayName())) {
                    PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `unactive_single_skill` WHERE `uuid` = ?");
                    preparedStatement.setString(1, player.getUniqueId().toString() + event.getCurrentItem().getItemMeta().getDisplayName());
                    preparedStatement.execute();
                    player.sendMessage(Constant.PREFIX + "§7Du hast den Skill §aaktiviert§7 (Rejoine, damit die Änderung übernommen wird).");
                    player.closeInventory();
                } else {
                    PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `unactive_single_skill` (`uuid`) VALUES (?)");
                    preparedStatement.setString(1, player.getUniqueId().toString() + event.getCurrentItem().getItemMeta().getDisplayName());preparedStatement.execute();

                    player.sendMessage(Constant.PREFIX + "§7Du hast den Skill §cdeaktiviert§7  (Rejoine, damit die Änderung übernommen wird).");
                    player.closeInventory();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            learn(player, event.getCurrentItem().getItemMeta().getDisplayName());
        }

    }

    public static boolean hasSkillDeActivated(Player player, String skill) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `unactive_single_skill` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString() + skill);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if(event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase("hero")) {
            event.getEntity().spigot().respawn();
            event.getEntity().teleport(new Location(Bukkit.getWorld("world"), 172.55, 132, -216.5));
        }
    }
    private void learn(Player player, String skill) {
        if(hasSkillDeActivated(player, skill)) {
            return;
        }
        Skill currentSkill = null;
        for(Map.Entry<String, Skill> entry : HeroCraft.getPlugin().getSkillManager().skills.entrySet()) {
            if(entry.getValue().getName().equalsIgnoreCase(skill))
                currentSkill = entry.getValue();
        }
        if(currentSkill == null)
            return;
        if(currentSkill.players.containsKey(player)) {
            currentSkill.startTraining(player);
        } else {
            currentSkill.learnSkill(player);
        }
    }

}
