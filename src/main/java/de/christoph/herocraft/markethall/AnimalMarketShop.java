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

public class AnimalMarketShop implements CommandExecutor {

    private final MarketShop marketShop;

    public AnimalMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::animaldrops:",
                true,
                new MarketItem(Material.LEATHER, 16, Constant.LEATHER_BUY_PRICE, Constant.LEATHER_SELL_PRICE),
                new MarketItem(Material.FEATHER, 16, Constant.FEATHER_BUY_PRICE, Constant.FEATHER_SELL_PRICE),
                new MarketItem(Material.WHITE_WOOL, 16, Constant.WOOL_BUY_PRICE, Constant.WOOL_SELL_PRICE),
                new MarketItem(Material.MILK_BUCKET, 1, Constant.MILK_BUCKET_BUY_PRICE, Constant.MILK_BUCKET_SELL_PRICE),
                new MarketItem(Material.EGG, 4, Constant.EGG_BUY_PRICE, Constant.EGG_SELL_PRICE),
                new MarketItem(Material.HONEYCOMB, 8, Constant.HONEYCOMB_BUY_PRICE, Constant.HONEYCOMB_SELL_PRICE),
                new MarketItem(Material.HONEY_BOTTLE, 2, Constant.HONEY_BOTTLE_BUY_PRICE, Constant.HONEY_BOTTLE_SELL_PRICE),
                new MarketItem(Material.INK_SAC, 8, Constant.INK_SAC_BUY_PRICE, Constant.INK_SAC_SELL_PRICE),
                new MarketItem(Material.NAUTILUS_SHELL, 1, Constant.NAUTILUS_BUY, Constant.NAUTILUS_SELL),
                new MarketItem(Material.ARMADILLO_SCUTE, 1, Constant.AMADILO_SCUTE_BUY, Constant.AMADILO_SCUTE_SELL),
                new MarketItem(Material.TURTLE_SCUTE, 1, Constant.TURTLE_SUCTE_BUY, Constant.TURTLE_SUCTE_SELL)
        );
        HeroCraft.getPlugin().getCommand("animaldrops").setExecutor(this);
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
