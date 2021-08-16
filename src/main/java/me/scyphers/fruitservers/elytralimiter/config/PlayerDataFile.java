package me.scyphers.fruitservers.elytralimiter.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

public class PlayerDataFile extends StorageFile {

    public PlayerDataFile(FileManager manager, UUID playerUUID) throws Exception {
        super(manager, playerUUID);
    }


    @Override
    public void load(YamlConfiguration storageFile) throws Exception {

    }

    @Override
    public void saveData(YamlConfiguration configuration) throws Exception {

    }
}
