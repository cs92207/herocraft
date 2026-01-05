/*     */ package de.christoph.herocraft.lands;
/*     */
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import de.christoph.herocraft.lands.roles.LandPermission;
/*     */ import de.christoph.herocraft.lands.roles.LandRole;
/*     */ import de.christoph.herocraft.utils.ItemBuilder;
/*     */ import dev.lone.itemsadder.api.CustomStack;
/*     */ import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
/*     */ import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
/*     */ import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
/*     */ import dev.lone.itemsadder.api.ItemsAdder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.Action;
/*     */ import org.bukkit.event.inventory.InventoryAction;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.inventory.InventoryCloseEvent;
/*     */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.event.player.PlayerToggleSneakEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.inventory.meta.SkullMeta;
/*     */ import org.bukkit.plugin.Plugin;
/*     */
/*     */
/*     */ public class Goverment
        /*     */   implements Listener
        /*     */ {
    /*  39 */   private static ArrayList<Player> payInPlayers = new ArrayList<>();
    /*  40 */   private static ArrayList<Player> payOutPlayers = new ArrayList<>();
    /*  41 */   private static ArrayList<Player> setLandPlayers = new ArrayList<>();
    /*  42 */   private static HashMap<Player, Integer> membersShowPagePlayers = new HashMap<>();
    /*  43 */   private static HashMap<Player, String> memberAdminDetailPagePlayers = new HashMap<>();
    /*  44 */   public static ArrayList<Player> inviterPlayers = new ArrayList<>();
    /*  45 */   public static ArrayList<Player> landMaxBlockPlayers = new ArrayList<>();
    /*  46 */   public static HashMap<Player, Integer> landMaxSubmitPlayers = new HashMap<>();
    /*  47 */   public static HashMap<Player, Land> invitedPlayers = new HashMap<>();
    /*  48 */   public static HashMap<Player, Location> firstEdgeResizePlayers = new HashMap<>();
    /*  49 */   public static HashMap<Player, Location> secondEdgeResizePlayers = new HashMap<>();
    /*  50 */   public static ArrayList<Player> resizePlayers = new ArrayList<>();
    /*  51 */   public static ArrayList<Player> resizeOnePlayers = new ArrayList<>();
    /*  52 */   public static ArrayList<Player> resizeWaitingPlayers = new ArrayList<>();
    /*  53 */   public static ArrayList<Player> setPrisonSpawnPlayers = new ArrayList<>();
    /*     */
    /*     */   private static void openGovermentGUI(Player player, Land land) {
        /*  56 */     Inventory inventory = Bukkit.createInventory(null, 45, ":offset_-16::goverment:");
        /*  57 */     inventory.setItem(11, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§7Coins: §e§l" + land.getCoins()).setLore(new String[] { "§7Armee Coins: §e§l" + land.getArmeeCoins() }).build());
        /*  58 */     inventory.setItem(15, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglieder").build());
        /*  59 */     inventory.setItem(28, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lCoins einzahlen").build());
        /*  60 */     inventory.setItem(29, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lCoins einzahlen").build());
        /*  61 */     inventory.setItem(30, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lCoins einzahlen").build());
        /*  62 */     inventory.setItem(32, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lAdmin Menu").build());
        /*  63 */     inventory.setItem(33, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lAdmin Menu").build());
        /*  64 */     inventory.setItem(34, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lAdmin Menu").build());
        /*  65 */     player.openInventory(inventory);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onGovermentGUIClick(FurnitureInteractEvent event) {
        /*  70 */     Player player = event.getPlayer();
        /*  71 */     if (event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude") || event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lLand erstellen §0(Item platzieren)")) {
            /*  72 */       Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            /*  73 */       if (land == null)
                /*     */         return;
            /*  75 */       if (!land.canBuild(player)) {
                /*  76 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Du kannst nicht auf Regierungen von anderen Ländern zugreifen.");
                /*     */         return;
                /*     */       }
            /*  79 */       openGovermentGUI(player, land);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onGovermentBlockTryBreak(FurnitureBreakEvent event) {
        /*  85 */     Player player = event.getPlayer();
        /*  86 */     if (event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
            /*  87 */       Land land = LandManager.getLandAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            /*  88 */       if (land == null)
                /*     */         return;
            /*  90 */       if (land.isModerator(event.getPlayer().getName()) || land.isOwner(event.getPlayer().getName())) {
                /*  91 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Bitte setze die Regierung nun an eine andere Stelle des Landes.");
                /*     */       } else {
                /*  93 */         event.setCancelled(true);
                /*  94 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Nur Moderatoren oder Eigentümer des Landes dürfen die Regierung umsetzen.");
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFurniturePlace(FurniturePlaceSuccessEvent event) {
        /* 101 */     Player player = event.getPlayer();
        /* 102 */     if (event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
            /* 103 */       Land land = LandManager.getLandAtLocation(event.getBukkitEntity().getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands());
            /* 104 */       if (land == null || !land.canBuild(player)) {
                /* 105 */         event.getFurniture().remove(false);
                /* 106 */         ItemStack goverment = null;
                /* 107 */         for (CustomStack i : ItemsAdder.getAllItems()) {
                    /* 108 */           if (i.getDisplayName().equalsIgnoreCase("§4§lRegierungsgebäude")) {
                        /* 109 */             goverment = i.getItemStack();
                        /*     */           }
                    /*     */         }
                /* 112 */         player.getInventory().addItem(new ItemStack[] { goverment });
                /* 113 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Bitte setze das Regierungsgebäude auf dein Land.");
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   private static void openGovermentAdminGUI(Player player, Land land) {
        /* 119 */     Inventory inventory = Bukkit.createInventory(null, 45, ":offset_-16::goverment_admin:");
        /* 120 */     inventory.setItem(10, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lLand löschen").build());
        /* 121 */     inventory.setItem(12, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglieder verwalten").build());
        /* 122 */     inventory.setItem(14, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lGröße ändern").setLore(new String[] { "", "§eLinksklick: §7Fläche ändern", "§eRechtsklick: §7Größere Maximale Fläche kaufen", "", "§7Maximal: §e" + land.getMaxBlocks() + " Blöcke" }).build());
        /* 123 */     inventory.setItem(16, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lStadt gründen").setLore(new String[] { "", "§7Kosten: §e10000.0" }).build());
        /* 124 */     inventory.setItem(28, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lCoins auszahlen").build());
        /* 125 */     inventory.setItem(29, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lCoins auszahlen").build());
        /* 126 */     inventory.setItem(30, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lCoins auszahlen").build());
        /* 127 */     inventory.setItem(32, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lSpawnpoint setzen").build());
        /* 128 */     inventory.setItem(34, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lLand Rollen verwalten").build());
        /* 129 */     player.openInventory(inventory);
        /*     */   }
    /*     */
    /*     */   private static void resizeLand(Player player) {
        /* 133 */     player.closeInventory();
        /* 134 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Klicke in die erste Ecke deines Landes.");
        /* 135 */     player.sendMessage("§4Sneake zum abbrechen!");
        /* 136 */     resizePlayers.add(player);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerClickEdge(PlayerInteractEvent event) {
        /* 141 */     if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
            /*     */       return;
        /* 143 */     if (event.getClickedBlock() == null)
            /*     */       return;
        /* 145 */     final Player player = event.getPlayer();
        /* 146 */     if (resizeWaitingPlayers.contains(player))
            /*     */       return;
        /* 148 */     if (resizePlayers.contains(player)) {
            /* 149 */       firstEdgeResizePlayers.put(player, event.getClickedBlock().getLocation());
            /* 150 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Sehr gut! Klicke nun in die andere gegenüberliegende Ecke des Landes.");
            /* 151 */       resizeWaitingPlayers.add(player);
            /* 152 */       resizePlayers.remove(player);
            /* 153 */       resizeOnePlayers.add(player);
            /* 154 */       Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                    /*     */           {
                /*     */             public void run() {
                    /* 157 */               Goverment.resizeWaitingPlayers.remove(player);
                    /*     */             }
                /*     */           },  20L);
            /* 160 */     } else if (resizeOnePlayers.contains(player)) {
            /* 161 */       secondEdgeResizePlayers.put(player, event.getClickedBlock().getLocation());
            /* 162 */       Location first = firstEdgeResizePlayers.get(player);
            /* 163 */       Location second = secondEdgeResizePlayers.get(player);
            /* 164 */       Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            /* 165 */       boolean canSize = LandManager.canCreateLandSize(first
/* 166 */           .getX(), first
/* 167 */           .getZ(), second
/* 168 */           .getX(), second
/* 169 */           .getZ(), land
/* 170 */           .getMaxBlocks());
            /*     */
            /* 172 */       if (!canSize) {
                /* 173 */         resizeOnePlayers.remove(player);
                /* 174 */         resizePlayers.remove(player);
                /* 175 */         firstEdgeResizePlayers.remove(player);
                /* 176 */         secondEdgeResizePlayers.remove(player);
                /* 177 */         resizeWaitingPlayers.remove(player);
                /* 178 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Dein Land darf maximal §4" + land.getMaxBlocks() + " Blöcke §7groß sein und braucht mindestens eine Fläche von §c4 Blöcken§7. Vorgang abgebrochen.");
                /*     */         return;
                /*     */       }
            /* 181 */       boolean canPosition = LandManager.canCreateLandLocation(first
/* 182 */           .getX(), first
/* 183 */           .getZ(), second
/* 184 */           .getX(), second
/* 185 */           .getZ(),
                    /* 186 */           HeroCraft.getPlugin().getLandManager().getAllLands(), land
/* 187 */           .getName());
            /*     */
            /* 189 */       if (!canPosition) {
                /* 190 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Ein anderes Land würde deines überschneiden. Vorgang abgebrochen.");
                /* 191 */         resizeOnePlayers.remove(player);
                /* 192 */         resizePlayers.remove(player);
                /* 193 */         firstEdgeResizePlayers.remove(player);
                /* 194 */         secondEdgeResizePlayers.remove(player);
                /* 195 */         resizeWaitingPlayers.remove(player);
                /*     */         return;
                /*     */       }
            /* 198 */       land.changeSize(first
/* 199 */           .getBlock().getX(), first
/* 200 */           .getBlock().getZ(), second
/* 201 */           .getBlock().getX(), second
/* 202 */           .getBlock().getZ());
            /*     */
            /* 204 */       player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            /* 205 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Größe des Landes §ageändert§7!");
            /* 206 */       resizeOnePlayers.remove(player);
            /* 207 */       resizePlayers.remove(player);
            /* 208 */       firstEdgeResizePlayers.remove(player);
            /* 209 */       secondEdgeResizePlayers.remove(player);
            /* 210 */       resizeWaitingPlayers.remove(player);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   private static void makeMaxBlocksHigher(Player player, Land land) {
        /* 215 */     player.closeInventory();
        /* 216 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Deine maximale Landgröße beträgt momentan §e" + land.getMaxBlocks() + " Blöcke§7. Wieviele Blöcke soll sie neu betragen? §0(§e+ 1 Block = 10 Coins)");
        /* 217 */     player.sendMessage("§0(Schreibe eine Zahl von §7" + land.getMaxBlocks() + 1 + " - 20000.0§0 in den Chat)");
        /* 218 */     player.sendMessage("§cSneake zum abbrechen!");
        /* 219 */     landMaxBlockPlayers.add(player);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onMaxChatEvent(AsyncPlayerChatEvent event) {
        /* 224 */     Player player = event.getPlayer();
        /* 225 */     if (landMaxSubmitPlayers.containsKey(player) && event.getMessage().equalsIgnoreCase("bestätigen")) {
            /* 226 */       event.setCancelled(true);
            /* 227 */       Land land1 = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            /* 228 */       int i = ((Integer)landMaxSubmitPlayers.get(player)).intValue();
            /* 229 */       int j = (i - land1.getMaxBlocks()) * 10;
            /* 230 */       if (land1.getCoins() < j) {
                /* 231 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Hierzu hat dein Land nicht genug §cCoins§7. Vorgang abgebrochen!");
                /* 232 */         landMaxSubmitPlayers.remove(player);
                /*     */         return;
                /*     */       }
            /* 235 */       land1.setMaxBlocks(i);
            /* 236 */       land1.setCoins(land1.getCoins() - j);
            /* 237 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Du hast die maximale Landgröße §avergrößert§7.");
            /* 238 */       player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            /* 239 */       landMaxSubmitPlayers.remove(player);
            /*     */       return;
            /*     */     }
        /* 242 */     if (!landMaxBlockPlayers.contains(player))
            /*     */       return;
        /* 244 */     event.setCancelled(true);
        /* 245 */     int newSize = 0;
        /*     */     try {
            /* 247 */       newSize = Integer.parseInt(event.getMessage());
            /* 248 */     } catch (NumberFormatException e) {
            /* 249 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dies ist keine gültige Zahl. Probiere es erneut.");
            /*     */       return;
            /*     */     }
        /* 252 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 253 */     if (land.getMaxBlocks() >= newSize) {
            /* 254 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Die new Größe muss größer sein, als die, die dein Land bisher haben kann. Probiere es erneut!");
            /*     */       return;
            /*     */     }
        /* 257 */     if (newSize > 20000.0D) {
            /* 258 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Die maximale Größe beträgt §c20000.0 Blöcke§7. Probiere es erneut!");
            /*     */       return;
            /*     */     }
        /* 261 */     int price = (newSize - land.getMaxBlocks()) * 10;
        /* 262 */     landMaxBlockPlayers.remove(player);
        /* 263 */     landMaxSubmitPlayers.put(player, Integer.valueOf(newSize));
        /* 264 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Dieser Vorgang würde §e" + price + " Coins §7kosten. Schreibe §abestätigen§7, um ihn durchzuführen.");
        /* 265 */     player.sendMessage("§cSneake zum abbrechen!");
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onGovermentAdminAreaGUIClick(InventoryClickEvent event) {
        /* 270 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /* 272 */     Player player = (Player)event.getWhoClicked();
        /* 273 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_admin:"))
            /*     */       return;
        /* 275 */     event.setCancelled(true);
        /* 276 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /* 278 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /* 280 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 281 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 282 */     if (displayName.equalsIgnoreCase("§4§lLand löschen")) {
            /* 283 */       land.delete();
            /* 284 */       player.closeInventory();
            /* 285 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Land erfolgreich gelöscht.");
            /* 286 */     } else if (displayName.equalsIgnoreCase("§4§lMitglieder verwalten")) {
            /* 287 */       openMembersShowInventory(player, 1);
            /* 288 */     } else if (displayName.equalsIgnoreCase("§4§lCoins auszahlen")) {
            /* 289 */       player.closeInventory();
            /* 290 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Schreibe in den Chat wieviele Coins du auszahlen möchtest.");
            /* 291 */       player.sendMessage("§e§lAnyBlocks §7§l| §4Sneake zum abbrechen!");
            /* 292 */       payOutPlayers.add(player);
            /* 293 */     } else if (displayName.equalsIgnoreCase("§4§lSpawnpoint setzen")) {
            /* 294 */       if (event.getAction() == InventoryAction.PICKUP_HALF) {
                /* 295 */         player.closeInventory();
                /* 296 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Sneake an der Stelle, wo der neue Spawnpoint sein soll.");
                /* 297 */         setLandPlayers.add(player);
                /*     */       } else {
                /* 299 */         player.closeInventory();
                /* 300 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Sneake an der Stelle, wo der neue Gefängnis-Spawnpoint sein soll.");
                /* 301 */         setPrisonSpawnPlayers.add(player);
                /*     */       }
            /*     */
            /* 304 */     } else if (displayName.equalsIgnoreCase("§4§lLand Rollen verwalten")) {
            /* 305 */       (HeroCraft.getPlugin()).landRoleManager.openManageLandRolesInventory(player);
            /* 306 */     } else if (displayName.equalsIgnoreCase("§4§lGröße ändern")) {
            /* 307 */       if (event.getAction() == InventoryAction.PICKUP_HALF) {
                /* 308 */         makeMaxBlocksHigher(player, land);
                /*     */       } else {
                /* 310 */         resizeLand(player);
                /*     */       }
            /* 312 */     } else if (displayName.equalsIgnoreCase("§4§lStadt gründen")) {
            /* 313 */       if (land.getCoins() >= 600.0D) {
                /* 314 */         land.setCoins(land.getCoins() - 600.0D);
                /* 315 */       } else if ((HeroCraft.getPlugin()).coin.getCoins(player) >= 600.0D) {
                /* 316 */         (HeroCraft.getPlugin()).coin.removeMoney(player, 600.0D);
                /*     */       } else {
                /* 318 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Dein Land hat nicht genug Coins, und du hast nicht genug Coins, um die Kosten zu übernehmen.");
                /*     */         return;
                /*     */       }
            /* 321 */       ItemStack goverment = null;
            /* 322 */       for (CustomStack i : ItemsAdder.getAllItems()) {
                /* 323 */         if (i.getDisplayName().equalsIgnoreCase("§4§lStadt")) {
                    /* 324 */           goverment = i.getItemStack();
                    /*     */         }
                /*     */       }
            /* 327 */       player.getInventory().addItem(new ItemStack[] { goverment });
            /* 328 */       player.closeInventory();
            /* 329 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Erstelle eine Stadt, indem du den Stadt Block §ain deinem Land §7platzierst.");
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerSneak(PlayerToggleSneakEvent event) {
        /* 335 */     Player player = event.getPlayer();
        /* 336 */     if (landMaxBlockPlayers.contains(player)) {
            /* 337 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen");
            /* 338 */       landMaxBlockPlayers.remove(player);
            /*     */     }
        /* 340 */     if (landMaxSubmitPlayers.containsKey(player)) {
            /* 341 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen");
            /* 342 */       landMaxSubmitPlayers.remove(player);
            /*     */     }
        /* 344 */     if (resizePlayers.contains(player) || resizeOnePlayers.contains(player)) {
            /* 345 */       resizeOnePlayers.remove(player);
            /* 346 */       resizePlayers.remove(player);
            /* 347 */       firstEdgeResizePlayers.remove(player);
            /* 348 */       secondEdgeResizePlayers.remove(player);
            /* 349 */       resizeWaitingPlayers.remove(player);
            /* 350 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen");
            /*     */     }
        /* 352 */     if (setPrisonSpawnPlayers.contains(player)) {
            /* 353 */       Land land1 = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            /* 354 */       if (!LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands()).getName().equalsIgnoreCase(land1.getName())) {
                /* 355 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Sneake in deinem Land!");
                /*     */         return;
                /*     */       }
            /* 358 */       land1.setPrisonSpawnPoint(player.getLocation());
            /* 359 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Gefängnis-Spawnpoint gesetzt.");
            /* 360 */       setPrisonSpawnPlayers.remove(player);
            /*     */     }
        /* 362 */     if (!setLandPlayers.contains(player))
            /*     */       return;
        /* 364 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 365 */     if (!LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands()).getName().equalsIgnoreCase(land.getName())) {
            /* 366 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Sneake in deinem Land!");
            /*     */       return;
            /*     */     }
        /* 369 */     land.setSpawnPoint(player.getLocation());
        /* 370 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Spawnpoint gesetzt.");
        /* 371 */     setLandPlayers.remove(player);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onGovermentGUIClick(InventoryClickEvent event) {
        /* 376 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /* 378 */     Player player = (Player)event.getWhoClicked();
        /* 379 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment:"))
            /*     */       return;
        /* 381 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /* 383 */     event.setCancelled(true);
        /* 384 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /* 386 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 387 */     if (displayName.equalsIgnoreCase("§4§lMitglieder")) {
            /* 388 */       openMembersShowInventory(player, 1);
            /* 389 */     } else if (displayName.equalsIgnoreCase("§4§lCoins einzahlen")) {
            /* 390 */       player.closeInventory();
            /* 391 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Gebe die anzahl an Coins ein, die du einzahlen möchtest.");
            /* 392 */       player.sendMessage("§4Sneaken zum abbrechen!");
            /* 393 */       payInPlayers.add(player);
            /* 394 */     } else if (displayName.equalsIgnoreCase("§4§lAdmin Menu")) {
            /* 395 */       Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            /*     */
            /* 397 */       ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, land);
            /* 398 */       if (perms != null &&
                    /* 399 */         perms.contains(LandPermission.ADMIN_ACCESS)) {
                /* 400 */         openGovermentAdminGUI(player, HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player));
                /*     */
                /*     */         return;
                /*     */       }
            /*     */
            /* 405 */       if (HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).isModerator(player.getName()) || HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).isOwner(player.getName())) {
                /* 406 */         openGovermentAdminGUI(player, HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player));
                /*     */       } else {
                /* 408 */         player.closeInventory();
                /* 409 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Du darfst nicht auf diesen Bereich zugreifen.");
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   private static void openMembersShowInventory(Player player, int page) {
        /* 415 */     membersShowPagePlayers.put(player, Integer.valueOf(page));
        /* 416 */     Inventory inventory = Bukkit.createInventory(null, 45, ":offset_-16::goverment_member:");
        /* 417 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 418 */     ArrayList<String> allLandPlayers = new ArrayList<>();
        /* 419 */     allLandPlayers.add(land.getFounderName());
        /* 420 */     allLandPlayers.addAll(Arrays.asList(land.getCoFounderNames()));
        /* 421 */     allLandPlayers.addAll(Arrays.asList(land.getMemberNames()));
        /* 422 */     ArrayList<String> pageNames = filterByPage(allLandPlayers, page, 36);
        /* 423 */     ItemStack back = null;
        /* 424 */     ItemStack next = null;
        /* 425 */     ItemStack invite = null;
        /* 426 */     for (CustomStack i : ItemsAdder.getAllItems()) {
            /* 427 */       if (i.getDisplayName().contains("§f< Back")) {
                /* 428 */         back = i.getItemStack(); continue;
                /* 429 */       }  if (i.getDisplayName().contains("§fNext >")) {
                /* 430 */         next = i.getItemStack(); continue;
                /* 431 */       }  if (i.getDisplayName().contains("§fSearch")) {
                /* 432 */         invite = i.getItemStack();
                /*     */       }
            /*     */     }
        /* 435 */     ItemMeta itemMeta = back.getItemMeta();
        /* 436 */     itemMeta.setDisplayName("§4§lVorherige Seite");
        /* 437 */     back.setItemMeta(itemMeta);
        /* 438 */     ItemMeta itemMeta1 = next.getItemMeta();
        /* 439 */     itemMeta1.setDisplayName("§4§lNächste Seite");
        /* 440 */     next.setItemMeta(itemMeta1);
        /* 441 */     ItemMeta itemMeta2 = invite.getItemMeta();
        /* 442 */     itemMeta2.setDisplayName("§4§lMitglied einladen");
        /* 443 */     invite.setItemMeta(itemMeta2);
        /* 444 */     for (String currentName : pageNames) {
            /* 445 */       if (currentName.equalsIgnoreCase(""))
                /*     */         continue;
            /* 447 */       ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            /* 448 */       SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
            /* 449 */       if (land.isOwner(currentName)) {
                /* 450 */         skullMeta.setDisplayName("§4" + currentName);
                /* 451 */       } else if (land.isModerator(currentName)) {
                /* 452 */         skullMeta.setDisplayName("§b" + currentName);
                /*     */       } else {
                /* 454 */         skullMeta.setDisplayName("§7" + currentName);
                /*     */       }
            /* 456 */       skullMeta.setOwner(currentName);
            /* 457 */       itemStack.setItemMeta((ItemMeta)skullMeta);
            /* 458 */       inventory.addItem(new ItemStack[] { itemStack });
            /*     */     }
        /*     */
        /* 461 */     if (land.isOwner(player.getName()) || land.isModerator(player.getName()))
            /* 462 */       inventory.setItem(40, invite);
        /* 463 */     if (page != 1);
        /*     */
        /*     */
        /* 466 */     player.openInventory(inventory);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onMembersInventoryClose(InventoryCloseEvent event) {
        /* 471 */     if (!(event.getPlayer() instanceof Player))
            /*     */       return;
        /* 473 */     final Player player = (Player)event.getPlayer();
        /* 474 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member:"))
            /*     */       return;
        /* 476 */     Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                /*     */         {
            /*     */           public void run() {
                /* 479 */             if (!player.getOpenInventory().getTitle().equalsIgnoreCase(":offset_-16::goverment_member:")) {
                    /* 480 */               Goverment.membersShowPagePlayers.remove(player);
                    /*     */             }
                /*     */           }
            /*     */         },  10L);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onMembersShowInventory(InventoryClickEvent event) {
        /* 488 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /* 490 */     Player player = (Player)event.getWhoClicked();
        /* 491 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member:"))
            /*     */       return;
        /* 493 */     event.setCancelled(true);
        /* 494 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /* 496 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /* 498 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 499 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 500 */     if (displayName.equalsIgnoreCase("§4§lVorherige Seite")) {
            /* 501 */       openMembersShowInventory(player, ((Integer)membersShowPagePlayers.get(player)).intValue() - 1); return;
            /*     */     }
        /* 503 */     if (displayName.equalsIgnoreCase("§4§lNächste Seite")) {
            /* 504 */       openMembersShowInventory(player, ((Integer)membersShowPagePlayers.get(player)).intValue() + 1); return;
            /*     */     }
        /* 506 */     if (displayName.equalsIgnoreCase("§4§lMitglied einladen")) {
            /* 507 */       if (!player.hasPermission("herowars.cc") &&
                    /* 508 */         land.getAllLandNames().size() >= 10) {
                /* 509 */         player.closeInventory();
                /* 510 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Dein Land darf maximal 10 Mitglieder haben.");
                /*     */
                /*     */         return;
                /*     */       }
            /* 514 */       inviterPlayers.add(player);
            /* 515 */       player.closeInventory();
            /* 516 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Gebe den Spielernamen ein, den du in dein Land einladen möchtest.");
            /* 517 */       player.sendMessage("§4Sneaken zum abbrechen!");
            /*     */       return;
            /*     */     }
        /* 520 */     if (!land.isOwner(player.getName()) && !land.isModerator(player.getName()))
            /*     */       return;
        /* 522 */     String currentPlayerName = displayName.substring(2);
        /* 523 */     openMemberDetailAdminPage(player, currentPlayerName);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onInviteBreak(PlayerToggleSneakEvent event) {
        /* 528 */     if (inviterPlayers.contains(event.getPlayer())) {
            /* 529 */       inviterPlayers.remove(event.getPlayer());
            /* 530 */       event.getPlayer().sendMessage("§4Vorgang abgebrochen!");
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onInviterChatEvent(AsyncPlayerChatEvent event) {
        /* 536 */     Player player = event.getPlayer();
        /* 537 */     if (!inviterPlayers.contains(player))
            /*     */       return;
        /* 539 */     event.setCancelled(true);
        /* 540 */     Player target = Bukkit.getPlayer(event.getMessage());
        /* 541 */     if (target == null) {
            /* 542 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen! Der Spieler ist nicht online.");
            /* 543 */       inviterPlayers.remove(player);
            /*     */       return;
            /*     */     }
        /* 546 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 547 */     if (land.canBuild(target)) {
            /* 548 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen! Dieser Spieler ist bereits Mitglied deines Landes.");
            /* 549 */       inviterPlayers.remove(player);
            /*     */       return;
            /*     */     }
        /* 552 */     if (invitedPlayers.containsKey(target)) {
            /* 553 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen! Dieser Spieler hat bereits eine Einladung erhalten.");
            /* 554 */       inviterPlayers.remove(player);
            /*     */       return;
            /*     */     }
        /* 557 */     if (HeroCraft.getPlugin().getLandManager().getLandFromPlayer(target) != null) {
            /* 558 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen! Dieser Spieler ist bereits Mitglied eines Landes.");
            /* 559 */       inviterPlayers.remove(player);
            /*     */       return;
            /*     */     }
        /* 562 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Der Spieler wurde erfolgreich §aeingeladen§7.");
        /* 563 */     inviterPlayers.remove(player);
        /* 564 */     invitedPlayers.put(target, land);
        /* 565 */     target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
        /* 566 */     target.sendMessage("§e§lAnyBlocks §7§l| §7Du wurdest vom Land §e§l" + land.getName() + "§7 eingeladen.");
        /* 567 */     target.sendMessage("§7benutze §a/landeinladungannehmen §7oder §c/landeinladungablehnen");
        /*     */   }
    /*     */
    /*     */   public static void openMemberDetailAdminPage(Player player, String currentPlayer) {
        /* 571 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 572 */     Inventory inventory = Bukkit.createInventory(null, 45, ":offset_-16::goverment_member_detail:");
        /* 573 */     ItemStack currentPlayerItem = new ItemStack(Material.PLAYER_HEAD);
        /* 574 */     SkullMeta skullMeta = (SkullMeta)currentPlayerItem.getItemMeta();
        /* 575 */     skullMeta.setOwner(currentPlayer);
        /* 576 */     if (land.isOwner(currentPlayer)) {
            /* 577 */       skullMeta.setDisplayName("§4" + currentPlayer);
            /* 578 */     } else if (land.isModerator(currentPlayer)) {
            /* 579 */       skullMeta.setDisplayName("§b" + currentPlayer);
            /*     */     } else {
            /* 581 */       skullMeta.setDisplayName("§7" + currentPlayer);
            /*     */     }
        /* 583 */     currentPlayerItem.setItemMeta((ItemMeta)skullMeta);
        /* 584 */     inventory.setItem(13, currentPlayerItem);
        /* 585 */     inventory.setItem(28, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglied kicken").build());
        /* 586 */     inventory.setItem(29, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglied kicken").build());
        /* 587 */     inventory.setItem(30, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglied kicken").build());
        /* 588 */     inventory.setItem(32, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglied Befördern/Degradieren").build());
        /* 589 */     inventory.setItem(33, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglied Befördern/Degradieren").build());
        /* 590 */     inventory.setItem(34, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lMitglied Befördern/Degradieren").build());
        /* 591 */     inventory.setItem(39, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lRolle ändern").build());
        /* 592 */     inventory.setItem(40, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lRolle ändern").build());
        /* 593 */     inventory.setItem(41, (new ItemBuilder(Material.STONE_AXE)).setCustomModelData(1000).setDisplayName("§4§lRolle ändern").build());
        /* 594 */     player.openInventory(inventory);
        /* 595 */     memberAdminDetailPagePlayers.put(player, currentPlayer);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onMemberDetailAdminPageClick(InventoryClickEvent event) {
        /* 600 */     if (!(event.getWhoClicked() instanceof Player))
            /*     */       return;
        /* 602 */     Player player = (Player)event.getWhoClicked();
        /* 603 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member_detail:"))
            /*     */       return;
        /* 605 */     if (event.getCurrentItem() == null)
            /*     */       return;
        /* 607 */     event.setCancelled(true);
        /* 608 */     if (!event.getCurrentItem().hasItemMeta())
            /*     */       return;
        /* 610 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 611 */     String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        /* 612 */     if (displayName.equalsIgnoreCase("§4§lMitglied kicken")) {
            /* 613 */       land.removeMember(memberAdminDetailPagePlayers.get(player));
            /* 614 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Mitglied gekickt.");
            /* 615 */       player.closeInventory();
            /* 616 */     } else if (displayName.equalsIgnoreCase("§4§lMitglied Befördern/Degradieren")) {
            /* 617 */       String targetName = memberAdminDetailPagePlayers.get(player);
            /* 618 */       if (land.isModerator(targetName)) {
                /* 619 */         land.degradePlayer(targetName);
                /* 620 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Spieler degradiert.");
                /* 621 */         player.closeInventory();
                /*     */       } else {
                /* 623 */         land.promotePlayer(targetName);
                /* 624 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Spieler befördert.");
                /* 625 */         player.closeInventory();
                /*     */       }
            /* 627 */     } else if (displayName.equalsIgnoreCase("§4§lRolle ändern")) {
            /* 628 */       Inventory inventory = Bukkit.createInventory(null, 45, "§4§lBenutzer Rollen verwalten");
            /* 629 */       ArrayList<LandRole> landRoles = (HeroCraft.getPlugin()).landRoleManager.getRolesFromLand(land);
            /* 630 */       Player target = Bukkit.getPlayer(memberAdminDetailPagePlayers.get(player));
            /* 631 */       for (LandRole i : landRoles) {
                /* 632 */         if (target != null && i.isPlayerMember(target)) {
                    /* 633 */           inventory.addItem(new ItemStack[] { (new ItemBuilder(Material.GRAY_DYE)).setDisplayName(i.getName()).setLore(new String[] { "", "§a§lSpieler ist in der Rolle", "", "§7(Klicke zum entfernen)" }).build() }); continue;
                    /*     */         }
                /* 635 */         inventory.addItem(new ItemStack[] { (new ItemBuilder(Material.CYAN_DYE)).setDisplayName(i.getName()).setLore(new String[] { "", "§c§lSpieler ist nicht in der Rolle", "", "§7(Klicke zum hinzufügen)" }).build() });
                /*     */       }
            /*     */
            /* 638 */       (HeroCraft.getPlugin()).landRoleManager.rolePlayerEditPlayers.put(player, memberAdminDetailPagePlayers.get(player));
            /* 639 */       player.openInventory(inventory);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onMemberAdminDetailPageClose(InventoryCloseEvent event) {
        /* 645 */     if (!(event.getPlayer() instanceof Player))
            /*     */       return;
        /* 647 */     Player player = (Player)event.getPlayer();
        /* 648 */     if (!event.getView().getTitle().equalsIgnoreCase(":offset_-16::goverment_member_detail:"))
            /*     */       return;
        /* 650 */     memberAdminDetailPagePlayers.remove(player);
        /*     */   }
    /*     */
    /*     */   public static ArrayList<String> filterByPage(ArrayList<String> originalList, int page, int pageSize) {
        /* 654 */     ArrayList<String> resultList = new ArrayList<>();
        /*     */
        /* 656 */     int startIndex = (page - 1) * pageSize;
        /* 657 */     int endIndex = Math.min(startIndex + pageSize, originalList.size());
        /*     */
        /* 659 */     for (int i = startIndex; i < endIndex; i++) {
            /* 660 */       resultList.add(originalList.get(i));
            /*     */     }
        /*     */
        /* 663 */     return resultList;
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerPayInBreak(PlayerToggleSneakEvent event) {
        /* 668 */     if (payInPlayers.contains(event.getPlayer())) {
            /* 669 */       payInPlayers.remove(event.getPlayer());
            /* 670 */       event.getPlayer().sendMessage("§4§lVorgang abgebrochen!");
            /* 671 */     } else if (payOutPlayers.contains(event.getPlayer())) {
            /* 672 */       payOutPlayers.remove(event.getPlayer());
            /* 673 */       event.getPlayer().sendMessage("§4§lVorgang abgebrochen!");
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPayInPlayersChat(AsyncPlayerChatEvent event) {
        /* 679 */     final Player player = event.getPlayer();
        /* 680 */     if (payOutPlayers.contains(event.getPlayer())) {
            /* 681 */       event.setCancelled(true);
            /* 682 */       double d1 = 0.0D;
            /*     */       try {
                /* 684 */         d1 = Double.parseDouble(event.getMessage());
                /* 685 */       } catch (NumberFormatException e) {
                /* 686 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
                /*     */         return;
                /*     */       }
            /* 689 */       if (d1 <= 0.0D) {
                /* 690 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
                /*     */         return;
                /*     */       }
            /* 693 */       Land land1 = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
            /* 694 */       if (land1.getCoins() < d1) {
                /* 695 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Dazu hat das Land nicht genügend Coins. Bitte versuche es erneut.");
                /*     */         return;
                /*     */       }
            /* 698 */       payOutPlayers.remove(player);
            /* 699 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Coins erfolgreich ausgezahlt.");
            /* 700 */       land1.setCoins(land1.getCoins() - d1);
            /* 701 */       final double finalAmount = d1;
            /* 702 */       Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                    /*     */           {
                /*     */             public void run() {
                    /* 705 */               (HeroCraft.getPlugin()).coin.addMoney(player, finalAmount);
                    /*     */             }
                /*     */           }, 10L);
            /*     */       return;
            /*     */     }
        /* 710 */     if (!payInPlayers.contains(event.getPlayer()))
            /*     */       return;
        /* 712 */     event.setCancelled(true);
        /* 713 */     double amount = 0.0D;
        /*     */     try {
            /* 715 */       amount = Double.parseDouble(event.getMessage());
            /* 716 */     } catch (NumberFormatException e) {
            /* 717 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
            /*     */       return;
            /*     */     }
        /* 720 */     if (amount <= 0.0D) {
            /* 721 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dies ist keine gültige Zahl. Bitte versuche es erneut.");
            /*     */       return;
            /*     */     }
        /* 724 */     if ((HeroCraft.getPlugin()).coin.getCoins(player) < amount) {
            /* 725 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dazu hast du nicht genügend Coins. Bitte versuche es erneut.");
            /*     */       return;
            /*     */     }
        /* 728 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 729 */     land.setCoins(land.getCoins() + amount);
        /* 730 */     HeroCraft.getPlugin().getLandManager().saveLand(land);
        /* 731 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Coins erfolgreich eingezahlt.");
        /* 732 */     payInPlayers.remove(player);
        /* 733 */     final double finalAmount = amount;
        /* 734 */     Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                /*     */         {
            /*     */           public void run() {
                /* 737 */             (HeroCraft.getPlugin()).coin.removeMoney(player, finalAmount);
                /*     */           }
            /*     */         }, 10L);
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\Goverment.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */