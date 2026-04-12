/*     */ package de.christoph.herocraft.lands;
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Color;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Particle;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ import org.bukkit.util.Vector;
/*     */
/*     */ public class Land {
    /*     */   private static final int PARTICLE_DENSITY = 5;
    /*     */   private String name;
    /*     */   private String founderUUID;
    /*     */   private String founderName;
    /*     */   private String[] coFounderUUIDs;
    /*     */   private String[] coFounderNames;
    /*     */   private String[] memberUUIDs;
    /*     */   private String[] memberNames;
    /*     */   private double x1;
    /*     */   private double z1;
    /*     */   private double x2;
    /*     */   private double z2;
    /*     */   private double spawnX;
    /*     */   private double spawnY;
    /*     */   private double spawnZ;
    /*     */   private double coins;
    /*     */   private int maxBlocks;
    /*     */   private String[] trusted;
    /*     */   private String[] semiTrusted;
    /*     */   private double spawnYaw;
    /*     */   private double spawnPitch;
    /*     */   private double armeeCoins;
    /*     */   private double prisonSpawnX;
    /*     */   private double prisonSpawnZ;
    /*     */   private double prisonSpawnY;
    /*     */
    /*     */   public Land(String name, String founderUUID, String founderName, String[] coFounderUUIDs, String[] coFounderNames, String[] memberUUIDs, String[] memberNames, double x1, double z1, double x2, double z2, double spawnX, double spawnY, double spawnZ, double coins, int maxBlocks, String[] trusted, String[] semiTrusted, double spawnYaw, double spawnPitch, double armeeCoins, double prisonSpawnX, double prisonSpawnZ, double prisonSpawnY) {
        /*  48 */     this.name = name;
        /*  49 */     this.founderUUID = founderUUID;
        /*  50 */     this.founderName = founderName;
        /*  51 */     this.coFounderUUIDs = coFounderUUIDs;
        /*  52 */     this.coFounderNames = coFounderNames;
        /*  53 */     this.memberUUIDs = memberUUIDs;
        /*  54 */     this.memberNames = memberNames;
        /*  55 */     this.x1 = x1;
        /*  56 */     this.z1 = z1;
        /*  57 */     this.x2 = x2;
        /*  58 */     this.z2 = z2;
        /*  59 */     this.spawnX = spawnX;
        /*  60 */     this.spawnY = spawnY;
        /*  61 */     this.spawnZ = spawnZ;
        /*  62 */     this.coins = coins;
        /*  63 */     this.maxBlocks = maxBlocks;
        /*  64 */     this.trusted = trusted;
        /*  65 */     this.semiTrusted = semiTrusted;
        /*  66 */     this.spawnYaw = spawnYaw;
        /*  67 */     this.spawnPitch = spawnPitch;
        /*  68 */     this.armeeCoins = armeeCoins;
        /*  69 */     this.prisonSpawnX = prisonSpawnX;
        /*  70 */     this.prisonSpawnZ = prisonSpawnZ;
        /*  71 */     this.prisonSpawnY = prisonSpawnY;
        /*     */   }
    /*     */
    /*     */   public boolean isInLand(Player player) {
        /*  75 */     if (this.founderUUID.equals(player.getUniqueId().toString()))
            /*  76 */       return true;
        /*  77 */     for (String i : this.coFounderUUIDs) {
            /*  78 */       if (i.equals(player.getUniqueId().toString()))
                /*  79 */         return true;
            /*     */     }
        /*  81 */     for (String i : this.memberUUIDs) {
            /*  82 */       if (i.equals(player.getUniqueId().toString())) {
                /*  83 */         return true;
                /*     */       }
            /*     */     }
        /*  86 */     return false;
        /*     */   }
    /*     */
    /*     */   public Location getPrisonSpawnPoint() {
        /*  90 */     return new Location(Bukkit.getWorld("world"), this.prisonSpawnX, this.prisonSpawnY, this.prisonSpawnZ);
        /*     */   }
    /*     */
    /*     */   public ArrayList<String> getAllLandNames() {
        /*  94 */     ArrayList<String> allLandNames = new ArrayList<>();
        /*  95 */     allLandNames.addAll(List.of(this.memberNames));
        /*  96 */     allLandNames.addAll(List.of(this.coFounderNames));
        /*  97 */     allLandNames.add(this.founderName);
        /*  98 */     return allLandNames;
        /*     */   }
    /*     */
    /*     */   public boolean canBuild(Player player) {
        /* 102 */     if (this.founderUUID.equals(player.getUniqueId().toString()))
            /* 103 */       return true;
        /* 104 */     for (String i : this.coFounderUUIDs) {
            /* 105 */       if (i.equals(player.getUniqueId().toString()))
                /* 106 */         return true;
            /*     */     }
        /* 108 */     for (String i : this.memberUUIDs) {
            /* 109 */       if (i.equals(player.getUniqueId().toString())) {
                /* 110 */         return true;
                /*     */       }
            /*     */     }
        /* 113 */     for (String i : this.trusted) {
            /* 114 */       if (i.equalsIgnoreCase(player.getUniqueId().toString())) {
                /* 115 */         return true;
                /*     */       }
            /*     */     }
        /* 118 */     return false;
        /*     */   }
    /*     */
    /*     */   public boolean isSemiTrusted(Player player) {
        /* 122 */     for (String i : this.semiTrusted) {
            /* 123 */       if (i.equals(player.getUniqueId().toString()))
                /* 124 */         return true;
            /*     */     }
        /* 126 */     return false;
        /*     */   }
    /*     */
    /*     */   public boolean isOwner(String name) {
        /* 130 */     return this.founderName.equalsIgnoreCase(name);
        /*     */   }
    /*     */
    /*     */   public boolean isOwnerUUID(String uuid) {
        /* 134 */     return this.founderUUID.equalsIgnoreCase(uuid);
        /*     */   }
    /*     */
    /*     */   public boolean isModerator(String name) {
        /* 138 */     for (String i : this.coFounderNames) {
            /* 139 */       if (i.equalsIgnoreCase(name))
                /* 140 */         return true;
            /*     */     }
        /* 142 */     return false;
        /*     */   }
    /*     */
    /*     */   public boolean isModeratorUUID(String uuid) {
        /* 146 */     for (String i : this.coFounderUUIDs) {
            /* 147 */       if (i.equalsIgnoreCase(uuid))
                /* 148 */         return true;
            /*     */     }
        /* 150 */     return false;
        /*     */   }
    /*     */
    /*     */
    /*     */   public void showLandBorder(Player player) {
        /* 155 */     createLaserEffect(new Location(
                /* 156 */           Bukkit.getWorld("world"), this.x1,
                /*     */
                /* 158 */           Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), this.x1, 10.0D, this.z1)), this.z1), new Location(
                /*     */
                /*     */
                /* 161 */           Bukkit.getWorld("world"), this.x1, 1000.0D, this.z1));
        /*     */
        /*     */
        /*     */
        /*     */
        /* 166 */     createLaserEffect(new Location(
                /* 167 */           Bukkit.getWorld("world"), this.x2,
                /*     */
                /* 169 */           Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), this.x2, 10.0D, this.z2)), this.z2), new Location(
                /*     */
                /*     */
                /* 172 */           Bukkit.getWorld("world"), this.x2, 1000.0D, this.z2));
        /*     */
        /*     */
        /*     */
        /* 176 */     double x3 = this.x1;
        /* 177 */     double z3 = this.z2;
        /* 178 */     double x4 = this.x2;
        /* 179 */     double z4 = this.z1;
        /* 180 */     createLaserEffect(new Location(
                /* 181 */           Bukkit.getWorld("world"), x3,
                /*     */
                /* 183 */           Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x3, 10.0D, z3)), z3), new Location(
                /*     */
                /*     */
                /* 186 */           Bukkit.getWorld("world"), x3, 1000.0D, z3));
        /*     */
        /*     */
        /*     */
        /* 190 */     createLaserEffect(new Location(
                /* 191 */           Bukkit.getWorld("world"), x4,
                /*     */
                /* 193 */           Bukkit.getWorld("world").getHighestBlockYAt(new Location(Bukkit.getWorld("world"), x4, 10.0D, z4)), z4), new Location(
                /*     */
                /*     */
                /* 196 */           Bukkit.getWorld("world"), x4, 1000.0D, z4));
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   public void changeSize(int x1, int z1, int x2, int z2) {
        /* 203 */     this.x1 = x1;
        /* 204 */     this.z1 = z1;
        /* 205 */     this.x2 = x2;
        /* 206 */     this.z2 = z2;
        /*     */     try {
            /* 208 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `lands` SET `x1` = ?, `z1` = ?, `x2` = ?, `z2` = ? WHERE `name` = ?");
            /* 209 */       preparedStatement.setInt(1, x1);
            /* 210 */       preparedStatement.setInt(2, z1);
            /* 211 */       preparedStatement.setInt(3, x2);
            /* 212 */       preparedStatement.setInt(4, z2);
            /* 213 */       preparedStatement.setString(5, this.name);
            /* 214 */       preparedStatement.execute();
            /* 215 */     } catch (SQLException e) {
            /* 216 */       e.printStackTrace();
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void createLaserEffect(final Location start, Location end) {
        /* 221 */     final World world = start.getWorld();
        /* 222 */     final double particleDistance = 0.25D;
        /* 223 */     final Vector direction = end.clone().subtract(start).toVector();
        /* 224 */     final double length = direction.length();
        /* 225 */     direction.normalize();
        /*     */
        /* 227 */     final int particlesPerTick = 10;
        /* 228 */     final int totalTicks = (int)Math.ceil(length / particleDistance / particlesPerTick);
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
        /* 250 */     BukkitTask task = (new BukkitRunnable() { int tick = 0; public void run() { for (int i = 0; i < particlesPerTick; i++) { double progress = (this.tick * particlesPerTick + i) / (totalTicks * particlesPerTick); if (progress < 1.0D) { double particleDistancePerTick = particleDistance * particlesPerTick; double currentLength = progress * length; Location location = start.clone().add(direction.clone().multiply(currentLength)); location.setY(start.getY() + progress * length); world.spawnParticle(Particle.FIREWORK, location, 1, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0F)); }  }  if (++this.tick >= totalTicks) cancel();  } }).runTaskTimer((Plugin)HeroCraft.getPlugin(), 0L, 1L);
        /*     */
        /* 252 */     Bukkit.getScheduler().runTaskLater((Plugin)HeroCraft.getPlugin(), () -> task.cancel(), 160L);
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */   public void teleportTo(Player player) {
        /* 258 */     player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        /* 259 */     player.teleport(new Location(Bukkit.getWorld("world"), this.spawnX, this.spawnY, this.spawnZ, (float)this.spawnYaw, (float)this.spawnPitch));
        /*     */   }
    /*     */
    /*     */   public void addMember(Player player) {
        /* 263 */
        List<String> membUUIDs = new ArrayList<>(Arrays.asList(this.memberUUIDs));
        /* 264 */
        List<String> membNames = new ArrayList<>(Arrays.asList(this.memberNames));
        /* 265 */
        if (membUUIDs.contains(player.getUniqueId().toString()))
            /*     */ return;
        /* 267 */
        membUUIDs.add(player.getUniqueId().toString());
        /* 268 */
        membNames.add(player.getName());
        /* 269 */
        this.memberUUIDs = membUUIDs.<String>toArray(new String[0]);
        /* 270 */
        this.memberNames = membNames.<String>toArray(new String[0]);
        /* 271 */
        HeroCraft.getPlugin().getLandManager().saveLand(this);
        /* 272 */
        HeroCraft.getPlugin().getStatisticsManager().markLandJoined(player.getUniqueId());
    }
    /*     */
    /*     */   public void promotePlayer(String name) {
        /* 275 */     List<String> membNames = new ArrayList<>(Arrays.asList(this.memberNames));
        /* 276 */     List<String> membUUIDs = new ArrayList<>(Arrays.asList(this.memberUUIDs));
        /* 277 */     List<String> moderatorUUIDs = new ArrayList<>(Arrays.asList(this.coFounderUUIDs));
        /* 278 */     List<String> moderatorNames = new ArrayList<>(Arrays.asList(this.coFounderNames));
        /* 279 */     if (!membNames.contains(name))
            /*     */       return;
        /* 281 */     int index = membNames.indexOf(name);
        /* 282 */     membNames.remove(name);
        /* 283 */     String uuid = "";
        /* 284 */     for (String i : membUUIDs) {
            /* 285 */       if (membUUIDs.indexOf(i) == index) {
                /* 286 */         uuid = i;
                /*     */       }
            /*     */     }
        /* 289 */     membUUIDs.remove(uuid);
        /* 290 */     membNames.remove(name);
        /* 291 */     moderatorNames.add(name);
        /* 292 */     moderatorUUIDs.add(uuid);
        /* 293 */     this.memberNames = membNames.<String>toArray(new String[0]);
        /* 294 */     this.memberUUIDs = membUUIDs.<String>toArray(new String[0]);
        /* 295 */     this.coFounderNames = moderatorNames.<String>toArray(new String[0]);
        /* 296 */     this.coFounderUUIDs = moderatorUUIDs.<String>toArray(new String[0]);
        /* 297 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void degradePlayer(String name) {
        /* 301 */     List<String> membNames = new ArrayList<>(Arrays.asList(this.memberNames));
        /* 302 */     List<String> membUUIDs = new ArrayList<>(Arrays.asList(this.memberUUIDs));
        /* 303 */     List<String> moderatorUUIDs = new ArrayList<>(Arrays.asList(this.coFounderUUIDs));
        /* 304 */     List<String> moderatorNames = new ArrayList<>(Arrays.asList(this.coFounderNames));
        /* 305 */     if (!moderatorNames.contains(name))
            /*     */       return;
        /* 307 */     int index = moderatorNames.indexOf(name);
        /* 308 */     moderatorNames.remove(name);
        /* 309 */     String uuid = "";
        /* 310 */     for (String i : moderatorUUIDs) {
            /* 311 */       if (moderatorUUIDs.indexOf(i) == index) {
                /* 312 */         uuid = i;
                /*     */       }
            /*     */     }
        /* 315 */     membUUIDs.add(uuid);
        /* 316 */     membNames.add(name);
        /* 317 */     moderatorNames.remove(name);
        /* 318 */     moderatorUUIDs.remove(uuid);
        /* 319 */     this.memberNames = membNames.<String>toArray(new String[0]);
        /* 320 */     this.memberUUIDs = membUUIDs.<String>toArray(new String[0]);
        /* 321 */     this.coFounderNames = moderatorNames.<String>toArray(new String[0]);
        /* 322 */     this.coFounderUUIDs = moderatorUUIDs.<String>toArray(new String[0]);
        /* 323 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void removeMember(String playerName) {
        /* 327 */     List<String> moderatorUUIDs = new ArrayList<>(Arrays.asList(this.coFounderUUIDs));
        /* 328 */     List<String> moderatorNames = new ArrayList<>(Arrays.asList(this.coFounderNames));
        /* 329 */     if (!moderatorNames.contains(playerName)) {
            /* 330 */       List<String> membUUIDs = new ArrayList<>(Arrays.asList(this.memberUUIDs));
            /* 331 */       List<String> membNames = new ArrayList<>(Arrays.asList(this.memberNames));
            /* 332 */       if (!membNames.contains(playerName))
                /*     */         return;
            /* 334 */       int i = membNames.indexOf(playerName);
            /* 335 */       membNames.remove(playerName);
            /* 336 */       String str = "";
            /* 337 */       for (String str1 : membUUIDs) {
                /* 338 */         if (membUUIDs.indexOf(str1) == i) {
                    /* 339 */           str = str1;
                    /*     */         }
                /*     */       }
            /* 342 */       membUUIDs.remove(str);
            /* 343 */       this.memberUUIDs = membUUIDs.<String>toArray(new String[0]);
            /* 344 */       this.memberNames = membNames.<String>toArray(new String[0]);
            /* 345 */       HeroCraft.getPlugin().getLandManager().saveLand(this);
            /*     */       return;
            /*     */     }
        /* 348 */     int index = moderatorNames.indexOf(playerName);
        /* 349 */     moderatorNames.remove(playerName);
        /* 350 */     String uuid = "";
        /* 351 */     for (String i : moderatorUUIDs) {
            /* 352 */       if (moderatorUUIDs.indexOf(i) == index) {
                /* 353 */         uuid = i;
                /*     */       }
            /*     */     }
        /* 356 */     moderatorUUIDs.remove(uuid);
        /* 357 */     this.coFounderUUIDs = moderatorUUIDs.<String>toArray(new String[0]);
        /* 358 */     this.coFounderNames = moderatorNames.<String>toArray(new String[0]);
        /* 359 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void setSpawnPoint(Location location) {
        /* 363 */     this.spawnX = location.getX();
        /* 364 */     this.spawnY = location.getY();
        /* 365 */     this.spawnZ = location.getZ();
        /* 366 */     this.spawnYaw = location.getYaw();
        /* 367 */     this.spawnPitch = location.getPitch();
        /* 368 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void setPrisonSpawnPoint(Location location) {
        /* 372 */     this.prisonSpawnX = location.getX();
        /* 373 */     this.prisonSpawnY = location.getY();
        /* 374 */     this.prisonSpawnZ = location.getZ();
        /* 375 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void delete() {
        /*     */     try {
            /* 380 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `lands` WHERE `name` = ?");
            /* 381 */       preparedStatement.setString(1, this.name);
            /* 382 */       preparedStatement.execute();
            /* 383 */       HeroCraft.getPlugin().getLandManager().getAllLands().remove(this);
            /* 384 */     } catch (SQLException e) {
            /* 385 */       throw new RuntimeException(e);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void setArmeeCoins(double amount) {
        /* 390 */     this.armeeCoins = amount;
        /*     */     try {
            /* 392 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `lands` SET `armee_coins` = ? WHERE `name` = ?");
            /* 393 */       preparedStatement.setDouble(1, amount);
            /* 394 */       preparedStatement.setString(2, this.name);
            /* 395 */       preparedStatement.execute();
            /* 396 */     } catch (SQLException e) {
            /* 397 */       throw new RuntimeException(e);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void setCoins(double amount) {
        /* 402 */     this.coins = amount;
        /*     */     try {
            /* 404 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `lands` SET `coins` = ? WHERE `name` = ?");
            /* 405 */       preparedStatement.setDouble(1, amount);
            /* 406 */       preparedStatement.setString(2, this.name);
            /* 407 */       preparedStatement.execute();
            /* 408 */     } catch (SQLException e) {
            /* 409 */       throw new RuntimeException(e);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void setMaxBlocks(int maxBlocks) {
        /* 414 */     this.maxBlocks = maxBlocks;
        /*     */     try {
            /* 416 */       PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `lands` SET `max_blocks` = ? WHERE `name` = ?");
            /* 417 */       preparedStatement.setInt(1, maxBlocks);
            /* 418 */       preparedStatement.setString(2, this.name);
            /* 419 */       preparedStatement.execute();
            /* 420 */     } catch (SQLException e) {
            /* 421 */       throw new RuntimeException(e);
            /*     */     }
        /*     */   }
    /*     */
    /*     */   public void semiTrustPlayer(Player player) {
        /* 426 */     boolean isTrusted = false;
        /* 427 */     for (String i : this.semiTrusted) {
            /* 428 */       if (i.equalsIgnoreCase(player.getUniqueId().toString())) {
                /* 429 */         isTrusted = true;
                /*     */       }
            /*     */     }
        /* 432 */     if (isTrusted)
            /*     */       return;
        /* 434 */     List<String> trustedUUIDs = new ArrayList<>(Arrays.asList(this.semiTrusted));
        /* 435 */     trustedUUIDs.add(player.getUniqueId().toString());
        /* 436 */     this.semiTrusted = trustedUUIDs.<String>toArray(new String[0]);
        /* 437 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void trustPlayer(Player player) {
        /* 441 */     boolean isTrusted = false;
        /* 442 */     for (String i : this.trusted) {
            /* 443 */       if (i.equalsIgnoreCase(player.getUniqueId().toString())) {
                /* 444 */         isTrusted = true;
                /*     */       }
            /*     */     }
        /* 447 */     if (isTrusted)
            /*     */       return;
        /* 449 */     List<String> trustedUUIDs = new ArrayList<>(Arrays.asList(this.trusted));
        /* 450 */     trustedUUIDs.add(player.getUniqueId().toString());
        /* 451 */     this.trusted = trustedUUIDs.<String>toArray(new String[0]);
        /* 452 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void unSemiTrustPlayer(Player player) {
        /* 456 */     boolean isTrusted = false;
        /* 457 */     for (String i : this.semiTrusted) {
            /* 458 */       if (i.equalsIgnoreCase(player.getUniqueId().toString())) {
                /* 459 */         isTrusted = true;
                /*     */       }
            /*     */     }
        /* 462 */     if (!isTrusted)
            /*     */       return;
        /* 464 */     List<String> trustedUUIDs = new ArrayList<>(Arrays.asList(this.semiTrusted));
        /* 465 */     trustedUUIDs.remove(player.getUniqueId().toString());
        /* 466 */     this.semiTrusted = trustedUUIDs.<String>toArray(new String[0]);
        /* 467 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public void unTrustPlayer(Player player) {
        /* 471 */     boolean isTrusted = false;
        /* 472 */     for (String i : this.trusted) {
            /* 473 */       if (i.equalsIgnoreCase(player.getUniqueId().toString())) {
                /* 474 */         isTrusted = true;
                /*     */       }
            /*     */     }
        /* 477 */     if (!isTrusted)
            /*     */       return;
        /* 479 */     List<String> trustedUUIDs = new ArrayList<>(Arrays.asList(this.trusted));
        /* 480 */     trustedUUIDs.remove(player.getUniqueId().toString());
        /* 481 */     this.trusted = trustedUUIDs.<String>toArray(new String[0]);
        /* 482 */     HeroCraft.getPlugin().getLandManager().saveLand(this);
        /*     */   }
    /*     */
    /*     */   public String getName() {
        /* 486 */     return this.name;
        /*     */   }
    /*     */
    /*     */   public double getX1() {
        /* 490 */     return this.x1;
        /*     */   }
    /*     */
    /*     */   public double getX2() {
        /* 494 */     return this.x2;
        /*     */   }
    /*     */
    /*     */   public double getZ1() {
        /* 498 */     return this.z1;
        /*     */   }
    /*     */
    /*     */   public double getZ2() {
        /* 502 */     return this.z2;
        /*     */   }
    /*     */
    /*     */   public String getFounderName() {
        /* 506 */     return this.founderName;
        /*     */   }
    /*     */
    /*     */   public String getFounderUUID() {
        /* 510 */     return this.founderUUID;
        /*     */   }
    /*     */
    /*     */   public String[] getCoFounderNames() {
        /* 514 */     return this.coFounderNames;
        /*     */   }
    /*     */
    /*     */   public String[] getCoFounderUUIDs() {
        /* 518 */     return this.coFounderUUIDs;
        /*     */   }
    /*     */
    /*     */   public double getSpawnX() {
        /* 522 */     return this.spawnX;
        /*     */   }
    /*     */
    /*     */   public double getSpawnY() {
        /* 526 */     return this.spawnY;
        /*     */   }
    /*     */
    /*     */   public double getSpawnZ() {
        /* 530 */     return this.spawnZ;
        /*     */   }
    /*     */
    /*     */   public double getCoins() {
        /* 534 */     return this.coins;
        /*     */   }
    /*     */
    /*     */   public double getArmeeCoins() {
        /* 538 */     return this.armeeCoins;
        /*     */   }
    /*     */
    /*     */   public String[] getMemberNames() {
        /* 542 */     return this.memberNames;
        /*     */   }
    /*     */
    /*     */   public String[] getMemberUUIDs() {
        /* 546 */     return this.memberUUIDs;
        /*     */   }
    /*     */
    /*     */   public int getMaxBlocks() {
        /* 550 */     return this.maxBlocks;
        /*     */   }
    /*     */
    /*     */   public String[] getTrusted() {
        /* 554 */     return this.trusted;
        /*     */   }
    /*     */
    /*     */   public String[] getSemiTrusted() {
        /* 558 */     return this.semiTrusted;
        /*     */   }
    /*     */
    /*     */   public double getSpawnYaw() {
        /* 562 */     return this.spawnYaw;
        /*     */   }
    /*     */
    /*     */   public double getSpawnPitch() {
        /* 566 */     return this.spawnPitch;
        /*     */   }
    /*     */
    /*     */   public double getPrisonSpawnX() {
        /* 570 */     return this.prisonSpawnX;
        /*     */   }
    /*     */
    /*     */   public double getPrisonSpawnY() {
        /* 574 */     return this.prisonSpawnY;
        /*     */   }
    /*     */
    /*     */   public double getPrisonSpawnZ() {
        /* 578 */     return this.prisonSpawnZ;
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\Land.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */