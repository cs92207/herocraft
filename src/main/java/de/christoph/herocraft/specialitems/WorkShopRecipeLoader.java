package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.utils.ItemBuilder;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class WorkShopRecipeLoader {

    public WorkShopRecipeLoader() {
        loadPistolRecipe();
        loadHawkEyeBowRecipe();
        loadCapShieldRecipe();
        loadWolverineRecipe();
        loadSpidermansNetshooterRecipe();
        loadJetPackRecipe();
        loadMjoelnirRecipe();

        loadScaleStickRecipe();
        loadDarkWeaponRecipe();
        loadNatureSwordRecipe();
        loadSandstormRecipe();
    }

    private void loadScaleStickRecipe() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(getItemsAdderItem("§4§lScale Waffe"));
        RecipeChoice.ExactChoice specialChoice = new RecipeChoice.ExactChoice(getItemsAdderItem("§4§lDunkler Stab"));
        shapedRecipe.shape("NDN", "DKD", "NDN");
        shapedRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        shapedRecipe.setIngredient('K', Material.HEAVY_CORE);
        shapedRecipe.setIngredient('D', specialChoice);
        Bukkit.addRecipe(shapedRecipe);
    }

    private void loadSandstormRecipe() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(getItemsAdderItem("§4§lSandsturm"));
        RecipeChoice.ExactChoice specialChoice = new RecipeChoice.ExactChoice(getItemsAdderItem("§4§lWüsten Herz"));
        shapedRecipe.shape("APA", "PHP", "APA");
        shapedRecipe.setIngredient('P', Material.PHANTOM_MEMBRANE);
        shapedRecipe.setIngredient('H', specialChoice);
        Bukkit.addRecipe(shapedRecipe);
    }

    private void loadDarkWeaponRecipe() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(getItemsAdderItem("§4§lDunkler Stab"));
        RecipeChoice.ExactChoice specialChoice = new RecipeChoice.ExactChoice(getItemsAdderItem("§4§lDunkles Herz"));

        shapedRecipe.shape("APP", "AHP", "SAA");
        shapedRecipe.setIngredient('P', Material.PURPLE_STAINED_GLASS);
        shapedRecipe.setIngredient('H', specialChoice);
        shapedRecipe.setIngredient('S', Material.STICK);
        Bukkit.addRecipe(shapedRecipe);
    }

    private void loadNatureSwordRecipe() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(getItemsAdderItem("§4§lNatur Schwert"));
        RecipeChoice.ExactChoice specialChoice = new RecipeChoice.ExactChoice(getItemsAdderItem("§4§lDark Crystal"));
        shapedRecipe.shape("ARV", "ACB", "SAA");
        shapedRecipe.setIngredient('R', Material.RED_TULIP);
        shapedRecipe.setIngredient('V', Material.ALLIUM);
        shapedRecipe.setIngredient('C', specialChoice);
        shapedRecipe.setIngredient('B', Material.CORNFLOWER);
        shapedRecipe.setIngredient('S', Material.STICK);
        Bukkit.addRecipe(shapedRecipe);
    }

    private ItemStack getItemsAdderItem(String name) {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase(name)) {
                itemStack = i.getItemStack();
            }
        }
        return itemStack;
    }

    private void loadPistolRecipe() {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lPistole")) {
                itemStack = i.getItemStack();
           }
        }
        ShapedRecipe pistolRecipe = new ShapedRecipe(itemStack);
        pistolRecipe.shape("AAA", "SSS", "WDA");
        pistolRecipe.setIngredient('S', Material.STONE);
        pistolRecipe.setIngredient('W', Material.SPRUCE_PLANKS);
        pistolRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        Bukkit.addRecipe(pistolRecipe);

    }

    private void loadHawkEyeBowRecipe() {
        ShapedRecipe hawkEyeRecipe = new ShapedRecipe(new ItemBuilder(Material.BOW).setDisplayName("§4§lHawk Eyes Bogen").setLore("", "§eRechtsklick §7» Pfeilart auswählen").build());
        hawkEyeRecipe.shape("WSA", "WDS", "WSA");
        hawkEyeRecipe.setIngredient('W', Material.STRING);
        hawkEyeRecipe.setIngredient('S', Material.STICK);
        hawkEyeRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        Bukkit.addRecipe(hawkEyeRecipe);
    }

    private void loadCapShieldRecipe() {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lCaptain Americas Schild")) {
                itemStack = i.getItemStack();
            }
        }
        ShapedRecipe shapedRecipe = new ShapedRecipe(itemStack);
        shapedRecipe.shape("RBR", "BSB", "RBR");
        shapedRecipe.setIngredient('R', Material.RED_DYE);
        shapedRecipe.setIngredient('B', Material.BLUE_DYE);
        shapedRecipe.setIngredient('S', Material.NETHER_STAR);
        Bukkit.addRecipe(shapedRecipe);
    }

    private void loadWolverineRecipe() {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lWolverins Krallen")) {
                itemStack = i.getItemStack();
            }
        }
        ShapedRecipe shapedRecipe = new ShapedRecipe(itemStack);
        shapedRecipe.shape("IAI", "ISI", "III");
        shapedRecipe.setIngredient('I', Material.IRON_BARS);
        shapedRecipe.setIngredient('S', Material.NETHER_STAR);
        Bukkit.addRecipe(shapedRecipe);
    }

    private void loadSpidermansNetshooterRecipe() {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lSpidermans Netzshooter")) {
                itemStack = i.getItemStack();
            }
        }
        ShapedRecipe shapedRecipe = new ShapedRecipe(itemStack);
        shapedRecipe.shape("AAA", "PRS", "AAA");
        shapedRecipe.setIngredient('R', Material.REDSTONE);
        shapedRecipe.setIngredient('S', Material.STRING);
        shapedRecipe.setIngredient('P', Material.NETHER_STAR);
        Bukkit.addRecipe(shapedRecipe);
    }

    private void loadJetPackRecipe() {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lJet Pack")) {
                itemStack = i.getItemStack();
            }
        }
        ShapedRecipe shapedRecipe = new ShapedRecipe(itemStack);
        shapedRecipe.shape("SAS", "SPS", "RAR");
        shapedRecipe.setIngredient('S', Material.STONE);
        shapedRecipe.setIngredient('R', Material.REDSTONE);
        shapedRecipe.setIngredient('P', Material.BEACON);
        Bukkit.addRecipe(shapedRecipe);
    }

    private void loadMjoelnirRecipe() {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase("§4§lMjölnir")) {
                itemStack = i.getItemStack();
            }
        }
        ShapedRecipe shapedRecipe = new ShapedRecipe(itemStack);
        shapedRecipe.shape("SNS", "ARA", "AWA");
        shapedRecipe.setIngredient('S', Material.STONE);
        shapedRecipe.setIngredient('N', Material.NETHER_STAR);
        shapedRecipe.setIngredient('W', Material.SPRUCE_PLANKS);
        shapedRecipe.setIngredient('R', Material.BEACON);
        Bukkit.addRecipe(shapedRecipe);
    }


}
