package de.christoph.herocraft.booster;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BreakBooster extends Booster {

    public BreakBooster() {
        super("Break-Booster", "break_booster", Material.GOLDEN_PICKAXE);
    }

    @Override
    public void onBoosterActivate() {
        for(Player all : getBoosterPlayers()) {
            all.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 900*20, 3));
        }
    }

    @Override
    public void onBoosterMultiply() {

    }

    @Override
    public void onBoosterMultiplyEnded() {
        for(Player all : getBoosterPlayers()) {
            all.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 900*20, 3));
        }
    }

    @Override
    public void onBoosterDeactivated() {

    }

    @Override
    public void onPlayerEnterBoosterWorld(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 900*20, 3));
    }

    @Override
    public void onPlayerLeaveBoosterWorld(Player player) {
        player.removePotionEffect(PotionEffectType.HASTE);
    }

}
