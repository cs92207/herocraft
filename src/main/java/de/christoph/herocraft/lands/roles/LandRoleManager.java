package de.christoph.herocraft.lands.roles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class LandRoleManager implements Listener {

    public HashMap<Player, String> rolePlayerEditPlayers;
    private HashMap<String, ArrayList<LandRole>> landRoles; // Land, LandRoles
    private HashMap<Player, ArrayList<LandPermission>> createLandRoleSelectedPermission;
    private HashMap<Player, String> createLandRoleNamePlayers;
    private ArrayList<Player> createLandRoleDescriptionPlayers;

    private HashMap<Player, LandRole> detailPlayers;

    // TODO: :offset_-16::landrole_detail: Inventory Click --> Permission bearbeiten.

    public LandRoleManager() {
        this.rolePlayerEditPlayers = new HashMap<>();
        this.detailPlayers = new HashMap<>();
        this.createLandRoleNamePlayers = new HashMap<>();
        this.createLandRoleDescriptionPlayers = new ArrayList<>();
        this.createLandRoleSelectedPermission = new HashMap<>();
        this.landRoles = new HashMap<>();
        loadLandRoles();
    }

    public ArrayList<LandRole> getRolesFromLand(Land land) {
        return landRoles.get(land.getName());
    }

    @Nullable
    public ArrayList<LandPermission> getLandPermissionFromPlayer(Player player, Land land) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        ArrayList<LandPermission> permissions = new ArrayList<>();
        boolean noLandRole = true;
        if (landRolesFromLand != null) {
            for (LandRole i : landRolesFromLand) {
                if (i.isPlayerMember(player)) {
                    noLandRole = false;
                    permissions.addAll(i.getPermissions());
                }
            }
        }
        if(noLandRole) {
            return null;
        }
        return permissions;
    }

    public void openManageLandRolesInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::manage_land_roles:");
        inventory.setItem(40, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lRolle erstellen").build());
        int i = 0;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(landRoles.get(land.getName()) != null) {
            for(LandRole landRole : landRoles.get(land.getName())) {
                inventory.addItem(new ItemBuilder(Material.LIGHT_BLUE_DYE).setDisplayName(landRole.getName()).setLore("", "§7" + landRole.getDescription()).build());
            }
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onRolePlayerEditInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase("§4§lBenutzer Rollen verwalten"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        ArrayList<LandRole> landRolesFromLand = getRolesFromLand(land);
        LandRole currentLandRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(displayName)) {
                currentLandRole = i;
            }
        }
        if(currentLandRole == null)
            return;
        Player target = Bukkit.getPlayer(rolePlayerEditPlayers.get(player));
        if(target == null) {
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Dieser Spieler ist nicht auf dem §cServer§7.");
            return;
        }
        if(currentLandRole.isPlayerMember(target)) {
            removePlayerFromRole(land, currentLandRole.getName(), target.getUniqueId().toString());
        } else {
            addPlayerToRole(land, currentLandRole.getName(), target.getUniqueId().toString());
        }
        player.closeInventory();
        player.sendMessage(Constant.PREFIX + "§7Rolle geändert.");
    }

    @EventHandler
    public void onManageLandRolesClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::manage_land_roles:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lRolle erstellen")) {
            createLandRoleSelectedPermission.put(player, new ArrayList<>());
            openCreateLandRoleInventory(player);
            return;
        }
        LandRole landRole = null;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        for(LandRole i : landRoles.get(land.getName())) {
            if(i.getName().equalsIgnoreCase(displayName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return;
        openLandRoleDetailInventory(player, landRole, land);
    }

    public void openLandRoleDetailInventory(Player player, LandRole landRole, Land land) {
        detailPlayers.put(player, landRole);
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::landrole_detail:");
        for(LandPermission landPermission : LandPermission.values()) {
            if(!hasRolePermission(land, landRole.getName(), landPermission)) {
                inventory.addItem(new ItemBuilder(Material.PAPER).setDisplayName(landPermission.getName()).setLore("", "§7" + landPermission.getDescription()).build());
            } else {
                inventory.addItem(new ItemBuilder(Material.GRAY_DYE).setDisplayName(landPermission.getName()).setLore("", "§7" + landPermission.getDescription(), "", "§aAUSGEWÄHLT").build());
            }
        }
        ItemStack cancel = HeroCraft.getItemsAdderItem("§fCancel");
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§4§lRolle löschen");
        cancel.setItemMeta(cancelMeta);
        inventory.setItem(40, cancel);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onLandRoleDetailInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::landrole_detail:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lRolle löschen")) {
            deleteRole(land, detailPlayers.get(player).getName());
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Rolle erflogreich §cgelöscht§7.");
            return;
        }
        LandPermission landPermission = null;
        for(LandPermission i : LandPermission.values()) {
            if(i.getName().equalsIgnoreCase(displayName)) {
                landPermission = i;
            }
        }
        if(landPermission == null) {
            return;
        }
        LandRole landRole = detailPlayers.get(player);
        if(hasRolePermission(land, landRole.getName(), landPermission)) {
            removePermissionFromRole(land, landRole.getName(), landPermission);
        } else {
            addPermissionToRole(land, landRole.getName(), landPermission);
        }
        openLandRoleDetailInventory(player, landRole, land);
    }

    public void openCreateLandRoleInventory(Player player) {
        ItemStack next = HeroCraft.getItemsAdderItem("§fNext >");
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName("§4§lWeiter");
        next.setItemMeta(nextMeta);
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::create_landrole:");
        inventory.setItem(12, new ItemBuilder(Material.NETHER_STAR).setDisplayName("§4§lWähle alle Berechtigungen, die diese Rolle haben soll.").build());
        inventory.setItem(14, new ItemBuilder(Material.ARROW).setDisplayName("§4§lWeiter").build());
        int i = 18;
        for(LandPermission landPermission : LandPermission.values()) {
            if(!createLandRoleSelectedPermission.get(player).contains(landPermission))
                inventory.setItem(i, new ItemBuilder(Material.PAPER).setDisplayName(landPermission.getName()).setLore("", "§7" + landPermission.getDescription()).build());
            else
                inventory.setItem(i, new ItemBuilder(Material.GRAY_DYE).setDisplayName(landPermission.getName()).setLore("", "§7" + landPermission.getDescription(), "", "§a§lAUSGEWÄHLT").build());
            i++;
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onCreateLandRoleInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::create_landrole:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lWeiter")) {
            createLandRoleNamePlayers.put(player, "");
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Wie soll die Rolle heißen? (Schreibe in den Chat)");
            player.sendMessage("§4Sneake zum abbrechen.");
            return;
        }
        LandPermission currentLandPermission = null;
        for(LandPermission landPermission : LandPermission.values()) {
            if(landPermission.getName().equalsIgnoreCase(displayName)) {
                currentLandPermission = landPermission;
            }
        }
        if(currentLandPermission == null)
            return;
        createLandRoleSelectedPermission.get(player).add(currentLandPermission);
        openCreateLandRoleInventory(player);
    }

    @EventHandler
    public void onCreateLandRoleSneak(PlayerToggleSneakEvent event) {
        if(createLandRoleNamePlayers.containsKey(event.getPlayer())) {
            createLandRoleNamePlayers.remove(event.getPlayer());
            createLandRoleDescriptionPlayers.remove(event.getPlayer());
            event.getPlayer().sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen.");
        }
    }

    @EventHandler
    public void onCreateLandRoleNameMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!createLandRoleNamePlayers.containsKey(player))
            return;
        if(createLandRoleDescriptionPlayers.contains(player))
            return;
        event.setCancelled(true);
        String name = event.getMessage();
        if(name.length() > 20) {
            player.sendMessage(Constant.PREFIX + "§7Der Name darf maximal 20 Zeichen lang sein. Versuche es erneut.");
            return;
        }
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(existsRole(land, name)) {
            player.sendMessage(Constant.PREFIX + "§7Dein Land hat bereits eine Rolle mit diesem Namen.");
            return;
        }
        createLandRoleNamePlayers.put(player, name);
        player.sendMessage(Constant.PREFIX + "§7Perfekt! Jetzt gebe eine Beschreibung für die Rolle ein:");
        player.sendMessage("§4Sneake zum abbrechen!");
        createLandRoleDescriptionPlayers.add(player);
    }

    @EventHandler
    public void onCreateLandRoleDescriptionMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!createLandRoleDescriptionPlayers.contains(player))
            return;
        event.setCancelled(true);
        String description = event.getMessage();
        if(description.length() > 250) {
            player.sendMessage(Constant.PREFIX + "§7Der Name darf maximal 250 Zeichen lang sein. Versuche es erneut.");
            return;
        }
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        createLandRole(land, createLandRoleNamePlayers.get(player), description, createLandRoleSelectedPermission.get(player));
        player.sendMessage(Constant.PREFIX + "§7Rolle §aerstellt§7.");
    }

    public ArrayList<LandPermission> getPermissionsFromLandRole(Land land, String roleName) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        LandRole landRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(roleName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return null;
        return landRole.getPermissions();
    }

    public boolean hasRolePermission(Land land, String roleName, LandPermission landPermission) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        LandRole landRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(roleName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return false;
        ArrayList<LandPermission> permissions = landRole.getPermissions();
        return (permissions.contains(landPermission));
    }

    public void removePermissionFromRole(Land land, String roleName, LandPermission landPermission) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        LandRole landRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(roleName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return;
        ArrayList<LandPermission> permissions = landRole.getPermissions();
        permissions.remove(landPermission);
        landRole.setPermissions(permissions);
        saveLandRole(landRole);
    }

    public void deleteRole(Land land, String roleName) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        LandRole landRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(roleName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `land_roles` WHERE `name` = ? AND `land` = ?");
            preparedStatement.setString(1, roleName);
            preparedStatement.setString(2, land.getName());
            preparedStatement.execute();
            ArrayList<LandRole> lR = landRoles.get(land.getName());
            lR.remove(landRole);
            landRoles.put(land.getName(), lR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPermissionToRole(Land land, String roleName, LandPermission landPermission) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        LandRole landRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(roleName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return;
        ArrayList<LandPermission> permissions = landRole.getPermissions();
        permissions.add(landPermission);
        landRole.setPermissions(permissions);
        saveLandRole(landRole);
    }

    public void removePlayerFromRole(Land land, String roleName, String playerUUID) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        LandRole landRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(roleName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return;
        ArrayList<String> playersList = landRole.getPlayers();
        playersList.remove(playerUUID);
        landRole.setPlayers(playersList);
        saveLandRole(landRole);
    }

    public void addPlayerToRole(Land land, String roleName, String playerUUID) {
        ArrayList<LandRole> landRolesFromLand = landRoles.get(land.getName());
        LandRole landRole = null;
        for(LandRole i : landRolesFromLand) {
            if(i.getName().equalsIgnoreCase(roleName)) {
                landRole = i;
            }
        }
        if(landRole == null)
            return;
        ArrayList<String> playersList = landRole.getPlayers();
        playersList.add(playerUUID);
        landRole.setPlayers(playersList);
        saveLandRole(landRole);
    }

    public void saveLandRole(LandRole landRole) {
        ArrayList<LandRole> landLandRoles;
        if(landRoles.containsKey(landRole.getLand())) {
            landLandRoles = landRoles.get(landRole.getLand());
        } else {
            landLandRoles = new ArrayList<>();
        }
        LandRole removeLandRole = null;
        for(LandRole i : landLandRoles) {
            if(i.getName().equalsIgnoreCase(landRole.getName())) {
                removeLandRole = i;
            }
        }
        if(removeLandRole != null) {
            landLandRoles.remove(removeLandRole);
        }
        landLandRoles.add(landRole);
        landRoles.put(landRole.getLand(), landLandRoles);

        try {
            deleteLandRoleFromDatabase(landRole);
            ObjectMapper mapper = new ObjectMapper();
            String jsonArray = mapper.writeValueAsString(landRole.getPermissions());
            ObjectMapper mapper1 = new ObjectMapper();
            String playersObject = mapper1.writeValueAsString(landRole.getPlayers());
            PreparedStatement preparedStatement = null;
            preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `land_roles` (`name`, `description`, `land`, `players`, `permissions`) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, landRole.getName());
            preparedStatement.setString(2, landRole.getDescription());
            preparedStatement.setString(3, landRole.getLand());
            preparedStatement.setString(4, playersObject);
            preparedStatement.setString(5, jsonArray);
            preparedStatement.execute();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteLandRoleFromDatabase(LandRole landRole) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `land_roles` WHERE `name` = ? AND `land` = ?");
            preparedStatement.setString(1, landRole.getName());
            preparedStatement.setString(2, landRole.getLand());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createLandRole(Land land, String name, String description, ArrayList<LandPermission> permissions) {
        saveLandRole(new LandRole(
            name,
            description,
            land.getName(),
            new ArrayList<>(),
            permissions
        ));
    }

    public boolean existsRole(Land land, String name) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_roles` WHERE `name` = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadLandRoles() {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_roles`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                // PLAYER & Permission
                ObjectMapper mapper = new ObjectMapper();
                ArrayList<String> players = mapper.readValue(resultSet.getString("players"), mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
                ObjectMapper mapper1 = new ObjectMapper();
                ArrayList<String> permissions = mapper1.readValue(resultSet.getString("permissions"), mapper1.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
                ArrayList<LandPermission> landPermissions = new ArrayList<>();
                for(String i : permissions) {
                    landPermissions.add(LandPermission.valueOf(i));
                }
                LandRole landRole = new LandRole(
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("land"),
                    players,
                    landPermissions
                );
                if(!landRoles.containsKey(landRole.getLand())) {
                    landRoles.put(landRole.getLand(), new ArrayList<>());
                }
                ArrayList<LandRole> roles = landRoles.get(landRole.getLand());
                roles.add(landRole);
                landRoles.put(landRole.getLand(), roles);
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
