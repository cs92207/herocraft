package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FastLandCreationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player) != null) {
            player.sendMessage(Constant.PREFIX + "§7Du bist bereits in einem §cLand§7.");
            return false;
        }
        player.sendMessage(Constant.PREFIX + "§7Land wird erstellt...");
        Bukkit.getScheduler().runTask(HeroCraft.getPlugin(), () -> {
            Random random = new Random();
            while (true) {
                int x = random.nextInt(5000);
                int z = random.nextInt(5000);
                double x1 = x + 50;
                double z1 = z + 50;
                double x2 = x - 50;
                double z2 = z - 50;
                if(!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                    continue;
                }
                if(!LandManager.canCreateLandProvinceLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), "world", "", "")) {
                    continue;
                }
                String name = player.getName() + "Land";
                double y = Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x, 1, z));
                if(Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), x, y, z)).isLiquid()) {
                    continue;
                }
                Land land = new Land(
                        name,
                        player.getUniqueId().toString(),
                        player.getName(),
                        new String[]{""},
                        new String[]{""},
                        new String[]{""},
                        new String[]{""},
                        x1,
                        z1,
                        x2,
                        z2,
                        x,
                        y,
                        z,
                        0,
                        4500,
                        new String[]{""}
                );
                HeroCraft.getPlugin().getLandManager().getAllLands().add(land);
                HeroCraft.getPlugin().getLandManager().saveLand(land);
                player.sendMessage(Constant.PREFIX + "§7Sehr gut, du besitzt nun dein eigenes Land!");
                ItemStack goverment = null;
                for(CustomStack i : ItemsAdder.getAllItems()) {
                    if(i.getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
                        goverment = i.getItemStack();
                    }
                }
                player.sendMessage(Constant.PREFIX + "§7Platziere nun das Regierungsgebäude auf deinem Land. §4Wenn du es verlierst, musst du dir mit /regierungsshop ein neues kaufen.");
                player.getInventory().addItem(goverment);
                Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
                    }
                }, 20);
                break;
            }
        });
        return false;
    }

}
