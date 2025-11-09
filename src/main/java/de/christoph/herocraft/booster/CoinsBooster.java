package de.christoph.herocraft.booster;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CoinsBooster extends Booster {

    public CoinsBooster() {
        super("Coins-Booster", "coins_booster", Material.GOLD_NUGGET);
    }

    @Override
    public void onBoosterActivate() {
        for(Player all : getBoosterPlayers()) {
            all.sendMessage(Constant.PREFIX + "§a+ 1000 Coins");
            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            HeroCraft.getPlugin().coin.addMoney(all, 1000);
        }
    }

    @Override
    public void onBoosterMultiply() {

    }

    @Override
    public void onBoosterMultiplyEnded() {
        for(Player all : getBoosterPlayers()) {
            all.sendMessage(Constant.PREFIX + "§a+ 1000 Coins");
            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            HeroCraft.getPlugin().coin.addMoney(all, 1000);
        }
    }

    @Override
    public void onBoosterDeactivated() {

    }

    @Override
    public void onPlayerEnterBoosterWorld(Player player) {

    }

    @Override
    public void onPlayerLeaveBoosterWorld(Player player) {

    }

}
