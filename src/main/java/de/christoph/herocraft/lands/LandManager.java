/*     */ package de.christoph.herocraft.lands;
/*     */
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import de.christoph.herocraft.lands.province.Province;
/*     */ import de.christoph.herocraft.lands.roles.LandPermission;
/*     */ import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
/*     */ import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ import net.md_5.bungee.api.chat.BaseComponent;
/*     */ import net.md_5.bungee.api.chat.ClickEvent;
/*     */ import net.md_5.bungee.api.chat.TextComponent;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.ArmorStand;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.ItemFrame;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Projectile;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.Action;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.block.BlockPlaceEvent;
/*     */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*     */ import org.bukkit.event.hanging.HangingBreakByEntityEvent;
/*     */ import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.event.player.PlayerMoveEvent;
/*     */ import org.bukkit.entity.ArmorStand;
/*     */ import org.bukkit.entity.ItemFrame;
/*     */
/*     */ public class LandManager
        /*     */   implements Listener
        /*     */ {
    /*     */   private ArrayList<Land> allLands;
    /*     */   private Map<Player, Land> playerLandCache;
    /*  40 */   private final Map<String, Land> landChunkCache = new HashMap<>();
    /*     */
    /*     */   public LandManager() {
        /*  43 */     this.allLands = new ArrayList<>();
        /*  44 */     this.playerLandCache = new HashMap<>();
        /*  45 */     loadSavedLands();
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFastGovermentLandCreationPlaced(FurniturePlaceSuccessEvent event) {
        /*  50 */     Player player = event.getPlayer();
        /*  51 */     if (!event.getFurniture().getItemStack().hasItemMeta())
            /*     */       return;
        /*  53 */     if (!event.getFurniture().getItemStack().getItemMeta().hasDisplayName())
            /*     */       return;
        /*  55 */     if (!event.getFurniture().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lLand erstellen §0(Item platzieren)"))
            /*     */       return;
        /*  57 */     if (HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player) != null) {
            /*  58 */       if (!HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).isInLand(player)) {
                /*  59 */         event.getFurniture().remove(true);
                /*  60 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Platziere das Gebäude in deinem Land.");
                /*     */       }
            /*     */       return;
            /*     */     }
        /*  64 */     double x = event.getFurniture().getEntity().getLocation().getX();
        /*  65 */     double z = event.getFurniture().getEntity().getLocation().getZ();
        /*  66 */     double x1 = x + 50.0D;
        /*  67 */     double z1 = z + 50.0D;
        /*  68 */     double x2 = x - 50.0D;
        /*  69 */     double z2 = z - 50.0D;
        /*  70 */     if (!canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
            /*  71 */       event.getFurniture().remove(true);
            /*  72 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dein Land ist zu nahe an einem anderen Land oder am Spawn.");
            /*  73 */       TextComponent textComponent = new TextComponent("§a§l(RandomTP)");
            /*  74 */       textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp 1288"));
            /*  75 */       player.spigot().sendMessage((BaseComponent)textComponent);
            /*     */       return;
            /*     */     }
        /*  78 */     if (!canCreateLandProvinceLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), "world", "", "")) {
            /*  79 */       event.getFurniture().remove(true);
            /*  80 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dein Land ist zu nahe an einer Stadt oder am Spawn.");
            /*  81 */       TextComponent textComponent = new TextComponent("§a§l(RandomTP)");
            /*  82 */       textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp 1288"));
            /*  83 */       player.spigot().sendMessage((BaseComponent)textComponent);
            /*     */       return;
            /*     */     }
        /*  86 */     String name = player.getName() + "Land";
        /*  87 */     double y = Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x, 1.0D, z));
        /*  88 */     if (Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), x, y, z)).isLiquid()) {
            /*     */       return;
            /*     */     }
        /*     */
        /*     */
        /*     */
        /*  94 */     Land land = new Land(name, player.getUniqueId().toString(), player.getName(), new String[] { "" }, new String[] { "" }, new String[] { "" }, new String[] { "" }, x1, z1, x2, z2, x, y, z, 0.0D, 4500, new String[] { "" }, new String[] { "" }, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 117 */     HeroCraft.getPlugin().getLandManager().getAllLands().add(land);
        /* 118 */     HeroCraft.getPlugin().getLandManager().saveLand(land);
        /* 119 */     HeroCraft.getPlugin().getStatisticsManager().markLandCreated(player.getUniqueId());
        /* 119 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Sehr gut, du besitzt nun dein eigenes Land!");
        /*     */   }
    /*     */
    /*     */   public void removeLandFromIndex(Land land) {
        /* 123 */     this.landChunkCache.entrySet().removeIf(entry -> ((Land)entry.getValue()).equals(land));
        /*     */   }
    /*     */
    /*     */   public void loadSavedLands() {
        /*     */     try {
            /* 128 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `lands`");
            /* 129 */       ResultSet resultSet = preparedStatement.executeQuery();
            /*     */
            /*     */
            /* 132 */       this.allLands.clear();
            /* 133 */       this.landChunkCache.clear();
            /*     */
            /* 135 */       while (resultSet.next()) {
                /* 136 */         String semiTrustedArray[], semiTrusted = resultSet.getString("spawnYaw");
                /*     */
                /* 138 */         if (semiTrusted == null) {
                    /* 139 */           semiTrustedArray = new String[] { "" };
                    /*     */         } else {
                    /* 141 */           semiTrustedArray = semiTrusted.split(",");
                    /*     */         }

                /* 169 */         Land loadedLand = new Land(resultSet.getString("name"), resultSet.getString("founderUUID"), resultSet.getString("founderName"), resultSet.getString("coFounderUUIDs").split(","), resultSet.getString("coFounderNames").split(","), resultSet.getString("memberUUIDs").split(","), resultSet.getString("memberNames").split(","), resultSet.getDouble("x1"), resultSet.getDouble("z1"), resultSet.getDouble("x2"), resultSet.getDouble("z2"), resultSet.getDouble("spawnX"), resultSet.getDouble("spawnY"), resultSet.getDouble("spawnZ"), resultSet.getDouble("coins"), resultSet.getInt("max_blocks"), resultSet.getString("trusted").split(","), semiTrustedArray, resultSet.getDouble("spawnYaw"), resultSet.getDouble("spawnPitch"), resultSet.getDouble("armee_coins"), resultSet.getDouble("prison_spawn_x"), resultSet.getDouble("prison_spawn_z"), resultSet.getDouble("prison_spawn_Y"));
                /*     */
                /*     */
                /*     */
                /* 173 */         this.allLands.add(loadedLand);
                /* 174 */         indexLand(loadedLand);
                /*     */       }
            /*     */
            /* 177 */       Bukkit.getLogger().info("[HeroCraft] " + this.allLands.size() + " Lands geladen und indexiert.");
            /*     */     }
        /* 179 */     catch (SQLException e) {
            /* 180 */       e.printStackTrace();
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   public void scanForLand(Player player) {
        /* 186 */     Land land = getLandAtLocation(player.getLocation(), this.allLands);
        /* 187 */     if (land == null) {
            /* 188 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Hier gibt es kein §cLand§7.");
            /*     */       return;
            /*     */     }
        /* 191 */     land.showLandBorder(player);
        /* 192 */     player.sendMessage("");
        /* 193 */     player.sendMessage("");
        /* 194 */     player.sendMessage("");
        /* 195 */     player.sendMessage("§7-- §e§l" + land.getName() + " §7--");
        /* 196 */     player.sendMessage("");
        /* 197 */     player.sendMessage("§7Gründer: §e" + land.getFounderName());
        /* 198 */     String moderators = "";
        /* 199 */     if ((land.getCoFounderUUIDs()).length != 0) {
            /* 200 */       for (String i : land.getCoFounderNames())
                /* 201 */         moderators = moderators + moderators + ", ";
            /*     */     } else {
            /* 203 */       moderators = "Keine";
            /* 204 */     }  if (moderators.equals(", ")) {
            /* 205 */       moderators = "Keine";
            /*     */     }
        /* 207 */     player.sendMessage("§7Moderatoren: §e" + moderators);
        /* 208 */     player.sendMessage("");
        /* 209 */     String members = "";
        /* 210 */     if ((land.getMemberUUIDs()).length != 0) {
            /* 211 */       for (String i : land.getMemberUUIDs())
                /* 212 */         members = members + members + ", ";
            /*     */     } else {
            /* 214 */       members = "Keine";
            /* 215 */     }  if (members.equals(", ")) {
            /* 216 */       members = "Keine";
            /*     */     }
        /* 218 */     player.sendMessage("§7Mitglieder: §e" + members);
        /* 219 */     player.sendMessage("");
        /*     */   }
    /*     */
    /*     */   public void saveLand(Land land) {
        /*     */     try {
            /* 224 */       boolean exists = hasOwnLand(land.getFounderUUID());
            /*     */
            /* 226 */       if (exists) {
                /*     */
                /* 228 */         Land existing = getLandByName(land.getName());
                /* 229 */         if (existing != null && (existing
/* 230 */           .getX1() != land.getX1() || existing
/* 231 */           .getZ1() != land.getZ1() || existing
/* 232 */           .getX2() != land.getX2() || existing
/* 233 */           .getZ2() != land.getZ2())) {
                    /*     */
                    /*     */
                    /* 236 */           removeLandFromIndex(existing);
                    /* 237 */           indexLand(land);
                    /*     */         }
                /*     */
                /* 240 */         PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `lands` WHERE `founderUUID` = ?");
                /* 241 */         preparedStatement.setString(1, land.getFounderUUID());
                /* 242 */         preparedStatement.execute();
                /*     */       } else {
                /*     */
                /* 245 */         indexLand(land);
                /*     */       }
            /*     */
            /* 248 */       PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `lands`(`name`,`founderUUID`,`founderName`,`coFounderUUIDs`,`coFounderNames`,`memberUUIDs`,`memberNames`,`x1`,`z1`,`x2`,`z2`,`spawnX`,`spawnY`,`spawnZ`,`coins`,`max_blocks`,`trusted`, `semi_trusted`, `spawnYaw`, `spawnPitch`, `prison_spawn_x`, `prison_spawn_z`, `prison_spawn_y`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            /*     */
            /*     */
            /*     */
            /* 252 */       preparedStatement1.setString(1, land.getName());
            /* 253 */       preparedStatement1.setString(2, land.getFounderUUID());
            /* 254 */       preparedStatement1.setString(3, land.getFounderName());
            /* 255 */       preparedStatement1.setString(4, arrayToString(land.getCoFounderUUIDs(), ","));
            /* 256 */       preparedStatement1.setString(5, arrayToString(land.getCoFounderNames(), ","));
            /* 257 */       preparedStatement1.setString(6, arrayToString(land.getMemberUUIDs(), ","));
            /* 258 */       preparedStatement1.setString(7, arrayToString(land.getMemberNames(), ","));
            /* 259 */       preparedStatement1.setDouble(8, land.getX1());
            /* 260 */       preparedStatement1.setDouble(9, land.getZ1());
            /* 261 */       preparedStatement1.setDouble(10, land.getX2());
            /* 262 */       preparedStatement1.setDouble(11, land.getZ2());
            /* 263 */       preparedStatement1.setDouble(12, land.getSpawnX());
            /* 264 */       preparedStatement1.setDouble(13, land.getSpawnY());
            /* 265 */       preparedStatement1.setDouble(14, land.getSpawnZ());
            /* 266 */       preparedStatement1.setDouble(15, land.getCoins());
            /* 267 */       preparedStatement1.setInt(16, land.getMaxBlocks());
            /* 268 */       preparedStatement1.setString(17, arrayToString(land.getTrusted(), ","));
            /* 269 */       preparedStatement1.setString(18, arrayToString(land.getSemiTrusted(), ","));
            /* 270 */       preparedStatement1.setDouble(19, land.getSpawnYaw());
            /* 271 */       preparedStatement1.setDouble(20, land.getSpawnPitch());
            /* 272 */       preparedStatement1.setDouble(21, land.getPrisonSpawnX());
            /* 273 */       preparedStatement1.setDouble(22, land.getPrisonSpawnZ());
            /* 274 */       preparedStatement1.setDouble(23, land.getPrisonSpawnY());
            /* 275 */       preparedStatement1.execute();
            /*     */     }
        /* 277 */     catch (SQLException e) {
            /* 278 */       e.printStackTrace();
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        /* 285 */     Entity damager = event.getDamager();
        /* 286 */     Entity damaged = event.getEntity();
        /*     */
        /* 288 */     Land land = getLandAtLocation(damaged.getLocation(), this.allLands);
        /* 289 */     if (land == null) {
            /*     */       return;
            /*     */     }
        /*     */
        /* 293 */     if (damager instanceof Player && damaged instanceof Player) {
            /* 294 */       Player attacker = (Player)damager;
            /* 295 */       Player victim = (Player)damaged;
            /* 296 */       if (!land.canBuild(attacker)) {
                /* 297 */         event.setCancelled(true);
                /*     */       } else {
                /* 299 */         ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, land);
                /* 300 */         if (perms == null)
                    /*     */           return;
                /* 302 */         if (!perms.contains(LandPermission.PVP)) {
                    /* 303 */           event.setCancelled(true);
                    /*     */         }
                /*     */       }
            /*     */
            /*     */       return;
            /*     */     }
        /*     */
        /* 310 */     if (damager instanceof Player) {
            /* 311 */       Player attacker = (Player)damager;
            /*     */
            /* 313 */       if (isFriendlyEntity(damaged)) {
                /* 314 */         if (!land.canBuild(attacker)) {
                    /* 315 */           event.setCancelled(true);
                    /*     */         } else {
                    /* 317 */           ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, land);
                    /* 318 */           if (perms == null)
                        /*     */             return;
                    /* 320 */           if (!perms.contains(LandPermission.PVE_FRIENDLY))
                        /*     */           {
                        /* 322 */             event.setCancelled(true);
                        /*     */           }
                    /*     */         }
                /*     */
                /*     */         return;
                /*     */       }
            /* 328 */       if (isHostileEntity(damaged)) {
                /* 329 */         if (!land.canBuild(attacker)) {
                    /* 330 */           if (land.isSemiTrusted(attacker)) {
                        /*     */             return;
                        /*     */           }
                    /* 333 */           event.setCancelled(true);
                    /*     */         } else {
                    /* 335 */           ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, land);
                    /* 336 */           if (perms == null)
                        /*     */             return;
                    /* 338 */           if (!perms.contains(LandPermission.PVE_UNFRIENDLY))
                        /*     */           {
                        /* 340 */             event.setCancelled(true);
                        /*     */           }
                    /*     */         }
                /*     */
                /*     */         return;
                /*     */       }
            /*     */     }
        /*     */
        /* 348 */     if (damager instanceof Projectile) {
            /* 349 */       Projectile projectile = (Projectile)damager;
            /*     */
            /* 351 */       if (projectile.getShooter() instanceof Player) {
                /* 352 */         Player attacker = (Player)projectile.getShooter();
                /*     */
                /*     */
                /* 355 */         if (damaged instanceof Player) {
                    /* 356 */           if (!land.canBuild(attacker)) {
                        /* 357 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 359 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, land);
                        /* 360 */             if (perms == null)
                            /*     */               return;
                        /* 362 */             if (!perms.contains(LandPermission.PVP))
                            /*     */             {
                            /* 364 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */
                    /*     */           return;
                    /*     */         }
                /*     */
                /* 371 */         if (isFriendlyEntity(damaged)) {
                    /* 372 */           if (!land.canBuild(attacker)) {
                        /* 373 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 375 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, land);
                        /* 376 */             if (perms == null)
                            /*     */               return;
                        /* 378 */             if (!perms.contains(LandPermission.PVE_FRIENDLY))
                            /*     */             {
                            /* 380 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */
                    /*     */           return;
                    /*     */         }
                /*     */
                /* 387 */         if (isHostileEntity(damaged)) {
                    /* 388 */           if (!land.canBuild(attacker)) {
                        /* 389 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 391 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, land);
                        /* 392 */             if (perms == null)
                            /*     */               return;
                        /* 394 */             if (!perms.contains(LandPermission.PVE_UNFRIENDLY))
                            /*     */             {
                            /* 396 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */           return;
                    /*     */         }
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private boolean isFriendlyEntity(Entity entity) {
        /* 407 */     EntityType type = entity.getType();
        /* 408 */     return (type == EntityType.VILLAGER || type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM || type == EntityType.WANDERING_TRADER || type == EntityType.ALLAY || type == EntityType.HORSE || type == EntityType.COW || type == EntityType.SHEEP || type == EntityType.PIG || type == EntityType.CHICKEN || type == EntityType.RABBIT || type == EntityType.WOLF || type == EntityType.CAT || type == EntityType.PARROT);
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   private boolean isHostileEntity(Entity entity) {
        /* 425 */     EntityType type = entity.getType();
        /* 426 */     return (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.CREEPER || type == EntityType.SPIDER || type == EntityType.ENDERMAN || type == EntityType.DROWNED || type == EntityType.PILLAGER || type == EntityType.WITCH || type == EntityType.BLAZE || type == EntityType.SLIME || type == EntityType.PHANTOM);
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onBlockBreak(BlockBreakEvent event) {
        /* 490 */     Player player = event.getPlayer();
        /* 491 */     if (player.hasPermission("herowars.build"))
            /*     */       return;
        /* 493 */     Land land = getLandAtLocation(event.getBlock().getLocation(), this.allLands);
        /* 494 */     if (land == null) {
            /*     */       return;
            /*     */     }
        /* 497 */     if (!land.canBuild(player)) {
            /* 498 */       event.setCancelled(true);
            /*     */     } else {
            /* 500 */       ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, land);
            /* 501 */       if (perms == null)
                /*     */         return;
            /* 503 */       if (!perms.contains(LandPermission.BREAK)) {
                /* 504 */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean isInOtherLand(Player player) {
        /* 511 */     Land land = getLandAtLocation(player.getLocation(), this.allLands);
        /* 512 */     if (land == null)
            /* 513 */       return false;
        /* 514 */     return !land.canBuild(player);
        /*     */   }
    /*     */
    /*     */   private boolean isDoor(Material type) {
        /* 518 */     return (type == Material.OAK_DOOR || type == Material.BIRCH_DOOR || type == Material.SPRUCE_DOOR || type == Material.JUNGLE_DOOR || type == Material.ACACIA_DOOR || type == Material.DARK_OAK_DOOR || type == Material.MANGROVE_DOOR || type == Material.CHERRY_DOOR || type == Material.CRIMSON_DOOR || type == Material.WARPED_DOOR);
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerInteract(PlayerInteractEvent event) {
        /* 532 */     Player player = event.getPlayer();
        /* 533 */     if (player.hasPermission("herowars.build"))
            /*     */       return;
        /* 535 */     Land land = getLandAtLocation(player.getLocation(), this.allLands);
        /* 536 */     if (land == null)
            /*     */       return;
        /* 538 */     if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            /* 539 */       Block clickedBlock = event.getClickedBlock();
            /* 540 */       if (clickedBlock != null) {
                /* 541 */         Material type = clickedBlock.getType();
                /* 542 */         if (type == Material.CHEST || type == Material.TRAPPED_CHEST) {
                    /* 543 */           if (!land.canBuild(player)) {
                        /* 544 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 546 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, land);
                        /* 547 */             if (perms == null)
                            /*     */               return;
                        /* 549 */             if (!perms.contains(LandPermission.OPEN_CHESTS))
                            /*     */             {
                            /* 551 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */
                    /*     */           return;
                    /*     */         }
                /* 557 */         if (isDoor(type)) {
                    /* 558 */           if (!land.canBuild(player)) {
                        /* 559 */             if (land.isSemiTrusted(player)) {
                            /*     */               return;
                            /*     */             }
                        /* 562 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 564 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, land);
                        /* 565 */             if (perms == null)
                            /*     */               return;
                        /* 567 */             if (!perms.contains(LandPermission.OPEN_DOOR))
                            /*     */             {
                            /* 569 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */         }
                /*     */       }
            /*     */     }
        /* 575 */     if (!land.canBuild(player)) {
            /* 576 */       if (event.getClickedBlock() == null) {
                /*     */         return;
                /*     */       }
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /* 588 */       event.setCancelled(true);
            /*     */     } else {
            /* 590 */       ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, land);
            /* 591 */       if (perms == null)
                /*     */         return;
            /* 593 */       if (!perms.contains(LandPermission.INTERACT)) {
                /* 594 */         if (land.isOwnerUUID(player.getUniqueId().toString())) {
                    /*     */           return;
                    /*     */         }
                /*     */
                /* 598 */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onBlockBreak(BlockPlaceEvent event) {
        /* 605 */     Player player = event.getPlayer();
        /* 606 */     if (player.hasPermission("herowars.build"))
            /*     */       return;
        /* 608 */     Land land = getLandAtLocation(event.getBlock().getLocation(), this.allLands);
        /* 609 */     if (land == null) {
            /*     */       return;
            /*     */     }
        /* 612 */     if (!land.canBuild(player)) {
            /* 613 */       event.setCancelled(true);
            /*     */     } else {
            /* 615 */       ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, land);
            /* 616 */       if (perms == null)
                /*     */         return;
            /* 618 */       if (!perms.contains(LandPermission.BUILD))
                /*     */       {
                /* 620 */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerMove(PlayerMoveEvent event) {
        /* 627 */     Player player = event.getPlayer();
        /* 628 */     Location to = event.getTo();
        /* 629 */     Location from = event.getFrom();
        /*     */
        /*     */
        /* 632 */     if (to == null || (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ())) {
            /*     */       return;
            /*     */     }
        /*     */
        /*     */
        /* 637 */     Land cachedLand = this.playerLandCache.get(player);
        /* 638 */     Land enteredLand = getLandAtLocation(to, this.allLands);
        /*     */
        /*     */
        /* 641 */     if ((cachedLand == null && enteredLand == null) || (cachedLand != null && enteredLand != null && cachedLand
/* 642 */       .equals(enteredLand))) {
            /*     */       return;
            /*     */     }
        /*     */
        /*     */
        /* 647 */     if (enteredLand != null && (cachedLand == null || !enteredLand.equals(cachedLand))) {
            /* 648 */       player.sendTitle("§a§l" + enteredLand.getName(), "§7Wurde betreten...", 5, 40, 5);
            /* 649 */       this.playerLandCache.put(player, enteredLand);
            /*     */
            /*     */       return;
            /*     */     }
        /* 653 */     if (cachedLand != null && enteredLand == null) {
            /* 654 */       player.sendTitle("§c§l" + cachedLand.getName(), "§7Wurde verlassen...", 5, 40, 5);
            /* 655 */       this.playerLandCache.remove(player);
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private void indexLand(Land land) {
        if (land == null) return;

        int minChunkX = (int) Math.floor(Math.min(land.getX1(), land.getX2()) / 16.0);
        int maxChunkX = (int) Math.floor(Math.max(land.getX1(), land.getX2()) / 16.0);
        int minChunkZ = (int) Math.floor(Math.min(land.getZ1(), land.getZ2()) / 16.0);
        int maxChunkZ = (int) Math.floor(Math.max(land.getZ1(), land.getZ2()) / 16.0);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                landChunkCache.put("world:" + cx + ":" + cz, land);
            }
        }
    }


    /*     */
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFurnitureBreak(FurnitureBreakEvent event) {
        /* 679 */     Player player = event.getPlayer();
        /* 680 */     Land land = getLandAtLocation(event.getFurniture().getEntity().getLocation(), this.allLands);
        /* 681 */     if (land == null)
            /*     */       return;
        /* 683 */     if (!land.canBuild(player))
            /* 684 */       event.setCancelled(true);
        /*     */   }
    /*     */


    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getRightClicked().getLocation();
        // Prüfe, ob die Location in einer Danger Zone ist
        if (!LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands()).canBuild(player)) {
            event.setCancelled(true);
            player.sendMessage("§cDu darfst hier keine Rüstungsständer bearbeiten!");
        }
    }

    @EventHandler
    public void onPlayerInteractFrameEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            Player player = event.getPlayer();
            Location loc = event.getRightClicked().getLocation();
            // Prüfe, ob die Location in einer Danger Zone ist
            if (!LandManager.getLandAtLocation(player.getLocation(), HeroCraft.getPlugin().getLandManager().getAllLands()).canBuild(player)) {
                event.setCancelled(true);
                player.sendMessage("§cDu darfst hier keine Item Frames bearbeiten!");
            }
        }
    }

    /*     */
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onHangingBreak(HangingBreakByEntityEvent event) {
        /*     */     if (!(event.getRemover() instanceof Player)) {
            /*     */       return;
            /*     */     }
        /*     */     Player player = (Player) event.getRemover();
        /*     */     if (player.hasPermission("herowars.build")) {
            /*     */       return;
            /*     */     }
        /*     */     if (event.getEntity() instanceof ItemFrame) {
            /*     */       Land land = getLandAtLocation(event.getEntity().getLocation(), this.allLands);
            /*     */       if (land == null) {
                /*     */         return;
                /*     */       }
            /*     */       if (!land.canBuild(player)) {
                /*     */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */

    /*     */   @EventHandler
    /*     */   public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        /*     */     Player player = event.getPlayer();
        /*     */     if (player.hasPermission("herowars.build")) {
            /*     */       return;
            /*     */     }
        /*     */     if (event.getRightClicked() instanceof ItemFrame || event.getRightClicked() instanceof ArmorStand) {
            /*     */       Land land = getLandAtLocation(event.getRightClicked().getLocation(), this.allLands);
            /*     */       if (land == null) {
                /*     */         return;
                /*     */       }
            /*     */       if (!land.canBuild(player)) {
                /*     */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @Nullable
    /*     */   public Land getLandFromPlayer(Player player) {
        /* 689 */     for (Land i : this.allLands) {
            /* 690 */       if (i.isInLand(player))
                /* 691 */         return i;
            /*     */     }
        /* 693 */     return null;
        /*     */   }
    /*     */
    /*     */   @Nullable
    /*     */   public Land getLandByName(String name) {
        /* 698 */     for (Land i : this.allLands) {
            /* 699 */       if (i.getName().equalsIgnoreCase(name))
                /* 700 */         return i;
            /*     */     }
        /* 702 */     return null;
        /*     */   }
    /*     */
    /*     */   public static String arrayToString(String[] array, String delimiter) {
        /* 706 */     StringBuilder result = new StringBuilder();
        /* 707 */     for (int i = 0; i < array.length; i++) {
            /* 708 */       result.append(array[i]);
            /* 709 */       if (i < array.length - 1) {
                /* 710 */         result.append(delimiter);
                /*     */       }
            /*     */     }
        /* 713 */     return result.toString();
        /*     */   }
    /*     */
    /*     */
    /*     */   public boolean hasOwnLand(String uuid) {
        /*     */     try {
            /* 719 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `lands` WHERE `founderUUID` = ?");
            /* 720 */       preparedStatement.setString(1, uuid);
            /* 721 */       ResultSet resultSet = preparedStatement.executeQuery();
            /* 722 */       if (resultSet.next())
                /* 723 */         return true;
            /* 724 */     } catch (SQLException e) {
            /* 725 */       throw new RuntimeException(e);
            /*     */     }
        /* 727 */     return false;
        /*     */   }
    /*     */
    /*     */   public static Land getLandAtLocation(Location location, ArrayList<Land> lands) {
                    if (location == null || location.getWorld() == null) return null;
                    if (!location.getWorld().getName().equalsIgnoreCase("world")) return null;

                    int chunkX = location.getBlockX() >> 4;
                    int chunkZ = location.getBlockZ() >> 4;

                    String key = location.getWorld().getName() + ":" + chunkX + ":" + chunkZ;

                    return HeroCraft.getPlugin().getLandManager().landChunkCache.get(key);
                }
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   public static boolean canCreateLandSize(double x1, double z1, double x2, double z2, int maxBlocks) {
        /* 788 */     double area = Math.abs((x2 - x1) * (z2 - z1));
        /* 789 */     if (area > maxBlocks) {
            /* 790 */       return false;
            /*     */     }
        /* 792 */     if (area < 4.0D) {
            /* 793 */       return false;
            /*     */     }
        /* 795 */     return true;
        /*     */   }
    /*     */   public static boolean canCreateLandLocation(double x1, double z1, double x2, double z2, ArrayList<Land> existingLands, String igonringLandName) {
        /* 798 */     double minX = Math.min(x1, x2);
        /* 799 */     double maxX = Math.max(x1, x2);
        /* 800 */     double minZ = Math.min(z1, z2);
        /* 801 */     double maxZ = Math.max(z1, z2);
        /* 802 */     for (Land land : existingLands) {
            /* 803 */       double existingMinX = Math.min(land.getX1(), land.getX2());
            /* 804 */       double existingMaxX = Math.max(land.getX1(), land.getX2());
            /* 805 */       double existingMinZ = Math.min(land.getZ1(), land.getZ2());
            /* 806 */       double existingMaxZ = Math.max(land.getZ1(), land.getZ2());
            /* 807 */       if (maxX <= existingMinX || minX >= existingMaxX || maxZ <= existingMinZ || minZ >= existingMaxZ || (
                    /* 808 */         !igonringLandName.equalsIgnoreCase("") &&
                    /* 809 */         igonringLandName.equalsIgnoreCase(land.getName()))) {
                /*     */         continue;
                /*     */       }
            /*     */
            /* 813 */       return false;
            /*     */     }
        /*     */
        /* 816 */     return true;
        /*     */   }
    /*     */
    /*     */   public static boolean canCreateLandProvinceLocation(double x1, double z1, double x2, double z2, ArrayList<Province> existingProvinces, String worldName, String igonringLandName, String ignoringProvinceName) {
        /* 820 */     double minX = Math.min(x1, x2);
        /* 821 */     double maxX = Math.max(x1, x2);
        /* 822 */     double minZ = Math.min(z1, z2);
        /* 823 */     double maxZ = Math.max(z1, z2);
        /* 824 */     for (Province province : existingProvinces) {
            /* 825 */       if (!province.getWorld().equalsIgnoreCase(worldName))
                /*     */         continue;
            /* 827 */       double existingMinX = Math.min(province.getX1(), province.getX2());
            /* 828 */       double existingMaxX = Math.max(province.getX1(), province.getX2());
            /* 829 */       double existingMinZ = Math.min(province.getZ1(), province.getZ2());
            /* 830 */       double existingMaxZ = Math.max(province.getZ1(), province.getZ2());
            /* 831 */       if (maxX <= existingMinX || minX >= existingMaxX || maxZ <= existingMinZ || minZ >= existingMaxZ || (
                    /* 832 */         !igonringLandName.equalsIgnoreCase("") &&
                    /* 833 */         igonringLandName.equalsIgnoreCase(province.getLand()) && ignoringProvinceName.equalsIgnoreCase(province.getName()))) {
                /*     */         continue;
                /*     */       }
            /*     */
            /* 837 */       return false;
            /*     */     }
        /*     */
        /* 840 */     return true;
        /*     */   }
    /*     */
    /*     */   public ArrayList<Land> getAllLands() {
        /* 844 */     return this.allLands;
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\LandManager.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */