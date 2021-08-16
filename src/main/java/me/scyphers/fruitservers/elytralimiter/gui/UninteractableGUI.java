package me.scyphers.fruitservers.elytralimiter.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * When closing a GUI to perform an action (e.g. teleport a player, chat prompt etc) there is a small window that can
 * be created for players to interact with the GUI before it closes due to scheduling or lag. <br>
 * To prevent any attempts to open more than one GUI at once, opening this GUI instead ensures the behaviour does not
 * change due to extra ticks between the GUI closing or lag
 */
public class UninteractableGUI extends InventoryGUI {

    /**
     * @param lastGUI The GUI that was open before this one - appearance is copied from the previous GUI
     */
    public UninteractableGUI(@NotNull InventoryGUI lastGUI) {
        super(lastGUI, lastGUI.getPlugin(), lastGUI.getPlayer(), lastGUI.getName(), lastGUI.getSize());
        this.inventoryItems = lastGUI.getInventoryItems();
    }

    /**
     * Creates a deadlock scenario - players cannot do anything from this GUI except for closing it
     * @param event event from interacting with this GUI
     * @return this GUI
     */
    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {
        return this;
    }
    
    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
