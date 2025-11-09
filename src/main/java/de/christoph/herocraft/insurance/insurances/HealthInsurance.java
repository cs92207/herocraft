package de.christoph.herocraft.insurance.insurances;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.insurance.Insurance;
import de.christoph.herocraft.insurance.PlayerInsurance;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;

public class HealthInsurance extends Insurance implements Listener {

    private ArrayList<Player> waitingPlayers;

    public HealthInsurance() {
        super("Krankenversicherung", "Du bekommst Medizin, wenn du wenig Herzen hast.", 75, Material.ENCHANTED_GOLDEN_APPLE);
        waitingPlayers = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(HeroCraft.getPlugin().getInsuranceManager().getPlayerInsuranceByPlayerAndName(player, getName()) == null)
            return;
        double newHealth = player.getHealth() - event.getDamage();
        if(newHealth < 4) {
            if(waitingPlayers.contains(player)) {
                player.sendMessage(Constant.PREFIX + "§7Du bekommst keine Medizin, da sie §a30 Sekunden Cooldown §7hat.");
                return;
            }
            player.setHealth(player.getHealth() + 14);
            player.playSound(player.getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Du hast Medizin bekommen, da du eine §a" + getName() + " §7hast.");
            waitingPlayers.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    waitingPlayers.remove(player);
                }
            }, 20*30);
        }
    }

}
