package de.christoph.herocraft.market;

import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.ItemStack;

public class Product {

    private int id;
    private String salesManName;
    private String salesManUUID;
    private ItemStack itemStack;
    private JsonNode offers;

   public Product(int id, String salesManName, String salesManUUID, ItemStack itemStack, JsonNode offers) {
        this.id = id;
        this.salesManName = salesManName;
        this.salesManUUID = salesManUUID;
        this.itemStack = itemStack;
        this.offers = offers;
    }

    public int getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public JsonNode getOffers() {
        return offers;
    }

    public String getSalesManName() {
        return salesManName;
    }

    public String getSalesManUUID() {
        return salesManUUID;
    }

}
