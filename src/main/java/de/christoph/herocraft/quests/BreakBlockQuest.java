/*    */ package de.christoph.herocraft.quests;
/*    */
/*    */ import org.bukkit.Material;
/*    */
/*    */ public class BreakBlockQuest
        /*    */   implements Quest {
    /*    */   private final String description;
    /*    */   private final Material block;
    /*    */   private final int goal;
    /* 10 */   private int current = 0;
    /*    */
    /*    */   public BreakBlockQuest(String description, Material block, int goal) {
        /* 13 */     this.description = description;
        /* 14 */     this.block = block;
        /* 15 */     this.goal = goal;
        /*    */   }
    /*    */
    /*    */   public void onBreak(Material broken) {
        /* 19 */     if (broken == this.block)
            /* 20 */       setProgress(this.current + 1);
        /*    */   }
    /*    */
    /*    */   public boolean isComplete() {
        /* 24 */     return (this.current >= this.goal);
        /* 25 */   } public String getDescription() { return this.description; } public int getProgress() {
        /* 26 */     return this.current;
        /*    */   }
    /*    */
    /*    */   public void setProgress(int progress) {
        /* 30 */     this.current = progress;
        /*    */   }
    /*    */
    /* 33 */   public String getGoal() { return String.valueOf(this.goal); } public Quest copy() {
        /* 34 */     return new BreakBlockQuest(this.description, this.block, this.goal);
        /*    */   }
    /*    */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\quests\BreakBlockQuest.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */