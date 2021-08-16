package me.scyphers.fruitservers.elytralimiter.config;

import me.scyphers.fruitservers.elytralimiter.api.ElytraTracker;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElytraTrackerFile extends ConfigStorageFile implements ElytraTracker {

    private Map<UUID, Integer> tracker;

    /**
     * Loads a file that the plugin manages using the FileManager
     *
     * @param manager  the manager for this file
     * @throws Exception if an exception occurs while initialising the file or reading data from it
     */
    public ElytraTrackerFile(FileManager manager) throws Exception {
        super(manager, "elytras.yml");
    }

    @Override
    public void load(YamlConfiguration storageFile) throws Exception {
        this.tracker = new HashMap<>();
        for (String playerKey : storageFile.getKeys(false)) {
            UUID uuid = UUID.fromString(playerKey);
            int count = storageFile.getInt(playerKey, 0);
            tracker.put(uuid, count);
        }
    }

    @Override
    public void saveData(YamlConfiguration configuration) throws Exception {
        for (UUID key : tracker.keySet()) {
            configuration.set(tracker.toString(), tracker.get(key));
        }
    }

    @Override
    public int getElytraAmount(UUID uuid) {
        return tracker.getOrDefault(uuid, 0);
    }

    @Override
    public void addElytra(UUID uuid) {
        if (this.tracker.containsKey(uuid)) {
            int currentCount = this.tracker.get(uuid);
            this.tracker.put(uuid, currentCount + 1);
        } else {
            this.tracker.put(uuid, 1);
        }
    }
}
