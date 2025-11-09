package de.christoph.herocraft.lands;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.utils.Constant;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

public class LandCreator implements CommandExecutor, Listener {

    public static HashMap<Player, LandCreatorSaver> creatorPlayers = new HashMap<>();
    public static HashMap<Player, CreatorSteps> creatorStepPlayers = new HashMap<>();
    public static ArrayList<Player> clickWaitingPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        for(Land i : HeroCraft.getPlugin().getLandManager().getAllLands()) {
            if(i.canBuild(player)) {
                player.sendMessage(Constant.PREFIX + "§7Du bist bereits Mitglied eines Landes.");
                return false;
            }
        }
        if(!player.getWorld().getName().equalsIgnoreCase("world")) {
            player.sendMessage(Constant.PREFIX + "§7Du darfst in dieser Welt kein Land erstellen.");
            return false;
        }
        creatorPlayers.put(player, new LandCreatorSaver());
        creatorStepPlayers.put(player, CreatorSteps.NAME);
        player.sendMessage(Constant.PREFIX + "§7Welchen Name soll dein Land haben? (Schreibe in den Chat)");
        player.sendMessage(Constant.PREFIX + "§4Sneake zum abbrechen");
        return false;
    }

    @EventHandler
    public void onPlayerNameChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!creatorStepPlayers.containsKey(player))
            return;
        if(creatorStepPlayers.get(player).equals(CreatorSteps.NAME)) {
            event.setCancelled(true);
            String name = event.getMessage();
            if(name.contains(" ")) {
                player.sendMessage(Constant.PREFIX + "§7Der Name darf leine Leerzeichen.");
                return;
            }
            if(name.matches(".*[^a-zA-Z0-9 ].*")) {
                player.sendMessage(Constant.PREFIX + "§7Der Name darf keine Sonderzeichen enthalten.");
                return;
            }
            if(name.length() > 25) {
                player.sendMessage(Constant.PREFIX + "§7Der Name darf höchstens 25 Zeichen haben.");
                return;
            }
            for(Land i : HeroCraft.getPlugin().getLandManager().getAllLands()) {
                if(i.getName().equalsIgnoreCase(name)) {
                    player.sendMessage(Constant.PREFIX + "§7Dieser Name existiert §cbereits§7.");
                    return;
                }
            }
            creatorPlayers.get(player).name = event.getMessage();
            player.sendMessage(Constant.PREFIX + "§7Sehr gut! Jetzt Linksklicke den Block in der einen Ecke deines Landes.");
            creatorStepPlayers.put(player, CreatorSteps.POS_1);
        } else if(creatorStepPlayers.get(player).equals(CreatorSteps.CONFIRM)) {
            event.setCancelled(true);
            if(event.getMessage().equalsIgnoreCase("erstellen")) {
                if(HeroCraft.getPlugin().coin.getCoins(player) >= Constant.LAND_PRICE || player.hasPermission("herowars.cc")) {
                    LandCreatorSaver saver = creatorPlayers.get(player);
                    if(!LandManager.canCreateLandSize(saver.x1, saver.z1, saver.x2, saver.z2, 4500)) {
                        if(!player.hasPermission("herocraft.land.big") && !player.hasPermission("herowars.cc")) {
                            player.sendMessage(Constant.PREFIX + "§7Das Land darf maximal §c4500 Blöcke §7haben und muss mindestens §c4 Blöcke §7groß sein.");
                            creatorStepPlayers.remove(player);
                            creatorPlayers.remove(player);
                            return;
                        }
                    }
                    if(!LandManager.canCreateLandLocation(saver.x1, saver.z1, saver.x2, saver.z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "") || !LandManager.canCreateLandProvinceLocation(saver.x1, saver.z1, saver.x2, saver.z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), player.getLocation().getWorld().getName() ,"", "")) {
                        player.sendMessage(Constant.PREFIX + "§7Das Land ist zu nahe an einem anderen Land.");
                        creatorStepPlayers.remove(player);
                        creatorPlayers.remove(player);
                        return;
                    }
                    double minX = Math.min(saver.x1, saver.x2);
                    double maxX = Math.max(saver.x1, saver.x2);
                    double minZ = Math.min(saver.z1, saver.z2);
                    double maxZ = Math.max(saver.z1, saver.z2);
                    double centerX = (minX + maxX) / 2;
                    double centerZ = (minZ + maxZ) / 2;
                    double y = Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), centerX, 1, centerZ));
                    y++;
                    Land land = new Land(
                        saver.name,
                        player.getUniqueId().toString(),
                        player.getName(),
                        new String[]{""},
                        new String[]{""},
                        new String[]{""},
                        new String[]{""},
                        saver.x1,
                        saver.z1,
                        saver.x2,
                        saver.z2,
                        centerX,
                        y,
                        centerZ,
                        0,
                        4500,
                        new String[]{""},
                        new String[]{""},
                        0,
                            0,

                            0,
                            0,
                            0,
                            0
                    );
                    HeroCraft.getPlugin().getLandManager().getAllLands().add(land);
                    HeroCraft.getPlugin().getLandManager().saveLand(land);
                    player.sendMessage(Constant.PREFIX + "§7Sehr gut, du besitzt nun dein eigenes Land!");
                    ItemStack goverment = null;
                    for(CustomStack i : ItemsAdder.getAllItems()) {
                        if(i.getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
                            goverment = i.getItemStack();
                        }
                    }
                    player.sendMessage(Constant.PREFIX + "§7Platziere nun das Regierungsgebäude auf deinem Land. §4Wenn du es verlierst, musst du dir mit /regierungsshop ein neues kaufen.");
                    player.getInventory().addItem(goverment);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            if(!player.hasPermission("herowars.cc"))
                                HeroCraft.getPlugin().getCoin().removeMoney(player, Constant.LAND_PRICE);
                        }
                    }, 10);
                } else {
                    player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
                }
                creatorStepPlayers.remove(player);
                creatorPlayers.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!creatorPlayers.containsKey(player))
            return;
        if(event.getClickedBlock() == null)
            return;
        event.setCancelled(true);
        if(creatorStepPlayers.get(player).equals(CreatorSteps.POS_1)) {
            if(ProtectionListener.isInDangerZone(player.getLocation())) {
                player.sendMessage(Constant.PREFIX + "§7Verlasse den Spawn!");
                return;
            }
            creatorPlayers.get(player).x1 = event.getClickedBlock().getLocation().getX();
            creatorPlayers.get(player).z1 = event.getClickedBlock().getLocation().getZ();
            player.sendMessage(Constant.PREFIX + "§7Sehr gut! Linksklicke nun auf den Block in der anderen Ecke deines Landes.");
            creatorStepPlayers.put(player, CreatorSteps.POS_2);
            clickWaitingPlayers.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    clickWaitingPlayers.remove(player);
                }
            }, 20);
        } else if((creatorStepPlayers.get(player)).equals(CreatorSteps.POS_2)) {
            if(clickWaitingPlayers.contains(player))
                return;
            if(ProtectionListener.isInDangerZone(player.getLocation())) {
                player.sendMessage(Constant.PREFIX + "§7Verlasse den Spawn!");
                return;
            }
            creatorPlayers.get(player).x2 = event.getClickedBlock().getLocation().getX();
            creatorPlayers.get(player).z2 = event.getClickedBlock().getLocation().getZ();
            player.sendMessage(Constant.PREFIX + "§7Sehr gut! Das erstellen eines Landes kostet §e" + Constant.LAND_PRICE + " Coins§7. Um es zu erstellen schreibe §eerstellen§7.");
            creatorStepPlayers.put(player, CreatorSteps.CONFIRM);
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(creatorPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§4§lVorgang abgebrochen!");
            creatorPlayers.remove(player);
            creatorStepPlayers.remove(player);
        }
    }

    public enum CreatorSteps {
        NAME,
        POS_1,
        POS_2,
        CONFIRM
    }

    public class LandCreatorSaver {

        public String name;
        public double x1;
        public double z1;
        public double x2;
        public double z2;

    }

}
