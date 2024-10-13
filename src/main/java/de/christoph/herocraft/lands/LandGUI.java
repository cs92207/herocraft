package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class LandGUI implements CommandExecutor, Listener {

    public static ArrayList<Player> visitPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        player.sendMessage(Constant.TUTORIAL_PREFIX + "§7Kennst du dich bereits mit unserem Land System aus?");
        TextComponent textComponent = new TextComponent("§a§l(Nein, erklären)");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/starttutorial"));
        TextComponent textComponent1 = new TextComponent("§c§l(Nein nicht erklären)");
        textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tutorialnein"));
        player.spigot().sendMessage(textComponent);
        player.spigot().sendMessage(textComponent1);
        HeroCraft.getPlugin().getConfig().set("Tutorial." + player.getUniqueId().toString(), true);
        HeroCraft.getPlugin().saveConfig();
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::land_menu:");
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lZum Land teleportieren").build());
        inventory.setItem(13, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand besuchen").build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand verlassen").build());
            inventory.setItem(28, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lNach Land Scannen").build());
            inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lNach Land Scannen").build());
            inventory.setItem(30, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lNach Land Scannen").build());
            inventory.setItem(32, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand erstellen").build());
            inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand erstellen").build());
            inventory.setItem(34, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand erstellen").build());

        player.openInventory(inventory);
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::land_menu:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lZum Land teleportieren")) {
            Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            if(land == null) {
                player.sendMessage(Constant.PREFIX + "§7Du bist kein Teil eines Landes.");
                return;
            }
            land.teleportTo(player);
            player.sendMessage(Constant.PREFIX + "§7Du wurdest zum Land §a" + land.getName() + "§7 teleportiert.");
        } else if(displayName.equalsIgnoreCase("§4§lLand besuchen")) {
            visitPlayers.add(player);
            player.sendMessage(Constant.PREFIX + "§7Gebe den Namen des Landes ein, welches du besuchen willst.");
            player.sendMessage("§4Sneaken zum abbrechen!");
            player.closeInventory();
        } else if(displayName.equalsIgnoreCase("§4§lNach Land Scannen")) {
            player.closeInventory();
            HeroCraft.getPlugin().getLandManager().scanForLand(player);
        } else if(displayName.equalsIgnoreCase("§4§lLand erstellen")) {
            player.closeInventory();
            player.performCommand("createland");
        } else if(displayName.equalsIgnoreCase("§4§lLand verlassen")) {
            player.closeInventory();
            Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            if(land == null) {
                player.sendMessage(Constant.PREFIX + "§7Du bist in keinem Land.");
                return;
            }
            if(land.isOwner(player.getName())) {
                player.sendMessage(Constant.PREFIX + "§7Du kannst dein eigenes Land nicht verlassen. Lösche es.");
                return;
            }
            land.removeMember(player.getName());
            player.sendMessage(Constant.PREFIX + "§7Land verlassen.");
        }
    }


    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(visitPlayers.contains(player)) {
            visitPlayers.remove(player);
            player.sendMessage("§4Vorgang abgebrochen!");
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!visitPlayers.contains(player))
            return;
        event.setCancelled(true);
        Land land = HeroCraft.getPlugin().getLandManager().getLandByName(event.getMessage());
        if(land == null) {
            player.sendMessage(Constant.PREFIX + "§7Dieses Land existiert §cnicht§7. Versuche es erneut.");
            return;
        }
        visitPlayers.remove(player);
        player.sendMessage(Constant.PREFIX + "§7Du besuchst das Land §a" + land.getName() + "§7 teleportiert.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                land.teleportTo(player);
            }
        }, 15);
    }

}
