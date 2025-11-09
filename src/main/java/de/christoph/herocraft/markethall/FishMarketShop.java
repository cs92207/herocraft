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

import java.util.ArrayList;

public class FishMarketShop implements CommandExecutor {

    private MarketShop marketShop;

    public FishMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::fisher:",
                true,
                new MarketItem(Material.COOKED_COD, 16, Constant.COOKED_COD_PRICE, Constant.COOKED_SELL_COD_PRICE),
                new MarketItem(Material.COOKED_SALMON, 16, Constant.COOKED_SALMON_PRICE, Constant.COOKED_SELL_SALMON_PRICE),
                new MarketItem(Material.TROPICAL_FISH, 16, Constant.TROPICAL_PRICE, Constant.TROPICAL_SELL_PRICE),
                new MarketItem(Material.PUFFERFISH, 16, Constant.PUFFER_BUY_PRICE, Constant.PUFFER_SELL_PRICE),
                new MarketItem(Material.COD, 16, Constant.RAW_MEET_BUY_PRICE, Constant.RAW_MEET_SELL_PRICE),
                new MarketItem(Material.SALMON, 16, Constant.RAW_MEET_BUY_PRICE, Constant.RAW_MEET_SELL_PRICE),
                new MarketItem(Material.KELP, 16, Constant.RAW_KELP_BUY_PRICE, Constant.RAW_KELP_SELL_PRICE),
                new MarketItem(Material.SEA_PICKLE, 16, Constant.SEA_PICKLE_BUY_PRICE, Constant.SEA_PICKLE_SELL_PRICE),
                new MarketItem(Material.TUBE_CORAL_BLOCK, 8, Constant.CORAL_BUY_PRICE, Constant.CORAL_SELL_PRICE),
                new MarketItem(Material.BRAIN_CORAL_BLOCK, 8, Constant.CORAL_BUY_PRICE, Constant.CORAL_SELL_PRICE),
                new MarketItem(Material.BUBBLE_CORAL_BLOCK, 8, Constant.CORAL_BUY_PRICE, Constant.CORAL_SELL_PRICE),
                new MarketItem(Material.FIRE_CORAL_BLOCK, 8, Constant.CORAL_BUY_PRICE, Constant.CORAL_SELL_PRICE),
                new MarketItem(Material.HORN_CORAL_BLOCK, 8, Constant.CORAL_BUY_PRICE, Constant.CORAL_SELL_PRICE),
                new MarketItem(Material.SEA_LANTERN, 8, Constant.SEE_LANTERN_BUY_PRICE, Constant.SEE_LANTERN_SELL_PRICE),
                new MarketItem(Material.PRISMARINE_SHARD, 8, Constant.PRISMARIN_SHARP_BUY_PRICE, Constant.PRISMARIN_SHARP_SELL_PRICE),
                new MarketItem(Material.AXOLOTL_BUCKET, 1, Constant.AXOLOTL_BUCKET_BUY_PRICE, Constant.AXOLOTL_BUCKET_SELL_PRICE)
        );
        HeroCraft.getPlugin().getCommand("fishmarket").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this.marketShop, HeroCraft.getPlugin());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        marketShop.openShopMenu(player);
        return false;
    }

}
