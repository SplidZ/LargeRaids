package com.solarrabbit.largeraids;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public interface AbstractVillages {

    void addVillage(Location location, Runnable ifSuccess, Runnable ifFail);

    void removeVillage(Location location);

    default JavaPlugin getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

}
