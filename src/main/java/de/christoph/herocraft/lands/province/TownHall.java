/*     */ package de.christoph.herocraft.lands.province;
/*     */
/*     */ import de.christoph.herocraft.HeroCraft;
/*     */ import de.christoph.herocraft.lands.Land;
/*     */ import de.christoph.herocraft.lands.LandManager;
/*     */ import de.christoph.herocraft.protection.ProtectionListener;
/*     */ import dev.lone.itemsadder.api.CustomFurniture;
/*     */ import dev.lone.itemsadder.api.CustomStack;
/*     */ import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
/*     */ import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
/*     */ import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
/*     */ import dev.lone.itemsadder.api.ItemsAdder;
/*     */ import java.util.HashMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*     */ import org.bukkit.event.player.PlayerToggleSneakEvent;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.plugin.Plugin;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class TownHall
        /*     */   implements Listener
        /*     */ {
    /*  32 */   public static HashMap<Player, Province> townhallProvinceCreationPlayersNameProvince = new HashMap<>();
    /*  33 */   public static HashMap<Player, Province> townhallProvinceCreationPlayers = new HashMap<>();
    /*  34 */   public static HashMap<Player, CustomFurniture> townhallProvinceCreationPlayersNames = new HashMap<>();
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFurniturePlace(FurniturePlaceSuccessEvent event) {
        /*  38 */     Player player = event.getPlayer();
        /*  39 */     if (!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRathaus"))
            /*     */       return;
        /*  41 */     if (CityBlock.townHallSetPlayers.containsKey(event.getPlayer())) {
            /*  42 */       Location location = event.getFurniture().getEntity().getLocation();
            /*  43 */       double x1 = location.getX() + 50.0D;
            /*  44 */       double z1 = location.getZ() + 50.0D;
            /*  45 */       double x2 = location.getX() - 50.0D;
            /*  46 */       double z2 = location.getZ() - 50.0D;
            /*  47 */       String worldName = location.getWorld().getName();
            /*  48 */       if (worldName.equalsIgnoreCase("autumDimension") || worldName.equalsIgnoreCase("winterDimension") || worldName.equalsIgnoreCase("springDimension") || worldName.equalsIgnoreCase("summerDimension")) {
                /*  49 */         x1 += 100.0D;
                /*  50 */         z1 += 100.0D;
                /*  51 */         x2 -= 100.0D;
                /*  52 */         z2 -= 100.0D;
                /*     */       }
            /*  54 */       String world = location.getWorld().getName();
            /*  55 */       if (!LandManager.canCreateLandProvinceLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), world, "", "")) {
                /*  56 */         if (!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                    /*  57 */           player.sendMessage("§e§lAnyBlocks §7§l| §7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                    /*  58 */           event.getFurniture().remove(false);
                    /*  59 */           ItemStack goverment = null;
                    /*  60 */           for (CustomStack i : ItemsAdder.getAllItems()) {
                        /*  61 */             if (i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                            /*  62 */               goverment = i.getItemStack();
                            /*     */             }
                        /*     */           }
                    /*  65 */           player.getInventory().addItem(new ItemStack[] { goverment });
                    /*     */         }
                /*     */         return;
                /*     */       }
            /*  69 */       if (world.equalsIgnoreCase("world") &&
                    /*  70 */         !LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                /*  71 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                /*  72 */         event.getFurniture().remove(false);
                /*  73 */         ItemStack goverment = null;
                /*  74 */         for (CustomStack i : ItemsAdder.getAllItems()) {
                    /*  75 */           if (i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                        /*  76 */             goverment = i.getItemStack();
                        /*     */           }
                    /*     */         }
                /*  79 */         player.getInventory().addItem(new ItemStack[] { goverment });
                /*     */
                /*     */         return;
                /*     */       }
            /*  83 */       Province province1 = new Province(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).getName(), CityBlock.townHallSetPlayers.get(player), x1, z1, x2, z2, world);
            /*  84 */       HeroCraft.getPlugin().getProvinceManager().getProvinces().add(province1);
            /*  85 */       HeroCraft.getPlugin().getProvinceManager().saveProvince(province1);
            /*  86 */       event.getFurniture().getEntity().setCustomName("§7Stadt: §e§l" + province1.getName());
            /*  87 */       event.getFurniture().getEntity().setCustomNameVisible(true);
            /*  88 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Stadt erfolgreich erstellt.");
            /*  89 */       CityBlock.townHallSetPlayers.remove(player);
            /*     */       return;
            /*     */     }
        /*  92 */     Province province = ProvinceManager.getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        /*  93 */     if (province != null && province.canBuild(player)) {
            /*  94 */       event.getFurniture().remove(false);
            /*  95 */       ItemStack goverment = null;
            /*  96 */       for (CustomStack i : ItemsAdder.getAllItems()) {
                /*  97 */         if (i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                    /*  98 */           goverment = i.getItemStack();
                    /*     */         }
                /*     */       }
            /* 101 */       player.getInventory().addItem(new ItemStack[] { goverment });
            /* 102 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Bitte setze das Rathaus in deine Stadt.");
            /* 103 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Rathaus für die Stadt §a" + province.getName() + " §7deines Landes §e" + province.getLand() + " §7gesetzt.");
            /* 104 */       event.getFurniture().getEntity().setCustomName("§7Stadt: §e§l" + province.getName());
            /* 105 */       event.getFurniture().getEntity().setCustomNameVisible(true);
            /*     */     } else {
            /* 107 */       if (ProtectionListener.isInDangerZone(event.getFurniture().getEntity().getLocation())) {
                /* 108 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Die Stadt ist zu nahe am §cSpawn§7.");
                /* 109 */         event.getFurniture().remove(true);
                /*     */
                /*     */         return;
                /*     */       }
            /* 113 */       if (HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player) == null) {
                /* 114 */         event.getFurniture().remove(true);
                /* 115 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Du benötigst zuerst ein Land, bevor du eine Stadt erstellen kannst!");
                /* 116 */         player.sendMessage("");
                /* 117 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Gehe zum Spawn, oder platziere irgendwo ein Regierungsgebäude!");
                /*     */         return;
                /*     */       }
            /* 120 */       Location location = event.getFurniture().getEntity().getLocation();
            /* 121 */       double x1 = location.getX() + 50.0D;
            /* 122 */       double z1 = location.getZ() + 50.0D;
            /* 123 */       double x2 = location.getX() - 50.0D;
            /* 124 */       double z2 = location.getZ() - 50.0D;
            /* 125 */       String worldName = location.getWorld().getName();
            /* 126 */       if (worldName.equalsIgnoreCase("autumDimension") || worldName.equalsIgnoreCase("winterDimension") || worldName.equalsIgnoreCase("springDimension") || worldName.equalsIgnoreCase("summerDimension")) {
                /* 127 */         x1 += 100.0D;
                /* 128 */         z1 += 100.0D;
                /* 129 */         x2 -= 100.0D;
                /* 130 */         z2 -= 100.0D;
                /*     */       }
            /* 132 */       String world = location.getWorld().getName();
            /* 133 */       if (!LandManager.canCreateLandProvinceLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getProvinceManager().getProvinces(), world, "", "")) {
                /* 134 */         if (!LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                    /* 135 */           player.sendMessage("§e§lAnyBlocks §7§l| §7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                    /* 136 */           event.getFurniture().remove(false);
                    /* 137 */           ItemStack goverment = null;
                    /* 138 */           for (CustomStack i : ItemsAdder.getAllItems()) {
                        /* 139 */             if (i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                            /* 140 */               goverment = i.getItemStack();
                            /*     */             }
                        /*     */           }
                    /* 143 */           player.getInventory().addItem(new ItemStack[] { goverment });
                    /*     */         }
                /*     */         return;
                /*     */       }
            /* 147 */       if (world.equalsIgnoreCase("world") &&
                    /* 148 */         !LandManager.canCreateLandLocation(x1, z1, x2, z2, HeroCraft.getPlugin().getLandManager().getAllLands(), "")) {
                /* 149 */         player.sendMessage("§e§lAnyBlocks §7§l| §7Die Stadt wäre zu nah an einem anderen Land. Versuche es erneut!");
                /* 150 */         event.getFurniture().remove(false);
                /* 151 */         ItemStack goverment = null;
                /* 152 */         for (CustomStack i : ItemsAdder.getAllItems()) {
                    /* 153 */           if (i.getDisplayName().equalsIgnoreCase("§4§lRathaus")) {
                        /* 154 */             goverment = i.getItemStack();
                        /*     */           }
                    /*     */         }
                /* 157 */         player.getInventory().addItem(new ItemStack[] { goverment });
                /*     */
                /*     */         return;
                /*     */       }
            /* 161 */       Province province1 = new Province(HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).getName(), "", x1, z1, x2, z2, world);
            /* 162 */       townhallProvinceCreationPlayersNameProvince.put(player, province1);
            /* 163 */       townhallProvinceCreationPlayersNames.put(player, event.getFurniture());
            /* 164 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Wie soll die Stadt heißen: (§0Schreibe in den Chat§7)");
            /* 165 */       player.sendMessage("§cSneake zum abbrechen!");
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onChat(AsyncPlayerChatEvent event) {
        /* 171 */     final Player player = event.getPlayer();
        /* 172 */     Land land = HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player);
        /* 173 */     if (!townhallProvinceCreationPlayersNames.containsKey(player)) {
            /*     */       return;
            /*     */     }
        /* 176 */     event.setCancelled(true);
        /* 177 */     String name = event.getMessage();
        /* 178 */     if (name.contains(" ")) {
            /* 179 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Der Name darf leine Leerzeichen. Veruche es erneut!");
            /*     */       return;
            /*     */     }
        /* 182 */     if (name.matches(".*[^a-zA-Z0-9 ].*")) {
            /* 183 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Der Name darf keine Sonderzeichen enthalten. Veruche es erneut!");
            /*     */       return;
            /*     */     }
        /* 186 */     if (name.length() > 25) {
            /* 187 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Der Name darf höchstens 25 Zeichen haben. Veruche es erneut!");
            /*     */       return;
            /*     */     }
        /* 190 */     Province provinceNameCheck = HeroCraft.getPlugin().getProvinceManager().getProvinceByName(land.getName(), name);
        /* 191 */     if (provinceNameCheck != null) {
            /* 192 */       player.sendMessage("§e§lAnyBlocks §7§l| §7Dein Land hat bereits eine Stadt mit diesem Namen. Versuche es erneut!");
            /*     */       return;
            /*     */     }
        /* 195 */     CustomFurniture customFurniture = townhallProvinceCreationPlayersNames.get(player);
        /* 196 */     customFurniture.getEntity().setCustomName("§7Stadt: §e§l" + name);
        /* 197 */     customFurniture.getEntity().setCustomNameVisible(true);
        /*     */
        /* 199 */     townhallProvinceCreationPlayersNames.remove(player);
        /* 200 */     Province province = townhallProvinceCreationPlayersNameProvince.get(player);
        /* 201 */     province.setName(name);
        /* 202 */     townhallProvinceCreationPlayers.put(player, province);
        /* 203 */     townhallProvinceCreationPlayersNameProvince.remove(player);
        /* 204 */     player.sendMessage("");
        /* 205 */     player.sendMessage("");
        /* 206 */     player.sendMessage("");
        /* 207 */     player.sendMessage("");
        /* 208 */     player.sendMessage("");
        /* 209 */     player.sendMessage("");
        /* 210 */     player.sendMessage("");
        /* 211 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Platziere nun den Stadtblock §ainnerhalb deines Landes§7.");
        /* 212 */     Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                /*     */         {
            /*     */           public void run() {
                /* 215 */             HeroCraft.getPlugin().getLandManager().getLandFromPlayer(player).teleportTo(player);
                /* 216 */             Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                        /*     */                 {
                    /*     */                   public void run() {
                        /* 219 */                     ItemStack goverment = null;
                        /* 220 */                     for (CustomStack i : ItemsAdder.getAllItems()) {
                            /* 221 */                       if (i.getDisplayName().equalsIgnoreCase("§4§lStadt")) {
                                /* 222 */                         goverment = i.getItemStack();
                                /*     */                       }
                            /*     */                     }
                        /* 225 */                     player.getInventory().addItem(new ItemStack[] { goverment } );
                        /*     */                   }
                    /*     */                 },  40L);
                /*     */           }
            /*     */         }, 20L);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onSneak(PlayerToggleSneakEvent event) {
        /* 234 */     Player player = event.getPlayer();
        /* 235 */     if (!townhallProvinceCreationPlayersNames.containsKey(player))
            /*     */       return;
        /* 237 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Vorgang abgebrochen...");
        /* 238 */     ((CustomFurniture)townhallProvinceCreationPlayersNames.get(player)).remove(true);
        /* 239 */     townhallProvinceCreationPlayersNames.remove(player);
        /* 240 */     townhallProvinceCreationPlayersNameProvince.remove(player);
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFurnitureBreak(FurnitureBreakEvent event) {
        /* 245 */     Player player = event.getPlayer();
        /* 246 */     if (!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRathaus"))
            /*     */       return;
        /* 248 */     Province province = ProvinceManager.getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        /* 249 */     if (province == null)
            /*     */       return;
        /* 251 */     Land land = HeroCraft.getPlugin().getLandManager().getLandByName(province.getLand());
        /* 252 */     if (!land.canBuild(player)) {
            /* 253 */       event.setCancelled(true);
            /*     */       return;
            /*     */     }
        /* 256 */     player.sendMessage("§e§lAnyBlocks §7§l| §7Setze das Rathaus der Stadt §e" + province.getName() + "§7 nun an eine andere Stelle.");
        /*     */   }
    /*     */
    /*     */   @EventHandler
    /*     */   public void onFurnitureInteract(FurnitureInteractEvent event) {
        /* 261 */     final Player player = event.getPlayer();
        /* 262 */     if (!event.getFurniture().getDisplayName().equalsIgnoreCase("§4§lRathaus"))
            /*     */       return;
        /* 264 */     Province province = ProvinceManager.getProvinceAtLocation(event.getFurniture().getEntity().getLocation(), HeroCraft.getPlugin().getProvinceManager().getProvinces());
        /* 265 */     if (province == null)
            /*     */       return;
        /* 267 */     final Land land = HeroCraft.getPlugin().getLandManager().getLandByName(province.getLand());
        /* 268 */     player.sendTitle("§e§lReise gestartet", "§7...Zum Land §a" + land.getName());
        /* 269 */     player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
        /* 270 */     Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)HeroCraft.getPlugin(), new Runnable()
                /*     */         {
            /*     */           public void run() {
                /* 273 */             land.teleportTo(player);
                /*     */           }
            /*     */         },  60L);
        /*     */   }
    /*     */ }


/* Location:              C:\Users\schmi\Desktop\Allgemein\Programmieren\Speicher\WebApps\HeroCraft-1.0-SNAPSHOT-shaded.jar!\de\christoph\herocraft\lands\province\TownHall.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */