package de.christoph.herocraft.jobs;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class JobGUI implements Listener {

    public void openJobGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::all_offers:");

        JobManager jobManager = HeroCraft.getPlugin().getJobManager();
        Job currentJob = null;
        if (jobManager.hasJob(player)) {
            currentJob = jobManager.getJob(player);
        }

        // Job Items erstellen - 5 Jobs in einer Reihe
        int[] slots = {10, 11, 13, 15, 16};
        int slotIndex = 0;

        for (JobType jobType : JobType.values()) {
            if (slotIndex >= slots.length) break;

            boolean isCurrentJob = currentJob != null && currentJob.getJobType() == jobType;
            
            ItemStack jobItem = createJobItem(jobType, isCurrentJob, currentJob);
            inventory.setItem(slots[slotIndex], jobItem);
            slotIndex++;
        }

        // Info Item in der Mitte (Slot 22 ist die Mitte einer 9x3 Reihe, aber bei 9x6 ist es weiter unten)
        // Wir nutzen Slot 31 für die Info (4. Reihe, mittlerer Slot)
        if (currentJob != null) {
            ItemStack infoItem = createInfoItem(currentJob);
            inventory.setItem(31, infoItem);
        } else {
            ItemStack noJobItem = new ItemBuilder(Material.BARRIER)
                    .setDisplayName("§cKein Job ausgewählt")
                    .setLore("", "§7Du hast aktuell keinen Job.", "§7Klicke auf einen Job, um ihn anzunehmen!")
                    .build();
            inventory.setItem(31, noJobItem);
        }

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.5f, 1.0f);
    }

    private ItemStack createJobItem(JobType jobType, boolean isCurrentJob, Job currentJob) {
        Material material = getJobMaterial(jobType);
        String displayName = (isCurrentJob ? "§a§l" : "§7") + jobType.getDisplayName();
        if (isCurrentJob) {
            displayName += " §8(§aAktiv§8)";
        }

        List<String> lore = new ArrayList<>();
        lore.add("");
        
        if (isCurrentJob && currentJob != null) {
            lore.add("§7Level: §a" + currentJob.getLevel());
            lore.add("§7XP: §e" + String.format("%.1f", currentJob.getXp()) + " / " + String.format("%.1f", currentJob.getXpForNextLevel()));
            lore.add("§7Coins-Multiplikator: §6" + String.format("%.1fx", currentJob.getCoinsMultiplier()));
            lore.add("");
            lore.add("§cKlicke, um diesen Job zu behalten");
        } else {
            lore.add("§7Klicke, um diesen Job");
            lore.add("§7anzunehmen");
            lore.add("");
            lore.add(getJobDescription(jobType));
        }

        ItemBuilder builder = new ItemBuilder(material)
                .setDisplayName(displayName);


        if (isCurrentJob) {
            builder.addEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);
        }

        ItemStack item = builder.build();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createInfoItem(Job currentJob) {
        JobType jobType = currentJob.getJobType();
        double progress = (currentJob.getXp() / currentJob.getXpForNextLevel()) * 100;

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Job: §a" + jobType.getDisplayName());
        lore.add("§7Aktuelles Level: §e" + currentJob.getLevel());
        lore.add("");
        lore.add("§7XP Fortschritt:");
        lore.add("§e" + String.format("%.1f", currentJob.getXp()) + " §7/ §e" + String.format("%.1f", currentJob.getXpForNextLevel()) + " §7(" + String.format("%.1f", progress) + "%)");
        lore.add("");
        lore.add("§7Coins-Multiplikator: §6" + String.format("%.1fx", currentJob.getCoinsMultiplier()));
        lore.add("");
        lore.add("§8XP für Level " + (currentJob.getLevel() + 1) + ": §e" + String.format("%.1f", currentJob.getXpForNextLevel()));

        ItemStack item = new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .setDisplayName("§6§lDein Job-Status")
                .build();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private Material getJobMaterial(JobType jobType) {
        switch (jobType) {
            case HOLZFAELLER:
                return Material.WOODEN_AXE;
            case MINENARBEITER:
                return Material.IRON_PICKAXE;
            case FARMER:
                return Material.GOLDEN_HOE;
            case SCHLACHTER:
                return Material.IRON_SWORD;
            case LANDSCHAFTSBAUER:
                return Material.STONE_SHOVEL;
            default:
                return Material.PAPER;
        }
    }

    private String getJobDescription(JobType jobType) {
        switch (jobType) {
            case HOLZFAELLER:
                return "§7Baue Holz ab, um";
            case MINENARBEITER:
                return "§7Baue Steine ab, um";
            case FARMER:
                return "§7Ernte Pflanzen, um";
            case SCHLACHTER:
                return "§7Töte Tiere, um";
            case LANDSCHAFTSBAUER:
                return "§7Baue Erde/Sand ab, um";
            default:
                return "§7Verdiene Coins";
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(":offset_-16::all_offers:")) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (displayName == null || displayName.equals(" ")) {
            return;
        }

        // Job aus DisplayName extrahieren
        JobType selectedJobType = null;
        for (JobType jobType : JobType.values()) {
            if (displayName.contains(jobType.getDisplayName())) {
                selectedJobType = jobType;
                break;
            }
        }

        if (selectedJobType == null) {
            return;
        }

        JobManager jobManager = HeroCraft.getPlugin().getJobManager();
        Job currentJob = null;
        if (jobManager.hasJob(player)) {
            currentJob = jobManager.getJob(player);
        }

        // Wenn bereits dieser Job aktiv ist, nichts tun
        if (currentJob != null && currentJob.getJobType() == selectedJobType) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }

        // Job setzen
        jobManager.setJob(player, selectedJobType);
        player.sendMessage("§e§lAnyBlocks §7§l| §7Du hast den Job §a" + selectedJobType.getDisplayName() + "§7 angenommen!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f);
        
        // GUI neu öffnen
        openJobGUI(player);
    }
}

