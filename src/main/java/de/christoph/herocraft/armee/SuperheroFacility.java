package de.christoph.herocraft.armee;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SuperheroFacility implements Listener {

    // Überblick: Ausbildungslager, Ausbildung, Forschung

    public static ArrayList<Land> researchLands = new ArrayList<>();
    public static HashMap<Land, Superhero> lastResearchedLand = new HashMap<>();

    public void openOverviewInventory(Player player) {
        // TODO: Check if Database table exists for Land of Player (else create one)
    }

    public void openResearchInventory(Player player) {
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        if(researchLands.contains(land)) {
            Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::already_researching:");
            player.openInventory(inventory);
            return;
        }
        double researchCosts = HeroCraft.getPlugin().getSuperheroManager().getResearchCosts(land);
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::research_inventory:");
        inventory.setItem(13, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lForschungskosten:").setLore("", "§7Kosten: §e" + researchCosts + " Coins").build());
        inventory.setItem(30, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lForschen").build());
        inventory.setItem(31, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lForschen").build());
        inventory.setItem(32, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lForschen").build());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onResearchInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::research_inventory:"))
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lForschen")) {
            Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            if(researchLands.contains(land)) {
                Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::already_researching:");
                player.openInventory(inventory);
                return;
            }
            double researchCosts = HeroCraft.getPlugin().getSuperheroManager().getResearchCosts(land);
            if(land.getCoins() < researchCosts) {
                player.closeInventory();
                player.sendMessage(Constant.PREFIX + "§7Dein Land hat nicht genug §cCoins§7.");
                return;
            }
            land.setCoins(land.getCoins() - researchCosts);
            startResearching(land);
            player.sendMessage(Constant.PREFIX + "§7Dein Land erforscht nun einen neuen Superhelden. Dies dauert §e2 Minuten§7.");
        }
    }

    private void startResearching(Land land) {
        researchLands.add(land);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                researchLands.remove(land);
                for(Player all : Bukkit.getOnlinePlayers()) {
                    if(land.canBuild(all)) {
                        all.sendMessage(Constant.PREFIX + "§7Dein Land hat eine Forschung §aabgeschlossen§7.");
                        TextComponent textComponent = new TextComponent("§a§l(Ergebniss ansehen)");
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/forschungsergebnis"));
                        all.spigot().sendMessage(textComponent);
                    }
                }
                Superhero superhero = null;
                ArrayList<Superhero> unlocked = HeroCraft.getPlugin().getSuperheroManager().getUnlockedHeroes(land);
                while (superhero == null || unlocked.contains(superhero)) {
                    superhero = getRandomSuperhero();
                }
                lastResearchedLand.put(land, superhero);
            }
        }, 20*120);
    }

    private Superhero getRandomSuperhero() {
        Random random = new Random();
        int randomIndex = random.nextInt(HeroCraft.getPlugin().getSuperheroManager().getAllHeroes().size());
        return HeroCraft.getPlugin().getSuperheroManager().getAllHeroes().get(randomIndex);
    }

}