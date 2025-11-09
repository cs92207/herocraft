package de.christoph.herocraft.allthemobs;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllTheMobsManager implements Listener {

    public static Map<EntityType, Integer> normaleMobs = Map.ofEntries(
            Map.entry(EntityType.DONKEY, 16),
            Map.entry(EntityType.CHEST_MINECART, 19),
            Map.entry(EntityType.MARKER, 30),
            Map.entry(EntityType.BLAZE, 35),
            Map.entry(EntityType.RABBIT, 37),
            Map.entry(EntityType.SPECTRAL_ARROW, 46),
            Map.entry(EntityType.STRAY, 49),
            Map.entry(EntityType.GLOW_ITEM_FRAME, 52),
            Map.entry(EntityType.COD, 58),
            Map.entry(EntityType.VILLAGER, 59),
            Map.entry(EntityType.SKELETON, 60),
            Map.entry(EntityType.GIANT, 61),
            Map.entry(EntityType.MOOSHROOM, 65),
            Map.entry(EntityType.CAVE_SPIDER, 66),
            Map.entry(EntityType.SQUID, 67),
            Map.entry(EntityType.GUARDIAN, 70),
            Map.entry(EntityType.BEE, 70),
            Map.entry(EntityType.SHULKER_BULLET, 72),
            Map.entry(EntityType.SHEEP, 73),
            Map.entry(EntityType.FIREBALL, 74),
            Map.entry(EntityType.PLAYER, 78),
            Map.entry(EntityType.LLAMA_SPIT, 87),
            Map.entry(EntityType.INTERACTION, 89),
            Map.entry(EntityType.GOAT, 92),
            Map.entry(EntityType.CHICKEN, 92),
            Map.entry(EntityType.LLAMA, 96),
            Map.entry(EntityType.SALMON, 117),
            Map.entry(EntityType.ENDERMAN, 117),
            Map.entry(EntityType.ENDER_PEARL, 118),
            Map.entry(EntityType.EVOKER_FANGS, 119),
            Map.entry(EntityType.COW, 119),
            Map.entry(EntityType.WIND_CHARGE, 123),
            Map.entry(EntityType.MULE, 138),
            Map.entry(EntityType.SNOW_GOLEM, 141),
            Map.entry(EntityType.WOLF, 147),
            Map.entry(EntityType.WITHER_SKULL, 169),
            Map.entry(EntityType.ITEM_DISPLAY, 175),
            Map.entry(EntityType.BREEZE_WIND_CHARGE, 184),
            Map.entry(EntityType.CAT, 186),
            Map.entry(EntityType.SPIDER, 187),
            Map.entry(EntityType.HORSE, 188),
            Map.entry(EntityType.BAT, 195),
            Map.entry(EntityType.PIGLIN, 196),
            Map.entry(EntityType.CHEST_BOAT, 198),
            Map.entry(EntityType.IRON_GOLEM, 199),
            Map.entry(EntityType.ZOMBIE, 207),
            Map.entry(EntityType.PIG, 208),
            Map.entry(EntityType.HUSK, 212),
            Map.entry(EntityType.ZOMBIFIED_PIGLIN, 213),
            Map.entry(EntityType.TRIDENT, 216),
            Map.entry(EntityType.ARMADILLO, 217),
            Map.entry(EntityType.MAGMA_CUBE, 219),
            Map.entry(EntityType.CREEPER, 222),
            Map.entry(EntityType.DROWNED, 229),
            Map.entry(EntityType.TADPOLE, 230),
            Map.entry(EntityType.BOGGED, 234),
            Map.entry(EntityType.GHAST, 237),
            Map.entry(EntityType.SLIME, 238),
            Map.entry(EntityType.PILLAGER, 239),
            Map.entry(EntityType.SNOWBALL, 244)
    );

    public static Map<EntityType, Integer> selteneMobs = Map.ofEntries(
            Map.entry(EntityType.POLAR_BEAR, 253),
            Map.entry(EntityType.WITHER_SKELETON, 267),
            Map.entry(EntityType.FOX, 298),
            Map.entry(EntityType.PUFFERFISH, 300),
            Map.entry(EntityType.DOLPHIN, 316),
            Map.entry(EntityType.OCELOT, 320),
            Map.entry(EntityType.ENDERMITE, 330),
            Map.entry(EntityType.GLOW_SQUID, 339),
            Map.entry(EntityType.AXOLOTL, 342),
            Map.entry(EntityType.PHANTOM, 348),
            Map.entry(EntityType.STRIDER, 356),
            Map.entry(EntityType.FROG, 356),
            Map.entry(EntityType.ALLAY, 360),
            Map.entry(EntityType.HOGLIN, 366),
            Map.entry(EntityType.VEX, 396),
            Map.entry(EntityType.PARROT, 419),
            Map.entry(EntityType.TROPICAL_FISH, 422),
            Map.entry(EntityType.ZOGLIN, 472),
            Map.entry(EntityType.PANDA, 480)
    );

    public static Map<EntityType, Integer> legendaereMobs = Map.ofEntries(
            Map.entry(EntityType.RAVAGER, 516),
            Map.entry(EntityType.WANDERING_TRADER, 535),
            Map.entry(EntityType.SKELETON_HORSE, 537),
            Map.entry(EntityType.SHULKER, 551),
            Map.entry(EntityType.PIGLIN_BRUTE, 575),
            Map.entry(EntityType.ZOMBIE_VILLAGER, 600),
            Map.entry(EntityType.ZOMBIE_HORSE, 602),
            Map.entry(EntityType.SNIFFER, 622),
            Map.entry(EntityType.TRADER_LLAMA, 693),
            Map.entry(EntityType.BREEZE, 720),
            Map.entry(EntityType.ILLUSIONER, 755),
            Map.entry(EntityType.CAMEL, 767),
            Map.entry(EntityType.EVOKER, 770),
            Map.entry(EntityType.VINDICATOR, 792),
            Map.entry(EntityType.WITHER, 855),
            Map.entry(EntityType.ENDER_DRAGON, 878),
            Map.entry(EntityType.WARDEN, 908)
    );


    public static final String LAND_KILLED_ENITIES = "LandKilledEntities";

    @EventHandler
    public void onPlayerEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if(killer == null)
            return;
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(killer);
        if(land != null) {
            if(getAmountOfKilledLandEntities(event.getEntityType(), land) <= 0) {
            }
            addAmountOfKilledLandEntities(event.getEntityType(), land);
        }
    }

    public void addPointsToLand(EntityType entityType, Land land, boolean first) {
        int landPoints = 0;
        if(HeroCraft.getPlugin().getConfig().contains("LandPoints." + land.getName())) {
            landPoints = HeroCraft.getPlugin().getConfig().getInt("LandPoints." + land.getName());
        }
        int points = 0;
        if(normaleMobs.containsKey(entityType)) {
            points = normaleMobs.get(entityType);
        } else if(selteneMobs.containsKey(entityType)) {
            points = selteneMobs.get(entityType);
        } else if(legendaereMobs.containsKey(entityType)) {
            points = legendaereMobs.get(entityType);
        }
        if(!first) {
            points = Math.round((float) points / 3);
        }
        HeroCraft.getPlugin().getConfig().set("LandPoints." + land.getName(), landPoints + points);
        HeroCraft.getPlugin().saveConfig();
    }

    public void addAmountOfKilledLandEntities(EntityType entityType, Land land) {
        if(getAmountOfKilledLandEntities(entityType, land) <= 0) {
            addPointsToLand(entityType, land, true);
        } else {
            addPointsToLand(entityType, land, false);
        }
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        if(hasLandKilledEntity(entityType, land)) {
            config.set("LAND_KILLED_" + land.getName() + "_" + entityType.toString(), getAmountOfKilledLandEntities(entityType, land) + 1);
            HeroCraft.getPlugin().saveConfig();
            return;
        }
        config.set("LAND_KILLED_" + land.getName() + "_" + entityType.toString(), 1);
        HeroCraft.getPlugin().saveConfig();
        List<String> landKilledEnitites;
        if(config.contains(LAND_KILLED_ENITIES + "_" + land.getName())) {
            landKilledEnitites = config.getStringList(LAND_KILLED_ENITIES + "_" + land.getName());
        } else {
            landKilledEnitites = new ArrayList<>();
        }
        landKilledEnitites.add("LAND_KILLED_" + land.getName() + "_" + entityType.toString());
        config.set(LAND_KILLED_ENITIES + "_" + land.getName(), landKilledEnitites);
        HeroCraft.getPlugin().saveConfig();
    }

    public int getAmountOfKilledLandEntities(EntityType entityType, Land land) {
        FileConfiguration config = HeroCraft.getPlugin().getConfig();
        if(!config.contains(LAND_KILLED_ENITIES + "_" + land.getName())) {
            return 0;
        }
        List<String> landKilledEntities = config.getStringList(LAND_KILLED_ENITIES + "_" + land.getName());
        if(!landKilledEntities.contains("LAND_KILLED_" + land.getName() + "_" + entityType.toString())) {
            return 0;
        }
        return config.getInt("LAND_KILLED_" + land.getName() + "_" + entityType.toString());
    }

    public boolean hasLandKilledEntity(EntityType entityType, Land land) {
        return getAmountOfKilledLandEntities(entityType, land) > 0;
    }

}
