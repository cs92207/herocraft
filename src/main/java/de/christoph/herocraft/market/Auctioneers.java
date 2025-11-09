package de.christoph.herocraft.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.Constant;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Auctioneers implements Listener {

    public HashMap<Player, ArrayList<Product>> pageOverviewPlayers;
    public HashMap<Player, Integer> pagePlayers;
    public HashMap<Player, Product> detailPlayers;
    public HashMap<Player, Product> offerPlayers;
    public HashMap<Player, ArrayList<Product>> yourProductsOverviewPlayers;
    public HashMap<Player, Integer> yourProductsPagePlayers;
    public HashMap<Player, Product> yourProductsDetailPlayers;

    public Auctioneers() {
        pageOverviewPlayers = new HashMap<>();
        pagePlayers = new HashMap<>();
        detailPlayers = new HashMap<>();
        offerPlayers = new HashMap<>();
        yourProductsOverviewPlayers = new HashMap<>();
        yourProductsPagePlayers = new HashMap<>();
        yourProductsDetailPlayers = new HashMap<>();
    }

    public void openPage(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::all_offers:");
        ArrayList<Product> productsFromPage = getProductsOfPage(page);
        pagePlayers.put(player, page);
        pageOverviewPlayers.put(player, productsFromPage);
        int n = 0;
        for(Product i : productsFromPage) {
            inventory.setItem(n, i.getItemStack());
            n++;
        }
        if(productsFromPage.size() >= 27)
            inventory.setItem(44, new ItemBuilder(Material.ARROW).setDisplayName("§4§lNächste Seite").build());
        if(page != 1)
            inventory.setItem(44-8, new ItemBuilder(Material.ARROW).setDisplayName("§4§lVorherige Seite").build());
        inventory.setItem(40, new ItemBuilder(Material.BARRIER).setDisplayName("§4§lZurück zum Hauptmenü").build());
        player.openInventory(inventory);
    }

    public void openYourProductsPage(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::your_offers:");
        ArrayList<Product> productsFromPage = getPlayerProductsOfPage(page, player);
        yourProductsPagePlayers.put(player, page);
        yourProductsOverviewPlayers.put(player, productsFromPage);
        int n = 0;
        for(Product i : productsFromPage) {
            inventory.setItem(n, i.getItemStack());
            n++;
        }
        if(productsFromPage.size() >= 27)
            inventory.setItem(44, new ItemBuilder(Material.ARROW).setDisplayName("§4§lNächste Seite").build());
        if(page != 1)
            inventory.setItem(44-8, new ItemBuilder(Material.ARROW).setDisplayName("§4§lVorherige Seite").build());
        inventory.setItem(39, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lAngebot erstellen").build());
        inventory.setItem(41, new ItemBuilder(Material.STONE_AXE).setCustomModelData(1000).setDisplayName("§4§lZurück zum Hauptmenü").build());
        player.openInventory(inventory);
    }

    public ArrayList<Product> getPlayerProductsOfPage(int page, Player player) {
        ArrayList<Product> products = new ArrayList<>();
        int offset = (page - 1) * Constant.ITEMS_PER_AUCTIONEERS_PAGE;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `auctioneers` WHERE `salesman_uuid` = ? LIMIT ? OFFSET ? ");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setInt(2, Constant.ITEMS_PER_AUCTIONEERS_PAGE);
            preparedStatement.setInt(3, offset);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String salesManName = resultSet.getString("salesman_name");
                String salesmanUUID = resultSet.getString("salesman_uuid");
                byte[] itemBytes = resultSet.getBytes("item_stack");
                ItemStack itemStack = itemStackFromByteArray(itemBytes);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode offers = objectMapper.readTree(resultSet.getString("offers"));
                Product product = new Product(id, salesManName, salesmanUUID, itemStack, offers);
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public ArrayList<Product> getProductsOfPage(int page) {
        ArrayList<Product> products = new ArrayList<>();
        int offset = (page - 1) * Constant.ITEMS_PER_AUCTIONEERS_PAGE;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `auctioneers` LIMIT ? OFFSET ? ");
            preparedStatement.setInt(1, Constant.ITEMS_PER_AUCTIONEERS_PAGE);
            preparedStatement.setInt(2, offset);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String salesManName = resultSet.getString("salesman_name");
                String salesmanUUID = resultSet.getString("salesman_uuid");
                byte[] itemBytes = resultSet.getBytes("item_stack");
                ItemStack itemStack = itemStackFromByteArray(itemBytes);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode offers = objectMapper.readTree(resultSet.getString("offers"));
                Product product = new Product(id, salesManName, salesmanUUID, itemStack, offers);
                products.add(product);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public static byte[] itemStackToByteArray(ItemStack itemStack) {
        if (itemStack == null) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objectStream = new BukkitObjectOutputStream(byteStream)) {

            Map<String, Object> itemData = new HashMap<>();
            itemData.put("type", itemStack.getType().name());
            itemData.put("amount", itemStack.getAmount());
            itemData.put("durability", itemStack.getDurability()); // Use getDamage() in newer versions

            // Add more data as needed

            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();

                if (itemMeta.hasDisplayName()) {
                    itemData.put("display_name", itemMeta.getDisplayName());
                }

                if (itemMeta.hasLore()) {
                    itemData.put("lore", itemMeta.getLore());
                }

                if (itemMeta instanceof SkullMeta) {
                    SkullMeta skullMeta = (SkullMeta) itemMeta;
                    if (skullMeta.hasOwner()) {
                        itemData.put("skull_owner", skullMeta.getOwner());
                    }
                }

                if (itemMeta instanceof BannerMeta) {
                    BannerMeta bannerMeta = (BannerMeta) itemMeta;
                    DyeColor baseColor = bannerMeta.getPattern(0).getColor();
                    if (baseColor != null) {
                        itemData.put("banner_base_color", baseColor.name());
                    }
                    List<Map<String, String>> patterns = new ArrayList<>();
                    for (Pattern pattern : bannerMeta.getPatterns()) {
                        Map<String, String> patternData = new HashMap<>();
                        patternData.put("pattern_type", pattern.getPattern().name());
                        patternData.put("pattern_color", pattern.getColor().name());
                        patterns.add(patternData);
                    }
                    itemData.put("banner_patterns", patterns);
                }

                if (itemMeta.hasEnchants()) {
                    Map<String, Integer> enchantments = new HashMap<>();
                    for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                        enchantments.put(enchantment.getKey().getKey(), itemMeta.getEnchantLevel(enchantment));
                    }
                    itemData.put("enchantments", enchantments);
                }

                // Add more meta data as needed
            }

            objectStream.writeObject(itemData);
            return byteStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static ItemStack itemStackFromByteArray(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             ObjectInputStream objectStream = new BukkitObjectInputStream(byteStream)) {

            Map<String, Object> itemData = (Map<String, Object>) objectStream.readObject();

            Material material = Material.getMaterial((String) itemData.get("type"));
            int amount = (int) itemData.get("amount");
            short durability = ((Number) itemData.get("durability")).shortValue(); // Use getDamage() in newer versions

            ItemStack itemStack = new ItemStack(material, amount, durability);

            if (itemData.containsKey("display_name")) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName((String) itemData.get("display_name"));
                itemStack.setItemMeta(itemMeta);
            }

            if (itemData.containsKey("lore")) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore((List<String>) itemData.get("lore"));
                itemStack.setItemMeta(itemMeta);
            }

            if (itemData.containsKey("enchantments")) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                Map<String, Integer> enchantments = (Map<String, Integer>) itemData.get("enchantments");
                for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(entry.getKey()));
                    if (enchantment != null) {
                        itemMeta.addEnchant(enchantment, entry.getValue(), true);
                    }
                }
                itemStack.setItemMeta(itemMeta);
            }

            if (itemData.containsKey("skull_owner") && itemStack.getItemMeta() instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwner((String) itemData.get("skull_owner"));
                itemStack.setItemMeta(skullMeta);
            }

            if (itemData.containsKey("banner_base_color") && itemStack.getItemMeta() instanceof BannerMeta) {
                BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
                if (itemData.containsKey("banner_patterns")) {
                    List<Map<String, String>> patterns = (List<Map<String, String>>) itemData.get("banner_patterns");
                    for (Map<String, String> patternData : patterns) {
                        PatternType patternType = PatternType.valueOf(patternData.get("pattern_type"));
                        DyeColor patternColor = DyeColor.valueOf(patternData.get("pattern_color"));
                        bannerMeta.addPattern(new Pattern(patternColor, patternType));
                    }
                }
                itemStack.setItemMeta(bannerMeta);
            }


            return itemStack;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void openDetailPage(Player player, Product product) {
        pagePlayers.remove(player);
        pageOverviewPlayers.remove(player);
        ArrayNode offersArrayNode = (ArrayNode) product.getOffers();
        List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
        Iterator<JsonNode> iterator = offersArrayNode.elements();
       while (iterator.hasNext()) {
            JsonNode offerNode = iterator.next();
            String offersName = offerNode.get("offersName").asText();
            String offersUUID = offerNode.get("offersUUID").asText();
            int offersPrice = offerNode.get("offersPrice").asInt();
            AuctioneersOffer auctioneersOffer = new AuctioneersOffer(offersName, offersUUID, offersPrice);
            auctioneersOffers.add(auctioneersOffer);
        }
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::offer_detail:");
       inventory.setItem(11, product.getItemStack());
        inventory.setItem(15, new ItemBuilder(Material.GOLD_NUGGET).setDisplayName("§4§lAngebot abgeben").build());
        int i = 27;
        for(AuctioneersOffer current : auctioneersOffers) {
            if(i > 44) {
                continue;
            }
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setDisplayName("§e§l" + current.getOffersPrice() + " Coins");
            skullMeta.setOwner(current.getOffersName());
            itemStack.setItemMeta(skullMeta);
            inventory.setItem(i, itemStack);
            i++;
        }
        player.openInventory(inventory);
        detailPlayers.put(player, product);
    }

    public void openYourProductsDetailPage(Player player, Product product) {
        yourProductsPagePlayers.remove(player);
        yourProductsOverviewPlayers.remove(player);
        yourProductsDetailPlayers.put(player, product);
        ArrayNode offersArrayNode = (ArrayNode) product.getOffers();
        List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
        Iterator<JsonNode> iterator = offersArrayNode.elements();
        while (iterator.hasNext()) {
            JsonNode offerNode = iterator.next();
            String offersName = offerNode.get("offersName").asText();
            String offersUUID = offerNode.get("offersUUID").asText();
            int offersPrice = offerNode.get("offersPrice").asInt();
            AuctioneersOffer auctioneersOffer = new AuctioneersOffer(offersName, offersUUID, offersPrice);
            auctioneersOffers.add(auctioneersOffer);
        }
        Inventory inventory = Bukkit.createInventory(null, 9*5, "§4§lProdukt verwalten");
        inventory.setItem(11, product.getItemStack());
        inventory.setItem(13, new ItemBuilder(Material.BARRIER).setDisplayName("§4§lProdukt entfernen").build());
        int i = 27;
        for(AuctioneersOffer current : auctioneersOffers) {
            if(i > 44) {
                continue;
            }
            ArrayList<String> lore = new ArrayList();
            lore.add("");
            lore.add(current.getOffersUUID());
            lore.add("");
            lore.add("§eLinksklick §7- annehmen");
            lore.add("§eRechtsklick §7- entfernen");
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setDisplayName("§e§l" + current.getOffersPrice() + " Coins");
            skullMeta.setLore(lore);
            skullMeta.setOwner(current.getOffersName());
            itemStack.setItemMeta(skullMeta);
            inventory.setItem(i, itemStack);
            i++;
        }
        player.openInventory(inventory);
    }

    public void openCreateProductPage(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*5, ":offset_-16::create_offer:");
        inventory.setItem((9*5)-1, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("§4§lWeiter").build());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onCreateProductPageClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::create_offer:"))
            return;
        if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            if(displayName.contains("§4§lHier das zu Verkaufende Item reinlegen")) {
                event.setCancelled(true);
            } else if(displayName.equalsIgnoreCase("§4§lWeiter")) {
                event.setCancelled(true);
                ItemStack itemStack = player.getOpenInventory().getTopInventory().getItem(22);
                if(itemStack == null || itemStack.getType().equals(Material.AIR)) {
                    player.sendMessage(Constant.PREFIX + "§7Bitte lege das angebotene Item in den entsprechenden Slot.");
                    return;
                }
                sellItem(player, itemStack);
                player.closeInventory();
                player.sendMessage(Constant.PREFIX + "§7Angebot erstellt");
            }
        }
    }

    public void sellItem(Player player, ItemStack itemStack) {
        byte[] convertedItemStack = itemStackToByteArray(itemStack);
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `auctioneers` (`salesman_name`,`salesman_uuid`,`item_stack`,`offers`) VALUES (?,?,?,?)");
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.setBytes(3, convertedItemStack);
            List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString;
            try {
                jsonString = objectMapper.writeValueAsString(auctioneersOffers);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            preparedStatement.setString(4, jsonString);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `auctioneers_coins_getter` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            int amount = 0;
            while (resultSet.next()) {
                i++;
                amount += resultSet.getDouble("amount");
            }
            if(i != 0)
                player.sendMessage(Constant.PREFIX + "§e" + i + " §7deiner Angebote wurden abgelehnt. Rückerstattung: §a" + amount + " Coins§7.");
            HeroCraft.getPlugin().getCoin().addMoney(player, amount);
            PreparedStatement preparedStatement2 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `auctioneers_coins_getter` WHERE `uuid` = ?");
            preparedStatement2.setString(1, player.getUniqueId().toString());
            preparedStatement2.execute();
            PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT * FROM `auctioneers_item_getter` WHERE `uuid` = ?");
            preparedStatement1.setString(1, player.getUniqueId().toString());
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            int n = 0;
            while (resultSet1.next()) {
                if(hasFreeSlot(player)) {
                    n++;
                    player.getInventory().addItem(itemStackFromByteArray(resultSet1.getBytes("item_stack")));
                    PreparedStatement preparedStatement3 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `auctioneers_item_getter` WHERE `item_stack` = ?");
                    preparedStatement3.setBytes(1, resultSet1.getBytes("item_stack"));
                    preparedStatement3.execute();
                }
            }
            if(n != 0)
                player.sendMessage(Constant.PREFIX + "§e" + n + " §7deiner Angebote wurden §aangenommen§7. Du hast entsprechende Items nun in deinem Inventar.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean hasFreeSlot(Player player) {
        PlayerInventory inventar = player.getInventory();
        for (ItemStack stapel : inventar.getContents()) {
            if (stapel == null || stapel.getAmount() == 0) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onYourProductDetailPageClick(InventoryClickEvent event) throws SQLException {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!event.getView().getTitle().equalsIgnoreCase("§4§lProdukt verwalten"))
            return;
        event.setCancelled(true);
        Product product = yourProductsDetailPlayers.get(player);
        if(event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§4§lProdukt entfernen")) {
            ArrayNode offersArrayNode = (ArrayNode) product.getOffers();
            List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
            Iterator<JsonNode> iterator = offersArrayNode.elements();
            while (iterator.hasNext()) {
                JsonNode offerNode = iterator.next();
                String offersName = offerNode.get("offersName").asText();
                String offersUUID = offerNode.get("offersUUID").asText();
                int offersPrice = offerNode.get("offersPrice").asInt();
                AuctioneersOffer auctioneersOffer = new AuctioneersOffer(offersName, offersUUID, offersPrice);
                auctioneersOffers.add(auctioneersOffer);
            }
            for(AuctioneersOffer i : auctioneersOffers) {
                PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `auctioneers_coins_getter` (`uuid`,`amount`) VALUES (?,?)");
                preparedStatement1.setString(1, i.getOffersUUID());
                preparedStatement1.setDouble(2, i.getOffersPrice());
                preparedStatement1.execute();
            }

            PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `auctioneers` WHERE `id` = ?");
            preparedStatement1.setInt(1, product.getId());
            preparedStatement1.execute();
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Angebot entfernt.");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1);

            return;
        }
        if(!displayName.contains(" Coins"))
            return;
        if(!displayName.contains("§e§l"))
            return;
        if(event.getSlot() == 11)
            return;
        String offerUUID = event.getCurrentItem().getItemMeta().getLore().get(1);
        if(event.getAction() == InventoryAction.PICKUP_ALL) { // Leftclick

            ArrayNode offersArrayNode = (ArrayNode) product.getOffers();
            List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
            Iterator<JsonNode> iterator = offersArrayNode.elements();
            while (iterator.hasNext()) {
                JsonNode offerNode = iterator.next();
                String offersName = offerNode.get("offersName").asText();
                String offersUUID = offerNode.get("offersUUID").asText();
                int offersPrice = offerNode.get("offersPrice").asInt();
                AuctioneersOffer auctioneersOffer = new AuctioneersOffer(offersName, offersUUID, offersPrice);
                auctioneersOffers.add(auctioneersOffer);
            }
            AuctioneersOffer current = null;
            for(AuctioneersOffer i : auctioneersOffers) {
                if(i.getOffersUUID().equalsIgnoreCase(offerUUID))
                    current = i;
            }
            double price = current.getOffersPrice();
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `auctioneers_item_getter` (`uuid`,`item_stack`) VALUES (?,?)");
            preparedStatement.setString(1, offerUUID);
            preparedStatement.setBytes(2, itemStackToByteArray(product.getItemStack()));
            preparedStatement.execute();
            HeroCraft.getPlugin().coin.addMoney(player, price);
            auctioneersOffers.remove(current);

            // Insert other offers to coins getter database

            for(AuctioneersOffer i : auctioneersOffers) {
                PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `auctioneers_coins_getter` (`uuid`,`amount`) VALUES (?,?)");
                preparedStatement1.setString(1, i.getOffersUUID());
                preparedStatement1.setDouble(2, i.getOffersPrice());
                preparedStatement1.execute();
            }

            PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("DELETE FROM `auctioneers` WHERE `id` = ?");
            preparedStatement1.setInt(1, product.getId());
            preparedStatement1.execute();
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Angebot angenommen (§a+" + price + " Coins§7)");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        } else if(event.getAction() == InventoryAction.PICKUP_HALF) { // Rightclick
            ArrayNode offersArrayNode = (ArrayNode) product.getOffers();
            List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
            Iterator<JsonNode> iterator = offersArrayNode.elements();
            while (iterator.hasNext()) {
                JsonNode offerNode = iterator.next();
                String offersName = offerNode.get("offersName").asText();
                String offersUUID = offerNode.get("offersUUID").asText();
                int offersPrice = offerNode.get("offersPrice").asInt();
                AuctioneersOffer auctioneersOffer = new AuctioneersOffer(offersName, offersUUID, offersPrice);
                auctioneersOffers.add(auctioneersOffer);
            }
            AuctioneersOffer currentOffer = null;
            for(AuctioneersOffer current : auctioneersOffers) {
                if(current.getOffersUUID().equals(offerUUID)) {
                    currentOffer = current;
                }
            }
            auctioneersOffers.remove(currentOffer);
            PreparedStatement preparedStatement1 = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("INSERT INTO `auctioneers_coins_getter`(`uuid`,`amount`) VALUES (?,?)");
            preparedStatement1.setString(1, currentOffer.getOffersUUID());
            preparedStatement1.setDouble(2, currentOffer.getOffersPrice());
            preparedStatement1.execute();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString;
            try {
                jsonString = objectMapper.writeValueAsString(auctioneersOffers);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `auctioneers` SET `offers` = ? WHERE `id` = ?");
                preparedStatement.setString(1, jsonString);
                preparedStatement.setInt(2, product.getId());
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            player.closeInventory();
            player.sendMessage(Constant.PREFIX + "§7Angebot entfernt.");
        }
    }

    @EventHandler
    public void onYourProductsOverviewPageClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().contains(":offset_-16::your_offers:"))
            return;
        event.setCancelled(true);
        if(!yourProductsPagePlayers.containsKey(player))
            return;
        if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lNächste Seite")) {
            openYourProductsPage(player, yourProductsPagePlayers.get(player) + 1);
            return;
        } else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lVorherige Seite")) {
            openYourProductsPage(player, yourProductsPagePlayers.get(player) - 1);
            return;
        } else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lAngebot erstellen")) {
            openCreateProductPage(player);
            return;
        } else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lZurück zum Hauptmenü")) {
            pagePlayers.remove(player);
            pageOverviewPlayers.remove(player);
            player.closeInventory();
            player.performCommand("auktionshaus");
            return;
        }
        ArrayList<Product> products = yourProductsOverviewPlayers.get(player);
        Product current = products.get(event.getSlot());
        openYourProductsDetailPage(player, current);
        yourProductsPagePlayers.remove(player);
        yourProductsOverviewPlayers.remove(player);
    }

    @EventHandler
    public void onOverviewPageClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!pagePlayers.containsKey(player))
            return;
        if(!event.getView().getTitle().contains(":offset_-16::all_offers:"))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        System.out.println(pagePlayers.get(player));
        if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lNächste Seite")) {
            openPage(player, pagePlayers.get(player) + 1);
            return;
        } else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lVorherige Seite")) {
            openPage(player, pagePlayers.get(player) - 1);
            return;
        } else if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lZurück zum Hauptmenü")) {
            pagePlayers.remove(player);
            pageOverviewPlayers.remove(player);
            player.closeInventory();
            player.performCommand("auktionshaus");
            return;
        }
        event.setCancelled(true);
        ArrayList<Product> products = pageOverviewPlayers.get(player);
        Product current = products.get(event.getSlot());
        openDetailPage(player, current);
        pagePlayers.remove(player);
        pageOverviewPlayers.remove(player);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(offerPlayers.containsKey(player)) {
            player.sendMessage(Constant.PREFIX + "§7Vorgang abgebrochen!");
            offerPlayers.remove(player);
        }
    }

    @EventHandler
    public void onDetailPageClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        if(!event.getView().getTitle().contains(":offset_-16::offer_detail:"))
            return;
        event.setCancelled(true);
        if(!detailPlayers.containsKey(player))
            return;
        Product detailProduct = detailPlayers.get(player);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if(!event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lAngebot abgeben"))
            return;
        if(detailProduct.getSalesManUUID().equalsIgnoreCase(player.getUniqueId().toString())) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Du kannst nicht auf dein eigenes Produkt bieten.");
            return;
        }
        ArrayNode offersArrayNode = (ArrayNode) detailProduct.getOffers();
        List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
        Iterator<JsonNode> iterator = offersArrayNode.elements();
        while (iterator.hasNext()) {
            JsonNode offerNode = iterator.next();
            String offersName = offerNode.get("offersName").asText();
            String offersUUID = offerNode.get("offersUUID").asText();
            int offersPrice = offerNode.get("offersPrice").asInt();
            AuctioneersOffer auctioneersOffer = new AuctioneersOffer(offersName, offersUUID, offersPrice);
            auctioneersOffers.add(auctioneersOffer);
        }
        if(auctioneersOffers.size() >= 19) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Constant.PREFIX + "§7Dieses Produkt kann keine weiteren Angebote aufnehmen.");
            return;
        }
        detailPlayers.remove(player);
        offerPlayers.put(player, detailProduct);
        player.sendMessage(Constant.PREFIX + "§7Wieviel möchtest du auf dieses Produkt bieten? Schreibe es in den Chat!");
        player.sendMessage("§4Zum Abbrechen, sneaken!");
        player.closeInventory();
    }

    @EventHandler
    public void onPlayerOfferChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!offerPlayers.containsKey(player))
            return;
        event.setCancelled(true);
        double offerPrice;
        try {
            offerPrice = Double.parseDouble(event.getMessage());
        } catch (NumberFormatException e) {
            player.sendMessage(Constant.PREFIX + "§7Dies ist kein gültiger Preis. Versuche es erneut.");
            player.sendMessage("§4Sneaken zum abbrechen!");
            return;
        }
        Product product = offerPlayers.get(player);
        if(offerPrice <= 0) {
            player.sendMessage(Constant.PREFIX + "§7Du musst mindestens einen Coin bieten. Versuche es erneut.");
            return;
        }
        if(HeroCraft.getPlugin().coin.getCoins(player) < offerPrice) {
            player.sendMessage(Constant.PREFIX + "§7So viele Coins hast du nicht, versuche es erneut.");
            return;
        }
        ArrayNode offersArrayNode = (ArrayNode) product.getOffers();
        List<AuctioneersOffer> auctioneersOffers = new ArrayList<>();
        Iterator<JsonNode> iterator = offersArrayNode.elements();
        while (iterator.hasNext()) {
            JsonNode offerNode = iterator.next();
            String offersName = offerNode.get("offersName").asText();
            String offersUUID = offerNode.get("offersUUID").asText();
            int offersPrice = offerNode.get("offersPrice").asInt();
            AuctioneersOffer auctioneersOffer = new AuctioneersOffer(offersName, offersUUID, offersPrice);
            auctioneersOffers.add(auctioneersOffer);
        }
        auctioneersOffers.removeIf(current -> current.getOffersUUID().equalsIgnoreCase(player.getUniqueId().toString()));
        AuctioneersOffer newOffer = new AuctioneersOffer(player.getName(), player.getUniqueId().toString(), offerPrice);
        auctioneersOffers.add(newOffer);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(auctioneersOffers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("UPDATE `auctioneers` SET `offers` = ? WHERE `id` = ?");
            preparedStatement.setString(1, jsonString);
            preparedStatement.setInt(2, product.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        player.sendMessage(Constant.PREFIX + "§7Angebot abgegeben. Du wirst benachrichtigt, wenn es angenommen wurde.");
        offerPlayers.remove(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                HeroCraft.getPlugin().coin.removeMoney(player, offerPrice);
            }
        }, 10);
    }

    @EventHandler
    public void onYourProductDetailPageClick(InventoryCloseEvent event) {
        if(!event.getView().getTitle().contains("§4§lProdukt Verwalten"))
            return;
        if(!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(player.getOpenInventory().getTitle().contains("§4§lProdukt Verwalten"))
                    return;
                yourProductsDetailPlayers.remove(player);
            }
        }, 10);
    }

    @EventHandler
    public void onOverviewClose(InventoryCloseEvent event) {
        if(!event.getView().getTitle().contains(":offset_-16::all_offers:"))
            return;
        if(!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(!event.getPlayer().getOpenInventory().getTitle().contains(":offset_-16::all_offers:")) {
                    pagePlayers.remove(player);
                    pageOverviewPlayers.remove(player);
                }
            }
        }, 10);
    }

    @EventHandler
    public void onDetailPageClose(InventoryCloseEvent event) {
        if(!event.getView().getTitle().contains(":offset_-16::offer_detail:"))
            return;
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(!event.getPlayer().getOpenInventory().getTitle().contains(":offset_-16::offer_detail:")) {
                    pagePlayers.remove(player);
                    pageOverviewPlayers.remove(player);
                    detailPlayers.remove(player);
                }
            }
        }, 10);
    }

    @EventHandler
    public void onYourProductsOverviewPageClose(InventoryCloseEvent event) {
        if(!event.getView().getTitle().contains(":offset_-16::your_offers:"))
            return;
        if(!(event.getPlayer() instanceof Player))
            return;
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                yourProductsOverviewPlayers.remove(player);
                yourProductsPagePlayers.remove(player);
            }
        }, 10);
    }

    @EventHandler
    public void onMainMenuClick(InventoryClickEvent event) {
        if(!event.getView().getTitle().equalsIgnoreCase(":offset_-16::auctioneer_main:"))
            return;
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lAlle Angebote")) {
            openPage(player, 1);
        } else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4§lDeine Angebote")) {
            openYourProductsPage(player, 1);
        }
    }

}
