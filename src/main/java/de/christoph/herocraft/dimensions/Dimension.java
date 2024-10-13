package de.christoph.herocraft.dimensions;

import com.google.errorprone.annotations.ForOverride;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.Console;
import java.util.ArrayList;

public abstract class Dimension implements Listener {

    private String name;
    private String world;
    private String description;
    private String[] restrictions;
    private Material iconMaterial;

    protected ArrayList<Player> dimensionPlayers;

    public Dimension(String name, String world, String description, String[] restrictions, Material material) {
        this.name = name;
        this.world = world;
        this.description = description;
        this.restrictions = restrictions;
        this.dimensionPlayers = new ArrayList<>();
        this.iconMaterial = material;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(world)) {
            leaveDimension(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(world)) {
            enterDimension(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if(event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(world)) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*5, 500));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 500));
            event.getPlayer().sendTitle("§e§lNeue Dimension", "§7Reise nach §e§l" + name);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            enterDimension(event.getPlayer());
        } else if(dimensionPlayers.contains(event.getPlayer())) {
            leaveDimension(event.getPlayer());
        }
    }

    public void sendActionBar(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    private void enterDimension(Player player) {
        dimensionPlayers.add(player);
        onDimensionEntered(player);
    }

    private void leaveDimension(Player player) {
        dimensionPlayers.remove(player);
        onDimensionLeaved(player);
    }

    @ForOverride
    public void onTick() {  }

    public ItemStack getIcon() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§eBeschreibung:");
        lore.add("§7" + description);
        lore.add("");
        lore.add("§eEinschränkungen");
        for(String restriction : restrictions) {
            lore.add("§7- " + restriction);
        }
        lore.add("");
        return new ItemBuilder(iconMaterial)
                .setDisplayName("§4§l" + name)
                .setLore(lore)
                .build();
    }

    public abstract void onDimensionEntered(Player player);
    public abstract void onDimensionLeaved(Player player);

    public String getWorld() {
        return world;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getRestrictions() {
        return restrictions;
    }

    public Material getIconMaterial() {
        return iconMaterial;
    }

    public ArrayList<Player> getDimensionPlayers() {
        return dimensionPlayers;
    }

}
