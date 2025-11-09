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

public class ButcherMarketShop implements CommandExecutor {

    private final MarketShop marketShop;

    public ButcherMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::schlachter:",
                true,
                new MarketItem(Material.COOKED_BEEF, 16, Constant.COOKED_BEEF_PRICE, Constant.COOKED_BEEF_SELL_PRICE),
                new MarketItem(Material.COOKED_PORKCHOP, 16, Constant.COOKED_PORKCHOP_PRICE, Constant.COOKED_PORKCHOP_SELL_PRICE),
                new MarketItem(Material.COOKED_CHICKEN, 16, Constant.COOKED_CHICKEN_PRICE, Constant.COOKED_CHICKEN_SELL_PRICE),
                new MarketItem(Material.COOKED_MUTTON, 16, Constant.COOKED_MUTTON_PRICE, Constant.COOKED_MUTTON_SELL_PRICE),
                new MarketItem(Material.COOKED_RABBIT, 16, Constant.COOKED_RABBIT_PRICE, Constant.COOKED_RABBIT_SELL_PRICE),
                new MarketItem(Material.BEEF, 16, Constant.RAW_MEET_BUY_PRICE, Constant.RAW_MEET_SELL_PRICE),
                new MarketItem(Material.PORKCHOP, 16, Constant.RAW_MEET_BUY_PRICE, Constant.RAW_MEET_SELL_PRICE),
                new MarketItem(Material.CHICKEN, 16, Constant.RAW_MEET_BUY_PRICE, Constant.RAW_MEET_SELL_PRICE),
                new MarketItem(Material.MUTTON, 16, Constant.RAW_MEET_BUY_PRICE, Constant.RAW_MEET_SELL_PRICE),
                new MarketItem(Material.RABBIT, 16, Constant.RAW_MEET_BUY_PRICE, Constant.RAW_MEET_SELL_PRICE)
        );
        HeroCraft.getPlugin().getCommand("butchermarket").setExecutor(this);
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
