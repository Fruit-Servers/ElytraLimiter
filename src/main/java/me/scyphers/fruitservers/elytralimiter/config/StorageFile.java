package me.scyphers.fruitservers.elytralimiter.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public abstract class StorageFile extends DataFile {

    private final UUID uuid;

    /**
     * Loads a file that the plugin manages using the FileManager
     *
     * @param manager  the manager for this file
     * @param uuid the unique identifier for this storage file
     * @throws Exception if an exception occurs while initialising the file or reading data from it
     */
    public StorageFile(FileManager manager, UUID uuid) throws Exception {
        super(manager, manager.getEnclosingFolder().getName() + File.separator + uuid.toString() + ".yml");
        this.uuid = uuid;

        File file = this.getFile();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        YamlConfiguration storageFile = YamlConfiguration.loadConfiguration(file);
        this.load(storageFile);

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

    public UUID getUUID() {
        return uuid;
    }

}
