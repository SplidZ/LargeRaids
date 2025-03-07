package com.solarrabbit.largeraids.config;

import org.bukkit.configuration.ConfigurationSection;

public class MiscConfig {
    private final boolean shouldBellOutlineNormal;
    private final boolean shouldBellOutlineLarge;
    private final int bellOutlineDuration;

    public MiscConfig(ConfigurationSection config) {
        ConfigurationSection bellOutlineConfig = config.getConfigurationSection("bell-outline-raiders");
        shouldBellOutlineNormal = bellOutlineConfig.getBoolean("normal-raid");
        shouldBellOutlineLarge = bellOutlineConfig.getBoolean("large-raid");
        bellOutlineDuration = bellOutlineConfig.getInt("duration");
    }

    public boolean shouldBellOutlineLarge() {
        return shouldBellOutlineLarge;
    }

    public boolean shouldBellOutlineNormal() {
        return shouldBellOutlineNormal;
    }

    public int getBellOutlineDuration() {
        return bellOutlineDuration;
    }
}
