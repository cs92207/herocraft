package de.christoph.herocraft.insurance.insurances;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.insurance.Insurance;
import de.christoph.herocraft.utils.Constant;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;

public class XPInsurance extends Insurance implements Listener {

    private HashMap<Player, Float> playerXP;
    private HashMap<Player, Integer> playerLevel;

    public XPInsurance() {
        super("XP Versicherung", "Wenn du stirbst, behälst du deine XP", 15, Material.EXPERIENCE_BOTTLE);
        playerXP = new HashMap<>();
        playerLevel = new HashMap<>();
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(HeroCraft.getPlugin().getInsuranceManager().getPlayerInsuranceByPlayerAndName(player, getName()) == null)
            return;
        event.setDroppedExp(0);
        event.setKeepLevel(true);
        playerXP.put(player, player.getExp());
        playerLevel.put(player, player.getLevel());
        player.sendMessage(Constant.PREFIX + "§7Da du eine §a" + getName() + " §7hast, behälst du deine XP.");
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(!playerXP.containsKey(player))
            return;
        player.setExp(playerXP.get(player));
        player.setLevel(playerLevel.get(player));
        playerXP.remove(player);
        playerLevel.remove(player);
    }

}
