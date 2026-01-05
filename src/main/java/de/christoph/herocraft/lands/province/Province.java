/*    */ package de.christoph.herocraft.lands.province;
/*    */
/*    */ import de.christoph.herocraft.HeroCraft;
/*    */ import de.christoph.herocraft.lands.Land;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.Sound;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.plugin.Plugin;
/*    */
/*    */ public class Province {
    /*    */   private String land;
    /*    */   private String name;
    /*    */   private double x1;
    /*    */   private double z1;
    /*    */   private double x2;
    /*    */   private double z2;
    /*    */   private String world;
    /*    */
    /*    */   public Province(String land, String name, double x1, double z1, double x2, double z2, String world) {
        /* 21 */     this.land = land;
        /* 22 */     this.name = name;
        /* 23 */     this.x1 = x1;
        /* 24 */     this.z1 = z1;
        /* 25 */     this.x2 = x2;
        /* 26 */     this.z2 = z2;
        /* 27 */     this.world = world;
        /*    */   }
    /*    */
    /*    */   public boolean canBuild(Player player) {
        /* 31 */     Land landOb = HeroCraft.getPlugin().getLandManager().getLandByName(this.land);
        /* 32 */     return landOb.canBuild(player);
        /*    */   }
    /*    */
    /*    */   public void teleportTo(final Player player) {
        /* 36 */     double minX = Math.min(this.x1, this.x2);
        /* 37 */     double maxX = Math.max(this.x1, this.x2);
        /* 38 */     double minZ = Math.min(this.z1, this.z2);
        /* 39 */     double maxZ = Math.max(this.z1, this.z2);
        /* 40 */     final double centerX = (minX + maxX) / 2.0D;
        /* 41 */     final double centerZ = (minZ + maxZ) / 2.0D;
        /* 42 */     final double y = Bukkit.getWorld(this.world).getHighestBlockYAt(new Location(Bukkit.getWorld(this.world), centerX, 1.0D, centerZ));
        /* 43 */     player.sendTitle("§e§lReise startet..", "§7Zur Stadt §a" + this.name);
        /* 44 */     player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
        /* 45 */     Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                /*    */         {
            /*    */           public void run() {
                /* 48 */             player.teleport(new Location(Bukkit.getWorld(Province.this.world), centerX, y, centerZ));
                /*    */           }
            /*    */         }, 60L);
        /*    */   }
    /*    */
    /*    */   public String getName() {
        /* 54 */     return this.name;
        /*    */   }
    /*    */
    /*    */   public String getLand() {
        /* 58 */     return this.land;
        /*    */   }
    /*    */
    /*    */   public double getX1() {
        /* 62 */     return this.x1;
        /*    */   }
    /*    */
    /*    */   public double getX2() {
        /* 66 */     return this.x2;
        /*    */   }
    /*    */
    /*    */   public double getZ1() {
        /* 70 */     return this.z1;
        /*    */   }
    /*    */
    /*    */   public double getZ2() {
        /* 74 */     return this.z2;
        /*    */   }
    /*    */
    /*    */   public String getWorld() {
        /* 78 */     return this.world;
        /*    */   }
    /*    */
    /*    */   public void setName(String name) {
        /* 82 */     this.name = name;
        /*    */   }
    /*    */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\province\Province.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */