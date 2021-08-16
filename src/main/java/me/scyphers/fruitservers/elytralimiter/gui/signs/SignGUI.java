package me.scyphers.fruitservers.elytralimiter.gui.signs;

import me.scyphers.fruitservers.elytralimiter.ElytraLimiter;
import me.scyphers.fruitservers.elytralimiter.gui.GUI;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SignGUI implements GUI<SignChangeEvent> {

    protected final GUI<?> lastGUI;

    protected final ElytraLimiter plugin;

    protected final Player player;

    private final SignManager signManager;

    private final int signID;

    public SignGUI(GUI<?> lastGUI, ElytraLimiter plugin, Player player, String[] text) {
        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.player = player;
        this.signManager = plugin.getSignManager();

        signID = signManager.initSign(this, text);
    }

    /**
     * The outcome of this method should never be another SignGUI
     * @param event the event to handle the GUI interaction
     * @return the new GUI
     */
    @Override
    public abstract @NotNull GUI<?> handleInteraction(SignChangeEvent event);

    @Override
    public void open(Player player) {
        Sign sign = signManager.getSign(signID);
        if (sign != null) {
            player.openSign(sign);
        } else {
            plugin.getMessenger().msg(player, "errorMessages.cannotOpenSign");
        }

    }

    @Override
    public @Nullable GUI<?> getLastGUI() {
        return lastGUI;
    }

    @Override
    public @NotNull ElytraLimiter getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public boolean shouldClose() {
        return false;
    }

}
