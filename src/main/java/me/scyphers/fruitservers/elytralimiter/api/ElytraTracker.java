package me.scyphers.fruitservers.elytralimiter.api;

import java.util.UUID;

public interface ElytraTracker {

    int getElytraAmount(UUID uuid);

    void addElytra(UUID uuid);

}
