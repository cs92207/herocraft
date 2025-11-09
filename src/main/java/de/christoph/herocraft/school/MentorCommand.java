package de.christoph.herocraft.school;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.school.skills.Skill;
import de.christoph.herocraft.school.skills.SkillManager;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class MentorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Inventory inventory = Bukkit.createInventory(null, 9 * 5, ":offset_-16::skills:");
            if(HeroCraft.getPlugin().getSkillManager().isSkillsActive(player)) {
                inventory.setItem(44, getCustomItem("§fCancel", "§c§lSkills deaktivieren"));
            } else
                inventory.setItem(44, getCustomItem("§fSearch", "§a§lSkills aktivieren"));
            int i = 19;
            for(Map.Entry<String, Skill> entry : HeroCraft.getPlugin().getSkillManager().skills.entrySet()) {
                if(MentorListener.hasSkillDeActivated(player, entry.getValue().getName())) {
                    inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§7Rechtsklick zum §a§laktivieren", "", "§7Skill §cdeaktiviert").build());
                } else if(entry.getValue().getName().equalsIgnoreCase("§4§lStärke")) {
                    if(entry.getValue().players.containsKey(player))
                        inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§aGelernt §7[§eLvl. " + entry.getValue().players.get(player) + "§7]", "§7Linksklick zum §a§ltrainieren.", "§7Rechtsklick zum §c§ldeaktivieren§7/§a§laktivieren", "", "§7Coins: §e" + entry.getValue().getMinTrainingCoins(player)).build());
                    else {
                        if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player)) {
                            inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§7Kosten: §e" + entry.getValue().getFirstCosts() + " Coins", "", "§cSkills nicht aktiviert").build());
                        } else
                            inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§7Kosten: §e" + entry.getValue().getFirstCosts() + " Coins", "", "§cNicht gelernt").build());
                    }
                }
            }
            i++;
            for(Map.Entry<String, Skill> entry : HeroCraft.getPlugin().getSkillManager().skills.entrySet()) {
                if(entry.getValue().getName().equalsIgnoreCase("§4§lStärke"))
                    continue;
                if(MentorListener.hasSkillDeActivated(player, entry.getValue().getName())) {
                    inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§7Rechtsklick zum §a§laktivieren", "", "§7Skill §cdeaktiviert").build());
                } else if(entry.getValue().players.containsKey(player))
                    inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§aGelernt §7[§eLvl. " + entry.getValue().players.get(player) + "§7]", "§7Linksklick zum §a§ltrainieren.", "§7Rechtsklick zum §c§ldeaktivieren/§a§laktivieren", "", "§7Coins: §e" + entry.getValue().getMinTrainingCoins(player)).build());
                else {
                    if(!HeroCraft.getPlugin().getSkillManager().isSkillsActive(player)) {
                        inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§7Kosten: §e" + entry.getValue().getFirstCosts() + " Coins", "", "§cSkills nicht aktiviert").build());
                    } else
                        inventory.setItem(i, new ItemBuilder(Material.POTION).setDisplayName(entry.getValue().getName()).setLore("§7" + entry.getValue().getDescription(), "", "§7Kosten: §e" + entry.getValue().getFirstCosts() + " Coins", "", "§cNicht gelernt").build());
                }
                i++;
            }
            player.openInventory(inventory);
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

    private ItemStack getCustomItem(String name, String displayName) {
        for(CustomStack customStack : ItemsAdder.getAllItems()) {
            if(customStack.getDisplayName().equalsIgnoreCase(name)) {
                ItemStack itemStack = customStack.getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(displayName);
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }
        }
        return null;
    }

}
