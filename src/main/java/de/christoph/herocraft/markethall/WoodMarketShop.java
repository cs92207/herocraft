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

public class WoodMarketShop implements CommandExecutor {

    private final MarketShop marketShop;

    public WoodMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::holzfaeller:",
                true,
                new MarketItem(Material.OAK_LOG, 32, Constant.OAK_LOG_BUY_PRICE, Constant.OAK_LOG_SELL_PRICE),
                new MarketItem(Material.BIRCH_LOG, 32, Constant.OAK_LOG_BUY_PRICE, Constant.OAK_LOG_SELL_PRICE),
                new MarketItem(Material.SPRUCE_LOG, 32, Constant.OAK_LOG_BUY_PRICE, Constant.OAK_LOG_SELL_PRICE),
                new MarketItem(Material.DARK_OAK_LOG, 32, Constant.OAK_LOG_BUY_PRICE, Constant.OAK_LOG_SELL_PRICE),

                // Neue Holzarten (1.17+)
                new MarketItem(Material.AZALEA, 32, Constant.AZALEA_LOG_BUY_PRICE, Constant.AZALEA_LOG_SELL_PRICE),  // Azaleenholz (Blüte)
                new MarketItem(Material.MANGROVE_LOG, 32, Constant.MANGROVE_LOG_BUY_PRICE, Constant.MANGROVE_LOG_SELL_PRICE),  // Mangrovenholz
                new MarketItem(Material.CRIMSON_STEM, 32, Constant.AZALEA_LOG_BUY_PRICE, Constant.AZALEA_LOG_SELL_PRICE),  // Crimson-Stamm (Nether)
                new MarketItem(Material.WARPED_STEM, 32, Constant.AZALEA_LOG_BUY_PRICE, Constant.AZALEA_LOG_SELL_PRICE),  // Warped-Stamm (Nether)
                new MarketItem(Material.CHERRY_LOG, 32, Constant.MANGROVE_LOG_BUY_PRICE, Constant.MANGROVE_LOG_SELL_PRICE)  // Warped-Stamm (Nether)
        );

        HeroCraft.getPlugin().getCommand("woodmarket").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this.marketShop, HeroCraft.getPlugin());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        marketShop.openShopMenu(player);
        return false;
    }
}
