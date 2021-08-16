package me.scyphers.fruitservers.elytralimiter.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;

/**
 * A file dedicated for configuration of the plugin. These files are treated as read-only, so {@link DataFile#save()} is not implemented
 * These files are also assumed to be available using {@link Plugin#getResource(String)}
 */
public abstract class ConfigFile extends DataFile {

    public ConfigFile(FileManager manager, String filePath) throws Exception {
        super(manager, filePath);

        // Create the File
        if (!getFile().exists()) {
            getFile().getParentFile().mkdirs();
            manager.getPlugin().saveResource(filePath, false);
        }

        // Initialise YamlConfigurations - current configuration and default configuration
        YamlConfiguration current = YamlConfiguration.loadConfiguration(getFile());
        YamlConfiguration defaults = this.loadDefaultConfig();

        this.load(current, defaults);

    }

    /**
     * For loading all of the data into relevant data storage
     * @param current the file configuration to load data from.
     * @param defaults the default configuration for this file. Provided primarily for when config files require new values not added to config files
     * @throws Exception thrown when there is an error loading data
     */
    public abstract void load(YamlConfiguration current, YamlConfiguration defaults) throws Exception;

    /**
     * For reloading the file after changes have been made. This method only needs to be overridden if calling {@link ConfigFile#load(YamlConfiguration, YamlConfiguration)} multiple times is unsafe
     * @throws Exception if an error occurs while reloading
     */
    @Override
    public void reload() throws Exception {
        YamlConfiguration defaults = loadDefaultConfig();
        YamlConfiguration current = YamlConfiguration.loadConfiguration(getFile());
        this.load(current, defaults);
    }

    private YamlConfiguration loadDefaultConfig() {
        InputStream defaultsStream = manager.getPlugin().getResource(getFile().getName());
        if (defaultsStream == null) throw new IllegalArgumentException("Default resource is null");
        return YamlConfiguration.loadConfiguration(new InputStreamReader(defaultsStream));
    }

}
