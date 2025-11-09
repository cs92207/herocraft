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

public class FloristMarketShop implements CommandExecutor {

    private final MarketShop marketShop;

    public FloristMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::offer_detail:",
                true,
                new MarketItem(Material.DANDELION, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.POPPY, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.RED_TULIP, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.ORANGE_TULIP, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.WHITE_TULIP, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.PINK_TULIP, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.CORNFLOWER, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.PINK_PETALS, 16, Constant.DANDELION_BUY, Constant.DANDELION_SELL),
                new MarketItem(Material.BLUE_ORCHID, 16, Constant.ORCHID_BUY, Constant.ORCHID_SELL),
                new MarketItem(Material.AZURE_BLUET, 16, Constant.ORCHID_BUY, Constant.ORCHID_SELL),
                new MarketItem(Material.LILY_OF_THE_VALLEY, 16, Constant.ORCHID_BUY, Constant.ORCHID_SELL),
                new MarketItem(Material.SUNFLOWER, 16, Constant.ORCHID_BUY, Constant.ORCHID_SELL),
                new MarketItem(Material.ROSE_BUSH, 16, Constant.ORCHID_BUY, Constant.ORCHID_SELL),
                new MarketItem(Material.PEONY, 16, Constant.ORCHID_BUY, Constant.ORCHID_SELL),
                new MarketItem(Material.TORCHFLOWER, 1, Constant.TORCHFLOWER_BUY, Constant.TORCHFLOWER_SELL),
                new MarketItem(Material.PITCHER_PLANT, 1, Constant.TORCHFLOWER_BUY, Constant.TORCHFLOWER_SELL),
                new MarketItem(Material.SPORE_BLOSSOM, 1, Constant.TORCHFLOWER_BUY, Constant.TORCHFLOWER_SELL),
                new MarketItem(Material.BROWN_MUSHROOM, 16, Constant.MUSHROOM_BUY, Constant.MUSHROOM_SELL),
                new MarketItem(Material.RED_MUSHROOM, 16, Constant.MUSHROOM_BUY, Constant.MUSHROOM_SELL),
                new MarketItem(Material.CRIMSON_FUNGUS, 16, Constant.CRIMSON_FUNGUS_BUY, Constant.CRIMSON_FUNGUS_SELL),
                new MarketItem(Material.WARPED_FUNGUS, 16, Constant.CRIMSON_FUNGUS_BUY, Constant.CRIMSON_FUNGUS_SELL),
                new MarketItem(Material.GLOW_BERRIES, 16, Constant.GLOWBERRIES_BUY, Constant.GLOWBERRIES_SELL),
                new MarketItem(Material.FLOWER_POT, 16, Constant.FLOWER_POD_BUY, Constant.FLOWER_POD_SELL),
                new MarketItem(Material.DECORATED_POT, 1, Constant.DECORATION_POT_BUY, Constant.DECORATION_POT_SELL)
        );
        HeroCraft.getPlugin().getCommand("floristmarket").setExecutor(this);
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
