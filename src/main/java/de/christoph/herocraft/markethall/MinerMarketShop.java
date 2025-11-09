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

public class MinerMarketShop implements CommandExecutor {

    private final MarketShop marketShop;

    public MinerMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::bergarbeiter:",
                true,
                new MarketItem(Material.COAL, 16, Constant.COAL_BUY_PRICE, Constant.COAL_SELL_PRICE),
                new MarketItem(Material.IRON_INGOT, 16, Constant.IRON_BUY_PRICE, Constant.IRON_SELL_PRICE),
                new MarketItem(Material.GOLD_INGOT, 16, Constant.GOLD_BUY_PRICE, Constant.GOLD_SELL_PRICE),
                new MarketItem(Material.DIAMOND, 8, Constant.DIAMOND_BUY_PRICE, Constant.DIAMOND_SELL_PRICE),
                new MarketItem(Material.EMERALD, 8, Constant.EMERALD_BUY_PRICE, Constant.EMERALD_SELL_PRICE),
                new MarketItem(Material.NETHERITE_INGOT, 4, Constant.NETHERITE_BUY_PRICE, Constant.NETHERITE_SELL_PRICE),

                // Gesteine
                new MarketItem(Material.COBBLESTONE, 32, Constant.COBBLESTONE_BUY_PRICE, Constant.COBBLESTONE_SELL_PRICE),
                new MarketItem(Material.STONE, 32, Constant.STONE_BUY_PRICE, Constant.STONE_SELL_PRICE),
                new MarketItem(Material.DEEPSLATE, 32, Constant.DEEPSLATE_BUY_PRICE, Constant.DEEPSLATE_SELL_PRICE),
                new MarketItem(Material.TUFF, 32, Constant.TUFF_BUY_PRICE, Constant.TUFF_SELL_PRICE),
                new MarketItem(Material.ANDESITE, 32, Constant.ANDESITE_BUY_PRICE, Constant.ANDESITE_SELL_PRICE),
                new MarketItem(Material.DIORITE, 32, Constant.DIORITE_BUY_PRICE, Constant.DIORITE_SELL_PRICE),
                new MarketItem(Material.GRANITE, 32, Constant.GRANITE_BUY_PRICE, Constant.GRANITE_SELL_PRICE),
                new MarketItem(Material.DRIPSTONE_BLOCK, 32, Constant.DRIPSTONE_BUY_PRICE, Constant.DRIPSTONE_SELL_PRICE),
                new MarketItem(Material.CALCITE, 32, Constant.CALCITE_BUY_PRICE, Constant.CALCITE_SELL_PRICE),
                new MarketItem(Material.BASALT, 32, Constant.BASALT_BUY_PRICE, Constant.BASALT_SELL_PRICE),
                new MarketItem(Material.COPPER_INGOT, 16, Constant.COPPER_BUY_PRICE, Constant.COPPER_SELL_PRICE),
                new MarketItem(Material.QUARTZ, 32, Constant.QUARTZ_PRICE, Constant.QUARTZ_SELL_PRICE),
                new MarketItem(Material.AMETHYST_CLUSTER, 16, Constant.AMETHYST_BUY_PRICE, Constant.AMETHYST_SELL_PRICE),
                new MarketItem(Material.LARGE_AMETHYST_BUD, 16, Constant.AMETHYST_BUY_PRICE, Constant.AMETHYST_SELL_PRICE),
                new MarketItem(Material.MEDIUM_AMETHYST_BUD, 16, Constant.AMETHYST_BUY_PRICE, Constant.AMETHYST_SELL_PRICE),
                new MarketItem(Material.SMALL_AMETHYST_BUD, 16, Constant.AMETHYST_BUY_PRICE, Constant.AMETHYST_SELL_PRICE),
                new MarketItem(Material.BLACKSTONE, 32, Constant.BLACKSTONE_BUY_PRICE, Constant.BLACKSTONE_SELL_PRICE),
                new MarketItem(Material.NETHER_BRICK, 16, Constant.NETHER_BRICK_BUY_PRICE, Constant.NETHER_BRICK_SELL_PRICE),
                new MarketItem(Material.BRICK, 16, Constant.BRICK_BUY, Constant.BRICK_SELL),
                new MarketItem(Material.TERRACOTTA, 32, Constant.TERACOTTA_BUY, Constant.TERACOTTA_SELL),
                new MarketItem(Material.OBSIDIAN, 32, Constant.OBSIDIAN_BUY, Constant.OBSIDIAN_SELL),
                new MarketItem(Material.CRYING_OBSIDIAN, 16, Constant.CRYING_OBSIDIAN_BUY, Constant.CRYING_OBSIDIAN_SELL)
        );
        HeroCraft.getPlugin().getCommand("minermarket").setExecutor(this);
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
