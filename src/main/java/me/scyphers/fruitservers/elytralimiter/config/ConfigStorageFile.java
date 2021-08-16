package me.scyphers.fruitservers.elytralimiter.config;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class ConfigStorageFile extends DataFile {
    /**
     * Loads a file that the plugin manages using the FileManager
     *
     * @param manager  the manager for this file
     * @param filePath the path of the file, starting from this plugins data folder
     * @throws Exception if an exception occurs while initialising the file or reading data from it
     */
    public ConfigStorageFile(FileManager manager, String filePath) throws Exception {
        super(manager, filePath);

        // Create the File
        if (!getFile().exists()) {
            getFile().getParentFile().mkdirs();
            manager.getPlugin().saveResource(filePath, false);
        }

        // Initialise YamlConfigurations - current configuration and default configuration
        YamlConfiguration current = YamlConfiguration.loadConfiguration(getFile());

        this.load(current);

    }

    /**
     * Load all data from the storage file
     * @param storageFile the configuration to load data from
     */
    public abstract void load(YamlConfiguration storageFile) throws Exception;

    /**
     * For saving all data into an empty configuration which is then saved to file by {@link ConfigFile#save()}. If the config file is intended to be a ready only type file (e.g. config.yml) then this method should always return false
     * @throws Exception If an error occurs saving the data
     */
    public abstract void saveData(YamlConfiguration configuration) throws Exception;

    /**
     * Saves the data to the file.
     * @throws Exception if an exception occurs while trying to save data to configuration or file
     */
    @Override
    public void save() throws Exception {
        YamlConfiguration configuration = new YamlConfiguration();
        this.saveData(configuration);
        configuration.save(getFile());
    }
}
