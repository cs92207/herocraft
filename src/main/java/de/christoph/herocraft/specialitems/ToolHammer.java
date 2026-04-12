package de.christoph.herocraft.specialitems;

import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToolHammer implements Listener {

    private static final String TOOL_HAMMER_NAME = "§4§lHammer";
    private static final String GUI_TITLE = "§4§lTool Hammer";
    private static final int GUI_SIZE = 27;

    private final HeroCraft plugin;
    private final NamespacedKey hammerIdKey;
    private final NamespacedKey pickaxeKey;
    private final NamespacedKey shovelKey;
    private final NamespacedKey axeKey;
    private final NamespacedKey hoeKey;
    private final Map<UUID, ActiveSwap> activeSwaps = new HashMap<>();

    public ToolHammer(HeroCraft plugin) {
        this.plugin = plugin;
        this.hammerIdKey = new NamespacedKey(plugin, "tool_hammer_id");
        this.pickaxeKey = new NamespacedKey(plugin, "tool_hammer_pickaxe");
        this.shovelKey = new NamespacedKey(plugin, "tool_hammer_shovel");
        this.axeKey = new NamespacedKey(plugin, "tool_hammer_axe");
        this.hoeKey = new NamespacedKey(plugin, "tool_hammer_hoe");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            ItemStack hammer = player.getInventory().getItemInMainHand();
            if (!isToolHammer(hammer)) {
                return;
            }

            event.setCancelled(true);
            ItemStack hammerWithId = ensureHammerId(hammer.clone());
            player.getInventory().setItemInMainHand(hammerWithId);
            openHammerInventory(player, hammerWithId);
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        prepareToolSwap(event.getPlayer(), event.getClickedBlock() == null ? null : event.getClickedBlock().getType());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        prepareToolSwap(event.getPlayer(), event.getBlock().getType());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDamageMonitor(BlockDamageEvent event) {
        if (event.isCancelled()) {
            restoreHammer(event.getPlayer(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamageAbort(BlockDamageAbortEvent event) {
        restoreHammer(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        ActiveSwap activeSwap = activeSwaps.get(event.getPlayer().getUniqueId());
        if (activeSwap == null) {
            return;
        }

        if (activeSwap.toolType != HammerToolType.fromBlock(event.getBlock().getType())) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> restoreHammer(event.getPlayer(), true));
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        restoreHammer(event.getPlayer(), false);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        restoreHammer(event.getPlayer(), false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        restoreHammer(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if (event.getAmount() <= 0) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack hammer = player.getInventory().getItemInMainHand();
        if (!isToolHammer(hammer)) {
            return;
        }

        ItemStack updatedHammer = ensureHammerId(hammer.clone());
        int remainingXp = event.getAmount();
        boolean changed = false;

        for (HammerToolType toolType : HammerToolType.values()) {
            if (remainingXp <= 0) {
                break;
            }

            ItemStack tool = getStoredTool(updatedHammer, toolType);
            if (!canUseMending(tool)) {
                continue;
            }

            ItemMeta itemMeta = tool.getItemMeta();
            Damageable damageable = (Damageable) itemMeta;
            int damage = damageable.getDamage();
            int repairPoints = Math.min(damage, remainingXp * 2);
            if (repairPoints <= 0) {
                continue;
            }

            damageable.setDamage(damage - repairPoints);
            tool.setItemMeta(itemMeta);
            setStoredTool(updatedHammer, toolType, tool);

            remainingXp -= (repairPoints + 1) / 2;
            changed = true;
        }

        if (!changed) {
            return;
        }

        player.getInventory().setItemInMainHand(updatedHammer);
        event.setAmount(remainingXp);
        player.updateInventory();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            restoreHammer((Player) event.getWhoClicked(), false);
        }

        if (!(event.getView().getTopInventory().getHolder() instanceof HammerInventoryHolder)) {
            return;
        }

        Inventory topInventory = event.getView().getTopInventory();
        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();

        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }

        if (rawSlot >= topInventory.getSize()) {
            if (!event.isShiftClick()) {
                return;
            }

            HammerToolType toolType = HammerToolType.fromItem(event.getCurrentItem());
            if (toolType == null) {
                return;
            }

            int targetSlot = toolType.getSlot();
            if (hasRealItem(topInventory.getItem(targetSlot))) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
            topInventory.setItem(targetSlot, sanitizeToolForType(event.getCurrentItem().clone(), toolType));
            clearPlayerSlot(player.getInventory(), event.getSlot());
            player.updateInventory();
            return;
        }

        HammerToolType targetType = HammerToolType.fromSlot(rawSlot);
        if (targetType == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick().isKeyboardClick()) {
            event.setCancelled(true);
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (event.isShiftClick()) {
            event.setCancelled(true);
            if (hasRealItem(current)) {
                topInventory.setItem(rawSlot, null);
                player.getInventory().addItem(current.clone());
                player.updateInventory();
            }
            return;
        }

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
            return;
        }

        if (!hasRealItem(cursor)) {
            return;
        }

        HammerToolType cursorType = HammerToolType.fromItem(cursor);
        if (cursorType != targetType) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            restoreHammer((Player) event.getWhoClicked(), false);
        }

        if (!(event.getView().getTopInventory().getHolder() instanceof HammerInventoryHolder)) {
            return;
        }

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= event.getView().getTopInventory().getSize()) {
                continue;
            }

            HammerToolType toolType = HammerToolType.fromSlot(rawSlot);
            if (toolType == null || HammerToolType.fromItem(event.getOldCursor()) != toolType) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof HammerInventoryHolder)) {
            return;
        }

        HammerInventoryHolder holder = (HammerInventoryHolder) event.getInventory().getHolder();
        Player player = (Player) event.getPlayer();
        HammerLocation hammerLocation = findHammer(player, holder.getHammerId());

        if (hammerLocation == null) {
            returnTools(player, event.getInventory());
            return;
        }

        ItemStack hammer = hammerLocation.getItem().clone();
        for (HammerToolType toolType : HammerToolType.values()) {
            ItemStack tool = sanitizeToolForType(event.getInventory().getItem(toolType.getSlot()), toolType);
            setStoredTool(hammer, toolType, tool);
        }

        hammerLocation.setItem(player.getInventory(), hammer);
    }

    private void openHammerInventory(Player player, ItemStack hammer) {
        Inventory inventory = Bukkit.createInventory(new HammerInventoryHolder(getHammerId(hammer)), GUI_SIZE, GUI_TITLE);
        fillBackground(inventory);
        setInfoItems(inventory);

        for (HammerToolType toolType : HammerToolType.values()) {
            ItemStack tool = getStoredTool(hammer, toolType);
            if (tool != null && tool.getType() != Material.AIR) {
                inventory.setItem(toolType.getSlot(), tool);
            }
        }

        player.openInventory(inventory);
    }

    private void prepareToolSwap(Player player, Material blockType) {
        if (blockType == null) {
            return;
        }

        ActiveSwap activeSwap = activeSwaps.get(player.getUniqueId());
        if (activeSwap != null) {
            if (activeSwap.toolType == HammerToolType.fromBlock(blockType)) {
                return;
            }
            restoreHammer(player, false);
        }

        ItemStack hammer = player.getInventory().getItemInMainHand();
        if (!isToolHammer(hammer)) {
            return;
        }

        HammerToolType toolType = HammerToolType.fromBlock(blockType);
        if (toolType == null) {
            return;
        }

        ItemStack hammerWithId = ensureHammerId(hammer.clone());
        ItemStack storedTool = getStoredTool(hammerWithId, toolType);
        if (storedTool == null) {
            return;
        }

        activeSwaps.put(player.getUniqueId(), new ActiveSwap(hammerWithId, toolType, storedTool.clone()));
        player.getInventory().setItemInMainHand(storedTool.clone());
        player.updateInventory();
    }

    private void restoreHammer(Player player, boolean keepToolChanges) {
        ActiveSwap activeSwap = activeSwaps.remove(player.getUniqueId());
        if (activeSwap == null) {
            return;
        }

        ItemStack currentHand = player.getInventory().getItemInMainHand();
        ItemStack hammer = activeSwap.hammer.clone();

        if (keepToolChanges) {
            ItemStack updatedTool = sanitizeToolForType(currentHand, activeSwap.toolType);
            if (updatedTool == null) {
                updatedTool = activeSwap.tool.clone();
            }
            setStoredTool(hammer, activeSwap.toolType, updatedTool);
        }

        player.getInventory().setItemInMainHand(hammer);
        player.updateInventory();
    }

    private void fillBackground(Inventory inventory) {
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(" ")
                .build();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (HammerToolType.fromSlot(slot) != null || isInfoSlot(slot)) {
                continue;
            }
            inventory.setItem(slot, filler);
        }
    }

    private void setInfoItems(Inventory inventory) {
        inventory.setItem(1, createInfoItem(Material.DIAMOND_PICKAXE, "§bSpitzhacke"));
        inventory.setItem(3, createInfoItem(Material.DIAMOND_SHOVEL, "§eSchaufel"));
        inventory.setItem(5, createInfoItem(Material.DIAMOND_AXE, "§6Axt"));
        inventory.setItem(7, createInfoItem(Material.DIAMOND_HOE, "§aHacke"));
    }

    private ItemStack createInfoItem(Material material, String name) {
        return new ItemBuilder(material)
                .setDisplayName(name)
                .setLore("§7Lege hier das passende Werkzeug", "§7in den Slot darunter.")
                .build();
    }

    private boolean isInfoSlot(int slot) {
        return slot == 1 || slot == 3 || slot == 5 || slot == 7;
    }

    private ItemStack ensureHammerId(ItemStack hammer) {
        ItemMeta itemMeta = hammer.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(hammerIdKey, PersistentDataType.STRING)) {
            container.set(hammerIdKey, PersistentDataType.STRING, UUID.randomUUID().toString());
            hammer.setItemMeta(itemMeta);
        }
        return hammer;
    }

    private String getHammerId(ItemStack hammer) {
        ItemMeta itemMeta = hammer.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        return itemMeta.getPersistentDataContainer().get(hammerIdKey, PersistentDataType.STRING);
    }

    private void setStoredTool(ItemStack hammer, HammerToolType toolType, ItemStack tool) {
        ItemMeta itemMeta = hammer.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey dataKey = getDataKey(toolType);

        if (tool == null || tool.getType() == Material.AIR) {
            container.remove(dataKey);
        } else {
            container.set(dataKey, PersistentDataType.BYTE_ARRAY, serializeItemStack(tool));
        }

        hammer.setItemMeta(itemMeta);
    }

    private ItemStack getStoredTool(ItemStack hammer, HammerToolType toolType) {
        ItemMeta itemMeta = hammer.getItemMeta();
        if (itemMeta == null) {
            return null;
        }

        byte[] data = itemMeta.getPersistentDataContainer().get(getDataKey(toolType), PersistentDataType.BYTE_ARRAY);
        ItemStack itemStack = deserializeItemStack(data);
        return sanitizeToolForType(itemStack, toolType);
    }

    private NamespacedKey getDataKey(HammerToolType toolType) {
        switch (toolType) {
            case PICKAXE:
                return pickaxeKey;
            case SHOVEL:
                return shovelKey;
            case AXE:
                return axeKey;
            case HOE:
                return hoeKey;
            default:
                throw new IllegalArgumentException("Unsupported tool type: " + toolType);
        }
    }

    private ItemStack sanitizeToolForType(ItemStack itemStack, HammerToolType toolType) {
        if (!hasRealItem(itemStack)) {
            return null;
        }

        if (HammerToolType.fromItem(itemStack) != toolType) {
            return null;
        }

        ItemStack clone = itemStack.clone();
        clone.setAmount(1);
        return clone;
    }

    private boolean hasRealItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    private boolean canUseMending(ItemStack itemStack) {
        if (!hasRealItem(itemStack) || !itemStack.hasItemMeta()) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof Damageable)) {
            return false;
        }

        return itemMeta.hasEnchant(Enchantment.MENDING) && ((Damageable) itemMeta).getDamage() > 0;
    }

    private boolean isToolHammer(ItemStack itemStack) {
        if (!hasRealItem(itemStack) || !itemStack.hasItemMeta()) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta.hasDisplayName() && TOOL_HAMMER_NAME.equalsIgnoreCase(itemMeta.getDisplayName());
    }

    private byte[] serializeItemStack(ItemStack itemStack) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream objectStream = new BukkitObjectOutputStream(byteStream)) {
            objectStream.writeObject(itemStack);
            return byteStream.toByteArray();
        } catch (IOException exception) {
            exception.printStackTrace();
            return new byte[0];
        }
    }

    private ItemStack deserializeItemStack(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             BukkitObjectInputStream objectStream = new BukkitObjectInputStream(byteStream)) {
            Object object = objectStream.readObject();
            if (object instanceof ItemStack) {
                return (ItemStack) object;
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private HammerLocation findHammer(Player player, String hammerId) {
        if (hammerId == null) {
            return null;
        }

        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (!isToolHammer(itemStack)) {
                continue;
            }
            if (hammerId.equals(getHammerId(itemStack))) {
                return new HammerLocation(slot, false, itemStack);
            }
        }

        ItemStack offHand = inventory.getItemInOffHand();
        if (isToolHammer(offHand) && hammerId.equals(getHammerId(offHand))) {
            return new HammerLocation(-1, true, offHand);
        }

        return null;
    }

    private void clearPlayerSlot(PlayerInventory inventory, int slot) {
        inventory.setItem(slot, null);
    }

    private void returnTools(Player player, Inventory inventory) {
        for (HammerToolType toolType : HammerToolType.values()) {
            ItemStack tool = sanitizeToolForType(inventory.getItem(toolType.getSlot()), toolType);
            if (tool == null) {
                continue;
            }

            for (ItemStack leftover : player.getInventory().addItem(tool).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }
    }

    private enum HammerToolType {
        PICKAXE(10),
        SHOVEL(12),
        AXE(14),
        HOE(16);

        private final int slot;

        HammerToolType(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public static HammerToolType fromSlot(int slot) {
            for (HammerToolType toolType : values()) {
                if (toolType.slot == slot) {
                    return toolType;
                }
            }
            return null;
        }

        public static HammerToolType fromItem(ItemStack itemStack) {
            if (itemStack == null) {
                return null;
            }

            String materialName = itemStack.getType().name();
            if (materialName.endsWith("_PICKAXE")) {
                return PICKAXE;
            }
            if (materialName.endsWith("_SHOVEL")) {
                return SHOVEL;
            }
            if (materialName.endsWith("_AXE")) {
                return AXE;
            }
            if (materialName.endsWith("_HOE")) {
                return HOE;
            }
            return null;
        }

        public static HammerToolType fromBlock(Material material) {
            if (Tag.MINEABLE_PICKAXE.isTagged(material)) {
                return PICKAXE;
            }
            if (Tag.MINEABLE_SHOVEL.isTagged(material)) {
                return SHOVEL;
            }
            if (Tag.MINEABLE_AXE.isTagged(material)) {
                return AXE;
            }
            if (Tag.MINEABLE_HOE.isTagged(material)) {
                return HOE;
            }
            return null;
        }
    }

    private static class HammerInventoryHolder implements InventoryHolder {

        private final String hammerId;

        private HammerInventoryHolder(String hammerId) {
            this.hammerId = hammerId;
        }

        public String getHammerId() {
            return hammerId;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private static class HammerLocation {

        private final int slot;
        private final boolean offHand;
        private final ItemStack item;

        private HammerLocation(int slot, boolean offHand, ItemStack item) {
            this.slot = slot;
            this.offHand = offHand;
            this.item = item;
        }

        public ItemStack getItem() {
            return item;
        }

        public void setItem(PlayerInventory inventory, ItemStack itemStack) {
            if (offHand) {
                inventory.setItemInOffHand(itemStack);
                return;
            }
            inventory.setItem(slot, itemStack);
        }
    }

    private static class ActiveSwap {

        private final ItemStack hammer;
        private final HammerToolType toolType;
        private final ItemStack tool;

        private ActiveSwap(ItemStack hammer, HammerToolType toolType, ItemStack tool) {
            this.hammer = hammer;
            this.toolType = toolType;
            this.tool = tool;
        }
    }
}