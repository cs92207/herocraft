package de.christoph.herocraft.tutorial;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.utils.Constant;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TutorialVideo implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(!player.hasPermission("anyblocks.admin"))
            return false;
        if(strings.length != 1) {
            return false;
        }
        Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
        if(targetBlock.getType() == Material.AIR)
            return false;
        String saveString = targetBlock.getX() + "_" + targetBlock.getY() + "_" + targetBlock.getZ();
        List<String> tutorialList;
        if(HeroCraft.getPlugin().getConfig().contains("TutorialList")) {
            tutorialList = HeroCraft.getPlugin().getConfig().getStringList("TutorialList");
            tutorialList.add(saveString);
        } else {
            tutorialList = new ArrayList<>();
            tutorialList.add(saveString);
        }
        HeroCraft.getPlugin().getConfig().set("TutorialList", tutorialList);
        HeroCraft.getPlugin().getConfig().set(saveString, strings[0]);
        HeroCraft.getPlugin().saveConfig();
        player.sendMessage(Constant.PREFIX + "§7Tutorial Link §agesetzt§7.");
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getClickedBlock() == null)
            return;
        if(!ProtectionListener.isInDangerZone(event.getPlayer().getLocation()))
            return;
        if(!HeroCraft.getPlugin().getConfig().contains("TutorialList"))
            return;
        List<String> tutorialList = HeroCraft.getPlugin().getConfig().getStringList("TutorialList");
        for(String i : tutorialList) {
            String[] parts = i.split("_");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);
            Block block = event.getClickedBlock();
            if(block.getX() == x && block.getY() == y && block.getZ() == z) {
                String link = HeroCraft.getPlugin().getConfig().getString(i);
                net.md_5.bungee.api.chat.TextComponent textComponent = new TextComponent("§a§l(Tutorial Video)");
                textComponent.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, link));
                player.spigot().sendMessage(textComponent);
                break;
            }
        }
    }

}
