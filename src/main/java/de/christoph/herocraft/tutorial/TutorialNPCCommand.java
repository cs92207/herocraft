package de.christoph.herocraft.tutorial;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TutorialNPCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if(!HeroCraft.getPlugin().getConfig().contains("Tutorial." + player.getUniqueId().toString())) {
            player.sendMessage(Constant.TUTORIAL_PREFIX + "§7Hallo, willkommen auf Survival Lands! Kennst du dich schon aus, oder soll ich dir HeroCraft etwas erklären? Klicke mich jederzeit an für Hilfe!");
            TextComponent textComponent = new TextComponent("§a§l(Ja erklären)");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/starttutorial"));
            TextComponent textComponent1 = new TextComponent("§c§l(Nein nicht erklären)");
            textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tutorialnein"));
            player.spigot().sendMessage(textComponent);
            player.spigot().sendMessage(textComponent1);
            HeroCraft.getPlugin().getConfig().set("Tutorial." + player.getUniqueId().toString(), true);
            HeroCraft.getPlugin().saveConfig();

        } else {
            openSpidermanGUI(player);
        }
        return false;
    }

    public static void openSpidermanGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::tutorial_inv:");
        inventory.setItem(9, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand Tutorial").build());
        inventory.setItem(10, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand Tutorial").build());
        inventory.setItem(11, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lLand Tutorial").build());
        inventory.setItem(15, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMarkt Tutorial").build());
        inventory.setItem(16, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMarkt Tutorial").build());
        inventory.setItem(17, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lMarkt Tutorial").build());
        inventory.setItem(27, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCustom Items Tutorial").build());
        inventory.setItem(28, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCustom Items Tutorial").build());
        inventory.setItem(29, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lCustom Items Tutorial").build());
        inventory.setItem(33, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDungeons Tutorial").build());
        inventory.setItem(34, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDungeons Tutorial").build());
        inventory.setItem(35, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lDungeons Tutorial").build());
        player.openInventory(inventory);
    }

}
