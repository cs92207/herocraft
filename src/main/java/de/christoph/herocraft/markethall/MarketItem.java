package de.christoph.herocraft.markethall;

import de.anyblocks.api.utils.ItemBuilder;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class MarketItem {

    private Material material;
    private int amount;
    private double buyPrice;
    private double sellPrice;


    public MarketItem(Material material, int amount, double buyPrice, double sellPrice) {
        this.material = material;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public MarketItem(Material material, double buyPrice) {
        this.material = material;
        this.amount = 1;
        this.buyPrice = buyPrice;
        this.sellPrice = 0;
    }

    public MarketItem(Material material, int amount, double buyPrice) {
        this.material = material;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.sellPrice = 0;
    }

    public void sellItems(Player player, int sellAmount) {
        int foundAmount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                foundAmount += item.getAmount();
            }
        }

        if (foundAmount >= sellAmount) {
            int remainingToRemove = sellAmount;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == material) {
                    int itemAmount = item.getAmount();

                    if (itemAmount <= remainingToRemove) {
                        remainingToRemove -= itemAmount;
                        item.setAmount(0); // Stack leeren
                    } else {
                        item.setAmount(itemAmount - remainingToRemove);
                        remainingToRemove = 0;
                    }

                    if (remainingToRemove == 0) break;
                }
            }

            HeroCraft.getPlugin().coin.addMoney(player, sellPrice * sellAmount);
            player.sendMessage(Constant.PREFIX + "§7Du hast §a" + sellAmount + "x §7" + material.name() + " für §a" + (sellPrice * sellAmount) + " §7Coins verkauft!");
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        } else {
            player.sendMessage(Constant.PREFIX + "§7Du hast nicht genügend §c" + material.name() + " §7zum Verkaufen!");
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }

    public ItemStack getIcon() {
        if(sellPrice != 0)
            return new ItemBuilder(material).setAmount(amount).setLore("", "§7Kaufspreis: §e" + buyPrice, "§7Verkaufspreis: §e" + sellPrice, "", "§eLinksklick - §7Kaufen", "§eRechtsklick - §7Verkaufen").build();
        else
            return new ItemBuilder(material).setLore("", "§7Kaufspreis: §e" + buyPrice, "", "§eLinksklick - §7Kaufen").build();
    }

    public void buyItem(Player player) {
        if(HeroCraft.getPlugin().coin.getCoins(player) < buyPrice) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
            return;
        }
        HeroCraft.getPlugin().coin.removeMoney(player, buyPrice);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.getInventory().addItem(new ItemStack(material, amount));
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getAmount() {
        return amount;
    }

    public Material getMaterial() {
        return material;
    }

}
