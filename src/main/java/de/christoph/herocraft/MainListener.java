package de.christoph.herocraft;

import de.christoph.herocraft.specialitems.CraftingRecipeCommand;
import de.christoph.herocraft.specialitems.WorkShopRecipeLoader;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MainListener implements Listener {

    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        new WorkShopRecipeLoader();
        CraftingRecipeCommand.loadStacks();
    }

}
