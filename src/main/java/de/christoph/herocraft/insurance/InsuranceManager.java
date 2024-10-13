package de.christoph.herocraft.insurance;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.insurance.insurances.HealthInsurance;
import de.christoph.herocraft.insurance.insurances.InventoryInsurance;
import de.christoph.herocraft.insurance.insurances.ToolInsurance;
import de.christoph.herocraft.insurance.insurances.XPInsurance;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InsuranceManager implements Listener {

    private HashMap<Player, ArrayList<PlayerInsurance>> insurencePlayers;
    private ArrayList<Insurance> insurances;

    public InsuranceManager() {
        this.insurencePlayers = new HashMap<>();
        this.insurances = new ArrayList<>();
        loadInsurances();
    }

    private void loadInsurances() {
        InventoryInsurance inventoryInsurance = new InventoryInsurance();
        Bukkit.getPluginManager().registerEvents(inventoryInsurance, HeroCraft.getPlugin());
        insurances.add(inventoryInsurance);
        XPInsurance xpInsurance = new XPInsurance();
        Bukkit.getPluginManager().registerEvents(xpInsurance, HeroCraft.getPlugin());
        insurances.add(xpInsurance);
        HealthInsurance healthInsurance = new HealthInsurance();
        Bukkit.getPluginManager().registerEvents(healthInsurance, HeroCraft.getPlugin());
        insurances.add(healthInsurance);
        ToolInsurance toolInsurance = new ToolInsurance();
        Bukkit.getPluginManager().registerEvents(toolInsurance, HeroCraft.getPlugin());
        insurances.add(toolInsurance);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        insurencePlayers.put(event.getPlayer(), getInsurencesFromPlayer(event.getPlayer()));
        for(PlayerInsurance playerInsurance : insurencePlayers.get(event.getPlayer())) {
            playerInsurance.checkForExpire();
        }
        if(HeroCraft.getPlugin().getConfig().contains("InsuranceJoin." + player.getUniqueId().toString()))
            return;
        PlayerInsurance playerInsurance = new PlayerInsurance(player, "Inventar Versicherung", Date.valueOf(LocalDate.now().plusDays(2)));
        playerInsurance.save();
        player.sendMessage(Constant.PREFIX + "§7Willkommen! Da du neu bist, bekommst du für 2 Tage eine kostenlose §eInventar-Versicherung §7. Wenn du stirbst, behälst du also deine Sachen! (§e/versicherungen§7)");
        HeroCraft.getPlugin().getConfig().set("InsuranceJoin." + player.getUniqueId().toString(), true);
    }

    public void openNewInsuranceGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::new_insurance:");
        for(Insurance insurance : insurances) {
            inventory.addItem(insurance.getIcon());
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onNewInsuranceClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::new_insurance:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        displayName = displayName.substring(4);
        Insurance insurance = getInsuranceByName(displayName);
        if(insurance == null)
            return;
        if(hasAlreadyInsurance(player, displayName)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Du hast diese Versicherung bereits. Gehe zum Menüpunkt §aDeine Versicherungen §7um sie zu verwalten.");
            player.closeInventory();
            return;
        }
        if(HeroCraft.getPlugin().coin.getCoins(player) < insurance.getCost()) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            return;
        }
        HeroCraft.getPlugin().coin.removeMoney(player, insurance.getCost());
        PlayerInsurance playerInsurance = new PlayerInsurance(player, displayName, Date.valueOf(LocalDate.now().plusDays(2)));
        playerInsurance.save();
        player.sendMessage(Constant.PREFIX + "§7Du hast die Versicherung §a" + displayName + "§7 abgeschlossen.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.closeInventory();
    }

    private boolean hasAlreadyInsurance(Player player, String insuranceName) {
        for(PlayerInsurance playerInsurance : insurencePlayers.get(player)) {
            if(playerInsurance.getInsuranceName().equalsIgnoreCase(insuranceName))
                return true;
        }
        return false;
    }

    @Nullable
    private Insurance getInsuranceByName(String name) {
        for(Insurance insurance : insurances) {
            if(insurance.getName().equalsIgnoreCase(name))
                return insurance;
        }
        return null;
    }

    public void openYourInsurancesGUI(Player player) {
        System.out.println(insurencePlayers.get(player));
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::your_insurances:");
        for(PlayerInsurance playerInsurance : insurencePlayers.get(player)) {
            ItemStack itemStack = getIconByInsuranceName(playerInsurance.getInsuranceName());
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            lore.add("");
            lore.add("§eKündigen §7- Rechtsklick");
            lore.add("§eUm 2 Tage verlängern §7- Linksklick");
            lore.add("");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String formattedDate = dateFormat.format(playerInsurance.getNeedPaied());
            lore.add("§7Läuft aus am §e" + formattedDate);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.addItem(itemStack);
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onYourInsurancesGUIClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::your_insurances:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName().substring(4);
        PlayerInsurance playerInsurance = getPlayerInsuranceByPlayerAndName(player, displayName);
        if(playerInsurance == null)
            return;
        if(event.getAction() == InventoryAction.PICKUP_HALF) {
            // Delete Insurance (Rightclick)
            try {
                playerInsurance.deleteInsurance();
                player.sendMessage(Constant.PREFIX + "§7Du hast die Versicherung §c" + displayName + "§7 gekündigt.");
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Refresh Insurance (Leftclick)
            playerInsurance.refresh();
            openYourInsurancesGUI(player);
        }
    }

    @Nullable
    public PlayerInsurance getPlayerInsuranceByPlayerAndName(Player player, String displayName) {
        for(PlayerInsurance playerInsurance : insurencePlayers.get(player)) {
            if(playerInsurance.getInsuranceName().equalsIgnoreCase(displayName))
                return playerInsurance;
        }
        return null;
    }

    @Nullable
    public ItemStack getIconByInsuranceName(String insuranceName) {
        for(Insurance insurance : insurances) {
            if(insurance.getName().equalsIgnoreCase(insuranceName))
                return insurance.getIcon();
        }
        return null;
    }

    private ArrayList<PlayerInsurance> getInsurencesFromPlayer(Player player) {
        ArrayList<PlayerInsurance> playerInsurances = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `insurance` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String insuranceName = resultSet.getString("insuranceName");
                Date needPaied = resultSet.getDate("needPaied");
                playerInsurances.add(new PlayerInsurance(
                   player,
                   insuranceName,
                   needPaied
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playerInsurances;
    }

    public HashMap<Player, ArrayList<PlayerInsurance>> getInsurencePlayers() {
        return insurencePlayers;
    }

    public ArrayList<Insurance> getInsurances() {
        return insurances;
    }

}
