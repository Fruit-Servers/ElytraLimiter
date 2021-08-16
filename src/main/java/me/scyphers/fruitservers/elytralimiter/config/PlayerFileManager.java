package me.scyphers.fruitservers.elytralimiter.config;

import me.scyphers.fruitservers.elytralimiter.ElytraLimiter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerFileManager implements FileManager {
    
    private final ElytraLimiter plugin;
    
    private final Map<UUID, PlayerDataFile> dataFiles;

    private final File enclosingFolder;

    private boolean safeToSave = true;

    private final int saveTaskID;
    
    public PlayerFileManager(ElytraLimiter plugin, String dataFolderName) {
        this.plugin = plugin;
        this.dataFiles = new HashMap<>();

        this.enclosingFolder = new File(plugin.getDataFolder(), dataFolderName);
        enclosingFolder.mkdirs();

        int saveTicks = plugin.getSettings().getSaveTicks();
        this.saveTaskID = scheduleSaveTask(saveTicks);

    }
    
    // This is data storage, as such there is only reading and writing, no reloading.
    @Override
    public void reloadConfigs() throws Exception {
        
    }

    @Override
    public void saveAll() throws Exception {
        for (PlayerDataFile file : dataFiles.values()) {
            file.save();
        }
    }

    @Override
    public ElytraLimiter getPlugin() {
        return plugin;
    }

    @Override
    public File getEnclosingFolder() {
        return enclosingFolder;
    }

    @Override
    public int scheduleSaveTask(int saveTicks) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (safeToSave) {
                try {
                    saveAll();
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not save player data config files!");
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

    public PlayerDataFile getDataFile(UUID uuid) throws Exception {
        if (dataFiles.containsKey(uuid)) {
            return dataFiles.get(uuid);
        } else {
            PlayerDataFile dataFile = new PlayerDataFile(this, uuid);
            dataFiles.put(uuid, dataFile);
            return dataFile;
        }
    }
    
}
