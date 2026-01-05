/*    */ package de.christoph.herocraft.lands.roles;
/*    */
/*    */ import java.util.ArrayList;
/*    */ import org.bukkit.entity.Player;
/*    */
/*    */
/*    */
/*    */
/*    */ public class LandRole
        /*    */ {
    /*    */   private String name;
    /*    */   private String description;
    /*    */   private String land;
    /*    */   private ArrayList<String> players;
    /*    */   private ArrayList<LandPermission> permissions;
    /*    */
    /*    */   public LandRole(String name, String description, String land, ArrayList<String> players, ArrayList<LandPermission> permissions) {
        /* 18 */     this.name = name;
        /* 19 */     this.description = description;
        /* 20 */     this.land = land;
        /* 21 */     this.players = players;
        /* 22 */     this.permissions = permissions;
        /*    */   }
    /*    */
    /*    */   public boolean isPlayerMember(Player player) {
        /* 26 */     return this.players.contains(player.getUniqueId().toString());
        /*    */   }
    /*    */
    /*    */   public String getDescription() {
        /* 30 */     return this.description;
        /*    */   }
    /*    */
    /*    */   public String getName() {
        /* 34 */     return this.name;
        /*    */   }
    /*    */
    /*    */   public ArrayList<LandPermission> getPermissions() {
        /* 38 */     return this.permissions;
        /*    */   }
    /*    */
    /*    */   public ArrayList<String> getPlayers() {
        /* 42 */     return this.players;
        /*    */   }
    /*    */
    /*    */   public String getLand() {
        /* 46 */     return this.land;
        /*    */   }
    /*    */
    /*    */   public void setDescription(String description) {
        /* 50 */     this.description = description;
        /*    */   }
    /*    */
    /*    */   public void setLand(String land) {
        /* 54 */     this.land = land;
        /*    */   }
    /*    */
    /*    */   public void setName(String name) {
        /* 58 */     this.name = name;
        /*    */   }
    /*    */
    /*    */   public void setPermissions(ArrayList<LandPermission> permissions) {
        /* 62 */     this.permissions = permissions;
        /*    */   }
    /*    */
    /*    */   public void setPlayers(ArrayList<String> players) {
        /* 66 */     this.players = players;
        /*    */   }
    /*    */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\roles\LandRole.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */