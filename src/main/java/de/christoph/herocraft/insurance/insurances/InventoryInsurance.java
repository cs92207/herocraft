package de.christoph.herocraft.insurance.insurances;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.insurance.Insurance;
import de.christoph.herocraft.insurance.PlayerInsurance;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryInsurance extends Insurance implements Listener {

    private HashMap<Player, ItemStack[]> playerContents;
    private HashMap<Player, ItemStack[]> playerArmor;

    public InventoryInsurance() {
        super("Inventar Versicherung",
                "Wenn du stirbst, bekommst du deine Items wieder.",
                45,
                Material.CHEST
            );
        this.playerContents = new HashMap<>();
        this.playerArmor = new HashMap<>();
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(HeroCraft.getPlugin().getInsuranceManager().getPlayerInsuranceByPlayerAndName(player, getName()) == null) {
            if(!HeroCraft.getPlugin().getConfig().contains("InventoryInsurance." + player.getUniqueId().toString())) {
                player.sendMessage(Constant.PREFIX + "§eTipp: §7Kaufe dir eine Inventar Versicherung, damit du deine Items beim nächsten Tod behälst (§e/versicherungen§7)");
            }
            return;
        }
        if(HeroCraft.getPlugin().raidManager.isPlayerInRaid(player)) {
            return;
        }
        ItemStack[] inventoryContents = player.getInventory().getContents();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        player.sendMessage(Constant.PREFIX + "§7Da du eine Inventar Versicherung hast, wird dein Inventar wieder hergestellt...");
        playerContents.put(player, inventoryContents);
        playerArmor.put(player, armorContents);
        event.getDrops().clear();
        event.setKeepInventory(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(!playerContents.containsKey(player))
            return;
        /*Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                player.getInventory().clear();
                player.getInventory().setContents(playerContents.get(player));
                player.getInventory().setArmorContents(playerArmor.get(player));
                playerContents.remove(player);
                playerArmor.remove(player);
            }
        }, 20);*/
    }

}
