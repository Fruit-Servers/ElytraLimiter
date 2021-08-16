package me.scyphers.fruitservers.elytralimiter.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class Settings extends ConfigFile {

    private int saveTicks;

    public Settings(FileManager manager) throws Exception {
        super(manager, "config.yml");
    }

    @Override
    public void load(YamlConfiguration configuration, YamlConfiguration defaults) throws Exception {
        this.saveTicks = configuration.getInt("fileSaveTicks", 72000);
    }

    public int getSaveTicks() {
        return saveTicks;
    }
}
