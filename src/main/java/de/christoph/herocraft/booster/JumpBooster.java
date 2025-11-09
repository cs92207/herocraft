package de.christoph.herocraft.booster;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JumpBooster extends Booster {

    public JumpBooster() {
        super("Jump-Booster", "jump_booster", Material.FEATHER);
    }

    @Override
    public void onBoosterActivate() {
        for(Player all : getBoosterPlayers()) {
            all.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 900*20, 2));
        }
    }

    @Override
    public void onBoosterMultiplyEnded() {
        for(Player all : getBoosterPlayers()) {
            all.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 900*20, 2));
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 900*20, 2));
    }

    @Override
    public void onPlayerLeaveBoosterWorld(Player player) {
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
    }

}
