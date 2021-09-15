package me.scyphers.fruitservers.elytralimiter.config;

import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.config.SimpleFileManager;

public class ElytraFileManager extends SimpleFileManager {

    private final ElytraTrackerFile elytraTrackerFile;
    private final Settings settings;

    public ElytraFileManager(BasePlugin plugin) throws Exception {
        super(plugin);
        this.elytraTrackerFile = new ElytraTrackerFile(this);
        this.settings = new Settings(this);
    }

    @Override
    public void saveAll() throws Exception {
        super.saveAll();
        elytraTrackerFile.save();
    }

    public ElytraTrackerFile getElytraTrackerFile() {
        return elytraTrackerFile;
    }

    public Settings getSettings() {
        return settings;
    }
}
