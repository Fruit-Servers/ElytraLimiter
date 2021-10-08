package me.scyphers.fruitservers.elytralimiter.event;

import me.scyphers.fruitservers.elytralimiter.ElytraLimiter;
import me.scyphers.fruitservers.elytralimiter.config.Settings;
import me.scyphers.scycore.api.Messenger;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class EventListener implements Listener {

    private final ElytraLimiter plugin;
    private final Settings settings;
    private final Messenger m;
    private final NamespacedKey ELYTRA_TRACKING_KEY;
    private final NamespacedKey PLAYER_PLACED_KEY;

    public EventListener(ElytraLimiter plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.m = plugin.getMessenger();
        this.ELYTRA_TRACKING_KEY = new NamespacedKey(plugin, "elytra-tracking");
        this.PLAYER_PLACED_KEY = new NamespacedKey(plugin, "player-placed");
    }

    // TAG MANAGEMENT

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemFrameSpawnEvent(HangingPlaceEvent event) {

        // Check if the item frame is in the correct world
        if (invalidWorld(event.getEntity().getWorld())) return;

        // If the entity was not an item frame, ignore the event
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        // If a player placed this item frame
        if (event.getPlayer() != null) return;

        // Apply the tag
        applyTag(PLAYER_PLACED_KEY, event.getEntity());

        plugin.getLogger().info("Tagged item frame at " + event.getEntity().getLocation() + " with PLAYER_PLACED");

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemFrameBreakEvent(HangingBreakEvent event) {

        // Only fire in tracked worlds
        if (invalidWorld(event.getEntity().getWorld())) return;

        // Only track Item Frames
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        ItemFrame frame = (ItemFrame) event.getEntity();

        // Only track item frames with elytras
        ItemStack item = ((ItemFrame) event.getEntity()).getItem();
        if (item.getType() != Material.ELYTRA) return;

        // Check if a player placed this item frame
        if (frame.getPersistentDataContainer().has(PLAYER_PLACED_KEY, PersistentDataType.BYTE)) return;

        // Update the item in the frame
        frame.setItem(applyTag(ELYTRA_TRACKING_KEY, item));

    }

    // ITEM FRAME PROTECTION

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractItemFrameEvent(EntityDamageByEntityEvent event) {

        // Only watch for item frames
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        // Only watch in the monitored worlds
        if (invalidWorld(event.getEntity().getWorld())) return;

        // Check if there is an elytra in the frame
        ItemFrame frame = (ItemFrame) event.getEntity();
        ItemStack item = frame.getItem();
        if (item.getType() != Material.ELYTRA) return;

        // Only fire for players
        if (!(event.getDamager() instanceof Player player)) return;

        // Check if the frame is a natural item frame and not placed by the player
        if (frame.getPersistentDataContainer().has(PLAYER_PLACED_KEY, PersistentDataType.BYTE)) return;

        int elytraCount = plugin.getElytraTracker().getElytraAmount(player.getUniqueId());
        int maxCount = settings.getMaxElytras();

        // Only cancel the event if the player is over their limit or don't have the override permission
        if (belowMaxElytras(elytraCount, maxCount, player)) {

            // Cancel the event and inform the player they have hit their max
            event.setCancelled(true);
            m.msg(player, "elytraMessages.elytraLimitReached", "%max%", Integer.toString(maxCount));

        }

        plugin.getLogger().info("Player attempted to collect a tagged elytra");

    }

    // DROPPED ITEM PROTECTION

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGetItemEvent(EntityPickupItemEvent event) {

        // Only watch for elytras
        ItemStack item = event.getItem().getItemStack();
        if (item.getType() != Material.ELYTRA) return;

        // Only watch for players
        if (!(event.getEntity() instanceof Player player)) return;

        // Only watch in the monitored worlds
        if (invalidWorld(event.getEntity().getWorld())) return;

        // Check for the item tag
        if (!item.getItemMeta().getPersistentDataContainer().has(ELYTRA_TRACKING_KEY, PersistentDataType.BYTE)) return;

        plugin.getLogger().info("Player picked up a tagged elytra");

        int elytraCount = plugin.getElytraTracker().getElytraAmount(player.getUniqueId());
        int maxCount = settings.getMaxElytras();

        // Permission and elytra max check
        if (belowMaxElytras(elytraCount, maxCount, player)) {

            // Player hasn't reached their max elytra count and/or has override

            // add an elytra count for the player
            plugin.getElytraTracker().addElytra(player.getUniqueId());

            // remove the tracking tag
            removeTag(ELYTRA_TRACKING_KEY, item);

            // Let the player know
            m.msg(player, "elytraMessages.acquiredElytra", "%count%", Integer.toString(maxCount - elytraCount), "%max%", Integer.toString(maxCount));


        } else {
            // The player has hit their max, cancel the event and inform them
            event.setCancelled(true);
            m.msg(player, "elytraMessages.elytraLimitReached", "%max%", Integer.toString(maxCount));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onElytraDestroyEvent(EntityDeathEvent event) {

        // Only watch dropped items
        if (event.getEntityType() != EntityType.DROPPED_ITEM) return;

        // Only watch elytras
        ItemStack item = ((Item) event.getEntity()).getItemStack();
        if (item.getType() != Material.ELYTRA) return;

        // Only watch in the monitored worlds
        if (invalidWorld(event.getEntity().getWorld())) return;

        // Only watch elytras with the tag
        if (!item.getItemMeta().getPersistentDataContainer().has(ELYTRA_TRACKING_KEY, PersistentDataType.BYTE)) return;

        // Edge case of item being destroyed by void - needs to be permitted
        if (event.getEntity().getWorld().getMinHeight() > event.getEntity().getLocation().getBlockY()) return;

        // Cancel destroying the elytra
        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHopperPickupItemEvent(InventoryPickupItemEvent event) {

        // Check the world first
        if (invalidWorld(event.getItem().getWorld())) return;

        ItemStack item = event.getItem().getItemStack();

        // Check the item
        if (item.getType() != Material.ELYTRA) return;

        // Check for the tag
        if (!item.getItemMeta().getPersistentDataContainer().has(ELYTRA_TRACKING_KEY, PersistentDataType.BYTE)) return;

        // Cancel the pickup event
        event.setCancelled(true);

    }

    // UTILITY METHODS

    private boolean invalidWorld(World world) {
        List<String> worlds = settings.getWorlds();
        return !worlds.contains(world.getName());
    }

    private boolean belowMaxElytras(int count, int max, Player player) {
        return count < max || player.hasPermission("elytralimiter.override");
    }

    // Tag Management

    /* Temporarily removed to test new method for applying tag
    private void scheduleApplyTag(Location location, NamespacedKey tag) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Collection<Item> items = location.getWorld().getNearbyEntitiesByType(Item.class, location, 2);
            if (items.size() == 0) return;
            items.stream().filter(item1 -> item1.getItemStack().getType() == Material.ELYTRA).forEach(item1 -> applyTag(tag, item1.getItemStack()));
        });
    }
     */

    private ItemStack applyTag(NamespacedKey tag, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(tag, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(itemMeta);
        return item;
    }

    private void removeTag(NamespacedKey tag, ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().remove(tag);
        item.setItemMeta(itemMeta);
    }

    private void applyTag(NamespacedKey tag, Entity entity) {
        entity.getPersistentDataContainer().set(tag, PersistentDataType.BYTE, (byte) 1);
    }

}
