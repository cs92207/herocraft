/*     */ package de.christoph.herocraft.challenges.gui;
/*     */
/*     */ import java.time.LocalDate;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.FormatStyle;
/*     */ import java.util.HashMap;
/*     */ import java.util.UUID;
/*     */ import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.challenges.config.Aufgabe;
import de.christoph.herocraft.utils.Constant;
import org.bukkit.Material;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class GuiListener
/*     */   implements Listener {
/*  22 */   public HashMap<UUID, Integer> hasTask = new HashMap<>();

            public void abbrechen(Player player) {
                if (!this.hasTask.containsKey(player.getUniqueId())) {
                  player.sendMessage(Constant.PREFIX + "§cDu hast keine Aufgabe ausgewählt!");
                    return;
                }
                hasTask.remove(player.getUniqueId());
                player.sendMessage(Constant.PREFIX + "§7Du hast die Aufgabe §cabgebrochen§7.");
            }
/*     */   public void abgeben(Player player, boolean daily) {
/*     */     Aufgabe aufgabe;
/*  25 */     LocalDate date = LocalDate.now();
/*  26 */     String nachricht = Constant.PREFIX + "§cDu hast nicht genügend Items, dir fehlt: ";
/*     */     
/*  28 */     DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
/*  29 */     FileConfiguration configuration = HeroCraft.getPlugin().getConfig();
/*  30 */     if (daily) {
/*  31 */       aufgabe = HeroCraft.dailyAufgaben.get(date.getDayOfMonth() - 1);
/*  32 */       if (configuration.getString(String.valueOf(player.getUniqueId())).compareTo(date.format(dateTimeFormatter).toString()) == 0) {
/*  33 */         player.closeInventory();
/*  34 */         player.sendMessage(Constant.PREFIX + "§cDu hast die heutige Aufgabe bereits erledigt. Du findest weitere Aufgaben unter AllTime Aufgaben!");
/*     */         return;
/*     */       } 
/*     */     } else {
/*  38 */       if (!this.hasTask.containsKey(player.getUniqueId())) {
/*  39 */         player.sendMessage(Constant.PREFIX + "§cDu hast keine Aufgabe ausgewählt!");
/*     */         return;
/*     */       } 
/*  42 */       int nummer = ((Integer)this.hasTask.get(player.getUniqueId())).intValue();
/*  43 */       aufgabe = HeroCraft.allTimeAufgaben.get(nummer);
/*     */     } 
/*     */     
/*  46 */     boolean richtig = true;
/*  47 */     for (int i = 0; i < aufgabe.itemList.size(); i++) {
/*  48 */       if (!player.getInventory().contains(Material.valueOf(aufgabe.itemList.get(i)), ((Integer)aufgabe.anzahl.get(i)).intValue())) {
/*  49 */         richtig = false;
/*  50 */         nachricht = nachricht + aufgabe.itemList.get(i).toString() + " ";
/*     */       }
/*     */     } 
/*  53 */     if (richtig) {
/*  54 */       if (!daily) {
/*  55 */         this.hasTask.remove(player.getUniqueId());
/*     */       } else {
/*     */         
/*  58 */         configuration.set(player.getUniqueId().toString(), date.format(dateTimeFormatter).toString());
/*  59 */         HeroCraft.getPlugin().saveConfig();
/*     */       } 
/*  61 */       player.sendMessage(Constant.PREFIX + "§aDu hast die Aufgabe erfolgreich gemeistert, du erhälst §e" + aufgabe.belohnung + " §aSurvival Land Coins!");
/*     */       
/*  63 */       player.playSound((Entity)player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5F, 0.1F);
/*     */       
/*  65 */       for (int j = 0; j < aufgabe.itemList.size(); j++) {
/*  66 */         HashMap<Integer, ItemStack> items = (HashMap<Integer, ItemStack>) player.getInventory().all(Material.valueOf(aufgabe.itemList.get(j)));
/*  67 */         int anzahl = 0;
/*  68 */         for (ItemStack item : items.values()) {
/*  69 */           anzahl += item.getAmount();
/*     */         }
/*  71 */         anzahl -= ((Integer)aufgabe.anzahl.get(j)).intValue();
/*  72 */         player.getInventory().remove(Material.valueOf(aufgabe.itemList.get(j)));
/*  73 */         if (anzahl > 0) {
/*  74 */           player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.valueOf(aufgabe.itemList.get(j)), anzahl) });
/*     */         }
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/*  80 */       player.sendMessage(nachricht + ".");
/*  81 */       player.playSound((Entity)player, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0.5F, 0.5F);
/*     */     } 
/*  83 */     player.closeInventory();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onGUIClick(InventoryClickEvent event) {
/*  90 */     Player player = (Player)event.getWhoClicked();
/*  91 */     if (event.getCurrentItem() != null)
/*  92 */       if (event.getCurrentItem().hasItemMeta()) {
/*  93 */         if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
/*  94 */           if (event.getView().getTitle().compareTo(":offset_-16::challenges_main:") == 0) {
/*  95 */             if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§aTägliche Aufgaben") == 0) {
/*  96 */               player.playSound((Entity)player, Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*  97 */               HeroCraft.createGUI.buildDailyGUI(player, false);
/*  98 */               event.setCancelled(true);
/*  99 */             } else if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§6AllTime Aufgaben") == 0) {
/* 100 */               player.closeInventory();
/* 101 */               HeroCraft.createGUI.buildAllTimeGUI(player, false);
/* 102 */               player.playSound((Entity)player, Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 103 */               event.setCancelled(true);
/*     */             } else {
/* 105 */               event.setCancelled(true);
/*     */             
/*     */             }
/*     */           
/*     */           }
/* 110 */           else if (event.getView().getTitle().compareTo(":offset_-16::alltime_tasks:") == 0) {
/* 111 */             if (event.getSlot() <= HeroCraft.allTimeAufgaben.size()) {
/* 112 */               player.playSound((Entity)player, Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 113 */               if (!this.hasTask.containsKey(player.getUniqueId())) {
/* 114 */                 this.hasTask.put(player.getUniqueId(), Integer.valueOf(event.getSlot()));
/* 115 */                 player.sendMessage(Constant.PREFIX + "§aDu hast eine Aufgabe ausgewählt!");
/*     */               } else {
/*     */                 
/* 118 */                 player.sendMessage(Constant.PREFIX + "§cDu hast schon eine Aufgabe ausgewählt!");
/*     */               }
/*     */             
/*     */             }
/* 122 */             else if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§aItems Abgeben") == 0) {
/* 123 */               abgeben(player, false);
/* 124 */               event.setCancelled(true);
/*     */             } else if(event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§cAufgabe abbrechen") == 0) {
        abbrechen(player);
    event.setCancelled(true);
                    }else {
/*     */               
/* 127 */               event.setCancelled(true);
/*     */             } 
/* 129 */             event.setCancelled(true);
/* 130 */             player.closeInventory();
/* 131 */           } else if (event.getView().getTitle().compareTo(":offset_-16::challenges_main:") == 0) {
/* 132 */             if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§aTägliche Aufgaben") == 0) {
/* 133 */               player.playSound((Entity)player, Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 134 */               HeroCraft.createGUI.buildDailyGUI(player, true);
/* 135 */             } else if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§6AllTime Aufgaben") == 0) {
/* 136 */               player.closeInventory();
/* 137 */               HeroCraft.createGUI.buildAllTimeGUI(player, true);
/* 138 */               player.playSound((Entity)player, Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*     */             } else {
/* 140 */               event.setCancelled(true);
/*     */             } 
/* 142 */             event.setCancelled(true);
/* 143 */           } else if (event.getView().getTitle().compareTo(":offset_-16::daily_task:") == 0) {
/* 144 */             if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§aItems Abgeben") == 0) {
/* 145 */               abgeben(player, true);
/* 146 */               event.setCancelled(true);
/*     */             } else {
/*     */               
/* 149 */               event.setCancelled(true);
/*     */             } 
/*     */           } 
/* 152 */           if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§6Zurück") == 0) {
/* 153 */             player.playSound((Entity)player, Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 154 */             HeroCraft.createGUI.buildGUI(player, false);
/* 155 */             event.setCancelled(true);
/* 156 */           } else if (event.getCurrentItem().getItemMeta().getDisplayName().compareTo("§6§lZurück") == 0) {
/* 157 */             player.playSound((Entity)player, Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 158 */             HeroCraft.createGUI.buildGUI(player, true);
/* 159 */             event.setCancelled(true);
/*     */           }
/*     */         
/*     */         } 
/* 163 */       } else if (event.getView().getTitle().compareTo(":offset_-16::alltime_tasks:") == 0 || event.getView().getTitle().compareTo(":offset_-16::daily_task:") == 0 || event.getView().getTitle().compareTo(":offset_-16::challenges_main:") == 0 || event.getView().getTitle().compareTo("§l§6Aufgaben") == 0) {
/* 164 */         event.setCancelled(true);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Server\1_21\plugins\slchallenge-0.1-1.21.1.jar!\de\beedooo\sLChallenge\gui\GuiListener.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */