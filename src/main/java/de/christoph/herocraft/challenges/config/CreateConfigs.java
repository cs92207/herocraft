/*    */ package de.christoph.herocraft.challenges.config;
/*    */
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import de.christoph.herocraft.HeroCraft;
import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.configuration.file.FileConfiguration;
/*    */ import org.bukkit.configuration.file.YamlConfiguration;
/*    */ 
/*    */ 
/*    */ public class CreateConfigs
/*    */ {
/*    */   public static File allTimeFile;
/*    */   public static FileConfiguration allTimeConfig;
/*    */   public static File dailyFile;
/*    */   public static FileConfiguration dailyConfig;
/* 17 */   private final HeroCraft main = HeroCraft.getPlugin();
/*    */ 
/*    */   
/*    */   public void createAllTimeConfig() {
/* 21 */     allTimeFile = new File(this.main.getDataFolder(), "AllTime.yml");
/* 22 */     if (!allTimeFile.exists()) {
/*    */       try {
/* 24 */         allTimeFile.createNewFile();
/* 25 */       } catch (IOException e) {
/* 26 */         e.printStackTrace();
/*    */       } 
/*    */     }
/*    */     
/* 30 */     allTimeConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(allTimeFile);
/*    */   }
/*    */ 
/*    */   
/*    */   public void createDailyConfig() {
/* 35 */     dailyFile = new File(this.main.getDataFolder(), "DailyTask.yml");
/* 36 */     if (!dailyFile.exists()) {
/*    */       try {
/* 38 */         dailyFile.createNewFile();
/* 39 */       } catch (IOException e) {
/* 40 */         e.printStackTrace();
/*    */       } 
/*    */     }
/*    */     
/* 44 */     dailyConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(dailyFile);
/*    */   }
/*    */ 
/*    */   
/*    */   public Aufgabe readAllTime(int aufgabenNummer) {
/* 49 */     Aufgabe aufgabe = new Aufgabe();
/* 50 */     FileConfiguration config = allTimeConfig;
/* 51 */     ConfigurationSection section = config.getConfigurationSection("Aufgabe" + aufgabenNummer);
/* 52 */     if (section == null) {
/* 53 */       return null;
/*    */     }
/* 55 */     aufgabe.belohnung = section.getInt("coins");
/* 56 */     System.out.println(aufgabe.belohnung);
/* 57 */     aufgabe.itemList = section.getStringList("items");
/* 58 */     System.out.println(aufgabe.itemList);
/* 59 */     aufgabe.anzahl = section.getIntegerList("anzahl");
/* 60 */     System.out.println(aufgabe.anzahl);
/* 61 */     aufgabe.name = "§e" + section.getString("Name");
/*    */     
/* 63 */     return aufgabe;
/*    */   }
/*    */   
/*    */   public Aufgabe readDaily(int day, int welche) {
/* 67 */     Aufgabe aufgabe = new Aufgabe();
/* 68 */     FileConfiguration config = dailyConfig;
/* 69 */     ConfigurationSection tag = config.getConfigurationSection("Tag" + day);
/* 70 */     ConfigurationSection section = tag.getConfigurationSection("Aufgabe" + welche);
/* 71 */     if (section == null)
/* 72 */       return null; 
/* 73 */     aufgabe.itemList = section.getStringList("items");
/* 74 */     aufgabe.anzahl = section.getIntegerList("anzahl");
/* 75 */     aufgabe.belohnung = section.getInt("coins");
/* 76 */     aufgabe.name = "§aTägliche Aufgabe";
/* 77 */     return aufgabe;
/*    */   }
/*    */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Server\1_21\plugins\slchallenge-0.1-1.21.1.jar!\de\beedooo\sLChallenge\config\CreateConfigs.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */