package de.christoph.herocraft.lands.province;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.ChatClickBuilder;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class CityBlock implements Listener {

    public static HashMap<Player, CustomFurniture> provinceCreatePlayers = new HashMap<>();
    public static HashMap<Player, String> townHallSetPlayers = new HashMap<>();

    @EventHandler
    public void onFurniturePlace(FurniturePlaceSuccessEvent event) {
        Player player = event.getPlayer();
        System.out.println(event.getFurniture().getDisplayName());
        if(event.getFurniture().getDisplayName().contains("§7Stadt:")) {
            Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            if(!land.canBuild(player) || land == null) {
                player.sendMessage(Constant.PREFIX + "§7Setze den Stadtblock in dein §cLand§7.");
                event.getFurniture().remove(false);
                ItemStack newCityBlock = HeroCraft.getItemsAdderItem("§4§lStadt");
                ItemMeta itemMeta = newCityBlock.getItemMeta();
                itemMeta.setDisplayName(event.getFurniture().getDisplayName());
                newCityBlock.setItemMeta(itemMeta);
                player.getInventory().addItem(newCityBlock);
                player.sendMessage(Constant.PREFIX + "§7Bitte setze den Stadt-Block nun an eine andere Stelle des Landes.");
                return;
            }
            event.getFurniture().getEntity().setCustomName(event.getFurniture().getDisplayName());
            event.getFurniture().getEntity().setCustomNameVisible(true);
            return;
        }
        if(!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lStadt"))
            return;
        Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land == null || !land.canBuild(player)) {
            event.getFurniture().remove(false);
            ItemStack goverment = null;
            for(CustomStack i : ItemsAdder.getAllItems()) {
                if(i.getDisplayName().equalsIgnoreCase("§4§lStadt")) {
                    goverment = i.getItemStack();
                }
            }
            player.getInventory().addItem(goverment);
            player.sendMessage(Constant.PREFIX + "§7Bitte setze die Stadt auf dein Land.");
            return;
        }
        if(TownHall.townhallProvinceCreationPlayers.containsKey(player)) {
            // Creation with TownHall first
            Province province = TownHall.townhallProvinceCreationPlayers.get(player);
            event.getFurniture().getEntity().setCustomName("§7Stadt: §e§l" + province.getName());
            event.getFurniture().getEntity().setCustomNameVisible(true);
            HeroCraft.getPlugin().getProvinceManager().getProvinces().add(province);
            HeroCraft.getPlugin().getProvinceManager().saveProvince(province);
            player.sendMessage(Constant.PREFIX + "§7Stadt erfolgreich §aerstellt§7.");
            return;
        }
        player.sendMessage(Constant.PREFIX + "§7Gebe in den Chat ein: §aWie soll die Stadt heißen?");
        player.sendMessage("§4Sneake zum abbrechen!");
        provinceCreatePlayers.put(player, event.getFurniture());
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if(!provinceCreatePlayers.containsKey(event.getPlayer()))
            return;
        provinceCreatePlayers.get(event.getPlayer()).remove(true);
        event.getPlayer().sendMessage("§4Vorgang abgebrochen!");
        provinceCreatePlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        if(!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lStadt"))
            return;
        if(!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lStadt"))
            return;
        Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land == null)
            return;
        String provinceName = event.getFurniture().getEntity().getCustomName();
        event.getBukkitEntity().remove();
        ItemStack newCityBlock = HeroCraft.getItemsAdderItem("§4§lStadt");
        ItemMeta itemMeta = newCityBlock.getItemMeta();
        itemMeta.setDisplayName(provinceName);
        newCityBlock.setItemMeta(itemMeta);
        player.getInventory().addItem(newCityBlock);
        player.sendMessage(Constant.PREFIX + "§7Bitte setze den Stadt-Block nun an eine andere Stelle des Landes.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(!provinceCreatePlayers.containsKey(event.getPlayer()))
            return;
        event.setCancelled(true);
        CustomFurniture customFurniture = provinceCreatePlayers.get(event.getPlayer());
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(event.getPlayer());
        String name = event.getMessage();
        Player player = event.getPlayer();
        if(name.contains(" ")) {
            player.sendMessage(Constant.PREFIX + "§7Der Name darf leine Leerzeichen. Veruche es erneut!");
            return;
        }
        if(name.matches(".*[^a-zA-Z0-9 ].*")) {
            player.sendMessage(Constant.PREFIX + "§7Der Name darf keine Sonderzeichen enthalten. Veruche es erneut!");
            return;
        }
        if(name.length() > 25) {
            player.sendMessage(Constant.PREFIX + "§7Der Name darf höchstens 25 Zeichen haben. Veruche es erneut!");
            return;
        }
        Province provinceNameCheck = HeroCraft.getPlugin().getProvinceManager().getProvinceByName(land.getName(), name);
        if(provinceNameCheck != null) {
            player.sendMessage(Constant.PREFIX + "§7Dein Land hat bereits eine Stadt mit diesem Namen. Versuche es erneut!");
            return;
        }
        ItemStack townHall = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                townHall = i.getItemStack();
            }
        }
        player.getInventory().addItem(townHall);
        player.sendMessage(Constant.PREFIX + "§7Setze das §aRathaus §7nun an die Stelle, an der du die Stadt gründen möchtest.");
        new ChatClickBuilder("§7[§a§lRandomTP§7]", "/rtp 1288", "§7Klicke, um dich zufällig zu teleportieren.").sendToPlayer(player);
        customFurniture.getEntity().setCustomName("§7Stadt: §e§l" + name);
        customFurniture.getEntity().setCustomNameVisible(true);
        provinceCreatePlayers.remove(player);
        townHallSetPlayers.put(player, name);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!townHallSetPlayers.containsKey(event.getPlayer()))
            return;
        HeroCraft.getPlugin().getConfig().set("SetTownHall." + event.getPlayer().getUniqueId().toString(), townHallSetPlayers.get(event.getPlayer()));
        HeroCraft.getPlugin().saveConfig();
        townHallSetPlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(!event.getPlayer().getInventory().getItemInMainHand().hasItemMeta())
            return;
        if(!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
            return;
        if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lRathaus"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack[] contents = player.getInventory().getContents();
        if(!townHallSetPlayers.containsKey(player))
            return;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName() && meta.getDisplayName().equals("§4§lRathaus")) {
                    player.getInventory().setItem(i, null);
                }
            }
        }
        ItemStack goverment = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                goverment = i.getItemStack();
            }
        }
        player.getInventory().addItem(goverment);
        player.updateInventory();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(HeroCraft.getPlugin().getConfig().contains("SetTownHall." + event.getPlayer().getUniqueId().toString())) {
            if(HeroCraft.getPlugin().getConfig().getString("SetTownHall." + event.getPlayer().getUniqueId().toString()).equalsIgnoreCase(""))
                return;
            townHallSetPlayers.put(event.getPlayer(), HeroCraft.getPlugin().getConfig().getString("SetTownHall." + event.getPlayer().getUniqueId().toString()));
            HeroCraft.getPlugin().getConfig().set("SetTownHall." + event.getPlayer().getUniqueId().toString(), "");
            HeroCraft.getPlugin().saveConfig();
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    event.getPlayer().sendTitle("§e§lPlatziere das Rathaus", "§7Um deine Stadt zu gründen!");
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    event.getPlayer().sendMessage(Constant.PREFIX + "§7Platziere das Rathaus, um die Stadt zu gründen.");
                }
            }, 20*5);
        }
    }

    @EventHandler
    public void onFurnitureClick(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        if(!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lStadt") && !event.getFurniture().getDisplayName().contains("§7Stadt:"))
            return;
        Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land == null)
            return;
        String provinceName = event.getFurniture().getEntity().getCustomName().substring(13);
        Province province = HeroCraft.getPlugin().getProvinceManager().getProvinceByName(land.getName(), provinceName);
        if(province == null) {
            player.sendMessage(Constant.PREFIX + "§7Setze das §aRathaus §7nun an die Stelle, an der du die Stadt gründen möchtest.");
            new ChatClickBuilder("§7[§a§lRandomTP§7]", "/rtp 1288", "§7Klicke, um dich zufällig zu teleportieren.").sendToPlayer(player);
            return;
        }
        province.teleportTo(player);
    }

}
