package de.christoph.herocraft.markethall;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MonsterDropMarketShop implements CommandExecutor {

    private final MarketShop marketShop;

    public MonsterDropMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::monstershop:",
                true,
                new MarketItem(Material.BONE, 16, Constant.BONE_BUY_PRICE, Constant.BONE_SELL_PRICE),
                new MarketItem(Material.ROTTEN_FLESH, 16, Constant.ROTTEN_FLESH_BUY_PRICE, Constant.ROTTEN_FLESH_SELL_PRICE),
                new MarketItem(Material.STRING, 16, Constant.STRING_BUY_PRICE, Constant.STRING_SELL_PRICE),
                new MarketItem(Material.SPIDER_EYE, 8, Constant.SPIDER_EYE_BUY_PRICE, Constant.SPIDER_EYE_SELL_PRICE),
                new MarketItem(Material.ENDER_PEARL, 4, Constant.ENDER_PEARL_BUY_PRICE, Constant.ENDER_PEARL_SELL_PRICE),
                new MarketItem(Material.GUNPOWDER, 8, Constant.GUNPOWDER_BUY_PRICE, Constant.GUNPOWDER_SELL_PRICE),

                // Weitere Monster-Drops
                new MarketItem(Material.FEATHER, 16, Constant.FEATHER_BUY_PRICE, Constant.FEATHER_SELL_PRICE),  // von Rindern (und auch von Häuten durch Zombie-Varianten)
                new MarketItem(Material.MAGMA_CREAM, 4, Constant.MAGMA_CREAM_BUY_PRICE, Constant.MAGMA_CREAM_SELL_PRICE),  // von Magma Cubes
                new MarketItem(Material.GHAST_TEAR, 4, Constant.GHAST_TEAR_BUY_PRICE, Constant.GHAST_TEAR_SELL_PRICE),  // von Ghasts
                new MarketItem(Material.BLAZE_ROD, 4, Constant.BLAZE_ROD_BUY_PRICE, Constant.BLAZE_ROD_SELL_PRICE),  // von Blazes
                new MarketItem(Material.SLIME_BALL, 8, Constant.SLIME_BALL_BUY_PRICE, Constant.SLIME_BALL_SELL_PRICE),  // von Schleimen  // von Wither Skeletten
                new MarketItem(Material.PHANTOM_MEMBRANE, 4, Constant.PHANTOM_MEMBRANE_BUY_PRICE, Constant.PHANTOM_MEMBRANE_SELL_PRICE),  // von Phantomen
                new MarketItem(Material.RABBIT_FOOT, 4, Constant.RABBIT_FOOT_BUY_PRICE, Constant.RABBIT_FOOT_SELL_PRICE),  // von Hasen
                new MarketItem(Material.RABBIT_HIDE, 8, Constant.RABBIT_HIDE_BUY_PRICE, Constant.RABBIT_HIDE_SELL_PRICE),  // von Hasen
                new MarketItem(Material.BREEZE_ROD, 1, Constant.BREEZE_ROD_BUY_PRICE, Constant.BREEZE_ROD_SELL_PRICE),
                new MarketItem(Material.NETHER_STAR, 1, Constant.NETHER_STAR_BUY_PRICE, Constant.NETHER_STAR_SELL_PRICE)
        );
        HeroCraft.getPlugin().getCommand("monsterdrops").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this.marketShop, HeroCraft.getPlugin());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player player = (Player) sender;
        marketShop.openShopMenu(player);
        return false;
    }
}
