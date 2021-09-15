package me.scyphers.fruitservers.elytralimiter;

import me.scyphers.fruitservers.elytralimiter.api.ElytraTracker;
import me.scyphers.fruitservers.elytralimiter.command.CommandFactory;
import me.scyphers.fruitservers.elytralimiter.config.ElytraFileManager;
import me.scyphers.fruitservers.elytralimiter.config.Settings;
import me.scyphers.fruitservers.elytralimiter.event.EventListener;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.api.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public final class ElytraLimiter extends BasePlugin {

    private ElytraFileManager fileManager;

    private boolean successfulEnable = false;

    @Override
    public void onEnable() {

        // Register the Config Manager
        try {
            this.fileManager = new ElytraFileManager(this);
        } catch (Exception e) {
            getLogger().warning("Something went wrong loading configs!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        // Register the Admin Command
        CommandFactory commandFactory = new CommandFactory(this);
        this.getCommand("elytralimiter").setExecutor(commandFactory);
        this.getCommand("elytralimiter").setTabCompleter(commandFactory);

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        this.successfulEnable = true;

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reload(CommandSender sender) {
        try {
            sender.sendMessage("Reloading...");
            fileManager.reloadConfigs();
            sender.sendMessage("Successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("Error reloading! Check console for logs!");
            e.printStackTrace();
        }
    }

    @Override
    public boolean wasSuccessfulEnable() {
        return successfulEnable;
    }

    @Override
    public ElytraFileManager getFileManager() {
        return fileManager;
    }

    @Override
    public Settings getFileSettings() {
        return fileManager.getSettings();
    }

    public Messenger getMessenger() {
        return fileManager.getMessenger();
    }

    public ElytraTracker getElytraTracker() {
        return fileManager.getElytraTrackerFile();
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
