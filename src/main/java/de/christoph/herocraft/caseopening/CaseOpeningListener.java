package de.christoph.herocraft.caseopening;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import io.lumine.mythic.bukkit.utils.lib.jooq.SQL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CaseOpeningListener implements Listener {

    public static HashMap<Player, Integer> pagePlayers = new HashMap<>();


    @EventHandler
    public void onNormalCaseClick(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lInventar") || event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lMobiles Kisten Gewinnspiel")) {
            event.setCancelled(true);
            openPage(player, 0);
        }
    }

    @EventHandler
    public void onSurvivalLandsChestInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::survivallands_chests:"))
            return;
        event.setCancelled(true);
        if(!pagePlayers.containsKey(player))
            return;
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lZurück")) {
            if(pagePlayers.get(player) <= 0)
                return;
            openPage(player, pagePlayers.get(player) - 1);
        } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lNächste Seite")) {
            List<ItemStack> winnings = SetCaseOpeningCommand.loadInventory();
            int maxPages = winnings.size() / 40;
            maxPages++;
            if(pagePlayers.get(player) >= maxPages)
                return;
            openPage(player, pagePlayers.get(player) + 1);
        } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lKiste öffnen")) {
            int chests = getChestsFromPlayer(player);
            if(chests <= 0) {
                player.sendMessage(Constant.PREFIX + "§7Du hast keine §cSurvivalLands Kisten §7mehr. Kaufe sie im §a/survivallandsshop§7.");
                return;
            }
            List<ItemStack> winnings = SetCaseOpeningCommand.loadInventory();
            int progress = getProgressFromPlayer(player);
            if(winnings.size() < progress) {
                player.sendMessage(Constant.PREFIX + "§7Du hast keine offenen Belohnungen mehr. Warte bis nächsten Monat.");
                return;
            }
            player.getInventory().addItem(winnings.get(progress + 1));
            setProgressFromPlayer(player, progress + 1);
            setChestsForPlayer(player, chests - 1);
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Du hast eine §aSurvivalLands Kiste §7geöffnet.");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        pagePlayers.remove(event.getPlayer());
    }

    public void openPage(Player player, int page) {
        pagePlayers.put(player, page);
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::survivallands_chests:");
        List<ItemStack> winningItems = SetCaseOpeningCommand.loadInventory();
        int progress = getProgressFromPlayer(player);
        int n = page * 40;
        for(int i = n; i < n + 40; i++) {
            ItemStack itemStack;
            if(winningItems.size() <= i)
                continue;
            if(progress >= i) {
                itemStack = new ItemBuilder(Material.PAPER).setDisplayName("§4§lBereits abgeholt").setLore("", "§0" + winningItems.get(i).getType().toString() + UUID.randomUUID()).build();
            } else {
                itemStack = winningItems.get(i);
            }
            inventory.addItem(itemStack);
        }
        
        ItemStack back = HeroCraft.getItemsAdderItem("§f< Back");
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§4§lZurück");
        back.setItemMeta(backMeta);

        ItemStack next = HeroCraft.getItemsAdderItem("§fNext >");
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName("§4§lNächste Seite");
        next.setItemMeta(nextMeta);

        ItemStack open = HeroCraft.getItemsAdderItem("§4§lInventar");
        ItemMeta openMeta = open.getItemMeta();
        openMeta.setDisplayName("§4§lKiste öffnen");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Deine Kisten: §e" + getChestsFromPlayer(player));
        lore.add("");
        lore.add("§0(§7Rechtsklick zum öffnen§0)");
        openMeta.setLore(lore);
        open.setItemMeta(openMeta);

        inventory.setItem(36, back);
        inventory.setItem(40, open);
        inventory.setItem(44, next);
        player.openInventory(inventory);
    }

    public static void setProgressFromPlayer(Player player, int amount) {
        try {
            PreparedStatement preparedStatement;
            if(isInProgressDatabase(player)) {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("UPDATE `survivalland_cases_progresses` SET `progress` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("INSERT INTO `survivalland_cases_progresses` (`uuid`, `progress`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, amount);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInProgressDatabase(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT * FROM `survivalland_cases_progresses` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getProgressFromPlayer(Player player) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT * FROM `survivalland_cases_progresses` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? resultSet.getInt("progress") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
