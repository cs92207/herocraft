/*     */ package de.christoph.herocraft.lands.roles;
/*     */
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import de.christoph.herocraft.lands.Land;
/*     */ import de.christoph.herocraft.utils.ItemBuilder;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*     */ import org.bukkit.event.player.PlayerToggleSneakEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class LandRoleManager
        /*     */   implements Listener
        /*     */ {
    /*     */   public HashMap<Player, String> rolePlayerEditPlayers;
    /*     */   private HashMap<String, ArrayList<LandRole>> landRoles;
    /*     */   private HashMap<Player, ArrayList<LandPermission>> createLandRoleSelectedPermission;
    /*     */   private HashMap<Player, String> createLandRoleNamePlayers;
    /*     */   private ArrayList<Player> createLandRoleDescriptionPlayers;
    /*     */   private HashMap<Player, LandRole> detailPlayers;
    /*     */
    /*     */   public LandRoleManager() {
        /*  41 */     this.rolePlayerEditPlayers = new HashMap<>();
        /*  42 */     this.detailPlayers = new HashMap<>();
        /*  43 */     this.createLandRoleNamePlayers = new HashMap<>();
        /*  44 */     this.createLandRoleDescriptionPlayers = new ArrayList<>();
        /*  45 */     this.createLandRoleSelectedPermission = new HashMap<>();
        /*  46 */     this.landRoles = new HashMap<>();
        /*  47 */     loadLandRoles();
        /*     */   }
    /*     */
    /*     */   public ArrayList<LandRole> getRolesFromLand(Land land) {
        /*  51 */     return this.landRoles.get(land.getName());
        /*     */   }
    /*     */
    /*     */   @Nullable
    /*     */   public ArrayList<LandPermission> getLandPermissionFromPlayer(Player player, Land land) {
        /*  56 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /*  57 */     ArrayList<LandPermission> permissions = new ArrayList<>();
        /*  58 */     boolean noLandRole = true;
        /*  59 */     if (landRolesFromLand != null) {
            /*  60 */       for (LandRole i : landRolesFromLand) {
                /*  61 */         if (i.isPlayerMember(player)) {
                    /*  62 */           noLandRole = false;
                    /*  63 */           permissions.addAll(i.getPermissions());
                    /*     */         }
                /*     */       }
            /*     */     }
        /*  67 */     if (noLandRole) {
            /*  68 */       return null;
            /*     */     }
        /*  70 */     return permissions;
        /*     */   }
    /*     */
    /*     */   public void openManageLandRolesInventory(Player player) {
        /*  74 */     Inventory inventory = Bukkit.createInventory(null, 45, ":offset_-16::manage_land_roles:");
        /*  75 */     inventory.setItem(40, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lRolle erstellen").build());
        /*  76 */     int i = 0;
        /*  77 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /*  78 */     if (this.landRoles.get(land.getName()) != null) {
            /*  79 */       for (LandRole landRole : this.landRoles.get(land.getName())) {
                /*  80 */         inventory.addItem(new ItemStack[] { (new ItemBuilder(Material.LIGHT_BLUE_DYE)).setDisplayName(landRole.getName()).setLore(new String[] { "", "§7" + landRole.getDescription() }).build() });
                /*     */       }
            /*     */     }
        /*  83 */     player.openInventory(inventory);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onRolePlayerEditInventoryClick(InventoryClickEvent event) {
        /*  88 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /*  90 */     Player player = (Player)event.getWhoClicked();
        /*  91 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /*  93 */     if (!event.getView().getTitle().equalsIgnoreCase("§4§lBenutzer Rollen verwalten"))
            /*     */       return;
        /*  95 */     event.setCancelled(true);
        /*  96 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /*  98 */     if (!event.getCurrentItem().getItemMeta().hasDisplayName())
            /*     */       return;
        /* 100 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 101 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 102 */     ArrayList<LandRole> landRolesFromLand = getRolesFromLand(land);
        /* 103 */     LandRole currentLandRole = null;
        /* 104 */     for (LandRole i : landRolesFromLand) {
            /* 105 */       if (i.getName().equalsIgnoreCase(displayName)) {
                /* 106 */         currentLandRole = i;
                /*     */       }
            /*     */     }
        /* 109 */     if (currentLandRole == null)
            /*     */       return;
        /* 111 */     Player target = Bukkit.getPlayer(this.rolePlayerEditPlayers.get(player));
        /* 112 */     if (target == null) {
            /* 113 */       player.closeInventory();
            /* 114 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dieser Spieler ist nicht auf dem §cServer§7.");
            /*     */       return;
            /*     */     }
        /* 117 */     if (currentLandRole.isPlayerMember(target)) {
            /* 118 */       removePlayerFromRole(land, currentLandRole.getName(), target.getUniqueId().toString());
            /*     */     } else {
            /* 120 */       addPlayerToRole(land, currentLandRole.getName(), target.getUniqueId().toString());
            /*     */     }
        /* 122 */     player.closeInventory();
        /* 123 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Rolle geändert.");
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onManageLandRolesClick(InventoryClickEvent event) {
        /* 128 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /* 130 */     Player player = (Player)event.getWhoClicked();
        /* 131 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /* 133 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::manage_land_roles:"))
            /*     */       return;
        /* 135 */     event.setCancelled(true);
        /* 136 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /* 138 */     if (!event.getCurrentItem().getItemMeta().hasDisplayName())
            /*     */       return;
        /* 140 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 141 */     if (displayName.equalsIgnoreCase("§4§lRolle erstellen")) {
            /* 142 */       this.createLandRoleSelectedPermission.put(player, new ArrayList<>());
            /* 143 */       openCreateLandRoleInventory(player);
            /*     */       return;
            /*     */     }
        /* 146 */     LandRole landRole = null;
        /* 147 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 148 */     for (LandRole i : this.landRoles.get(land.getName())) {
            /* 149 */       if (i.getName().equalsIgnoreCase(displayName)) {
                /* 150 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 153 */     if (landRole == null)
            /*     */       return;
        /* 155 */     openLandRoleDetailInventory(player, landRole, land);
        /*     */   }
    /*     */
    /*     */   public void openLandRoleDetailInventory(Player player, LandRole landRole, Land land) {
        /* 159 */     this.detailPlayers.put(player, landRole);
        /* 160 */     Inventory inventory = Bukkit.createInventory(null, 45, ":offset_-16::landrole_detail:");
        /* 161 */     for (LandPermission landPermission : LandPermission.values()) {
            /* 162 */       if (!hasRolePermission(land, landRole.getName(), landPermission)) {
                /* 163 */         inventory.addItem(new ItemStack[] { (new ItemBuilder(Material.PAPER)).setDisplayName(landPermission.getName()).setLore(new String[] { "", "§7" + landPermission.getDescription() }).build() });
                /*     */       } else {
                /* 165 */         inventory.addItem(new ItemStack[] { (new ItemBuilder(Material.GRAY_DYE)).setDisplayName(landPermission.getName()).setLore(new String[] { "", "§7" + landPermission.getDescription(), "", "§aAUSGEWÄHLT" }).build() });
                /*     */       }
            /*     */     }
        /* 168 */     ItemStack cancel = HeroCraft.getItemsAdderItem("§fCancel");
        /* 169 */     ItemMeta cancelMeta = cancel.getItemMeta();
        /* 170 */     cancelMeta.setDisplayName("§4§lRolle löschen");
        /* 171 */     cancel.setItemMeta(cancelMeta);
        /* 172 */     inventory.setItem(40, cancel);
        /* 173 */     player.openInventory(inventory);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onLandRoleDetailInventoryClick(InventoryClickEvent event) {
        /* 178 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /* 180 */     Player player = (Player)event.getWhoClicked();
        /* 181 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /* 183 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::landrole_detail:"))
            /*     */       return;
        /* 185 */     event.setCancelled(true);
        /* 186 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /* 188 */     if (!event.getCurrentItem().getItemMeta().hasDisplayName())
            /*     */       return;
        /* 190 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 191 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 192 */     if (displayName.equalsIgnoreCase("§4§lRolle löschen")) {
            /* 193 */       deleteRole(land, ((LandRole)this.detailPlayers.get(player)).getName());
            /* 194 */       player.closeInventory();
            /* 195 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Rolle erflogreich §cgelöscht§7.");
            /*     */       return;
            /*     */     }
        /* 198 */     LandPermission landPermission = null;
        /* 199 */     for (LandPermission i : LandPermission.values()) {
            /* 200 */       if (i.getName().equalsIgnoreCase(displayName)) {
                /* 201 */         landPermission = i;
                /*     */       }
            /*     */     }
        /* 204 */     if (landPermission == null) {
            /*     */       return;
            /*     */     }
        /* 207 */     LandRole landRole = this.detailPlayers.get(player);
        /* 208 */     if (hasRolePermission(land, landRole.getName(), landPermission)) {
            /* 209 */       removePermissionFromRole(land, landRole.getName(), landPermission);
            /*     */     } else {
            /* 211 */       addPermissionToRole(land, landRole.getName(), landPermission);
            /*     */     }
        /* 213 */     openLandRoleDetailInventory(player, landRole, land);
        /*     */   }
    /*     */
    /*     */   public void openCreateLandRoleInventory(Player player) {
        /* 217 */     ItemStack next = HeroCraft.getItemsAdderItem("§fNext >");
        /* 218 */     ItemMeta nextMeta = next.getItemMeta();
        /* 219 */     nextMeta.setDisplayName("§4§lWeiter");
        /* 220 */     next.setItemMeta(nextMeta);
        /* 221 */     Inventory inventory = Bukkit.createInventory(null, 45, ":offset_-16::create_landrole:");
        /* 222 */     inventory.setItem(12, (new ItemBuilder(Material.NETHER_STAR)).setDisplayName("§4§lWähle alle Berechtigungen, die diese Rolle haben soll.").build());
        /* 223 */     inventory.setItem(14, (new ItemBuilder(Material.ARROW)).setDisplayName("§4§lWeiter").build());
        /* 224 */     int i = 18;
        /* 225 */     for (LandPermission landPermission : LandPermission.values()) {
            /* 226 */       if (!((ArrayList)this.createLandRoleSelectedPermission.get(player)).contains(landPermission)) {
                /* 227 */         inventory.setItem(i, (new ItemBuilder(Material.PAPER)).setDisplayName(landPermission.getName()).setLore(new String[] { "", "§7" + landPermission.getDescription() }).build());
                /*     */       } else {
                /* 229 */         inventory.setItem(i, (new ItemBuilder(Material.GRAY_DYE)).setDisplayName(landPermission.getName()).setLore(new String[] { "", "§7" + landPermission.getDescription(), "", "§a§lAUSGEWÄHLT" }).build());
                /* 230 */       }  i++;
            /*     */     }
        /* 232 */     player.openInventory(inventory);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onCreateLandRoleInventoryClick(InventoryClickEvent event) {
        /* 237 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /* 239 */     Player player = (Player)event.getWhoClicked();
        /* 240 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /* 242 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::create_landrole:"))
            /*     */       return;
        /* 244 */     event.setCancelled(true);
        /* 245 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /* 247 */     if (!event.getCurrentItem().getItemMeta().hasDisplayName())
            /*     */       return;
        /* 249 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 250 */     if (displayName.equalsIgnoreCase("§4§lWeiter")) {
            /* 251 */       this.createLandRoleNamePlayers.put(player, "");
            /* 252 */       player.closeInventory();
            /* 253 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Wie soll die Rolle heißen? (Schreibe in den Chat)");
            /* 254 */       player.sendMessage("§4Sneake zum abbrechen.");
            /*     */       return;
            /*     */     }
        /* 257 */     LandPermission currentLandPermission = null;
        /* 258 */     for (LandPermission landPermission : LandPermission.values()) {
            /* 259 */       if (landPermission.getName().equalsIgnoreCase(displayName)) {
                /* 260 */         currentLandPermission = landPermission;
                /*     */       }
            /*     */     }
        /* 263 */     if (currentLandPermission == null)
            /*     */       return;
        /* 265 */     ((ArrayList<LandPermission>)this.createLandRoleSelectedPermission.get(player)).add(currentLandPermission);
        /* 266 */     openCreateLandRoleInventory(player);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onCreateLandRoleSneak(PlayerToggleSneakEvent event) {
        /* 271 */     if (this.createLandRoleNamePlayers.containsKey(event.getPlayer())) {
            /* 272 */       this.createLandRoleNamePlayers.remove(event.getPlayer());
            /* 273 */       this.createLandRoleDescriptionPlayers.remove(event.getPlayer());
            /* 274 */       event.getPlayer().sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen.");
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onCreateLandRoleNameMessage(AsyncPlayerChatEvent event) {
        /* 280 */     Player player = event.getPlayer();
        /* 281 */     if (!this.createLandRoleNamePlayers.containsKey(player))
            /*     */       return;
        /* 283 */     if (this.createLandRoleDescriptionPlayers.contains(player))
            /*     */       return;
        /* 285 */     event.setCancelled(true);
        /* 286 */     String name = event.getMessage();
        /* 287 */     if (name.length() > 20) {
            /* 288 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Der Name darf maximal 20 Zeichen lang sein. Versuche es erneut.");
            /*     */       return;
            /*     */     }
        /* 291 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 292 */     if (existsRole(land, name)) {
            /* 293 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dein Land hat bereits eine Rolle mit diesem Namen.");
            /*     */       return;
            /*     */     }
        /* 296 */     this.createLandRoleNamePlayers.put(player, name);
        /* 297 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Perfekt! Jetzt gebe eine Beschreibung für die Rolle ein:");
        /* 298 */     player.sendMessage("§4Sneake zum abbrechen!");
        /* 299 */     this.createLandRoleDescriptionPlayers.add(player);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onCreateLandRoleDescriptionMessage(AsyncPlayerChatEvent event) {
        /* 304 */     Player player = event.getPlayer();
        /* 305 */     if (!this.createLandRoleDescriptionPlayers.contains(player))
            /*     */       return;
        /* 307 */     event.setCancelled(true);
        /* 308 */     String description = event.getMessage();
        /* 309 */     if (description.length() > 250) {
            /* 310 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Der Name darf maximal 250 Zeichen lang sein. Versuche es erneut.");
            /*     */       return;
            /*     */     }
        /* 313 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 314 */     createLandRole(land, this.createLandRoleNamePlayers.get(player), description, this.createLandRoleSelectedPermission.get(player));
        /* 315 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Rolle §aerstellt§7.");
        /*     */   }
    /*     */
    /*     */   public ArrayList<LandPermission> getPermissionsFromLandRole(Land land, String roleName) {
        /* 319 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /* 320 */     LandRole landRole = null;
        /* 321 */     for (LandRole i : landRolesFromLand) {
            /* 322 */       if (i.getName().equalsIgnoreCase(roleName)) {
                /* 323 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 326 */     if (landRole == null)
            /* 327 */       return null;
        /* 328 */     return landRole.getPermissions();
        /*     */   }
    /*     */
    /*     */   public boolean hasRolePermission(Land land, String roleName, LandPermission landPermission) {
        /* 332 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /* 333 */     LandRole landRole = null;
        /* 334 */     for (LandRole i : landRolesFromLand) {
            /* 335 */       if (i.getName().equalsIgnoreCase(roleName)) {
                /* 336 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 339 */     if (landRole == null)
            /* 340 */       return false;
        /* 341 */     ArrayList<LandPermission> permissions = landRole.getPermissions();
        /* 342 */     return permissions.contains(landPermission);
        /*     */   }
    /*     */
    /*     */   public void removePermissionFromRole(Land land, String roleName, LandPermission landPermission) {
        /* 346 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /* 347 */     LandRole landRole = null;
        /* 348 */     for (LandRole i : landRolesFromLand) {
            /* 349 */       if (i.getName().equalsIgnoreCase(roleName)) {
                /* 350 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 353 */     if (landRole == null)
            /*     */       return;
        /* 355 */     ArrayList<LandPermission> permissions = landRole.getPermissions();
        /* 356 */     permissions.remove(landPermission);
        /* 357 */     landRole.setPermissions(permissions);
        /* 358 */     saveLandRole(landRole);
        /*     */   }
    /*     */
    /*     */   public void deleteRole(Land land, String roleName) {
        /* 362 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /* 363 */     LandRole landRole = null;
        /* 364 */     for (LandRole i : landRolesFromLand) {
            /* 365 */       if (i.getName().equalsIgnoreCase(roleName)) {
                /* 366 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 369 */     if (landRole == null)
            /*     */       return;
        /*     */     try {
            /* 372 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `land_roles` WHERE `name` = ? AND `land` = ?");
            /* 373 */       preparedStatement.setString(1, roleName);
            /* 374 */       preparedStatement.setString(2, land.getName());
            /* 375 */       preparedStatement.execute();
            /* 376 */       ArrayList<LandRole> lR = this.landRoles.get(land.getName());
            /* 377 */       lR.remove(landRole);
            /* 378 */       this.landRoles.put(land.getName(), lR);
            /* 379 */     } catch (SQLException e) {
            /* 380 */       e.printStackTrace();
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void addPermissionToRole(Land land, String roleName, LandPermission landPermission) {
        /* 385 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /* 386 */     LandRole landRole = null;
        /* 387 */     for (LandRole i : landRolesFromLand) {
            /* 388 */       if (i.getName().equalsIgnoreCase(roleName)) {
                /* 389 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 392 */     if (landRole == null)
            /*     */       return;
        /* 394 */     ArrayList<LandPermission> permissions = landRole.getPermissions();
        /* 395 */     permissions.add(landPermission);
        /* 396 */     landRole.setPermissions(permissions);
        /* 397 */     saveLandRole(landRole);
        /*     */   }
    /*     */
    /*     */   public void removePlayerFromRole(Land land, String roleName, String playerUUID) {
        /* 401 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /* 402 */     LandRole landRole = null;
        /* 403 */     for (LandRole i : landRolesFromLand) {
            /* 404 */       if (i.getName().equalsIgnoreCase(roleName)) {
                /* 405 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 408 */     if (landRole == null)
            /*     */       return;
        /* 410 */     ArrayList<String> playersList = landRole.getPlayers();
        /* 411 */     playersList.remove(playerUUID);
        /* 412 */     landRole.setPlayers(playersList);
        /* 413 */     saveLandRole(landRole);
        /*     */   }
    /*     */
    /*     */   public void addPlayerToRole(Land land, String roleName, String playerUUID) {
        /* 417 */     ArrayList<LandRole> landRolesFromLand = this.landRoles.get(land.getName());
        /* 418 */     LandRole landRole = null;
        /* 419 */     for (LandRole i : landRolesFromLand) {
            /* 420 */       if (i.getName().equalsIgnoreCase(roleName)) {
                /* 421 */         landRole = i;
                /*     */       }
            /*     */     }
        /* 424 */     if (landRole == null)
            /*     */       return;
        /* 426 */     ArrayList<String> playersList = landRole.getPlayers();
        /* 427 */     playersList.add(playerUUID);
        /* 428 */     landRole.setPlayers(playersList);
        /* 429 */     saveLandRole(landRole);
        /*     */   }
    /*     */
    /*     */   public void saveLandRole(LandRole landRole) {
        /*     */     ArrayList<LandRole> landLandRoles;
        /* 434 */     if (this.landRoles.containsKey(landRole.getLand())) {
            /* 435 */       landLandRoles = this.landRoles.get(landRole.getLand());
            /*     */     } else {
            /* 437 */       landLandRoles = new ArrayList<>();
            /*     */     }
        /* 439 */     LandRole removeLandRole = null;
        /* 440 */     for (LandRole i : landLandRoles) {
            /* 441 */       if (i.getName().equalsIgnoreCase(landRole.getName())) {
                /* 442 */         removeLandRole = i;
                /*     */       }
            /*     */     }
        /* 445 */     if (removeLandRole != null) {
            /* 446 */       landLandRoles.remove(removeLandRole);
            /*     */     }
        /* 448 */     landLandRoles.add(landRole);
        /* 449 */     this.landRoles.put(landRole.getLand(), landLandRoles);
        /*     */
        /*     */     try {
            /* 452 */       deleteLandRoleFromDatabase(landRole);
            /* 453 */       ObjectMapper mapper = new ObjectMapper();
            /* 454 */       String jsonArray = mapper.writeValueAsString(landRole.getPermissions());
            /* 455 */       ObjectMapper mapper1 = new ObjectMapper();
            /* 456 */       String playersObject = mapper1.writeValueAsString(landRole.getPlayers());
            /* 457 */       PreparedStatement preparedStatement = null;
            /* 458 */       preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `land_roles` (`name`, `description`, `land`, `players`, `permissions`) VALUES (?,?,?,?,?)");
            /* 459 */       preparedStatement.setString(1, landRole.getName());
            /* 460 */       preparedStatement.setString(2, landRole.getDescription());
            /* 461 */       preparedStatement.setString(3, landRole.getLand());
            /* 462 */       preparedStatement.setString(4, playersObject);
            /* 463 */       preparedStatement.setString(5, jsonArray);
            /* 464 */       preparedStatement.execute();
            /* 465 */     } catch (SQLException|com.fasterxml.jackson.core.JsonProcessingException e) {
            /* 466 */       throw new RuntimeException(e);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   private void deleteLandRoleFromDatabase(LandRole landRole) {
        /*     */     try {
            /* 472 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `land_roles` WHERE `name` = ? AND `land` = ?");
            /* 473 */       preparedStatement.setString(1, landRole.getName());
            /* 474 */       preparedStatement.setString(2, landRole.getLand());
            /* 475 */       preparedStatement.execute();
            /* 476 */     } catch (SQLException e) {
            /* 477 */       throw new RuntimeException(e);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void createLandRole(Land land, String name, String description, ArrayList<LandPermission> permissions) {
        /* 482 */     saveLandRole(new LandRole(name, description, land
/*     */
/*     */
/* 485 */           .getName(), new ArrayList<>(), permissions));
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   public boolean existsRole(Land land, String name) {
        /*     */     try {
            /* 493 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_roles` WHERE `name` = ?");
            /* 494 */       preparedStatement.setString(1, name);
            /* 495 */       ResultSet resultSet = preparedStatement.executeQuery();
            /* 496 */       if (resultSet.next())
                /* 497 */         return true;
            /* 498 */     } catch (SQLException e) {
            /* 499 */       e.printStackTrace();
            /*     */     }
        /* 501 */     return false;
        /*     */   }
    /*     */
    /*     */   private void loadLandRoles() {
        /*     */     try {
            /* 506 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `land_roles`");
            /* 507 */       ResultSet resultSet = preparedStatement.executeQuery();
            /* 508 */       while (resultSet.next()) {
                /*     */
                /* 510 */         ObjectMapper mapper = new ObjectMapper();
                /* 511 */         ArrayList<String> players = (ArrayList<String>)mapper.readValue(resultSet.getString("players"), (JavaType)mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
                /* 512 */         ObjectMapper mapper1 = new ObjectMapper();
                /* 513 */         ArrayList<String> permissions = (ArrayList<String>)mapper1.readValue(resultSet.getString("permissions"), (JavaType)mapper1.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
                /* 514 */         ArrayList<LandPermission> landPermissions = new ArrayList<>();
                /* 515 */         for (String i : permissions) {
                    /* 516 */           landPermissions.add(LandPermission.valueOf(i));
                    /*     */         }
                /*     */
                /*     */
                /*     */
                /* 521 */         LandRole landRole = new LandRole(resultSet.getString("name"), resultSet.getString("description"), resultSet.getString("land"), players, landPermissions);
                /*     */
                /*     */
                /*     */
                /* 525 */         if (!this.landRoles.containsKey(landRole.getLand())) {
                    /* 526 */           this.landRoles.put(landRole.getLand(), new ArrayList<>());
                    /*     */         }
                /* 528 */         ArrayList<LandRole> roles = this.landRoles.get(landRole.getLand());
                /* 529 */         roles.add(landRole);
                /* 530 */         this.landRoles.put(landRole.getLand(), roles);
                /*     */       }
            /* 532 */     } catch (SQLException|com.fasterxml.jackson.core.JsonProcessingException e) {
            /* 533 */       throw new RuntimeException(e);
            /*     */     }
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\roles\LandRoleManager.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */