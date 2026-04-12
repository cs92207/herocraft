package de.christoph.herocraft.caseopening;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
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

    public static HashMap<UUID, CaseInventoryState> pagePlayers = new HashMap<>();

    public CaseOpeningListener() {
        createCaseTableIfNeeded(CaseType.NORMAL);
        createCaseTableIfNeeded(CaseType.PREMIUM);
    }


    @EventHandler
    public void onNormalCaseClick(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CaseType caseType = CaseType.fromFurnitureDisplayName(event.getFurniture().getDisplayName());
        if(caseType == null)
            return;
        event.setCancelled(true);
        openPage(player, 0, caseType);
    }

    @EventHandler
    public void onSurvivalLandsChestInvClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::survivallands_chests:"))
            return;
        event.setCancelled(true);
        CaseInventoryState inventoryState = pagePlayers.get(player.getUniqueId());
        if(inventoryState == null)
            return;
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lZurück")) {
            if(inventoryState.page <= 0)
                return;
            openPage(player, inventoryState.page - 1, inventoryState.caseType);
        } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lNächste Seite")) {
            List<ItemStack> winnings = SetCaseOpeningCommand.loadInventory(inventoryState.caseType);
            int maxPages = winnings.isEmpty() ? 0 : (winnings.size() - 1) / 40;
            if(inventoryState.page >= maxPages)
                return;
            openPage(player, inventoryState.page + 1, inventoryState.caseType);
        } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lKiste öffnen")) {
            int chests = getChestsFromPlayer(player, inventoryState.caseType);
            if(chests <= 0) {
                player.sendMessage(Constant.PREFIX + "§7Du hast keine §c" + inventoryState.caseType.getPluralDisplayName() + " §7mehr.");
                return;
            }
            List<ItemStack> winnings = SetCaseOpeningCommand.loadInventory(inventoryState.caseType);
            int progress = getProgressFromPlayer(player, inventoryState.caseType);
            int nextIndex = progress + 1;
            if(nextIndex < 0 || nextIndex >= winnings.size()) {
                player.sendMessage(Constant.PREFIX + "§7Du hast keine offenen Belohnungen mehr. Warte bis nächsten Monat.");
                return;
            }
            player.getInventory().addItem(winnings.get(nextIndex));
            setProgressFromPlayer(player, nextIndex, inventoryState.caseType);
            setChestsForPlayer(player, chests - 1, inventoryState.caseType);
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Du hast eine §a" + inventoryState.caseType.getSingularDisplayName() + " §7geöffnet.");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        pagePlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        pagePlayers.remove(event.getPlayer().getUniqueId());
    }

    public void openPage(Player player, int page, CaseType caseType) {
        pagePlayers.put(player.getUniqueId(), new CaseInventoryState(page, caseType));
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::survivallands_chests:");
        List<ItemStack> winningItems = SetCaseOpeningCommand.loadInventory(caseType);
        int progress = getProgressFromPlayer(player, caseType);
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
        lore.add("§7Typ: §e" + caseType.getSingularDisplayName());
        lore.add("§7Deine Kisten: §e" + getChestsFromPlayer(player, caseType));
        lore.add("");
        lore.add("§0(§7Rechtsklick zum öffnen§0)");
        openMeta.setLore(lore);
        open.setItemMeta(openMeta);

        inventory.setItem(36, back);
        inventory.setItem(40, open);
        inventory.setItem(44, next);
        player.openInventory(inventory);
    }

    public static void setProgressFromPlayer(Player player, int amount, CaseType caseType) {
        try {
            PreparedStatement preparedStatement;
            if(isInProgressDatabase(player, caseType)) {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("UPDATE `" + caseType.getProgressTable() + "` SET `progress` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("INSERT INTO `" + caseType.getProgressTable() + "` (`uuid`, `progress`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, amount);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInProgressDatabase(Player player, CaseType caseType) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT * FROM `" + caseType.getProgressTable() + "` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getProgressFromPlayer(Player player, CaseType caseType) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT * FROM `" + caseType.getProgressTable() + "` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? resultSet.getInt("progress") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isInDatabase(Player player, CaseType caseType) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `" + caseType.getChestTable() + "` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getChestsFromPlayer(Player player, CaseType caseType) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("SELECT `amount` FROM `" + caseType.getChestTable() + "` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return resultSet.getInt("amount");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setChestsForPlayer(Player player, int amount, CaseType caseType) {
        try {
            PreparedStatement preparedStatement;
            if(isInDatabase(player, caseType)) {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("UPDATE `" + caseType.getChestTable() + "` SET `amount` = ? WHERE `uuid` = ?");
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, player.getUniqueId().toString());
            } else {
                preparedStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement("INSERT INTO `" + caseType.getChestTable() + "` (`uuid`,`amount`) VALUES (?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, amount);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCaseTableIfNeeded(CaseType caseType) {
        try {
            PreparedStatement chestTableStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `" + caseType.getChestTable() + "` (" +
                            "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY," +
                            "`amount` INT NOT NULL DEFAULT 0" +
                            ")"
            );
            chestTableStatement.execute();

            PreparedStatement progressTableStatement = HeroCraft.getPlugin().getShopMySQL().getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `" + caseType.getProgressTable() + "` (" +
                            "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY," +
                            "`progress` INT NOT NULL DEFAULT -1" +
                            ")"
            );
            progressTableStatement.execute();
        } catch (SQLException e) {
            System.out.println("[HeroCraft] Fehler beim Erstellen der Tabellen für " + caseType.getConfigKey() + "e Kisten: " + e.getMessage());
        }
    }

    private static class CaseInventoryState {

        private final int page;
        private final CaseType caseType;

        private CaseInventoryState(int page, CaseType caseType) {
            this.page = page;
            this.caseType = caseType;
        }
    }

}
