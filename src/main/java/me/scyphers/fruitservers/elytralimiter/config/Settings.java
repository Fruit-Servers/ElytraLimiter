package me.scyphers.fruitservers.elytralimiter.config;

import me.scyphers.scycore.api.FileSettings;
import me.scyphers.scycore.config.ConfigFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings extends ConfigFile implements FileSettings {

    private int saveTicks;

    private int maxElytras;

    public Settings(FileManager manager) throws Exception {
        super(manager, "config.yml");
    }

    @Override
    public void load(YamlConfiguration configuration, YamlConfiguration defaults) throws Exception {
        this.saveTicks = configuration.getInt("fileSaveTicks", 72000);
        this.maxElytras = configuration.getInt("maxElytras", 2);

    }

    public int getMaxElytras() {
        return maxElytras;
    }

    @Override
    public int getSaveTicks() {
        return saveTicks;
    }
}
