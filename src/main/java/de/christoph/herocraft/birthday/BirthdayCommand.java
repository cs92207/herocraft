package de.christoph.herocraft.birthday;

import de.anyblocks.api.AnyBlocksAPI;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class BirthdayCommand implements CommandExecutor, Listener {

    public static HashMap<Player, QuizQuestion> quizPlayers = new HashMap();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if(HeroCraft.getPlugin().getConfig().contains("Birthday24." + player.getUniqueId().toString())) {
            player.sendMessage(Constant.PREFIX + "§7Du kannst das Quiz nur einmal §cspielen§7 :(");
            return false;
        }
        if(quizPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Du spielst das Quiz bereits. Schreibe die richtige Antwort in den Chat.");
            return false;
        }
        player.sendMessage("");
        player.sendMessage("§0-- §e§lQuiz Time §0--");
        player.sendMessage("");
        player.sendMessage("§7Wenn du dieses Quiz absolvierst, erhälst du ein §c§lTOLLES GESCHENK §7zu unserem Geburtstag.");
        player.sendMessage("");
        player.sendMessage("§7Schreibe die richtige Antwort in den Chat (§ea, b oder c§7)");
        player.sendMessage("");
        player.sendMessage("§eFrage 1: §7Wie alt ist AnyBlocks geworden?");
        player.sendMessage("§ea: 1 Jahr §7| §eb: 3 Jahre §7| §ec: 2 Jahre");
        player.sendMessage("");
        quizPlayers.put(player, QuizQuestion.HOW_OLD);
        return false;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!quizPlayers.containsKey(player))
            return;
        event.setCancelled(true);
        if(quizPlayers.get(player).equals(QuizQuestion.HOW_OLD)) {
            if(event.getMessage().contains("c") || event.getMessage().contains("C")) {
                player.sendMessage(Constant.PREFIX + "§a§lKorrekt!");
                player.sendMessage("§eFrage 2: §7Wie war der alte Name von AnyBlocks?");
                player.sendMessage("§ea: HeroCraft §7| §eb: HeroWars §7| §ec: AnyKea");
                quizPlayers.put(player, QuizQuestion.OLD_NAME);
            } else {
                if(HeroCraft.getPlugin().getConfig().contains("SecondChance." + player.getUniqueId().toString())) {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast das Quiz nicht geschafft :/");
                    HeroCraft.getPlugin().getConfig().set("Birthday24." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                } else {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast noch §aEine Chance§c um das Quiz zu wiederholen. Klicke erneut auf den NPC.");
                    HeroCraft.getPlugin().getConfig().set("SecondChance." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                }
            }
        } else if(quizPlayers.get(player).equals(QuizQuestion.OLD_NAME)) {
            if(event.getMessage().contains("b") || event.getMessage().contains("B")) {
                player.sendMessage(Constant.PREFIX + "§a§lKorrekt!");
                player.sendMessage("§eFrage 3: Wie hieß der aller erste Spielmodus auf AnyBlocks (bzw. damals HeroWars)");
                player.sendMessage("§ea: HeroCraft §7| §eb: HeroCity §7| §ec: Infinity Stones");
                quizPlayers.put(player, QuizQuestion.FIRST_GAMEMODE);
            } else {
                if(HeroCraft.getPlugin().getConfig().contains("SecondChance." + player.getUniqueId().toString())) {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast das Quiz nicht geschafft :/");
                    HeroCraft.getPlugin().getConfig().set("Birthday24." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                } else {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast noch §aEine Chance§c um das Quiz zu wiederholen. Klicke erneut auf den NPC.");
                    HeroCraft.getPlugin().getConfig().set("SecondChance." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                }
            }
        } else if(quizPlayers.get(player).equals(QuizQuestion.FIRST_GAMEMODE)) {
            if(event.getMessage().contains("b") || event.getMessage().contains("B")) {
                player.sendMessage(Constant.PREFIX + "§a§lKorrekt!");
                player.sendMessage("§eFrage 3: Welcher ist der aktuell älteste Spielmodus, den es momentan auf AnyBlocks gibt?");
                player.sendMessage("§ea: Safe StanLee §7| §eb: Survival Lands §7| §ec: Infinity Stones");
                quizPlayers.put(player, QuizQuestion.OLDEST_ACTUELL_GAMEMODE);
            } else {
                if(HeroCraft.getPlugin().getConfig().contains("SecondChance." + player.getUniqueId().toString())) {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast das Quiz nicht geschafft :/");
                    HeroCraft.getPlugin().getConfig().set("Birthday24." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                } else {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast noch §aEine Chance§c um das Quiz zu wiederholen. Klicke erneut auf den NPC.");
                    HeroCraft.getPlugin().getConfig().set("SecondChance." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                }
            }
        } else if(quizPlayers.get(player).equals(QuizQuestion.OLDEST_ACTUELL_GAMEMODE)) {
            if(event.getMessage().contains("a") || event.getMessage().contains("A")) {
                player.sendMessage(Constant.PREFIX + "§a§lKorrekt!");
                player.sendMessage("§eFrage 3: Wie heißt die Firma, die hinter AnyBlocks steht?");
                player.sendMessage("§ea: AnyHosting §7| §eb: SV Systems §7| §ec: SV Studios");
                quizPlayers.put(player, QuizQuestion.COMPANY_BEHIND);
            } else {
                if(HeroCraft.getPlugin().getConfig().contains("SecondChance." + player.getUniqueId().toString())) {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast das Quiz nicht geschafft :/");
                    HeroCraft.getPlugin().getConfig().set("Birthday24." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                } else {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast noch §aEine Chance§c um das Quiz zu wiederholen. Klicke erneut auf den NPC.");
                    HeroCraft.getPlugin().getConfig().set("SecondChance." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                }
            }
        } else if(quizPlayers.get(player).equals(QuizQuestion.COMPANY_BEHIND)) {
            if(event.getMessage().contains("c") || event.getMessage().contains("C")) {
                player.sendMessage(Constant.PREFIX + "§a§lKorrekt!");
                HeroCraft.getPlugin().getConfig().set("Birthday24." + player.getUniqueId().toString(), true);
                HeroCraft.getPlugin().saveConfig();
                quizPlayers.remove(player);
                player.sendMessage("");
                player.sendMessage(Constant.PREFIX + "§7Du hast das Quiz §aGeschafft!");
                player.sendMessage("");
                player.sendMessage("§a+ 1500 Survival Lands Coins");
                player.sendMessage("§a+ 5 AnyCoins");
                player.sendMessage("§a+ Besonders Item");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                AnyBlocksAPI.getInstance().getAnyCoinsAPI().addAnyCoins(player.getUniqueId().toString(), 5);
                player.getInventory().addItem(new ItemBuilder(Material.CAKE).setDisplayName("§4§l2. Geburtstag 2024").setLore("", "§7Danke, dass du mit uns", "§7Geburtstag gefeiert hast! <3").addEnchantment(Enchantment.LUCK_OF_THE_SEA, -1).build());
                Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        HeroCraft.getPlugin().coin.addMoney(player, 1500);
                    }
                }, 20);
            } else {
                if(HeroCraft.getPlugin().getConfig().contains("SecondChance." + player.getUniqueId().toString())) {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast das Quiz nicht geschafft :/");
                    HeroCraft.getPlugin().getConfig().set("Birthday24." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                } else {
                    player.sendMessage(Constant.PREFIX + "§cLeider Falsch ): Du hast noch §aEine Chance§c um das Quiz zu wiederholen. Klicke erneut auf den NPC.");
                    HeroCraft.getPlugin().getConfig().set("SecondChance." + player.getUniqueId().toString(), true);
                    HeroCraft.getPlugin().saveConfig();
                    quizPlayers.remove(player);
                }
            }
        }
    }

}

