package de.christoph.herocraft.lands.province;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandCreator;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import jdk.jfr.Enabled;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class TownHall implements Listener {

    public static HashMap<Player, Province> townhallProvinceCreationPlayersNameProvince = new HashMap<>();
    public static HashMap<Player, Province> townhallProvinceCreationPlayers= new HashMap<>();
    public static HashMap<Player, CustomFurniture> townhallProvinceCreationPlayersNames = new HashMap<>();

    @EventHandler
    public void onFurniturePlace(FurniturePlaceSuccessEvent event) {
        Player player = event.getPlayer();
        if(!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRathaus"))
            return;
        if(CityBlock.townHallSetPlayers.containsKey(event.getPlayer())) {
            Location location = event.getFurniture().getEntity().getLocation();
            double x1 = location.getX() + 50;
            double z1 = location.getZ() + 50;
            double x2 = location.getX() - 50;
            double z2 = location.getZ() - 50;
            String worldName = location.getWorld().getName();
            if(worldName.equalsIgnoreCase("autumDimension") || worldName.equalsIgnoreCase("winterDimension") || worldName.equalsIgnoreCase("springDimension") || worldName.equalsIgnoreCase("summerDimension")) {
                x1 += 100;
                z1 += 100;
                x2 += 100;
                z2 += 100;
            }
            String world = location.getWorld().getName();
            if(!LandManager.canCreateLandProvinceLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), world, "", "")) {
                if(!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                    player.sendMessage(Constant.PREFIX + "§7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                    event.getFurniture().remove(false);
                    ItemStack goverment = null;
                    for(CustomStack i : ItemsAdder.getAllItems()) {
                        if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                            goverment = i.getItemStack();
                        }
                    }
                    player.getInventory().addItem(goverment);
                }
                return;
            }
            if(world.equalsIgnoreCase("world")) {
                if(!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                    player.sendMessage(Constant.PREFIX + "§7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                    event.getFurniture().remove(false);
                    ItemStack goverment = null;
                    for(CustomStack i : ItemsAdder.getAllItems()) {
                        if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                            goverment = i.getItemStack();
                        }
                    }
                    player.getInventory().addItem(goverment);
                    return;
                }
            }
            Province province = new Province(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).getName(), CityBlock.townHallSetPlayers.get(player), x1, z1, x2, z2, world);
            HeroCraft.getPlugin().getProvinceManager().getProvinces().add(province);
            HeroCraft.getPlugin().getProvinceManager().saveProvince(province);
            event.getFurniture().getEntity().setCustomName("§7Stadt: §e§l" + province.getName());
            event.getFurniture().getEntity().setCustomNameVisible(true);
            player.sendMessage(Constant.PREFIX + "§7Stadt erfolgreich erstellt.");
            CityBlock.townHallSetPlayers.remove(player);
            return;
        }
        Province province = ProvinceManager.getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        if(province != null && province.canBuild(player)) {
            event.getFurniture().remove(false);
            ItemStack goverment = null;
            for(CustomStack i : ItemsAdder.getAllItems()) {
                if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                    goverment = i.getItemStack();
                }
            }
            player.getInventory().addItem(goverment);
            player.sendMessage(Constant.PREFIX + "§7Bitte setze das Rathaus in deine Stadt.");
            player.sendMessage(Constant.PREFIX + "§7Rathaus für die Stadt §a" + province.getName() + " §7deines Landes §e" + province.getLand() + " §7gesetzt.");
            event.getFurniture().getEntity().setCustomName("§7Stadt: §e§l" + province.getName());
            event.getFurniture().getEntity().setCustomNameVisible(true);
        } else {
            if(ProtectionListener.isInDangerZone(event.getFurniture().getEntity().getLocation())) {
                player.sendMessage(Constant.PREFIX + "§7Die Stadt ist zu nahe am §cSpawn§7.");
                event.getFurniture().remove(true);
                return;
            }
            // Stadt erstellen Prozess, mit Start vom Rathaus
            if(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player) == null) {
                event.getFurniture().remove(true);
                player.sendMessage(Constant.PREFIX + "§7Du benötigst zuerst ein Land, bevor du eine Stadt erstellen kannst!");
                player.sendMessage("");
                player.sendMessage(Constant.PREFIX + "§7Gehe zum Spawn, oder platziere irgendwo ein Regierungsgebäude!");
                return;
            }
            Location location = event.getFurniture().getEntity().getLocation();
            double x1 = location.getX() + 50;
            double z1 = location.getZ() + 50;
            double x2 = location.getX() - 50;
            double z2 = location.getZ() - 50;
            String worldName = location.getWorld().getName();
            if(worldName.equalsIgnoreCase("autumDimension") || worldName.equalsIgnoreCase("winterDimension") || worldName.equalsIgnoreCase("springDimension") || worldName.equalsIgnoreCase("summerDimension")) {
                x1 += 100;
                z1 += 100;
                x2 += 100;
                z2 += 100;
            }
            String world = location.getWorld().getName();
            if(!LandManager.canCreateLandProvinceLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), world, "", "")) {
                if(!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                    player.sendMessage(Constant.PREFIX + "§7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                    event.getFurniture().remove(false);
                    ItemStack goverment = null;
                    for(CustomStack i : ItemsAdder.getAllItems()) {
                        if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                            goverment = i.getItemStack();
                        }
                    }
                    player.getInventory().addItem(goverment);
                }
                return;
            }
            if(world.equalsIgnoreCase("world")) {
                if(!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                    player.sendMessage(Constant.PREFIX + "§7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                    event.getFurniture().remove(false);
                    ItemStack goverment = null;
                    for(CustomStack i : ItemsAdder.getAllItems()) {
                        if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                            goverment = i.getItemStack();
                        }
                    }
                    player.getInventory().addItem(goverment);
                    return;
                }
            }
            Province province1 = new Province(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).getName(), "", x1, z1, x2, z2, world);
            townhallProvinceCreationPlayersNameProvince.put(player, province1);
            townhallProvinceCreationPlayersNames.put(player, event.getFurniture());
            player.sendMessage(Constant.PREFIX + "§7Wie soll die Stadt heißen: (§0Schreibe in den Chat§7)");
            player.sendMessage("§cSneake zum abbrechen!");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(!townhallProvinceCreationPlayersNames.containsKey(player)) {
            return;
        }
        event.setCancelled(true);
        String name = event.getMessage();
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
        CustomFurniture customFurniture = townhallProvinceCreationPlayersNames.get(player);
        customFurniture.getEntity().setCustomName("§7Stadt: §e§l" + name);
        customFurniture.getEntity().setCustomNameVisible(true);

        townhallProvinceCreationPlayersNames.remove(player);
        Province province = townhallProvinceCreationPlayersNameProvince.get(player);
        province.setName(name);
        townhallProvinceCreationPlayers.put(player, province);
        townhallProvinceCreationPlayersNameProvince.remove(player);
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage(Constant.PREFIX + "§7Platziere nun den Stadtblock §ainnerhalb deines Landes§7.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).teleportTo(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        ItemStack goverment = null;
                        for(CustomStack i : ItemsAdder.getAllItems()) {
                            if(i.getDisplayName().equalsIgnoreCase("§4§lStadt")) {
                                goverment = i.getItemStack();
                            }
                        }
                        player.getInventory().addItem(goverment);
                    }
                }, 20*2);
            }
        }, 20);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(!townhallProvinceCreationPlayersNames.containsKey(player))
            return;
        player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen...");
        townhallProvinceCreationPlayersNames.get(player).remove(true);
        townhallProvinceCreationPlayersNames.remove(player);
        townhallProvinceCreationPlayersNameProvince.remove(player);
    }

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        if(!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRathaus"))
            return;
        Province province = ProvinceManager.getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        if(province == null)
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(province.getLand());
        if(!land.canBuild(player)) {
            event.setCancelled(true);
            return;
        }
        player.sendMessage(Constant.PREFIX + "§7Setze das Rathaus der Stadt §e" + province.getName() + "§7 nun an eine andere Stelle.");
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        if(!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRathaus"))
            return;
        Province province = ProvinceManager.getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        if(province == null)
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(province.getLand());
        player.sendTitle("§e§lReise gestartet", "§7...Zum Land §a" + land.getName());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                land.teleportTo(player);
            }
        }, 20*3);
    }

}
