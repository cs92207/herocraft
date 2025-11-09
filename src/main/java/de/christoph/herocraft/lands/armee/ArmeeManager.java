package de.christoph.herocraft.lands.armee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.christoph.herocraft.HeroCraft;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ArmeeManager implements Listener {

    public final HashMap<Material, Integer> NORMAL_ITEMS = new HashMap<>();
    /** Deutlich seltenere Items (20 Stück) */
    public final HashMap<Material, Integer> LEGENDARY_ITEMS = new HashMap<>();
    /** Extrem seltene Items (8 Stück) */
    public final HashMap<Material, Integer> BOSS_ITEMS = new HashMap<>();

    public ArmeeManager() {
        fillMaterialHashMaps();
    }

    private void fillMaterialHashMaps() {
        NORMAL_ITEMS.put(Material.IRON_INGOT,          64);   // braucht Schmelzofen + Mining
        NORMAL_ITEMS.put(Material.COPPER_INGOT,       32);
        NORMAL_ITEMS.put(Material.GOLD_INGOT,          32);   // erst Nether / Badlands oder Schweine-Tausch
        NORMAL_ITEMS.put(Material.COAL_BLOCK,          32);   // 9 × Coal
        NORMAL_ITEMS.put(Material.REDSTONE,           64);
        NORMAL_ITEMS.put(Material.LAPIS_LAZULI,       64);
        NORMAL_ITEMS.put(Material.GLOW_INK_SAC,        16);   // nur von Glow-Tintenfischen
        NORMAL_ITEMS.put(Material.AMETHYST_SHARD,      16);   // Geoden-Fund
        NORMAL_ITEMS.put(Material.OBSIDIAN,            32);   // Wasser + Lava oder Strukturen
        NORMAL_ITEMS.put(Material.SLIME_BALL,          32);   // Sumpf-Nacht / Schleim-Chunks
        NORMAL_ITEMS.put(Material.QUARTZ,              32);   // Nether-Erz
        NORMAL_ITEMS.put(Material.MAGMA_CREAM,         8);   // Magmawürfel-Drops
        NORMAL_ITEMS.put(Material.PRISMARINE_SHARD,    16);   // Wächter-Drops
        NORMAL_ITEMS.put(Material.PRISMARINE_CRYSTALS, 16);
        NORMAL_ITEMS.put(Material.HONEYCOMB,           8);   // Bienennest + Schere
        NORMAL_ITEMS.put(Material.DRIPSTONE_BLOCK,     16);
        NORMAL_ITEMS.put(Material.POINTED_DRIPSTONE,   16);
        NORMAL_ITEMS.put(Material.BAMBOO,             32);   // Jungle / Bambus-Wälder
        NORMAL_ITEMS.put(Material.SWEET_BERRIES,      32);   // Taigabiom-Sträucher
        NORMAL_ITEMS.put(Material.MOSS_BLOCK,         32);   // Schiffswrack / Höhlen
        NORMAL_ITEMS.put(Material.BLACKSTONE,         32);   // Basaltdelta / Festung
        NORMAL_ITEMS.put(Material.BASALT,             32);
        NORMAL_ITEMS.put(Material.COBBLED_DEEPSLATE,  64);   // tiefe Ebenen
        NORMAL_ITEMS.put(Material.GUNPOWDER,           32);   // Creeper-Drops
        NORMAL_ITEMS.put(Material.CLAY_BALL,          64);   // Fluss-/Seeboden
        NORMAL_ITEMS.put(Material.SEA_PICKLE,         42);   // Warm-Ozean-Korallen
        NORMAL_ITEMS.put(Material.PACKED_ICE,          32);   // Gefrorenes Ozeanbiom / Eisberge
        NORMAL_ITEMS.put(Material.PHANTOM_MEMBRANE,    16);   // Phantome ⇒ schläft selten
        NORMAL_ITEMS.put(Material.SHROOMLIGHT,         16);   // Nether-Warp-Wälder
        NORMAL_ITEMS.put(Material.IRON_NUGGET,        64);   // Nebenprodukt & Loot

        /* ---------- Rare (20) ---------- */
        LEGENDARY_ITEMS.put(Material.DIAMOND, 64);
        LEGENDARY_ITEMS.put(Material.EMERALD, 64);
        LEGENDARY_ITEMS.put(Material.BLAZE_ROD, 32);
        LEGENDARY_ITEMS.put(Material.ENDER_PEARL, 32);
        LEGENDARY_ITEMS.put(Material.GHAST_TEAR, 16);
        LEGENDARY_ITEMS.put(Material.SLIME_BALL, 64);
        LEGENDARY_ITEMS.put(Material.PRISMARINE_SHARD, 64);
        LEGENDARY_ITEMS.put(Material.PRISMARINE_CRYSTALS, 64);
        LEGENDARY_ITEMS.put(Material.SHULKER_SHELL, 24);
        LEGENDARY_ITEMS.put(Material.AMETHYST_SHARD, 16);
        LEGENDARY_ITEMS.put(Material.HEART_OF_THE_SEA, 2);
        LEGENDARY_ITEMS.put(Material.NAUTILUS_SHELL, 32);
        LEGENDARY_ITEMS.put(Material.LAPIS_LAZULI, 64);
        LEGENDARY_ITEMS.put(Material.GLOWSTONE_DUST, 64);
        LEGENDARY_ITEMS.put(Material.NETHER_QUARTZ_ORE, 64);
        LEGENDARY_ITEMS.put(Material.TURTLE_SCUTE, 16);
        LEGENDARY_ITEMS.put(Material.TRIDENT, 1);
        LEGENDARY_ITEMS.put(Material.WITHER_SKELETON_SKULL, 5);
        LEGENDARY_ITEMS.put(Material.SPONGE, 64);
        LEGENDARY_ITEMS.put(Material.HONEYCOMB, 16);

        BOSS_ITEMS.put(Material.CREEPER_HEAD, 1);              // nur 1× pro Welt
        BOSS_ITEMS.put(Material.ZOMBIE_HEAD, 1);              // nur 1× pro Welt
        BOSS_ITEMS.put(Material.SKELETON_SKULL, 1);              // nur 1× pro Welt
        BOSS_ITEMS.put(Material.SUSPICIOUS_GRAVEL, 2);       // rar in Trail Ruins
        BOSS_ITEMS.put(Material.SNIFFER_EGG, 5);             // nur über Trail Ruins & Sniffer-Zucht
        BOSS_ITEMS.put(Material.SCULK_SHRIEKER, 5);          // mit Seidenberührung nur selten farmbar
        BOSS_ITEMS.put(Material.REINFORCED_DEEPSLATE, 8);    // Ancient City only
        BOSS_ITEMS.put(Material.BUDDING_AMETHYST, 8);        // unabbau-/nicht verschiebbar – Plugin-only
        BOSS_ITEMS.put(Material.END_CRYSTAL, 16);             // craftbar, aber aufwendig und gefährlich
        BOSS_ITEMS.put(Material.DRAGON_BREATH, 16);
    }

    public void getArmeeSpawner(Player player) {
        int randomNumber = new Random().nextInt(100);
        ItemStack itemStack = HeroCraft.getItemsAdderItem("§fArrow Chest");
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Benötigte Items:");
        lore.add("§7(Rechtsklick zum einlösen)");
        lore.add("");
        if(randomNumber <= 60) {
            // Normal
            ItemStack randomStack = randomFrom(NORMAL_ITEMS);
            itemMeta.setDisplayName("§7§lNormaler Truppen Spawner");
            lore.add("§e" + randomStack.getType().toString());
            lore.add("§e" + randomStack.getAmount() + "§7x");
            lore.add("");
            itemMeta.setLore(lore);
        } else if(randomNumber <= 95) {
            // Legendary
            ItemStack randomStack = randomFrom(LEGENDARY_ITEMS);
            itemMeta.setDisplayName("§d§lLegendärer Truppen Spawner");
            lore.add("§e" + randomStack.getType().toString());
            lore.add("§e" + randomStack.getAmount() + "§7x");
            lore.add("");
            itemMeta.setLore(lore);
        } else {
            // Boss
            ItemStack randomStack = randomFrom(BOSS_ITEMS);
            itemMeta.setDisplayName("§5§lBoss Truppen Spawner");
            lore.add("§e" + randomStack.getType().toString());
            lore.add("§e" + randomStack.getAmount() + "§7x");
            lore.add("");
            itemMeta.setLore(lore);
            itemMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, -1, false);
        }
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
    }

    public static ItemStack randomFrom(Map<Material,Integer> pool) {
        if (pool.isEmpty()) return null;
        List<Map.Entry<Material,Integer>> entries = new ArrayList<>(pool.entrySet());
        Map.Entry<Material,Integer> entry = entries.get(
                ThreadLocalRandom.current().nextInt(entries.size())
        );
        return new ItemStack(entry.getKey(), entry.getValue());
    }

    public void interactArmeeSpawner(Player player) {
        // TODO: Alle Blöcke gesammelt? Dann Spawn und KAMPF um die Truppen zu gewinnen!
    }

    public boolean isLandInArmeeDatabase(String land) {
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `troops` WHERE `land` = ?");
            preparedStatement.setString(1, land);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setNormalTroopsToLand(String land, ArrayList<NormalTroop> normalTroops) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(normalTroops);
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("UPDATE `troops` SET `normal` = ? WHERE `land` = ?");
            preparedStatement.setString(1, jsonString);
            preparedStatement.setString(2, land);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Setzen der NormalTroops für Land: " + land, e);
        }
    }

    public void setLegendaryTroopsToLand(String land, ArrayList<LegendaryTroop> legendaryTroops) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(legendaryTroops);
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("UPDATE `troops` SET `legendary` = ? WHERE `land` = ?");
            preparedStatement.setString(1, jsonString);
            preparedStatement.setString(2, land);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Setzen der LegendaryTroops für Land: " + land, e);
        }
    }

    public ArrayList<NormalTroop> getNormalTroopsFromLand(String land) {
        if (isLandInArmeeDatabase(land))
            return new ArrayList<>();
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("SELECT `normal` FROM `troops` WHERE `land` = ?");
            preparedStatement.setString(1, land);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String jsonString = resultSet.getString("normal");
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonString, new TypeReference<ArrayList<NormalTroop>>() {});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }


    public ArrayList<LegendaryTroop> getLegendaryTroopsFromLand(String land) {
        if (isLandInArmeeDatabase(land))
            return new ArrayList<>();
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("SELECT `legendary` FROM `troops` WHERE `land` = ?");
            preparedStatement.setString(1, land);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String jsonString = resultSet.getString("legendary");
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonString, new TypeReference<ArrayList<LegendaryTroop>>() {});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

    public ArrayList<BossTroop> getBossTroopsFromLand(String land) {
        if (isLandInArmeeDatabase(land))
            return new ArrayList<>();
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection()
                    .prepareStatement("SELECT `boss` FROM `troops` WHERE `land` = ?");
            preparedStatement.setString(1, land);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String jsonString = resultSet.getString("boss");
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonString, new TypeReference<ArrayList<BossTroop>>() {});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

}
