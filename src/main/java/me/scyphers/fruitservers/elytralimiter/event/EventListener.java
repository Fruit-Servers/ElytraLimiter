package me.scyphers.fruitservers.elytralimiter.event;

import me.scyphers.fruitservers.elytralimiter.ElytraLimiter;
import me.scyphers.scycore.api.Messenger;
import me.scyphers.scycore.config.Settings;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class EventListener implements Listener {

    private final ElytraLimiter plugin;
    private final Settings settings;
    private final Messenger m;
    private final NamespacedKey TRACKING_KEY;

    public EventListener(ElytraLimiter plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.m = plugin.getMessenger();
        this.TRACKING_KEY = new NamespacedKey(plugin, "tagged");
    }

    // TAG MANAGEMENT

    public void onChunkLoadEvent(ChunkLoadEvent event) {
        event.getChunk().getTileEntities()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemFrameSpawnEvent(EntitySpawnEvent event) {

        System.out.println("Spawning a " + event.getEntityType());

        // Only watch for item frames
        if (event.getEntityType() != EntityType.ITEM_FRAME) return;

        // Only watch in the monitored worlds
        if (invalidWorld(event.getEntity())) return;

        // Check if there is an elytra in the frame
        ItemFrame frame = (ItemFrame) event.getEntity();
        ItemStack item = frame.getItem();
        if (item.getType() != Material.ELYTRA) return;

        // All checks passed, add the tag to the item
        item.getItemMeta().getPersistentDataContainer().set(TRACKING_KEY, PersistentDataType.BYTE, (byte) 1);

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
        if (elytraCount >= maxCount) {
            event.setCancelled(true);

        // The player has not hit the max, don't cancel event
        } else {
            plugin.getElytraTracker().addElytra(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameBreakEvent(HangingBreakByEntityEvent event) {

        // Only watch for item frames
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;

        // Only watch in the monitored worlds
        if (invalidWorld(event.getEntity())) return;

        // Check if there is an elytra in the frame
        ItemFrame frame = (ItemFrame) event.getEntity();
        ItemStack item = frame.getItem();
        if (item.getType() != Material.ELYTRA) return;

        // Check for the item tag
        if (!item.getItemMeta().getPersistentDataContainer().has(TRACKING_KEY, PersistentDataType.BYTE)) return;

        // ItemFrame was removed by some unknown cause (e.g. block broken from behind)
        if (event.getRemover() == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getRemover().getType() != EntityType.PLAYER) return;

        Player player = (Player) event.getRemover();

        // This is a new elytra, increment the player count or prevent the pickup attempt
        // Entity is assumed to be a player at this stage
        int elytraCount = plugin.getElytraTracker().getElytraAmount(player.getUniqueId());
        int maxCount = settings.getMaxElytras();

        // The player has reached the max amount of elytras
        // Prevent the player from picking up this elytra and make this item never despawn
        if (elytraCount >= maxCount) {
            event.setCancelled(true);
            m.msg(player, "elytraMessages.elytraPickupBlocked");
        // The player has not hit the max, don't cancel event
        } else {
            plugin.getElytraTracker().addElytra(player.getUniqueId());
        }

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
        if (!item.getItemMeta().getPersistentDataContainer().has(TRACKING_KEY, PersistentDataType.BYTE)) return;

        // This is a new elytra, increment the player count or prevent the pickup attempt
        // Entity is assumed to be a player at this stage
        int elytraCount = plugin.getElytraTracker().getElytraAmount(player.getUniqueId());
        int maxCount = settings.getMaxElytras();

        // The player has reached the max amount of elytras
        // Prevent the player from picking up this elytra and make this item never despawn
        if (elytraCount >= maxCount) {
            event.setCancelled(true);
            m.msg(player, "elytraMessages.elytraPickupBlocked");
        // The player has not hit the max, don't cancel event
        } else {
            plugin.getElytraTracker().addElytra(player.getUniqueId());
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
        if (!item.getItemMeta().getPersistentDataContainer().has(TRACKING_KEY, PersistentDataType.BYTE)) return;

        // Cancel the tagged elytra
        event.setCancelled(true);

    }

    private boolean invalidWorld(Entity entity) {
        List<String> worlds = settings.getWorlds();
        World world = entity.getWorld();
        return !worlds.contains(world.getName());
    }

}
