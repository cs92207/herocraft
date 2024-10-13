package de.christoph.herocraft.insurance.insurances;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.insurance.Insurance;
import de.christoph.herocraft.insurance.PlayerInsurance;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ToolInsurance extends Insurance implements Listener {

    public ToolInsurance() {
        super("Werkzeug Versicherung", "Wenn Werkzeug kaputt geht, bekommst du ein paar Materialien zurück", 65, Material.IRON_PICKAXE);
    }

    private ItemStack reward(Material material) {
        switch (material) {
            case WOODEN_HOE:
                return new ItemStack(Material.OAK_PLANKS, 1);
            case WOODEN_AXE:
                return new ItemStack(Material.OAK_PLANKS, 2);
            case WOODEN_SHOVEL:
                return new ItemStack(Material.OAK_PLANKS, 1);
            case WOODEN_PICKAXE:
                return new ItemStack(Material.OAK_PLANKS, 2);
            case STONE_HOE:
                return new ItemStack(Material.COBBLESTONE, 1);
            case STONE_AXE:
                return new ItemStack(Material.COBBLESTONE, 2);
            case STONE_SHOVEL:
                return new ItemStack(Material.COBBLESTONE, 1);
            case STONE_PICKAXE:
                return new ItemStack(Material.COBBLESTONE, 2);
            case IRON_HOE:
                return new ItemStack(Material.IRON_INGOT, 1);
            case IRON_AXE:
                return new ItemStack(Material.IRON_INGOT, 2);
            case IRON_SHOVEL:
                return new ItemStack(Material.IRON_INGOT, 5);
            case IRON_PICKAXE:
                return new ItemStack(Material.IRON_INGOT, 2);
            case GOLDEN_HOE:
                return new ItemStack(Material.GOLD_INGOT, 1);
            case GOLDEN_AXE:
                return new ItemStack(Material.GOLD_INGOT, 2);
            case GOLDEN_SHOVEL:
                return new ItemStack(Material.GOLD_NUGGET, 5);
            case GOLDEN_PICKAXE:
                return new ItemStack(Material.GOLD_INGOT, 2);
            case DIAMOND_HOE:
                return new ItemStack(Material.DIAMOND, 1);
            case DIAMOND_AXE:
                return new ItemStack(Material.DIAMOND, 2);
            case DIAMOND_SHOVEL:
                return new ItemStack(Material.DIAMOND, 1);
            case DIAMOND_PICKAXE:
                return new ItemStack(Material.DIAMOND, 2);
            case NETHERITE_HOE:
                return new ItemStack(Material.NETHERITE_SCRAP, 2);
            case NETHERITE_AXE:
                return new ItemStack(Material.NETHERITE_SCRAP, 3);
            case NETHERITE_SHOVEL:
                return new ItemStack(Material.NETHERITE_SCRAP, 1);
            case NETHERITE_PICKAXE:
                return new ItemStack(Material.NETHERITE_SCRAP, 2);
        }
        return null;
    }

    @EventHandler
    public void onToolBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if(HeroCraft.getPlugin().getInsuranceManager().getPlayerInsuranceByPlayerAndName(player, getName()) == null)
            return;
        ItemStack reward = reward(event.getBrokenItem().getType());
        if(reward != null) {
            player.getInventory().addItem(reward);
            player.sendMessage(Constant.PREFIX + "§7Da du eine §a" + getName() + "§7 hast, wurde ein Teil deines Werkzeugs ersetzt.");
        }
    }

}
