package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
//import dev.lone.itemsadder.api.ItemsAdder;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import it.unimi.dsi.fastutil.Hash;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Goverment implements Listener {

    private static ArrayList<Player> payInPlayers = new ArrayList<>();
    private static ArrayList<Player> payOutPlayers = new ArrayList<>();
    private static ArrayList<Player> setLandPlayers = new ArrayList<>();
    private static HashMap<Player, Integer> membersShowPagePlayers = new HashMap<>();
    private static HashMap<Player, String> memberAdminDetailPagePlayers = new HashMap<>();
    public static ArrayList<Player> inviterPlayers = new ArrayList<>();
    public static ArrayList<Player> landMaxBlockPlayers = new ArrayList<>();
    public static HashMap<Player, Integer> landMaxSubmitPlayers = new HashMap<>(); // size
    public static HashMap<Player, Land> invitedPlayers = new HashMap<>();
    public static HashMap<Player, Location> firstEdgeResizePlayers = new HashMap<>();
    public static HashMap<Player, Location> secondEdgeResizePlayers = new HashMap<>();
    public static ArrayList<Player> resizePlayers = new ArrayList<>();
    public static ArrayList<Player> resizeOnePlayers = new ArrayList<>();
    public static ArrayList<Player> resizeWaitingPlayers = new ArrayList<>();

    private static void openGovermentGUI(Player player, Land land) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::goverment:");
        inventory.setItem(11, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§7Coins: §e§l" + land.getCoins()).build());
        inventory.setItem(15, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglieder").build());
        inventory.setItem(28, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCoins einzahlen").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCoins einzahlen").build());
        inventory.setItem(30, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCoins einzahlen").build());
        inventory.setItem(32, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAdmin Menu").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAdmin Menu").build());
        inventory.setItem(34, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAdmin Menu").build());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onGovermentGUIClick(dev.lone.itemsadder.api.Events.FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
            Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            if(land == null)
                return;
            if(!land.canBuild(player)) {
                player.sendMessage(Constant.PREFIX + "§7Du kannst nicht auf Regierungen von anderen Ländern zugreifen.");
                return;
            }
            openGovermentGUI(player, land);
        }
    }

    @EventHandler
    public void onGovermentBlockTryBreak(dev.lone.itemsadder.api.Events.FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        if(event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
            Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            if(land == null)
                return;
            if(land.isModerator(event.getPlayer().getName()) || land.isOwner(event.getPlayer().getName())) {
                player.sendMessage(Constant.PREFIX + "§7Bitte setze die Regierung nun an eine andere Stelle des Landes.");
            } else {
                event.setCancelled(true);
                player.sendMessage(Constant.PREFIX + "§7Nur Moderatoren oder Eigentümer des Landes dürfen die Regierung umsetzen.");
            }
        }
    }

    @EventHandler
    public void onFurniturePlace(dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent event) {
        Player player = event.getPlayer();
        if(event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
            Land land = LandManager.getLandAtLocation(event.getBukkitEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            if(land == null || !land.canBuild(player)) {
                event.getFurniture().remove(false);
                ItemStack goverment = null;
                for(CustomStack i : ItemsAdder.getAllItems()) {
                    if(i.getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
                        goverment = i.getItemStack();
                    }
                }
                player.getInventory().addItem(goverment);
                player.sendMessage(Constant.PREFIX + "§7Bitte setze das Regierungsgebäude auf dein Land.");
            }
        }
    }

    private static void openGovermentAdminGUI(Player player, Land land) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::goverment_admin:");
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand löschen").build());
        inventory.setItem(12, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglieder verwalten").build());
        inventory.setItem(14, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lGröße ändern").setLore("", "§eLinksklick: §7Fläche ändern", "§eRechtsklick: §7Größere Maximale Fläche kaufen", "", "§7Maximal: §e" + land.getMaxBlocks() + " Blöcke").build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lStadt gründen").setLore("", "§7Kosten: §e" + Constant.CITY_PRICE).build());
        inventory.setItem(28, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCoins auszahlen").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCoins auszahlen").build());
        inventory.setItem(30, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCoins auszahlen").build());
        inventory.setItem(32, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lSpawnpoint setzen").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lSpawnpoint setzen").build());
        inventory.setItem(34, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lSpawnpoint setzen").build());
        player.openInventory(inventory);
    }

    private static void resizeLand(Player player) {
        player.closeInventory();
        player.sendMessage(Constant.PREFIX + "§7Klicke in die erste Ecke deines Landes.");
        player.sendMessage("§4Sneake zum abbrechen!");
        resizePlayers.add(player);
    }

    @EventHandler
    public void onPlayerClickEdge(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        if(event.getClickedBlock() == null)
            return;
        Player player = event.getPlayer();
        if(resizeWaitingPlayers.contains(player))
            return;
        if(resizePlayers.contains(player)) {
            firstEdgeResizePlayers.put(player, event.getClickedBlock().getLocation());
            player.sendMessage(Constant.PREFIX + "§7Sehr gut! Klicke nun in die andere gegenüberliegende Ecke des Landes.");
            resizeWaitingPlayers.add(player);
            resizePlayers.remove(player);
            resizeOnePlayers.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    resizeWaitingPlayers.remove(player);
                }
            }, 20);
        } else if(resizeOnePlayers.contains(player)) {
            secondEdgeResizePlayers.put(player, event.getClickedBlock().getLocation());
            Location first = firstEdgeResizePlayers.get(player);
            Location second = secondEdgeResizePlayers.get(player);
            Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            boolean canSize = LandManager.canCreateLandSize(
                first.getX(),
                first.getZ(),
                second.getX(),
                second.getZ(),
                land.getMaxBlocks()
            );
            if(!canSize) {
                resizeOnePlayers.remove(player);
                resizePlayers.remove(player);
                firstEdgeResizePlayers.remove(player);
                secondEdgeResizePlayers.remove(player);
                resizeWaitingPlayers.remove(player);
                player.sendMessage(Constant.PREFIX + "§7Dein Land darf maximal §4" + land.getMaxBlocks() + " Blöcke §7groß sein und braucht mindestens eine Fläche von §c4 Blöcken§7. Vorgang abgebrochen.");
                return;
            }
            boolean canPosition = LandManager.canCreateLandLocation(
                first.getX(),
                first.getZ(),
                second.getX(),
                second.getZ(),
                HeroCraft.getPlugin().getLandManager().getAllLands(),
                land.getName()
            );
            if(!canPosition) {
                player.sendMessage(Constant.PREFIX + "§7Ein anderes Land würde deines überschneiden. Vorgang abgebrochen.");
                resizeOnePlayers.remove(player);
                resizePlayers.remove(player);
                firstEdgeResizePlayers.remove(player);
                secondEdgeResizePlayers.remove(player);
                resizeWaitingPlayers.remove(player);
                return;
            }
            land.changeSize(
                first.getBlock().getX(),
                first.getBlock().getZ(),
                second.getBlock().getX(),
                second.getBlock().getZ()
            );
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Größe des Landes §ageändert§7!");
            resizeOnePlayers.remove(player);
            resizePlayers.remove(player);
            firstEdgeResizePlayers.remove(player);
            secondEdgeResizePlayers.remove(player);
            resizeWaitingPlayers.remove(player);
        }
    }

    private static void makeMaxBlocksHigher(Player player, Land land) {
        player.closeInventory();
        player.sendMessage(Constant.PREFIX + "§7Deine maximale Landgröße beträgt momentan §e" + land.getMaxBlocks() + " Blöcke§7. Wieviele Blöcke soll sie neu betragen? §0(§e+ 1 Block = 10 Coins)");
        player.sendMessage("§0(Schreibe eine Zahl von §7" + (land.getMaxBlocks() + 1) + " - " + Constant.MAX_LAND_SIZE + "§0 in den Chat)");
        player.sendMessage("§cSneake zum abbrechen!");
        landMaxBlockPlayers.add(player);
    }

    @EventHandler
    public void onMaxChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(landMaxSubmitPlayers.containsKey(player) && event.getMessage().equalsIgnoreCase("bestätigen")) {
            event.setCancelled(true);
            Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            int newSize = landMaxSubmitPlayers.get(player);
            int price = (newSize - land.getMaxBlocks()) * 10;
            if(land.getCoins() < price) {
                player.sendMessage(Constant.PREFIX + "§7Hierzu hat dein Land nicht genug §cCoins§7. Vorgang abgebrochen!");
                landMaxSubmitPlayers.remove(player);
                return;
            }
            land.setMaxBlocks(newSize);
            land.setCoins(land.getCoins() - price);
            player.sendMessage(Constant.PREFIX + "§7Du hast die maximale Landgröße §avergrößert§7.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            landMaxSubmitPlayers.remove(player);
            return;
        }
        if(!landMaxBlockPlayers.contains(player))
            return;
        event.setCancelled(true);
        int newSize = 0;
        try {
            newSize = Integer.parseInt(event.getMessage());
        } catch (NumberFormatException e) {
            player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige Zahl. Probiere es erneut.");
            return;
        }
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land.getMaxBlocks() >= newSize) {
            player.sendMessage(Constant.PREFIX + "§7Die new Größe muss größer sein, als die, die dein Land bisher haben kann. Probiere es erneut!");
            return;
        }
        if(newSize > Constant.MAX_LAND_SIZE) {
            player.sendMessage(Constant.PREFIX + "§7Die maximale Größe beträgt §c" + Constant.MAX_LAND_SIZE + " Blöcke§7. Probiere es erneut!");
            return;
        }
        int price = (newSize - land.getMaxBlocks()) * 10;
        landMaxBlockPlayers.remove(player);
        landMaxSubmitPlayers.put(player, newSize);
        player.sendMessage(Constant.PREFIX + "§7Dieser Vorgang würde §e" + price + " Coins §7kosten. Schreibe §abestätigen§7, um ihn durchzuführen.");
        player.sendMessage("§cSneake zum abbrechen!");
    }

    @EventHandler
    public void onGovermentAdminAreaGUIClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_admin:"))
            return;
        event.setCancelled(true);
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lLand löschen")) {
            land.delete();
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Land erfolgreich gelöscht.");
        } else if(displayName.equalsIgnoreCase("§4§lMitglieder verwalten")) {
            openMembersShowInventory(player, 1);
        } else if(displayName.equalsIgnoreCase("§4§lCoins auszahlen")) {
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Schreibe in den Chat wieviele Coins du auszahlen möchtest.");
            player.sendMessage(Constant.PREFIX + "§4Sneake zum abbrechen!");
            payOutPlayers.add(player);
        } else if(displayName.equalsIgnoreCase("§4§lSpawnpoint setzen")) {
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Sneake an der Stelle, wo der neue Spawnpoint sein soll.");
            setLandPlayers.add(player);
        } else if(displayName.equalsIgnoreCase("§4§lGröße ändern")) {
            if(event.getAction() == InventoryAction.PICKUP_HALF) { // Rightclicked
                makeMaxBlocksHigher(player, land);
            } else { // Leftclicked
                resizeLand(player);
            }
        } else if(displayName.equalsIgnoreCase("§4§lStadt gründen")) {
            if(land.getCoins() >= Constant.LAND_PRICE) {
                land.setCoins(land.getCoins() - Constant.LAND_PRICE);
            } else if(HeroCraft.getPlugin().coin.getCoins(player) >= Constant.LAND_PRICE) {
                HeroCraft.getPlugin().coin.removeMoney(player, Constant.LAND_PRICE);
            } else {
                player.sendMessage(Constant.PREFIX + "§7Dein Land hat nicht genug Coins, und du hast nicht genug Coins, um die Kosten zu übernehmen.");
                return;
            }
            ItemStack goverment = null;
            for(CustomStack i : ItemsAdder.getAllItems()) {
                if(i.getDisplayName().equalsIgnoreCase("§4§lStadt")) {
                    goverment = i.getItemStack();
                }
            }
            player.getInventory().addItem(goverment);
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Erstelle eine Stadt, indem du den Stadt Block §ain deinem Land §7platzierst.");
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(landMaxBlockPlayers.contains(player)) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen");
            landMaxBlockPlayers.remove(player);
        }
        if(landMaxSubmitPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen");
            landMaxSubmitPlayers.remove(player);
        }
        if(resizePlayers.contains(player) || resizeOnePlayers.contains(player)) {
            resizeOnePlayers.remove(player);
            resizePlayers.remove(player);
            firstEdgeResizePlayers.remove(player);
            secondEdgeResizePlayers.remove(player);
            resizeWaitingPlayers.remove(player);
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen");
        }
        if(!setLandPlayers.contains(player))
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(!LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands()).getName().equalsIgnoreCase(land.getName())) {
            player.sendMessage(Constant.PREFIX + "§7Sneake in deinem Land!");
            return;
        }
        land.setSpawnPoint(player.getLocation());
        player.sendMessage(Constant.PREFIX + "§7Spawnpoint gesetzt.");
        setLandPlayers.remove(player);
    }

    @EventHandler
    public void onGovermentGUIClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lMitglieder")) {
            openMembersShowInventory(player, 1);
        } else if(displayName.equalsIgnoreCase("§4§lCoins einzahlen")) {
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Gebe die anzahl an Coins ein, die du einzahlen möchtest.");
            player.sendMessage("§4Sneaken zum abbrechen!");
            payInPlayers.add(player);
        } else if(displayName.equalsIgnoreCase("§4§lAdmin Menu")) {
            if(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).isModerator(player.getName()) ||HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).isOwner(player.getName()))
                openGovermentAdminGUI(player, HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player));
            else {
                player.closeInventory();
                player.sendMessage(Constant.PREFIX + "§7Du darfst nicht auf diesen Bereich zugreifen.");
            }
        }
    }

    private static void openMembersShowInventory(Player player, int page) {
        membersShowPagePlayers.put(player, page);
         Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::goverment_member:");
         Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
         ArrayList<String> allLandPlayers = new ArrayList<>();
         allLandPlayers.add(land.getFounderName());
         allLandPlayers.addAll(Arrays.asList(land.getCoFounderNames()));
         allLandPlayers.addAll(Arrays.asList(land.getMemberNames()));
         ArrayList<String> pageNames = filterByPage(allLandPlayers, page, 36);
         ItemStack back = null;
         ItemStack next = null;
         ItemStack invite = null;
         for(CustomStack i : ItemsAdder.getAllItems()) {
             if(i.getDisplayName().contains("§f< Back")) {
                 back = i.getItemStack();
             } else if(i.getDisplayName().contains("§fNext >")) {
                 next = i.getItemStack();
             } else if(i.getDisplayName().contains("§fSearch")) {
                 invite = i.getItemStack();
             }
         }
         ItemMeta itemMeta = back.getItemMeta();
        itemMeta.setDisplayName("§4§lVorherige Seite");
        back.setItemMeta(itemMeta);
        ItemMeta itemMeta1 = next.getItemMeta();
        itemMeta1.setDisplayName("§4§lNächste Seite");
        next.setItemMeta(itemMeta1);
        ItemMeta itemMeta2 = invite.getItemMeta();
        itemMeta2.setDisplayName("§4§lMitglied einladen");
        invite.setItemMeta(itemMeta2);
        for(String currentName : pageNames) {
            if(currentName.equalsIgnoreCase(""))
                continue;
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            if(land.isOwner(currentName)) {
                skullMeta.setDisplayName("§4" + currentName);
            } else if(land.isModerator(currentName)) {
                skullMeta.setDisplayName("§b" + currentName);
            } else {
                skullMeta.setDisplayName("§7" + currentName);
            }
            skullMeta.setOwner(currentName);
            itemStack.setItemMeta(skullMeta);
            inventory.addItem(itemStack);
        }
        //inventory.setItem(44, next);
        if(land.isOwner(player.getName()) || land.isModerator(player.getName()))
            inventory.setItem(40, invite);
        if(page != 1) {
          //  inventory.setItem(38, back);
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onMembersInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member:"))
            return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
               if(!player.getOpenInventory().getTitle().equalsIgnoreCase(":offset_-16::goverment_member:")) {
                   membersShowPagePlayers.remove(player);
               }
            }
        }, 10);
    }

    @EventHandler
    public void onMembersShowInventory(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member:"))
            return;
        event.setCancelled(true);
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(displayName.equalsIgnoreCase("§4§lVorherige Seite")) {
            openMembersShowInventory(player, membersShowPagePlayers.get(player) - 1);
            return;
        } else if(displayName.equalsIgnoreCase("§4§lNächste Seite")) {
            openMembersShowInventory(player, membersShowPagePlayers.get(player) + 1);
            return;
        } else if(displayName.equalsIgnoreCase("§4§lMitglied einladen")) {
            if(!player.hasPermission("herowars.cc")) {
                if(land.getAllLandNames().size() >= 10) {
                    player.closeInventory();
                    player.sendMessage(Constant.PREFIX + "§7Dein Land darf maximal 10 Mitglieder haben.");
                    return;
                }
            }
            inviterPlayers.add(player);
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Gebe den Spielernamen ein, den du in dein Land einladen möchtest.");
            player.sendMessage("§4Sneaken zum abbrechen!");
            return;
        }
        if(!land.isOwner(player.getName()) && !land.isModerator(player.getName()))
            return;
        String currentPlayerName = displayName.substring(2);
        openMemberDetailAdminPage(player, currentPlayerName);
    }

    @EventHandler
    public void onInviteBreak(PlayerToggleSneakEvent event) {
        if(inviterPlayers.contains(event.getPlayer())) {
            inviterPlayers.remove(event.getPlayer());
            event.getPlayer().sendMessage("§4Vorgang abgebrochen!");
        }
    }

    @EventHandler
    public void onInviterChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!inviterPlayers.contains(player))
            return;
        event.setCancelled(true);
        Player target = Bukkit.getPlayer(event.getMessage());
        if(target == null) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen! Der Spieler ist nicht online.");
            inviterPlayers.remove(player);
            return;
        }
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land.canBuild(target)) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen! Dieser Spieler ist bereits Mitglied deines Landes.");
            inviterPlayers.remove(player);
            return;
        }
        if(invitedPlayers.containsKey(target)) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen! Dieser Spieler hat bereits eine Einladung erhalten.");
            inviterPlayers.remove(player);
            return;
        }
        if(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(target) != null) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen! Dieser Spieler ist bereits Mitglied eines Landes.");
            inviterPlayers.remove(player);
            return;
        }
        player.sendMessage(Constant.PREFIX + "§7Der Spieler wurde erfolgreich §aeingeladen§7.");
        inviterPlayers.remove(player);
        invitedPlayers.put(target, land);
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        target.sendMessage(Constant.PREFIX + "§7Du wurdest vom Land §e§l" + land.getName() + "§7 eingeladen.");
        target.sendMessage("§7benutze §a/landeinladungannehmen §7oder §c/landeinladungablehnen");
    }

    public static void openMemberDetailAdminPage(Player player, String currentPlayer) {
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::goverment_member_detail:");
        ItemStack currentPlayerItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) currentPlayerItem.getItemMeta();
        skullMeta.setOwner(currentPlayer);
        if(land.isOwner(currentPlayer)) {
            skullMeta.setDisplayName("§4" + currentPlayer);
        } else if(land.isModerator(currentPlayer)) {
            skullMeta.setDisplayName("§b" + currentPlayer);
        } else {
            skullMeta.setDisplayName("§7" + currentPlayer);
        }
        currentPlayerItem.setItemMeta(skullMeta);
        inventory.setItem(13, currentPlayerItem);
        inventory.setItem(28, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglied kicken").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglied kicken").build());
        inventory.setItem(30, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglied kicken").build());
        inventory.setItem(32, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglied Befördern/Degradieren").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglied Befördern/Degradieren").build());
        inventory.setItem(34, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMitglied Befördern/Degradieren").build());
        player.openInventory(inventory);
        memberAdminDetailPagePlayers.put(player, currentPlayer);
    }

    @EventHandler
    public void onMemberDetailAdminPageClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member_detail:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lMitglied kicken")) {
            land.removeMember(memberAdminDetailPagePlayers.get(player));
            player.sendMessage(Constant.PREFIX + "§7Mitglied gekickt.");
            player.closeInventory();
        } else if(displayName.equalsIgnoreCase("§4§lMitglied Befördern/Degradieren")) {
            String targetName = memberAdminDetailPagePlayers.get(player);
            if(land.isModerator(targetName)) {
                land.degradePlayer(targetName);
                player.sendMessage(Constant.PREFIX + "§7Spieler degradiert.");
                player.closeInventory();
            } else {
                land.promotePlayer(targetName);
                player.sendMessage(Constant.PREFIX + "§7Spieler befördert.");
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onMemberAdminDetailPageClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member_detail:"))
            return;
        memberAdminDetailPagePlayers.remove(player);
    }

    public static ArrayList<String> filterByPage(ArrayList<String> originalList, int page, int pageSize) {
        ArrayList<String> resultList = new ArrayList<>();

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, originalList.size());

        for (int i = startIndex; i < endIndex; i++) {
            resultList.add(originalList.get(i));
        }

        return resultList;
    }

    @EventHandler
    public void onPlayerPayInBreak(PlayerToggleSneakEvent event) {
        if(payInPlayers.contains(event.getPlayer())) {
            payInPlayers.remove(event.getPlayer());
            event.getPlayer().sendMessage("§4§lVorgang abgebrochen!");
        } else if(payOutPlayers.contains(event.getPlayer())) {
            payOutPlayers.remove(event.getPlayer());
            event.getPlayer().sendMessage("§4§lVorgang abgebrochen!");
        }
    }

    @EventHandler
    public void onPayInPlayersChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(payOutPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            double amount = 0;
            try {
                amount = Double.parseDouble(event.getMessage());
            } catch (NumberFormatException e) {
                player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
                return;
            }
            if(amount <= 0) {
                player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
                return;
            }
            Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            if(land.getCoins() < amount) {
                player.sendMessage(Constant.PREFIX + "§7Dazu hat das Land nicht genügend Coins. Bitte versuche es erneut.");
                return;
            }
            payOutPlayers.remove(player);
            player.sendMessage(Constant.PREFIX + "§7Coins erfolgreich ausgezahlt.");
            land.setCoins(land.getCoins() - amount);
            double finalAmount = amount;
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    HeroCraft.getPlugin().coin.addMoney(player, finalAmount);
                }
            }, 10);
            return;
        }
        if(!payInPlayers.contains(event.getPlayer()))
            return;
        event.setCancelled(true);
        double amount = 0;
        try {
            amount = Double.parseDouble(event.getMessage());
        } catch (NumberFormatException e) {
            player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
            return;
        }
        if(amount <= 0) {
            player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
            return;
        }
        if(HeroCraft.getPlugin().coin.getCoins(player) < amount) {
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genügend Coins. Bitte versuche es erneut.");
            return;
        }
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        land.setCoins(land.getCoins() + amount);
        HeroCraft.getPlugin().getLandManager().saveLand(land);
        player.sendMessage(Constant.PREFIX + "§7Coins erfolgreich eingezahlt.");
        payInPlayers.remove(player);
        double finalAmount = amount;
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                HeroCraft.getPlugin().coin.removeMoney(player, finalAmount);
            }
        }, 10);
    }

}
