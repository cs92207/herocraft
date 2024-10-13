package de.christoph.herocraft.economy;

import de.christoph.herocraft.HeroCraft;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(strings.length == 0) {
                player.sendMessage(Constant.PREFIX + "§7Deine Coins: §a" + HeroCraft.getPlugin().coin.getCoins(player));
            } else  if(strings.length == 1) {
                if(player.hasPermission("herowars.coins.admin"))
                    sendCoinCommands(player, true);
                else
                    sendCoinCommands(player, false);
            } else if(strings.length == 3) {
                if(strings[0].equalsIgnoreCase("senden")) {
                    if(strings[0].equals(player.getName())) {
                        player.sendMessage(Constant.PREFIX + "§7Du kannst dir selber keine §cCoins§7 senden. ");
                        return false;
                    }
                    Player target = Bukkit.getPlayer(strings[1]);
                    if(target != null) {
                        double amount = 0;
                        try {
                            amount = Double.parseDouble(strings[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige §cZahl§7.");
                        }
                        if(HeroCraft.getPlugin().coin.getCoins(player) >= amount) {
                            if(amount < 0) {
                                player.sendMessage(Constant.PREFIX + "§7Du kannst keinen §cnegativen §7Betrag senden.");
                                return false;
                            }
                            HeroCraft.getPlugin().coin.removeMoney(player, amount);
                            HeroCraft.getPlugin().coin.addMoney(target, amount);
                            player.sendMessage(Constant.PREFIX + "§7Du hast dem Spieler §a" + target.getName() + " " + amount + " Coins §7gegeben.");
                            target.sendMessage(Constant.PREFIX + "§7Der Spieler §a" + player.getName() + " §7hat dir §a" + amount + " Coins §7gegeben.");
                        } else
                            player.sendMessage(Constant.PREFIX + "§7Dazu hast du nicht genug §cCoins§7.");
                    } else
                        player.sendMessage(Constant.PLAYER_NOT_ONLINE);
                } else if(strings[0].equalsIgnoreCase("add")) {
                    if(player.hasPermission("herowars.coins.admin")) {
                        Player target = Bukkit.getPlayer(strings[1]);
                        if(target != null) {
                            double amount = 0;
                            try {
                                amount = Double.parseDouble(strings[2]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige §cZahl§7.");
                            }
                            HeroCraft.getPlugin().coin.addMoney(target, amount);
                            player.sendMessage(Constant.PREFIX + "§7Du hast dem Spieler §a" + target.getName() + " " + amount + " Coins §7gegeben.");
                        } else
                            player.sendMessage(Constant.PLAYER_NOT_ONLINE);
                    } else
                        player.sendMessage(Constant.NO_PERMISSION);
                } else if(strings[0].equalsIgnoreCase("set")) {
                    if(player.hasPermission("herowars.coins.admin")) {
                        Player target = Bukkit.getPlayer(strings[1]);
                        if(target != null) {
                            double amount = 0;
                            try {
                                amount = Double.parseDouble(strings[2]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige §cZahl§7.");
                            }
                            double finalAmount = amount;
                            HeroCraft.getPlugin().coin.setCoins(target, amount);
                            player.sendMessage(Constant.PREFIX + "§7Der Spieler §a" + target.getName() + " §7hat nun §a" + amount + " Coins§7.");
                        } else
                            player.sendMessage(Constant.PLAYER_NOT_ONLINE);
                    } else
                        player.sendMessage(Constant.NO_PERMISSION);
                } else if(strings[0].equalsIgnoreCase("remove")) {
                    if(player.hasPermission("herowars.coins.admin")) {
                        Player target = Bukkit.getPlayer(strings[1]);
                        if(target != null) {
                            double amount = 0;
                            try {
                                amount = Double.parseDouble(strings[2]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(Constant.PREFIX + "§7Dies ist keine gültige §cZahl§7.");
                            }
                            HeroCraft.getPlugin().coin.removeMoney(target, amount);
                            player.sendMessage(Constant.PREFIX + "§7Du hast dem Spieler §a" + target.getName() + " " + amount + " Coins §7weggenommen.");
                        } else
                            player.sendMessage(Constant.PLAYER_NOT_ONLINE);
                    } else
                        player.sendMessage(Constant.NO_PERMISSION);
                }
            }
        } else
            commandSender.sendMessage(Constant.NO_PLAYER);
        return false;
    }

    private void sendCoinCommands(Player player, boolean permission) {
        player.sendMessage("§e§lCoins");
        player.sendMessage("");
        player.sendMessage("§a/coins §7-> §7sehe deine Coins");
        player.sendMessage("§a/coins senden <Spieler> <Betrag> §7-> Überweise Coins an andere Spieler");
        player.sendMessage("§a/coins info §7-> Sehe die Übersicht aller Coin Befehle");
        player.sendMessage("§a/spawn §7-> Teleportiere dich zum Spawn");
        if(permission) {
            player.sendMessage("§a/coins add <Spieler> <Betrag> §7-> Gebe einem Spieler Coins");
            player.sendMessage("§a/coins set <Spieler> <Betrag> §7-> Setze die Coins eines Spielers");
            player.sendMessage("§a/coins remove <Spieler> <Betrag> §7-> Nehme Spielern Coins weg");
        }
        player.sendMessage("");
        player.sendMessage("§e§lCoins");
    }

    private class Constant {

        public static final String PREFIX = "§e§lAnyBlocks §7§l| ";
        public static final String NO_PERMISSION = "§e§lAnyBlocks §7§l| §7Dazu hast du keine §cRechte§7.";
        public static final String PLAYER_NOT_ONLINE = "§e§lAnyBlocks §7§l| §7Dieser Spieler ist nicht auf HeroCraft.";
        public static final String NO_PLAYER = "";

    }
}
