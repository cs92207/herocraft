package de.christoph.herocraft.lands.province;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandCreator;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import jdk.jfr.Enabled;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class TownHall implements Listener {

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
            double z2 = location.getZ() -50;
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
        if(province == null || !province.canBuild(player)) {
            event.getFurniture().remove(false);
            ItemStack goverment = null;
            for(CustomStack i : ItemsAdder.getAllItems()) {
                if(i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                    goverment = i.getItemStack();
                }
            }
            player.getInventory().addItem(goverment);
            player.sendMessage(Constant.PREFIX + "§7Bitte setze das Rathaus in deine Stadt.");
            return;
        }
        player.sendMessage(Constant.PREFIX + "§7Rathaus für die Stadt §a" + province.getName() + " §7deines Landes §e" + province.getLand() + " §7gesetzt.");
        event.getFurniture().getEntity().setCustomName("§7Stadt: §e§l" + province.getName());
        event.getFurniture().getEntity().setCustomNameVisible(true);
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
