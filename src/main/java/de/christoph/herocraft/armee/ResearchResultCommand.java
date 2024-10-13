package de.christoph.herocraft.armee;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ResearchResultCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(land == null)
            return false;
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::research_result:");
        if(!SuperheroFacility.lastResearchedLand.containsKey(land))
            return false;
        inventory.setItem(22, Superhero.getSuperheroIcon(SuperheroFacility.lastResearchedLand.get(land)));
        player.openInventory(inventory);
        return false;
    }

}
