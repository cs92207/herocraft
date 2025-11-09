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

public class ArmorerMarketShop implements CommandExecutor {

    private final MarketShop marketShop;

    public ArmorerMarketShop() {
        this.marketShop = new MarketShop(
                ":offset_-16::ruestungsschmied:",
                false,
                new MarketItem(Material.IRON_HELMET, 1, Constant.IRON_HELMET_BUY_PRICE),
                new MarketItem(Material.IRON_CHESTPLATE, 1, Constant.IRON_CHESTPLATE_BUY_PRICE),
                new MarketItem(Material.IRON_LEGGINGS, 1, Constant.IRON_LEGGINGS_BUY_PRICE),
                new MarketItem(Material.IRON_BOOTS, 1, Constant.IRON_BOOTS_BUY_PRICE),
                new MarketItem(Material.DIAMOND_CHESTPLATE, 1, Constant.DIAMOND_CHESTPLATE_BUY_PRICE),
                new MarketItem(Material.NETHERITE_CHESTPLATE, 1, Constant.NETHERITE_CHESTPLATE_BUY_PRICE)
        );
        HeroCraft.getPlugin().getCommand("armorermarket").setExecutor(this);
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
