/*     */ package de.christoph.herocraft.lands.province;
/*     */
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import de.christoph.herocraft.lands.roles.LandPermission;
/*     */ import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Projectile;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.Action;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.block.BlockPlaceEvent;
/*     */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.event.player.PlayerMoveEvent;
/*     */
/*     */
/*     */
/*     */ public class ProvinceManager
        /*     */   implements Listener
        /*     */ {
    /*     */   private ArrayList<Province> provinces;
    /*     */   private Map<Player, Province> playerProvinceCache;
    /*  36 */   private final Map<String, Province> provinceChunkCache = new HashMap<>();
    /*     */
    /*     */   public ProvinceManager() {
        /*  39 */     this.playerProvinceCache = new HashMap<>();
        /*  40 */     this.provinces = new ArrayList<>();
        /*  41 */     loadProvinces();
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onPlayerMove(PlayerMoveEvent event) {
        /*  46 */     Player player = event.getPlayer();
        /*  47 */     Location to = event.getTo();
        /*  48 */     Location from = event.getFrom();
        /*     */
        /*     */
        /*  51 */     if (to == null || (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()))
            /*     */       return;
        /*  53 */     Province last = this.playerProvinceCache.get(player);
        /*  54 */     Province now = getProvinceAtLocation(to, this.provinces);
        /*     */
        /*  56 */     if (last == now)
            /*     */       return;
        /*  58 */     if (now != null && (last == null || !now.equals(last))) {
            /*  59 */       player.sendTitle("§a§l" + now.getName(), "Stadt: §a" +
                    /*  60 */           HeroCraft.getPlugin().getLandManager().getLandByName(now.getLand()).getName(), 5, 40, 5);
            /*     */
            /*  62 */       this.playerProvinceCache.put(player, now);
            /*     */
            /*     */       return;
            /*     */     }
        /*  66 */     if (last != null && now == null) {
            /*  67 */       player.sendTitle("§c§l" + last.getName(), "Stadt: §c" +
                    /*  68 */           HeroCraft.getPlugin().getLandManager().getLandByName(last.getLand()).getName(), 5, 40, 5);
            /*     */
            /*  70 */       this.playerProvinceCache.remove(player);
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private void indexProvince(Province province) {
        if (province == null) return;

        String world = province.getWorld();
        int minChunkX = (int) Math.floor(Math.min(province.getX1(), province.getX2()) / 16.0);
        int maxChunkX = (int) Math.floor(Math.max(province.getX1(), province.getX2()) / 16.0);
        int minChunkZ = (int) Math.floor(Math.min(province.getZ1(), province.getZ2()) / 16.0);
        int maxChunkZ = (int) Math.floor(Math.max(province.getZ1(), province.getZ2()) / 16.0);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                provinceChunkCache.put(world + ":" + cx + ":" + cz, province);
            }
        }
    }

    /*     */
    /*     */   private void removeProvinceFromIndex(Province province) {
        /*  91 */     this.provinceChunkCache.entrySet().removeIf(e -> ((Province)e.getValue()).equals(province));
        /*     */   }
    /*     */
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFurnitureBreak(FurnitureBreakEvent event) {
        /*  97 */     Player player = event.getPlayer();
        /*  98 */     Province land = getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), this.provinces);
        /*  99 */     if (land == null)
            /*     */       return;
        /* 101 */     if (!land.canBuild(player))
            /* 102 */       event.setCancelled(true);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        /* 107 */     Entity damager = event.getDamager();
        /* 108 */     Entity damaged = event.getEntity();
        /*     */
        /* 110 */     Province land = getProvinceAtLocation(damaged.getLocation(), this.provinces);
        /* 111 */     if (land == null) {
            /*     */       return;
            /*     */     }
        /*     */
        /* 115 */     if (damager instanceof Player && damaged instanceof Player) {
            /* 116 */       Player attacker = (Player)damager;
            /* 117 */       Player victim = (Player)damaged;
            /* 118 */       if (!land.canBuild(attacker)) {
                /* 119 */         event.setCancelled(true);
                /*     */       } else {
                /* 121 */         ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                /* 122 */         if (perms == null)
                    /*     */           return;
                /* 124 */         if (!perms.contains(LandPermission.PVP)) {
                    /* 125 */           event.setCancelled(true);
                    /*     */         }
                /*     */       }
            /*     */
            /*     */       return;
            /*     */     }
        /*     */
        /* 132 */     if (damager instanceof Player) {
            /* 133 */       Player attacker = (Player)damager;
            /*     */
            /* 135 */       if (isFriendlyEntity(damaged)) {
                /* 136 */         if (!land.canBuild(attacker)) {
                    /* 137 */           event.setCancelled(true);
                    /*     */         } else {
                    /* 139 */           ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                    /* 140 */           if (perms == null)
                        /*     */             return;
                    /* 142 */           if (!perms.contains(LandPermission.PVE_FRIENDLY))
                        /*     */           {
                        /* 144 */             event.setCancelled(true);
                        /*     */           }
                    /*     */         }
                /*     */
                /*     */         return;
                /*     */       }
            /* 150 */       if (isHostileEntity(damaged)) {
                /* 151 */         if (!land.canBuild(attacker)) {
                    /* 152 */           if (HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()).isSemiTrusted(attacker)) {
                        /*     */             return;
                        /*     */           }
                    /* 155 */           event.setCancelled(true);
                    /*     */         } else {
                    /* 157 */           ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                    /* 158 */           if (perms == null)
                        /*     */             return;
                    /* 160 */           if (!perms.contains(LandPermission.PVE_UNFRIENDLY))
                        /*     */           {
                        /* 162 */             event.setCancelled(true);
                        /*     */           }
                    /*     */         }
                /*     */
                /*     */         return;
                /*     */       }
            /*     */     }
        /*     */
        /* 170 */     if (damager instanceof Projectile) {
            /* 171 */       Projectile projectile = (Projectile)damager;
            /*     */
            /* 173 */       if (projectile.getShooter() instanceof Player) {
                /* 174 */         Player attacker = (Player)projectile.getShooter();
                /*     */
                /*     */
                /* 177 */         if (damaged instanceof Player) {
                    /* 178 */           if (!land.canBuild(attacker)) {
                        /* 179 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 181 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        /* 182 */             if (perms == null)
                            /*     */               return;
                        /* 184 */             if (!perms.contains(LandPermission.PVP))
                            /*     */             {
                            /* 186 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */
                    /*     */           return;
                    /*     */         }
                /*     */
                /* 193 */         if (isFriendlyEntity(damaged)) {
                    /* 194 */           if (!land.canBuild(attacker)) {
                        /* 195 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 197 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        /* 198 */             if (perms == null)
                            /*     */               return;
                        /* 200 */             if (!perms.contains(LandPermission.PVE_FRIENDLY))
                            /*     */             {
                            /* 202 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */
                    /*     */           return;
                    /*     */         }
                /*     */
                /* 209 */         if (isHostileEntity(damaged)) {
                    /* 210 */           if (!land.canBuild(attacker)) {
                        /* 211 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 213 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(attacker, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        /* 214 */             if (perms == null)
                            /*     */               return;
                        /* 216 */             if (!perms.contains(LandPermission.PVE_UNFRIENDLY))
                            /*     */             {
                            /* 218 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */           return;
                    /*     */         }
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   private boolean isFriendlyEntity(Entity entity) {
        /* 228 */     EntityType type = entity.getType();
        /* 229 */     return (type == EntityType.VILLAGER || type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM || type == EntityType.WANDERING_TRADER || type == EntityType.ALLAY || type == EntityType.HORSE || type == EntityType.COW || type == EntityType.SHEEP || type == EntityType.PIG || type == EntityType.CHICKEN || type == EntityType.RABBIT || type == EntityType.WOLF || type == EntityType.CAT || type == EntityType.PARROT);
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
        /* 246 */     EntityType type = entity.getType();
        /* 247 */     return (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.CREEPER || type == EntityType.SPIDER || type == EntityType.ENDERMAN || type == EntityType.DROWNED || type == EntityType.PILLAGER || type == EntityType.WITCH || type == EntityType.BLAZE || type == EntityType.SLIME || type == EntityType.PHANTOM);
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
    /*     */   @EventHandler
    /*     */   public void onBlockBreak(BlockPlaceEvent event) {
        /* 262 */     Player player = event.getPlayer();
        /* 263 */     if (player.hasPermission("herowars.build"))
            /*     */       return;
        /* 265 */     Province land = getProvinceAtLocation(event.getBlock().getLocation(), this.provinces);
        /* 266 */     if (land == null) {
            /*     */       return;
            /*     */     }
        /* 269 */     if (!land.canBuild(player)) {
            /* 270 */       event.setCancelled(true);
            /*     */     } else {
            /* 272 */       ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
            /* 273 */       if (perms == null)
                /*     */         return;
            /* 275 */       if (!perms.contains(LandPermission.BUILD))
                /*     */       {
                /* 277 */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onBlockBreak(BlockBreakEvent event) {
        /* 284 */     Player player = event.getPlayer();
        /* 285 */     if (player.hasPermission("herowars.build"))
            /*     */       return;
        /* 287 */     Province land = getProvinceAtLocation(event.getBlock().getLocation(), this.provinces);
        /* 288 */     if (land == null) {
            /*     */       return;
            /*     */     }
        /* 291 */     if (!land.canBuild(player)) {
            /* 292 */       event.setCancelled(true);
            /*     */     } else {
            /* 294 */       ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
            /* 295 */       if (perms == null)
                /*     */         return;
            /* 297 */       if (!perms.contains(LandPermission.BREAK)) {
                /* 298 */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private boolean isDoor(Material type) {
        /* 305 */     return (type == Material.OAK_DOOR || type == Material.BIRCH_DOOR || type == Material.SPRUCE_DOOR || type == Material.JUNGLE_DOOR || type == Material.ACACIA_DOOR || type == Material.DARK_OAK_DOOR || type == Material.MANGROVE_DOOR || type == Material.CHERRY_DOOR || type == Material.CRIMSON_DOOR || type == Material.WARPED_DOOR);
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
        /* 319 */     Player player = event.getPlayer();
        /* 320 */     if (player.hasPermission("herowars.build"))
            /*     */       return;
        /* 322 */     Province land = getProvinceAtLocation(player.getLocation(), this.provinces);
        /* 323 */     if (land == null)
            /*     */       return;
        /* 325 */     if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            /* 326 */       Block clickedBlock = event.getClickedBlock();
            /* 327 */       if (clickedBlock != null) {
                /* 328 */         Material type = clickedBlock.getType();
                /* 329 */         if (type == Material.CHEST || type == Material.TRAPPED_CHEST) {
                    /* 330 */           if (!land.canBuild(player)) {
                        /* 331 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 333 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        /* 334 */             if (perms == null)
                            /*     */               return;
                        /* 336 */             if (!perms.contains(LandPermission.OPEN_CHESTS))
                            /*     */             {
                            /* 338 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */
                    /*     */           return;
                    /*     */         }
                /* 344 */         if (isDoor(type)) {
                    /* 345 */           if (!land.canBuild(player)) {
                        /* 346 */             if (HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()).isSemiTrusted(player)) {
                            /*     */               return;
                            /*     */             }
                        /* 349 */             event.setCancelled(true);
                        /*     */           } else {
                        /* 351 */             ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
                        /* 352 */             if (perms == null)
                            /*     */               return;
                        /* 354 */             if (!perms.contains(LandPermission.OPEN_DOOR))
                            /*     */             {
                            /* 356 */               event.setCancelled(true);
                            /*     */             }
                        /*     */           }
                    /*     */         }
                /*     */       }
            /*     */     }
        /* 362 */     if (!land.canBuild(player)) {
            /* 363 */       if (event.getClickedBlock() == null) {
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
            /* 375 */       event.setCancelled(true);
            /*     */     } else {
            /* 377 */       ArrayList<LandPermission> perms = (HeroCraft.getPlugin()).landRoleManager.getLandPermissionFromPlayer(player, HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()));
            /* 378 */       if (perms == null)
                /*     */         return;
            /* 380 */       if (!perms.contains(LandPermission.INTERACT)) {
                /* 381 */         if (HeroCraft.getPlugin().getLandManager().getLandByName(land.getLand()).isOwnerUUID(player.getUniqueId().toString())) {
                    /*     */           return;
                    /*     */         }
                /*     */
                /* 385 */         event.setCancelled(true);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   private void loadProvinces() {
        /*     */     try {
            /* 393 */       PreparedStatement ps = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `provinces`");
            /* 394 */       ResultSet rs = ps.executeQuery();
            /*     */
            /* 396 */       this.provinces.clear();
            /* 397 */       this.provinceChunkCache.clear();
            /*     */
            /* 399 */       while (rs.next()) {
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /* 407 */         Province p = new Province(rs.getString("land"), rs.getString("name"), rs.getDouble("x1"), rs.getDouble("z1"), rs.getDouble("x2"), rs.getDouble("z2"), rs.getString("world"));
                /*     */
                /* 409 */         this.provinces.add(p);
                /* 410 */         indexProvince(p);
                /*     */       }
            /*     */
            /* 413 */       HeroCraft.getPlugin().getLogger().info("[HeroCraft] " + this.provinces.size() + " Provinzen geladen und indexiert.");
            /*     */     }
        /* 415 */     catch (SQLException e) {
            /* 416 */       e.printStackTrace();
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   public void saveProvince(Province province) {
        /*     */     try {
            /* 423 */       Province existing = getProvinceByName(province.getLand(), province.getName());
            /* 424 */       boolean newProvince = (existing == null);
            /*     */
            /* 426 */       if (!newProvince) {
                /*     */
                /* 428 */         if (!existing.getWorld().equalsIgnoreCase(province.getWorld()) || existing
/* 429 */           .getX1() != province.getX1() || existing
/* 430 */           .getZ1() != province.getZ1() || existing
/* 431 */           .getX2() != province.getX2() || existing
/* 432 */           .getZ2() != province.getZ2()) {
                    /* 433 */           removeProvinceFromIndex(existing);
                    /* 434 */           indexProvince(province);
                    /*     */         }
                /*     */
                /* 437 */         PreparedStatement del = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `provinces` WHERE `land` = ? AND `name` = ?");
                /* 438 */         del.setString(1, province.getLand());
                /* 439 */         del.setString(2, province.getName());
                /* 440 */         del.execute();
                /*     */       } else {
                /*     */
                /* 443 */         indexProvince(province);
                /*     */       }
            /*     */
            /*     */
            /* 447 */       PreparedStatement ps = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `provinces` (`land`,`name`,`x1`,`z1`,`x2`,`z2`,`world`) VALUES (?,?,?,?,?,?,?)");
            /* 448 */       ps.setString(1, province.getLand());
            /* 449 */       ps.setString(2, province.getName());
            /* 450 */       ps.setDouble(3, province.getX1());
            /* 451 */       ps.setDouble(4, province.getZ1());
            /* 452 */       ps.setDouble(5, province.getX2());
            /* 453 */       ps.setDouble(6, province.getZ2());
            /* 454 */       ps.setString(7, province.getWorld());
            /* 455 */       ps.execute();
            /*     */     }
        /* 457 */     catch (SQLException e) {
            /* 458 */       e.printStackTrace();
            /*     */     }
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
    /*     */   public static Province getProvinceAtLocation(Location location, ArrayList<Province> provinces) {
        if (location == null || location.getWorld() == null) return null;

        ProvinceManager manager = HeroCraft.getPlugin().getProvinceManager();
        Map<String, Province> cache = manager.provinceChunkCache;

        String world = location.getWorld().getName();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        String key = world + ":" + chunkX + ":" + chunkZ;

        return cache.get(key);
    }

    /*     */
    /*     */
    /*     */   @Nullable
    /*     */   public Province getProvinceByName(String landName, String provinceName) {
        /* 523 */     for (Province current : this.provinces) {
            /* 524 */       if (current.getName().equalsIgnoreCase(provinceName) && current.getLand().equalsIgnoreCase(landName))
                /* 525 */         return current;
            /*     */     }
        /* 527 */     return null;
        /*     */   }
    /*     */
    /*     */   public ArrayList<Province> getProvinces() {
        /* 531 */     return this.provinces;
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\province\ProvinceManager.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */