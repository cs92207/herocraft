/*     */ package de.christoph.herocraft.challenges.gui;
/*     */ 

/*     */ import java.time.LocalDate;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.FormatStyle;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.challenges.config.Aufgabe;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.InventoryHolder;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreateGui
/*     */ {
/*     */   public Inventory inventory;
/*     */   public ItemStack daily;
/*     */   public ItemMeta getDailyMeta;
/*     */   public ItemStack allTime;
/*     */   public ItemMeta getAllTimeMeta;
/*     */   public Inventory dailyInventory;
/*     */   public ItemStack back;
/*     */   public ItemMeta getBackMeta;
/*     */   public ItemStack abgeben;
/*     */   public ItemMeta abgebenMeta;
            public ItemStack abbrechen;
            public ItemMeta abbrechenMeta;
/*     */   public Inventory allTimeInv;
/*     */   
/*     */   public void buildGUI(Player player, boolean npc) {
/*  38 */     this.inventory = npc ? Bukkit.createInventory((InventoryHolder)player, 9*5, ":offset_-16::challenges_main:") : Bukkit.createInventory((InventoryHolder)player, 9*5, ":offset_-16::challenges_main:");
/*     */     
/*  40 */     this.daily = new ItemStack(Material.PAPER);
/*  41 */     this.getDailyMeta = this.daily.getItemMeta();
/*  42 */     this.getDailyMeta.setDisplayName("§aTägliche Aufgaben");
/*  43 */     this.daily.setItemMeta(this.getDailyMeta);
              this.daily = new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§aTägliche Aufgaben").build();
/*     */     
/*  45 */     this.allTime = new ItemStack(Material.BOOK);
/*  46 */     this.getAllTimeMeta = this.allTime.getItemMeta();
/*  47 */     this.getAllTimeMeta.setDisplayName("§6AllTime Aufgaben");
/*  48 */     this.allTime.setItemMeta(this.getAllTimeMeta);
    this.allTime = new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§6AllTime Aufgaben").build();
/*     */     
/*  50 */     this.back = new ItemStack(Material.ARROW);
/*  51 */     this.getBackMeta = this.back.getItemMeta();
/*  52 */     this.getBackMeta.setDisplayName(npc ? "§6§lZurück" : "§6Zurück");
/*  53 */     this.back.setItemMeta(this.getBackMeta);
/*     */     
/*  55 */     this.abgeben = new ItemStack(Material.LIME_DYE);
/*  56 */     this.abgebenMeta = this.abgeben.getItemMeta();
/*  57 */     this.abgebenMeta.setDisplayName("§aItems Abgeben");
/*  58 */     this.abgeben.setItemMeta(this.abgebenMeta);

                this.abbrechen = new ItemStack(Material.RED_DYE);
/*  56 */     this.abbrechenMeta = this.abbrechen.getItemMeta();
/*  57 */     this.abbrechenMeta.setDisplayName("§cAufgabe abbrechen");
/*  58 */     this.abbrechen.setItemMeta(this.abbrechenMeta);
/*     */     
/*  60 */     this.inventory.setItem(11, this.allTime);
/*  60 */     this.inventory.setItem(12, this.allTime);
/*  60 */     this.inventory.setItem(20, this.allTime);
/*  60 */     this.inventory.setItem(21, this.allTime);

/*  61 */     this.inventory.setItem(14, this.daily);
/*  61 */     this.inventory.setItem(15, this.daily);
/*  61 */     this.inventory.setItem(23, this.daily);
/*  61 */     this.inventory.setItem(24, this.daily);

/*     */
/*  63 */     player.openInventory(this.inventory);
/*     */   }
/*     */ 
/*     */   
/*     */   public void buildDailyGUI(Player player, boolean npc) {
/*  68 */     FileConfiguration configuration = HeroCraft.getPlugin().getConfig();
/*  69 */     LocalDate date = LocalDate.now();
/*     */     
/*  71 */     DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
/*  72 */     this.dailyInventory = Bukkit.createInventory((InventoryHolder)player, 9*5, ":offset_-16::daily_task:");
/*     */     
/*  74 */     Aufgabe aufgabe = HeroCraft.dailyAufgaben.get(date.getDayOfMonth() - 1);
/*  75 */     ItemStack itemStack = new ItemStack(Material.valueOf(aufgabe.itemList.get(0)));
/*  76 */     ItemMeta itemMeta = itemStack.getItemMeta();
/*  77 */     List<String> lore = new ArrayList<>();
/*  78 */     if (!configuration.contains(player.getUniqueId().toString())) {
/*  79 */       configuration.set(player.getUniqueId().toString(), "");
/*  80 */       HeroCraft.getPlugin().saveConfig();
/*     */     }
/*  82 */     else if (configuration.getString(player.getUniqueId().toString()).compareTo(date.format(dateTimeFormatter).toString()) == 0) {
/*  83 */       itemStack.setType(Material.BEDROCK);
/*  84 */       itemMeta = itemStack.getItemMeta();
/*  85 */       itemMeta.setDisplayName("§cTägliche Aufgabe");
/*  86 */       lore.add("§eDu hast die heutige Aufgabe bereits erledigt!");
/*  87 */       lore.add("§eUnter AllTime Aufgaben findest du noch mehr Aufgaben!");
/*  88 */       itemMeta.setLore(lore);
/*  89 */       itemStack.setItemMeta(itemMeta);
/*  90 */       this.dailyInventory.setItem(13, itemStack);
/*  91 */       this.dailyInventory.setItem(18, this.back);
/*  92 */       player.openInventory(this.dailyInventory);
/*     */       
/*     */       return;
/*     */     } 
/*  96 */     itemMeta.setDisplayName("§aTägliche Aufgabe");
/*  97 */     lore.add(String.valueOf(ChatColor.GREEN) + "Benötigte Items:");
/*  98 */     for (int i = 0; i < aufgabe.itemList.size(); i++) {
/*  99 */       lore.add(String.valueOf(ChatColor.YELLOW) + "- " + String.valueOf(ChatColor.YELLOW) + " " + String.valueOf(aufgabe.anzahl.get(i)) + "x " + aufgabe.itemList.get(i));
/*     */     }
/* 101 */     lore.add(String.valueOf(ChatColor.GREEN) + "Belohnung:");
/* 102 */     lore.add(String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + aufgabe.belohnung + " Survivalland Coins");
/* 103 */     itemMeta.setLore(lore);
/* 104 */     itemStack.setItemMeta(itemMeta);
/* 105 */     this.dailyInventory.setItem(13, itemStack);
/* 106 */     this.dailyInventory.setItem(18, this.back);
/* 107 */     if (npc) {
/* 108 */       this.dailyInventory.setItem(26, this.abgeben);
/*     */     }
/* 110 */     player.openInventory(this.dailyInventory);
/*     */   }
/*     */   public void buildAllTimeGUI(Player player, boolean npc) {
/* 113 */     this.allTimeInv = Bukkit.createInventory((InventoryHolder)player, 36, ":offset_-16::alltime_tasks:");
/*     */     
/* 115 */     for (int nummer = 0; nummer < HeroCraft.allTimeAufgaben.size(); nummer++) {
/* 116 */       Aufgabe aufgabe = HeroCraft.allTimeAufgaben.get(nummer);
/* 117 */       ItemStack itemStack = new ItemStack(Material.valueOf(aufgabe.itemList.get(0)));
/* 118 */       ItemMeta itemMeta = itemStack.getItemMeta();
/* 119 */       itemMeta.setDisplayName(aufgabe.name);
/* 120 */       List<String> lore = new ArrayList<>();
/* 121 */       lore.add(String.valueOf(ChatColor.GREEN) + "Benötigte Items:");
/* 122 */       for (int i = 0; i < aufgabe.itemList.size(); i++) {
/* 123 */         lore.add(String.valueOf(ChatColor.YELLOW) + "- " + String.valueOf(ChatColor.YELLOW) + " " + String.valueOf(aufgabe.anzahl.get(i)));
/*     */       }
/* 125 */       lore.add(String.valueOf(ChatColor.GREEN) + "Belohnug:");
/* 126 */       lore.add(String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + " Survivallands Coins");
/* 127 */       if (HeroCraft.guiListener.hasTask.containsKey(player.getUniqueId()) && ((Integer)HeroCraft.guiListener.hasTask.get(player.getUniqueId())).intValue() == nummer) {
/* 128 */         itemMeta.setEnchantmentGlintOverride(Boolean.valueOf(true));
/* 129 */         lore.add(" ");
/* 130 */         lore.add("§a§lAusgewählt");
/*     */       } 
/*     */       
/* 133 */       itemMeta.setLore(lore);
/* 134 */       itemStack.setItemMeta(itemMeta);
/* 135 */       this.allTimeInv.setItem(nummer, itemStack);
/*     */     } 
/* 137 */     this.allTimeInv.setItem(27, this.back);
/* 138 */     if (npc) {
/* 139 */       this.allTimeInv.setItem(35, this.abgeben);
/* 139 */       this.allTimeInv.setItem(34, this.abbrechen);
/*     */     }
/*     */     
/* 142 */     player.openInventory(this.allTimeInv);
/*     */   }
/*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Server\1_21\plugins\slchallenge-0.1-1.21.1.jar!\de\beedooo\sLChallenge\gui\CreateGui.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */