package de.christoph.herocraft.caseopening;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalCrate {

    private ArrayList<Inventory> inventories = new ArrayList<>();

    public static List<ItemStack> content = new ArrayList<>();

    private String name;
    private HeroCraft plugin = HeroCraft.getPlugin();
    private int itemIndex = 0;

    public NormalCrate(String name) {
        this.name = name;
        content = SetCaseOpeningCommand.loadInventory();
    }

    public void spin(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*3, name);
        chooseItems(inventory);
        this.inventories.add(inventory);
        player.openInventory(inventory);

        Random random = new Random();
        final double seconds = 7D + 5D * random.nextDouble();

        new BukkitRunnable() {

            boolean finished = false;
            double delay = 0.0D;
            int ticks = 0;

            @Override
            public void run() {

                if(finished)
                    return;

                this.ticks++;
                this.delay += 1.0D / (20D * seconds);

                if(this.ticks > this.delay * 10) {
                    this.ticks = 0;

                    for(int itemstacks = 9; itemstacks < 18; itemstacks++) {
                        inventory.setItem(itemstacks, NormalCrate.content.get((itemstacks+NormalCrate.this.itemIndex) % NormalCrate.content.size()));
                    }

                    NormalCrate.this.itemIndex += 1;
                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1 ,3);

                    if(this.delay >= 0.5D) {
                        finished = true;

                        new BukkitRunnable() {

                            @Override
                            public void run() {

                                try {

                                    ItemStack item = inventory.getItem(13);
                                    player.getInventory().addItem(item);
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 3);

                                } catch (Exception exception) {
                                    System.out.println("[Casino] Error: 404");
                                }

                                player.updateInventory();
                                player.closeInventory();
                                cancel();

                            }
                        }.runTaskLater(plugin, 50L);
                        cancel();

                    }

                }

            }

        }.runTaskTimer(plugin, 0L, 1L);

    }

    private void chooseItems(Inventory inventory) {

        ItemStack placeFolder = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1, (short) 3);
        ItemMeta placeFolderItemMeta = placeFolder.getItemMeta();
        placeFolderItemMeta.setDisplayName("§0");
        placeFolder.setItemMeta(placeFolderItemMeta);

        for(int i = 0; i < (9*3); i++) {
            inventory.setItem(i, placeFolder);
        }


        ItemStack price = new ItemStack(Material.HOPPER);
        ItemMeta priceMeta = price.getItemMeta();
        priceMeta.setDisplayName("§a§lGewinn");
        price.setItemMeta(priceMeta);

        inventory.setItem(4, price);

        int startIndex = new Random().nextInt(content.size());

        for (int index = 0; index < startIndex; index++) {
            for(int k = 9; k < 18; k++) {
                inventory.setItem(k, content.get(((k + this.itemIndex) % content.size())));
            }
        }

    }

}
