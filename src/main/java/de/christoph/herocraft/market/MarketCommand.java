package de.christoph.herocraft.market;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class MarketCommand implements Listener {

    public static HashMap<Player, ItemStack> sellingPlayers = new HashMap<>();
    public static HashMap<Player, Double> sellingPlayersPrice = new HashMap<>();

    public static void buyItem(ItemStack itemStack, double price, Player player) {
        if(HeroCraft.getPlugin().coin.getCoins(player) >= price) {
            HeroCraft.getPlugin().coin.removeMoney(player, price);
            itemStack.getItemMeta().setLore(new ArrayList<>());
            ItemStack itemStack1 = new ItemStack(itemStack.getType(), itemStack.getAmount());
            player.getInventory().addItem(itemStack1);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3, 3);
        } else
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 3, 3);
    }

    public static void sellItem(ItemStack itemStack, double price, Player player) {
        player.closeInventory();
        player.sendMessage(Constant.PREFIX + "§7Wieviele Stück von diesem Item möchtest du verkaufen? (§e1 = " + price + " Coins§7)");
        player.sendMessage("§4Sneaken zum abbrechen!");
        sellingPlayers.put(player, itemStack);
        sellingPlayersPrice.put(player, price);
    }

    @EventHandler
    public void onSellingPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!sellingPlayers.containsKey(event.getPlayer()))
            return;
        event.setCancelled(true);
        int amount;
        try {
            amount = Integer.parseInt(event.getMessage());
        } catch (NumberFormatException e) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen! Du hast nicht genügend von diesem Item in deinem Inventar.");
            sellingPlayers.remove(player);
            sellingPlayersPrice.remove(player);
            return;
        }
        if(!hatMaterial(player, sellingPlayers.get(player).getType(), amount)) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen! Du hast nicht genügend von diesem Item in deinem Inventar.");
            sellingPlayers.remove(player);
            sellingPlayersPrice.remove(player);
            return;
        }
        double getCoinsAmount = amount * sellingPlayersPrice.get(player);
        entferneMaterial(player, sellingPlayers.get(player).getType(), amount);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.sendMessage(Constant.PREFIX + "§7Items verkauft");
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                HeroCraft.getPlugin().coin.addMoney(player, getCoinsAmount);
            }
        }, 10);
    }

    @EventHandler
    public void onSellingPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(!sellingPlayers.containsKey(player))
            return;
        player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen.");
        sellingPlayersPrice.remove(player);
        sellingPlayers.remove(player);
    }

    private static boolean hatMaterial(Player spieler, Material material, int anzahl) {
        ItemStack[] inventar = spieler.getInventory().getContents();
        int gesamtAnzahl = 0;
        for (ItemStack stapel : inventar) {
            if (stapel != null && stapel.getType() == material) {
                gesamtAnzahl += stapel.getAmount();
            }
        }
        return gesamtAnzahl >= anzahl;
    }

    public boolean entferneMaterial(Player spieler, Material material, int anzahl) {
        ItemStack[] inventar = spieler.getInventory().getContents();
        int zuEntfernendeAnzahl = anzahl;

        // Durchlaufe das Inventar und entferne die gewünschte Anzahl des Materials
        for (ItemStack stapel : inventar) {
            if (stapel != null && stapel.getType() == material) {
                int stapelAnzahl = stapel.getAmount();

                if (stapelAnzahl <= zuEntfernendeAnzahl) {
                    // Der Stapel enthält weniger oder gleich der zu entfernenden Anzahl
                    zuEntfernendeAnzahl -= stapelAnzahl;
                    spieler.getInventory().remove(stapel);
                } else {
                    // Der Stapel enthält mehr als die zu entfernende Anzahl
                    stapel.setAmount(stapelAnzahl - zuEntfernendeAnzahl);
                    zuEntfernendeAnzahl = 0;
                }

                if (zuEntfernendeAnzahl == 0) {
                    // Alle zu entfernenden Gegenstände wurden entfernt
                    break;
                }
            }
        }

        // Überprüfe, ob alle zu entfernenden Gegenstände entfernt wurden
        return zuEntfernendeAnzahl == 0;
    }

}
