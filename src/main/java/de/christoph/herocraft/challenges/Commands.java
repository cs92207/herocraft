/*     */ package de.christoph.herocraft.challenges;
/*     */
/*     */ import java.util.Random;
/*     */ import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.challenges.config.Aufgabe;
import org.bukkit.Material;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class Commands
/*     */   implements CommandExecutor {
/*  13 */   int aufgProTag = 2;
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
/*  16 */     if (args.length == 0) {
/*  17 */       if (sender instanceof Player) {
/*  18 */         Player player = (Player)sender;
/*  19 */         HeroCraft.createGUI.buildGUI(player, false);
/*     */       } else {
/*  21 */         sender.sendMessage("§cDu musst ein Spieler sein!");
/*     */       } 
/*  23 */     } else if (args.length == 1 && args[0].compareTo("reload") == 0) {
/*  24 */       if (sender.hasPermission("ch.rl")) {
/*  25 */         reloadConfig(sender);
/*     */       }
/*     */     }
/*  28 */     else if (args.length == 1 && args[0].compareTo("wegfizeidzfgsidfids") == 0) {
/*  29 */       if (sender instanceof Player) {
/*  30 */         Player player = (Player)sender;
/*  31 */         HeroCraft.createGUI.buildGUI(player, true);
/*     */       }
/*     */       else {
/*     */         
/*  36 */         sender.sendMessage("§cDu musst ein Spieler sein!");
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  41 */     return false;
/*     */   }
/*     */   
/*     */   public void reloadConfig(CommandSender sender) {
/*  45 */     HeroCraft.createConfig.createAllTimeConfig();
/*  46 */     HeroCraft.allTimeAufgaben.clear();
/*  47 */     String fehler = "";
/*     */     
/*  49 */     int nummer = 0;
/*     */     do {
/*  51 */       Aufgabe aufgabe = HeroCraft.createConfig.readAllTime(nummer);
/*  52 */       if (aufgabe == null) {
/*     */         break;
/*     */       }
/*  55 */       if (aufgabe.itemList.isEmpty()) {
/*  56 */         fehler = fehler + "\n§cAufgabe " + fehler + " hat keine Items!";
/*  57 */       } else if (aufgabe.anzahl.isEmpty()) {
/*  58 */         fehler = fehler + "\n§cAufgabe " + fehler + " hat keine Anzahlen!";
/*  59 */       } else if (aufgabe.anzahl.size() != aufgabe.itemList.size()) {
/*  60 */         fehler = fehler + "\n§cAufgabe" + fehler + " hat zu wenig oder zuviele Anzahlen!";
/*  61 */       } else if (aufgabe.belohnung < 1) {
/*  62 */         fehler = fehler + "\n§cAufgabe " + fehler + " hat keine Belohnung!";
/*     */       } else {
/*  64 */         int i = 0;
/*     */         try {
/*  66 */           for (; i < aufgabe.itemList.size(); i++) {
/*  67 */             Material.valueOf(aufgabe.itemList.get(i));
/*     */           }
/*     */           
/*  70 */           HeroCraft.allTimeAufgaben.add(aufgabe);
/*  71 */         } catch (Exception exception) {
/*  72 */           fehler = fehler + "\n§cAufgabe" + fehler + " hat ein falsches Item: " + nummer;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/*  77 */       ++nummer;
/*  78 */     } while (nummer < 27);
/*  79 */     if (sender != null) {
/*  80 */       sender.sendMessage(fehler);
/*     */     } else {
/*  82 */       System.out.println(fehler);
/*     */     } 
/*  84 */     HeroCraft.createConfig.createDailyConfig();
/*  85 */     HeroCraft.dailyAufgaben.clear();
/*  86 */     String fehlerDaily = "";
/*     */     
/*  88 */     int tag = 1;
/*     */     while (true) {
/*  90 */       Random random = new Random();
/*  91 */       int result = random.nextInt(this.aufgProTag) + 1;
/*  92 */       for (int i = 1; i <= this.aufgProTag; i++) {
/*  93 */         Aufgabe daily = HeroCraft.createConfig.readDaily(tag, i);
/*  94 */         if (daily == null) {
/*  95 */           fehlerDaily = fehlerDaily + "§c\nTag" + fehlerDaily + " Aufgabe" + tag + "fehlt!";
/*  96 */         } else if (daily.itemList.isEmpty()) {
/*  97 */           fehlerDaily = fehlerDaily + "\n§cTag " + fehlerDaily + " hat einen Fehler bei den Items!";
/*  98 */         } else if (daily.anzahl.isEmpty() || daily.anzahl.size() != daily.itemList.size()) {
/*  99 */           fehlerDaily = fehlerDaily + "\n§cTag" + fehlerDaily + " hat falsche Anzahlen!";
/* 100 */         } else if (daily.belohnung < 1) {
/* 101 */           fehlerDaily = fehlerDaily + "\n§cTag " + fehlerDaily + " hat keine Belohnung!";
/*     */         } else {
/* 103 */           int x = 0;
/*     */           try {
/* 105 */             for (; x < daily.itemList.size(); x++) {
/* 106 */               Material.valueOf(daily.itemList.get(x));
/*     */             }
/* 108 */             if (result == i)
/* 109 */               HeroCraft.dailyAufgaben.add(daily);
/* 110 */           } catch (Exception exception) {
/* 111 */             fehlerDaily = fehlerDaily + "\n§cTag " + fehlerDaily + " hat ein falsches Item: " + tag;
/*     */           } 
/*     */         } 
/*     */       } 
/* 115 */       tag++;
/* 116 */       if (tag >= 32) {
/* 117 */         if (sender != null) {
/* 118 */           sender.sendMessage(fehlerDaily);
/*     */         } else {
/* 120 */           System.out.println(fehlerDaily);
/*     */         } 
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Server\1_21\plugins\slchallenge-0.1-1.21.1.jar!\de\beedooo\sLChallenge\Commands.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */