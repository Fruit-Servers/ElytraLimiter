package me.scyphers.fruitservers.elytralimiter.event;

import me.scyphers.fruitservers.elytralimiter.ElytraLimiter;
import me.scyphers.fruitservers.elytralimiter.config.Settings;
import me.scyphers.scycore.api.Messenger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
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
        this.ELYTRA_TRACKING_KEY = new NamespacedKey(plugin, "tagged");
        this.PLAYER_PLACED_KEY = new NamespacedKey(plugin, "player-placed");
    }

    // TAG MANAGEMENT

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFramePlaceEvent(HangingPlaceEvent event) {

        // Only fire if placed by a player
        if (event.getPlayer() == null) return;

        // If the entity was not an item frame, ignore the event
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        // Check if the item frame is in the correct world
        if (invalidWorld(event.getEntity())) return;

        // Tag it as being placed by a player
        event.getEntity().getPersistentDataContainer().set(PLAYER_PLACED_KEY, PersistentDataType.BYTE, (byte) 1);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemFrameDropItemEvent(HangingBreakEvent event) {

        // If the entity was not an item frame, ignore the event
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        // Check if the item frame is in the correct world
        if (invalidWorld(event.getEntity())) return;

        // If the item frame was placed by a player, ignore the event
        if (event.getEntity().getPersistentDataContainer().has(PLAYER_PLACED_KEY, PersistentDataType.BYTE)) return;

        ItemFrame frame = (ItemFrame) event.getEntity();

        // If the item frame does not contain an elytra, ignore the event
        if (frame.getItem().getType() != Material.ELYTRA) return;

        // Add the tag to the dropped item
        frame.getItem().getItemMeta().getPersistentDataContainer().set(ELYTRA_TRACKING_KEY, PersistentDataType.BYTE, (byte) 1);
    }

    // ITEM FRAME PROTECTION

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractItemFrameEvent(PlayerInteractEntityEvent event) {

        // Only watch for item frames
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;

        // Only watch in the monitored worlds
        if (invalidWorld(event.getRightClicked())) return;

        // Check if there is an elytra in the frame
        ItemFrame frame = (ItemFrame) event.getRightClicked();
        ItemStack item = frame.getItem();
        if (item.getType() != Material.ELYTRA) return;

        // This is a new elytra, increment the player count or prevent the pickup attempt
        // Entity is assumed to be a player at this stage
        int elytraCount = plugin.getElytraTracker().getElytraAmount(event.getPlayer().getUniqueId());
        int maxCount = settings.getMaxElytras();

        // The player has reached the max amount of elytras
        // Prevent the player from picking up this elytra and make this item never despawn
        // ignore players with the override permission
        if (elytraCount >= maxCount && !event.getPlayer().hasPermission("elytralimiter.override")) {
            event.setCancelled(true);
            m.msg(event.getPlayer(), "elytraMessages.elytraLimitReached", "%max%", Integer.toString(maxCount));
        }

        // TODO - maybe make the item glow?

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBreakItemFrameEvent(HangingBreakEvent event) {

        // Only watch for item frames
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        // Only watch in the monitored worlds
        if (invalidWorld(event.getEntity())) return;

        // Check if there is an elytra in the frame
        ItemFrame frame = (ItemFrame) event.getEntity();
        ItemStack item = frame.getItem();
        if (item.getType() != Material.ELYTRA) return;

        // Check for the item tag
        if (!item.getItemMeta().getPersistentDataContainer().has(ELYTRA_TRACKING_KEY, PersistentDataType.BYTE)) return;

        // Prevent breaking the item frame
        event.setCancelled(true);

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
        if (invalidWorld(event.getEntity())) return;

        // Check for the item tag
        if (!item.getItemMeta().getPersistentDataContainer().has(ELYTRA_TRACKING_KEY, PersistentDataType.BYTE)) return;

        // This is a new elytra, increment the player count or prevent the pickup attempt
        // Entity is assumed to be a player at this stage
        int elytraCount = plugin.getElytraTracker().getElytraAmount(player.getUniqueId());
        int maxCount = settings.getMaxElytras();

        // The player has reached the max amount of elytras
        // Prevent the player from picking up this elytra and make this item never despawn
        // ignore players with the override permission
        if (elytraCount >= maxCount && !player.hasPermission("elytralimiter.override")) {
            event.setCancelled(true);
            m.msg(player, "elytraMessages.elytraLimitReached", "%max%", Integer.toString(maxCount));
        // The player has not hit the max, don't cancel event
        } else {
            plugin.getElytraTracker().addElytra(player.getUniqueId());
            m.msg(player, "elytraMessages.acquiredElytra", "%count%", Integer.toString(elytraCount), "%max%", Integer.toString(maxCount));
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
        if (invalidWorld(event.getEntity())) return;

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
        if (invalidWorld(event.getItem())) return;

        ItemStack item = event.getItem().getItemStack();

        // Check the item
        if (item.getType() != Material.ELYTRA) return;

        // Check for the tag
        if (!item.getItemMeta().getPersistentDataContainer().has(ELYTRA_TRACKING_KEY, PersistentDataType.BYTE)) return;

        // Cancel the pickup event
        event.setCancelled(true);

    }

    private boolean invalidWorld(Entity entity) {
        List<String> worlds = settings.getWorlds();
        World world = entity.getWorld();
        return !worlds.contains(world.getName());
    }

}
