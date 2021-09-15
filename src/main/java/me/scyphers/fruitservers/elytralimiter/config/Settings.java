package me.scyphers.fruitservers.elytralimiter.config;

import me.scyphers.scycore.api.PluginSettings;
import me.scyphers.scycore.config.ConfigFile;
import me.scyphers.scycore.config.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class Settings extends ConfigFile implements PluginSettings {

    private int saveTicks;
    private int maxElytras;
    private List<String> worlds;

    public Settings(FileManager manager) throws Exception {
        super(manager, "config.yml");
    }

    @Override
    public void load(YamlConfiguration configuration, YamlConfiguration defaults) {
        this.saveTicks = configuration.getInt("fileSaveTicks", 72000);
        this.maxElytras = configuration.getInt("maxElytras", 2);
        this.worlds = configuration.getStringList("worlds");
    }

    public int getMaxElytras() {
        return maxElytras;
    }

    @Override
    public int getSaveTicks() {
        return saveTicks;
    }

    public List<String> getWorlds() {
        return worlds;
    }
}
