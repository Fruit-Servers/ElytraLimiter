package me.scyphers.fruitservers.elytralimiter.config;

import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * An file that this plugin manages, either for configuration or data storage
 * Errors in file loading, configuration reading
 */
public abstract class DataFile {

    protected final FileManager manager;
    private final File file;

    /**
     * Loads a file that the plugin manages using the FileManager
     * @param manager the manager for this file
     * @param filePath the path of the file, starting from this plugins data folder
     * @throws Exception if an exception occurs while initialising the file or reading data from it
     */
    public DataFile(FileManager manager, String filePath) throws Exception {
        Plugin plugin = manager.getPlugin();

        this.manager = manager;
        this.file = new File(plugin.getDataFolder(), filePath);

        // Actual file loading will vary depending on implementation
    }

    /**
     * Save data to the file. Some files are read-only (like configuration files) so implementation is not always required
     * @throws Exception if an exception occurs while saving the data to file
     */
    void save() throws Exception {}

    /**
     * Reload the file. Some files are save-only (like storage files) so implementation is not always required
     * @throws Exception if an exception occurs while reloading the file
     */
    void reload() throws Exception {}

    /**
     * Gets the {@link FileManager} for this data file
     * @return the manager
     */
    public FileManager getManager() {
        return manager;
    }

    /**
     * Gets the {@link File} for this data file
     * @return the file
     */
    public File getFile() {
        return file;
    }

}
