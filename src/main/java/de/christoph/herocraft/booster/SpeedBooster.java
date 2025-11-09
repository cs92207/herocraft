package de.christoph.herocraft.booster;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBooster extends Booster {


    public SpeedBooster() {
        super("Speed-Booster", "speed_booster", Material.GOLDEN_BOOTS);
    }

    @Override
    public void onBoosterActivate() {
        for(Player all : getBoosterPlayers()) {
            all.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900*20, 2));
        }
    }

    @Override
    public void onBoosterMultiplyEnded() {
        for(Player all : getBoosterPlayers()) {
            all.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900*20, 2));
        }
    }

    @Override
    public void onBoosterMultiply() {

    }

    @Override
    public void onBoosterDeactivated() {

    }

    @Override
    public void onPlayerEnterBoosterWorld(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900*20, 2));
    }

    @Override
    public void onPlayerLeaveBoosterWorld(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
    }

}
