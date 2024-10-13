package de.christoph.herocraft.caseopening;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaseOpeningListener implements Listener {

    @EventHandler
    public void onNormalCaseClick(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lInventar")) {
            event.setCancelled(true);
            int cratesFromPlayer = getChestsFromPlayer(player);

            if(!player.isSneaking()) {
                player.sendMessage("§0-- §e§lSurvivalLand Crate §0--");
                player.sendMessage("");
                player.sendMessage("§7Eine Survival Land Kiste erhälst du, wenn du dir eine Werbung anschaust. Gehe dazu auf §ehttps://hero-wars.eu/free-anycoins.php§7.");
                player.sendMessage("");
                player.sendMessage("§e§lInhalt: §7Linksklicke 3 mal auf die Kiste um mögliche Gewinne zu sehen");
                player.sendMessage("");
                player.sendMessage("§e§lÖffnen: §7Sneake und rechtsklicke die Kiste um sie zu öffnen");
                player.sendMessage("");
                player.sendMessage("§e§lDeine Crates: §a" + cratesFromPlayer);
            } else {
                if(cratesFromPlayer <= 0) {
                    player.sendMessage(Constant.PREFIX + "§7Du hast keine §cCrates §7mehr. Gehe auf §ehttps://hero-wars.eu/free-anycoins.php §7und schaue eine Werbung.");
                    return;
                }
                setChestsForPlayer(player, cratesFromPlayer - 1);
                NormalCrate normalCrate = new NormalCrate("§4§lSurvival Lands Crate");
                normalCrate.spin(player);
            }
        }
    }

    @EventHandler
    public void onCaseInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        if(event.getView().getTitle().equalsIgnoreCase("§4§lSurvival Lands Crate") || event.getView().getTitle().equalsIgnoreCase("§4§lCrate Info"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onNormalCaseTryBreak(FurnitureBreakEvent event) {
        if(event.getPlayer().hasPermission("anyblocks.admin") && event.getPlayer().isSneaking()) {
            return;
        }
        Player player = event.getPlayer();
        if(event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lInventar")) {
            event.setCancelled(true);
            Inventory inventory = Bukkit.createInventory(null, 9*6, "§4§lCrate Info");
            for(ItemStack i : SetCaseOpeningCommand.loadInventory()) {
                inventory.addItem(i);
            }
            player.openInventory(inventory);
            player.sendMessage(Constant.PREFIX + "§7Diese Item können gewonnen werden. §eLinksklick für Infos§7.");
        }
    }

    public boolean isInDatabase(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getChestsFromPlayer(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `survivalland_cases` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return resultSet.getInt("amount");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setChestsForPlayer(Player player, int amount) {
        try {
            PreparedStatement preparedStatement;
            if(isInDatabase(player)) {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("UPDATE `survivalland_cases` SET `amount` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("INSERT INTO `survivalland_cases` (`uuid`,`amount`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, amount);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
