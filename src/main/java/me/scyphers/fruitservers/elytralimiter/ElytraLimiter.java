package me.scyphers.fruitservers.elytralimiter;

import me.scyphers.fruitservers.elytralimiter.api.Messenger;
import me.scyphers.fruitservers.elytralimiter.command.CommandFactory;
import me.scyphers.fruitservers.elytralimiter.config.Settings;
import me.scyphers.fruitservers.elytralimiter.config.SimpleFileManager;
import me.scyphers.fruitservers.elytralimiter.event.EventListener;
import me.scyphers.fruitservers.elytralimiter.gui.signs.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class ElytraLimiter extends JavaPlugin {

    private SimpleFileManager configManager;

    private SignManager signManager;

    private boolean successfulEnable = false;

    @Override
    public void onEnable() {

        // Register the Config Manager
        try {
            this.configManager = new SimpleFileManager(this);
        } catch (Exception e) {
            getLogger().warning("Something went wrong loading configs!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        this.signManager = new SignManager(this);

        // Register the Admin Command
        CommandFactory commandFactory = new CommandFactory(this);
        this.getCommand("elytralimiter").setExecutor(commandFactory);
        this.getCommand("elytralimiter").setTabCompleter(commandFactory);

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        this.successfulEnable = true;

    }

    @Override
    public void onDisable() {
        if (!successfulEnable) return;
    }

    public void reload(CommandSender sender) {
        try {
            sender.sendMessage("Reloading...");
            configManager.reloadConfigs();
            sender.sendMessage("Successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("Error reloading! Check console for logs!");
            e.printStackTrace();
        }
    }

    public SimpleFileManager getConfigManager() {
        return configManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public Settings getSettings() {
        return configManager.getSettings();
    }

    public Messenger getMessenger() {
        return configManager.getMessenger();
    }

    public List<String> getSplashText() {
        StringBuilder authors = new StringBuilder();
        for (String author : this.getDescription().getAuthors()) {
            authors.append(author).append(", ");
        }
        authors.delete(authors.length() - 1, authors.length());
        return Arrays.asList(
                "ElytraLimiter v" + this.getDescription().getVersion(),
                "Built by" + authors
        );
    }


}
