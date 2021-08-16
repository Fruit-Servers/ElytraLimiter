package me.scyphers.fruitservers.elytralimiter.config;

import me.scyphers.fruitservers.elytralimiter.ElytraLimiter;
import org.bukkit.Bukkit;

public class SimpleFileManager implements FileManager {

    private final ElytraLimiter plugin;

    // Config Files
    private final Settings settings;
    private final MessengerFile messengerFile;
    private final ElytraTrackerFile elytraTrackerFile;

    private boolean safeToSave = true;

    private final int saveTaskID;

    public SimpleFileManager(ElytraLimiter plugin) throws Exception {
        this.plugin = plugin;
        this.settings = new Settings(this);
        this.messengerFile = new MessengerFile(this);
        this.elytraTrackerFile = new ElytraTrackerFile(this);

        // Schedule a repeating task to save the configs
        int saveTicks = settings.getSaveTicks();
        this.saveTaskID = scheduleSaveTask(saveTicks);
    }

    @Override
    public void reloadConfigs() throws Exception {
        settings.reload();
        messengerFile.reload();
        elytraTrackerFile.reload();
    }

    @Override
    public void saveAll() throws Exception {
        settings.save();
        messengerFile.save();
        elytraTrackerFile.save();
    }

    @Override
    public ElytraLimiter getPlugin() {
        return plugin;
    }

    @Override
    public int scheduleSaveTask(int saveTicks) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (safeToSave) {
                try {
                    saveAll();
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not save config files!");
                    e.printStackTrace();
                    safeToSave = false;
                }
            }

        }, saveTicks, saveTicks);
    }

    @Override
    public void cancelSaveTask() {
        Bukkit.getScheduler().cancelTask(saveTaskID);
    }

    public Settings getSettings() {
        return settings;
    }

    public MessengerFile getMessenger() {
        return messengerFile;
    }

    public ElytraTrackerFile getElytraTrackerFile() {
        return elytraTrackerFile;
    }
}
