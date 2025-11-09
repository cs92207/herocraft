package de.christoph.herocraft.protection;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.basiccommands.SpawnCommand;
import de.christoph.herocraft.basiccommands.VanishCommand;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.lands.LandManager;
import de.christoph.herocraft.scoreboard.ScoreboardManager;
import de.christoph.herocraft.teleporter.Teleporter;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProtectionListener implements Listener {


    // Probieren: Regierungsgebäude Land erstellen, Teleporter Item
    // Machen: Land erstellen per FastLandCreationCommand verbessern mit Regierungsgebäude platzieren und Location aussuchen








    @EventHandler
    public void onPlayerThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType().equals(Material.WIND_CHARGE)) {
            event.setCancelled(true);
            player.sendMessage(Constant.PREFIX + "§7Du darfst die §cWindkugel §7nicht werfen!");
        }
    }






    private static boolean isInSpawn(Location location) {
        if(!location.getWorld().getName().equalsIgnoreCase("world")) {
            return false;
        }
        if(location.getX() >= 20 && location.getX() <= 270) {
            if(location.getZ() <= -105 && location.getZ() >= -327) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInBlackDessertSpawn(Location location) {
        if(!location.getWorld().getName().equalsIgnoreCase("blackDessert")) {
            return false;
        }
        if(location.getX() >= -187 && location.getX() <= -29) {
            if(location.getZ() <= 300 && location.getZ() >= 132) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInDessertSpawn(Location location) {
        if(!location.getWorld().getName().equalsIgnoreCase("dessert")) {
            return false;
        }
        if(location.getX() >= 170 && location.getX() <= 300) {
            if(location.getZ() <= 140 && location.getZ() >= 15) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInNatureSpawn(Location location) {
        if(!location.getWorld().getName().equalsIgnoreCase("nature")) {
            return false;
        }
        if(location.getX() >= 170 && location.getX() <= 300) {
            if(location.getZ() <= 140 && location.getZ() >= 15) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInHeroKea(Location location) {
        return true;
    }

    @EventHandler
    public void onCreeperExpolode(CreeperPowerEvent event) {
        if(isInDangerZone(event.getEntity().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onRaid(RaidTriggerEvent event) {
        Player player = event.getPlayer();
        Land land = LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
        if(land != null && !land.canBuild(player))  {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if(isInDangerZone(event.getLocation())) {
            if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        if(event.getClickedBlock() != null) {
            if(!isInDangerZone(event.getClickedBlock().getLocation()))
                return;
            if(event.getClickedBlock().getType().equals(Material.ITEM_FRAME))
                event.setCancelled(true);
            if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WATER_BUCKET) || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.LAVA_BUCKET))
                event.setCancelled(true);
            return;
        }
    }

    public static boolean isInDangerZone(Location location) {
        if(isInSpawn(location))
            return true;
        if(isInDessertSpawn(location))
            return true;
        if(isInBlackDessertSpawn(location))
            return true;
        if(isInNatureSpawn(location))
            return true;
        if(location.getWorld().getName().equalsIgnoreCase("hero"))
            return true;
        if(!location.getWorld().getName().equalsIgnoreCase("world")) {
            return false;
        }
        if(location.getX() >= 167 && location.getX() <= 193) {
            if(location.getZ() <= -181 && location.getZ() >= -208) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if(LandManager.getLandAtLocation(event.getBlock().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands()) == null)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;
        if(isInDangerZone(event.getPlayer().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;
        if(isInDangerZone(event.getPlayer().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.TNT || event.getEntityType() == EntityType.TNT_MINECART) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        if(isInDangerZone(event.getFurniture().getEntity().getLocation())) {
            if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(player.getGameMode() == GameMode.CREATIVE)
                return;
        }

        if(event.getEntity() instanceof Player) {
            if(!(event.getDamager() instanceof Player)) {
                Player player = (Player) event.getEntity();
            }
        }

        if(isInDangerZone(event.getDamager().getLocation())) {
            if(event.getEntity() instanceof Pig)
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) || event.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK) || event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            if(isInDangerZone(event.getEntity().getLocation()))
                event.setCancelled(true);
        }

    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
            if (event instanceof HangingBreakByEntityEvent) {
                HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) event;
                if (entityEvent.getRemover() instanceof Player) {
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(HeroCraft.getPlugin().coin.isInDatabase(player) && !HeroCraft.getPlugin().getConfig().contains("News." + player.getUniqueId().toString())) {
            HeroCraft.getPlugin().getConfig().set("News." + player.getUniqueId().toString(), true);
            HeroCraft.getPlugin().saveConfig();
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            player.sendMessage("");
            player.sendMessage("§7-- §e§lUpdate News §7--");
            player.sendMessage("");
            player.sendMessage("§e§lTrust System §7- Benutze §e/trust <Spieler> §7um einem Spieler, der nicht in deinem Land ist, Zugriff auf dieses zu geben. Mit §e/untrust <Spieler> §7entfernst du ihn wieder.");
            player.sendMessage("");
            player.sendMessage("§e§lStädte System §7- Du kannst in deinem Land nun Städte erstellen. Diese sind neue Bereiche, in denen du neu Anfangen kannst zu bauen. Sie sind mit einem Stadt Block (der in deinem Land platziert werden muss) und einem Rathaus (welches in der Stadt platziert wird) verbunden. Im §eAdmin Bereich deines Landes §7kannst du Städte erstellen.");
            player.sendMessage("");
            player.sendMessage("§7-- §e§lUpdate News §7--");
            player.sendMessage("");
        }
        Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        String suffix = "";
        if(land != null) {
            HeroCraft.getPlugin().landTagManager.landTags.put(player, HeroCraft.getPlugin().landTagManager.getTagFromLand(land.getName()));
            ScoreboardManager.setScoreboard(player);
            String landTag = HeroCraft.getPlugin().landTagManager.landTags.get(player);
            suffix = " §0(§e" + landTag + "§0)";
            if(landTag == "") {
                suffix = "";
            }
        } else {
            HeroCraft.getPlugin().landTagManager.landTags.put(player, "");
        }

        if(player.hasPermission("prefix.admin"))
            player.setPlayerListName("\uD83C\uDF40 §4" + player.getName() + suffix);
        else if(player.hasPermission("prefix.modleitung"))
            player.setPlayerListName("\uD83D\uDC8E §c" + player.getName() + suffix);
        else if(player.hasPermission("prefix.builderleitung"))
            player.setPlayerListName("\uD83C\uDF39 §3" + player.getName() + suffix);
        else if(player.hasPermission("prefix.devleitung"))
            player.setPlayerListName("\uD83C\uDF08 §b" + player.getName() + suffix);
        else if(player.hasPermission("prefix.moderator"))
            player.setPlayerListName("♚ §c" + player.getName() + suffix);
        else if(player.hasPermission("prefix.supporter"))
            player.setPlayerListName("✌ §a" + player.getName() + suffix);
        else if(player.hasPermission("prefix.developer"))
            player.setPlayerListName("\uD83C\uDCA1 §b" + player.getName() + suffix);
        else if(player.hasPermission("prefix.builder"))
            player.setPlayerListName("∞ §3" + player.getName() + suffix);
        else if(player.hasPermission("prefix.youtuber"))
            player.setPlayerListName("¿ §5" + player.getName() + suffix);
        else if(player.hasPermission("prefix.elite"))
            player.setPlayerListName("¡ §2" + player.getName() + suffix);
        else if(player.hasPermission("prefix.mvp"))
            player.setPlayerListName("\uD83C\uDFB2 §9" + player.getName() + suffix);
        else if(player.hasPermission("prefix.vip"))
            player.setPlayerListName("㋡ §d" + player.getName() + suffix);
        else if(player.hasPermission("prefix.premium"))
            player.setPlayerListName("© §6" + player.getName() + suffix);
        else
            player.setPlayerListName("\uD81A\uDD10 §7" + player.getName() + suffix);
        if(player.hasPermission("vanish.admin")) {
            player.sendMessage(Constant.PREFIX + "§7Standartmäßig befindest du dich im §aVanish§7.");
            VanishCommand.vanishPlayers.add(player);
            for (Player all : Bukkit.getOnlinePlayers()) {
                if(!all.hasPermission("herowars.vanish.show"))
                    all.hidePlayer(player);
            }
        }
        if(!VanishCommand.vanishPlayers.contains(player)) {
            event.setJoinMessage("§e§lAnyBlocks §7§l| §7" + event.getPlayer().getName() + " hat SurvivalLands §abetreten§7.");
        } else {
            event.setJoinMessage("");
        }
        if(!player.hasPermission("herowars.vanish.show")) {
            for(Player vPlayers : VanishCommand.vanishPlayers) {
                player.hidePlayer(vPlayers);
            }
        }
        //if(player.hasPermission("herowars.cc")) {
            player.setGameMode(GameMode.SURVIVAL);
            sendChallenge(player);
            HeroCraft.getPlugin().getConfig().set("Name." + player.getUniqueId().toString(), player.getName());
            HeroCraft.getPlugin().saveConfig();
            if(HeroCraft.getPlugin().getConfig().contains("allplayers")) {
                List<String> allPlayers = HeroCraft.getPlugin().getConfig().getStringList("allplayers");
                if(!allPlayers.contains(player.getUniqueId().toString())) {
                    allPlayers.add(player.getUniqueId().toString());
                }
                HeroCraft.getPlugin().getConfig().set("allplayers", allPlayers);
                HeroCraft.getPlugin().saveConfig();
            } else {
                List<String> allPlayers = new ArrayList<>();
                allPlayers.add(player.getUniqueId().toString());
                HeroCraft.getPlugin().getConfig().set("allplayers", allPlayers);
                HeroCraft.getPlugin().saveConfig();
            }
            if(!HeroCraft.getPlugin().coin.isInDatabase(player)) {
                HeroCraft.getPlugin().coin.addMoney(event.getPlayer(), Constant.START_MONEY);
                HeroCraft.getPlugin().saveConfig();
                player.getInventory().addItem(new ItemStack(Material.BREAD, 32));
                ItemStack goverment = null;
                for(CustomStack i : ItemsAdder.getAllItems()) {
                    if(i.getDisplayName().equalsIgnoreCase("§fWood Park Chair")) {
                        goverment = i.getItemStack();
                    }
                }
                player.getInventory().addItem(goverment);
                ItemStack goverment1 = null;
                for(CustomStack i : ItemsAdder.getAllItems()) {
                    if(i.getDisplayName().equalsIgnoreCase("§fSingle Park Table")) {
                        goverment1 = i.getItemStack();
                    }
                }
                player.getInventory().addItem(goverment1);
                ItemStack goverment2 = null;
                for(CustomStack i : ItemsAdder.getAllItems()) {
                    if(i.getDisplayName().equalsIgnoreCase("§fCobblestone pot of dripleaf")) {
                        goverment2 = i.getItemStack();
                    }
                }
                player.getInventory().addItem(goverment2);
                player.getInventory().addItem(Teleporter.getTeleporterItem());
                ItemStack goverment3 = null;
                for(CustomStack i : ItemsAdder.getAllItems()) {
                    if(i.getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
                        goverment3 = i.getItemStack();
                    }
                }
                ItemMeta itemMeta = goverment3.getItemMeta();
                itemMeta.setDisplayName("§4§lLand erstellen §0(Item platzieren)");
                goverment3.setItemMeta(itemMeta);
                player.getInventory().addItem(goverment3);
            }
        //} else {
        //    player.setGameMode(GameMode.SPECTATOR);
        //    player.sendMessage("§e§lHeroWars §7§l| §7Du bist Zuschauer.");
        //}
        if(!HeroCraft.getPlugin().getConfig().contains("Joined." + event.getPlayer().getUniqueId().toString())) {
            player.teleport(new Location(Bukkit.getWorld("world"), 77.5, 88.5, -229.5, -90F, 0.7F));
            HeroCraft.getPlugin().getConfig().set("Joined." + event.getPlayer().getUniqueId().toString(), true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    ScoreboardManager.setScoreboard(player);
                    if(!player.getLocation().getWorld().getName().equalsIgnoreCase("world")) {
                        player.teleport(new Location(Bukkit.getWorld("world"), 77.5, 88.5, -229.5, -90F, 0.7F));
                    }
                }
            }, 20);
        }
        
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!VanishCommand.vanishPlayers.contains(event.getEntity()))
            event.setDeathMessage(Constant.PREFIX + "§e" + event.getEntity().getPlayer().getName() + " §7ist gestorben.");
        else {
            event.setDeathMessage("");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String landTag = HeroCraft.getPlugin().landTagManager.landTags.get(player);
        String suffix = " §0(§e" + landTag + "§0)";
        if(landTag == "") {
            suffix = "";
        }
        message = message.replace("%", " Prozent");
        if(player.hasPermission("chat.color")) {
            message = message.replace("&", "§");
            message =  message.replace("§l", "&l");
        }
        if(player.hasPermission("prefix.admin"))
            event.setFormat("\uD83C\uDF40 §4" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.modleitung"))
            event.setFormat("\uD83D\uDC8E §c" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.builderleitung"))
            event.setFormat("\uD83C\uDF39 §3" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.devleitung"))
            event.setFormat("\uD83C\uDF08 §b" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.moderator"))
            event.setFormat("♚ §c" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.supporter"))
            event.setFormat("✌ §a" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.developer"))
            event.setFormat("\uD83C\uDCA1 §b" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.builder"))
            event.setFormat("∞ §3" + player.getName() + suffix + " » " + "§a" + message);
        else if(player.hasPermission("prefix.youtuber"))
            event.setFormat("¿ §5" + player.getName() + suffix + " » " + "§e" + message);
        else if(player.hasPermission("prefix.elite"))
            event.setFormat("¡ §2" + player.getName() + suffix + " » " + "§e" + message);
        else if(player.hasPermission("prefix.mvp"))
            event.setFormat("\uD83C\uDFB2 §9" + player.getName() + suffix + " » " + "§e" + message);
        else if(player.hasPermission("prefix.vip"))
            event.setFormat("㋡ §d" + player.getName() + suffix + " » " + "§e" + message);
        else if(player.hasPermission("prefix.premium"))
            event.setFormat("© §6" + player.getName() + suffix + " » " + "§e" + message);
        else
            event.setFormat("\uD81A\uDD10 §7" + player.getName() + suffix + " » " + "§e" + message);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(SpawnCommand.spawnPlayers.contains(event.getPlayer())) {
            event.getPlayer().sendMessage(Constant.PREFIX + "§7Teleportation abgebrochen.");
            SpawnCommand.spawnPlayers.remove(event.getPlayer());
        }
        if(!event.getPlayer().hasPermission("herowars.cc")) {
            //event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    private void sendChallenge(Player player) {
        LocalDate localDate = LocalDate.now();
        if(localDate.getDayOfMonth() == 1) {
            if(HeroCraft.getPlugin().getConfig().getInt("LastCreatedChallengeMonth") != localDate.getMonthValue()) {
                createChallenge();
            }
        }
        sendCurrentChallenge(player);
    }

    private void sendCurrentChallenge(Player player) {
        /*int challengeID = HeroCraft.getPlugin().getConfig().getInt("LastChallengeID");
        Challenge challenge = HeroCraft.getPlugin().getChallengeManager().challenges.get(challengeID);
        player.sendMessage("");
        player.sendMessage("§7-- §e§lChallenge §7--");
        player.sendMessage("");
        player.sendMessage("§a" + challenge.getName());
        player.sendMessage("§7" + challenge.getDescription());
        player.sendMessage("");
        player.sendMessage("§7-- §e§lChallenge §7--");
        player.sendMessage("");*/
    }

    public static void createChallenge() {
        LocalDate localDate = LocalDate.now();
        HeroCraft.getPlugin().getConfig().set("LastCreatedChallengeMonth", localDate.getMonthValue());
        HeroCraft.getPlugin().saveConfig();
        int lastChallengeID;
        if(HeroCraft.getPlugin().getConfig().contains("LastChallengeID")) {
            lastChallengeID = HeroCraft.getPlugin().getConfig().getInt("LastChallengeID");
            lastChallengeID++;
        } else {
            lastChallengeID = 0;
        }
        HeroCraft.getPlugin().getConfig().set("LastChallengeID", lastChallengeID);
        HeroCraft.getPlugin().saveConfig();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!VanishCommand.vanishPlayers.contains(event.getPlayer())) {
            event.setQuitMessage("§e§lAnyBlocks §7§l| §7" + event.getPlayer().getName() + " hat SurvivalLands §cverlassen§7.");
        } else {
            event.setQuitMessage("");
        }
    }

}
