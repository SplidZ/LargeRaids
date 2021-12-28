package com.solarrabbit.largeraids;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.solarrabbit.largeraids.PluginLogger.Level;
import com.solarrabbit.largeraids.command.GiveSummonItemCommand;
import com.solarrabbit.largeraids.command.ReloadPlugin;
import com.solarrabbit.largeraids.command.StartRaidCommand;
import com.solarrabbit.largeraids.command.StopRaidCommand;
import com.solarrabbit.largeraids.command.VillageCentresCommand;
import com.solarrabbit.largeraids.command.completer.StartRaidCommandCompleter;
import com.solarrabbit.largeraids.command.completer.VillageCentersCommandCompleter;
import com.solarrabbit.largeraids.config.RaidConfig;
import com.solarrabbit.largeraids.config.trigger.TriggersConfig;
import com.solarrabbit.largeraids.database.DatabaseAdapter;
import com.solarrabbit.largeraids.listener.BukkitRaidListener;
import com.solarrabbit.largeraids.listener.DropInLavaTriggerListener;
import com.solarrabbit.largeraids.listener.TimeBombTriggerListener;
import com.solarrabbit.largeraids.listener.TriggerListener;
import com.solarrabbit.largeraids.listener.omen.VillageAbsorbOmenListener;
import com.solarrabbit.largeraids.support.Placeholder;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LargeRaids extends JavaPlugin {
    private YamlConfiguration messages;
    private PluginLogger logger;
    private DatabaseAdapter db;
    private Set<TriggerListener> registeredTriggerListeners;
    private RaidConfig raidConfig;
    private TriggersConfig triggerConfig;
    private BukkitRaidListener mainListener;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.logger = new PluginLogger();

        this.db = new DatabaseAdapter(this);
        this.db.load();

        mainListener = new BukkitRaidListener(this);
        this.getServer().getPluginManager().registerEvents(mainListener, this);
        mainListener.init();

        getCommand("lrstart").setExecutor(new StartRaidCommand(this));
        getCommand("lrstart").setTabCompleter(new StartRaidCommandCompleter(db));
        getCommand("lrstop").setExecutor(new StopRaidCommand(this));
        getCommand("lrgive").setExecutor(new GiveSummonItemCommand(this));
        getCommand("lrreload").setExecutor(new ReloadPlugin(this));
        getCommand("lrcenters").setExecutor(new VillageCentresCommand(this));
        getCommand("lrcenters").setTabCompleter(new VillageCentersCommandCompleter(db));

        loadMessages();
        testConfig();
        raidConfig = new RaidConfig(getConfig().getConfigurationSection("raid"));
        triggerConfig = new TriggersConfig(getConfig().getConfigurationSection("trigger"));
        reloadTriggers();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder(this).register();
        }
    }

    public void log(String message, Level level) {
        this.logger.sendMessage(message, level);
        if (level == Level.FAIL) {
            this.logger.sendMessage("Disabling plugin...", level);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(this, key);
    }

    public void reload() {
        reloadConfig();
        testConfig();
        raidConfig = new RaidConfig(getConfig().getConfigurationSection("raid"));
        triggerConfig = new TriggersConfig(getConfig().getConfigurationSection("trigger"));
        reloadTriggers();
    }

    public String getMessage(String node) {
        return messages.getString(node, "");
    }

    public BukkitRaidListener getBukkitRaidListener() {
        return mainListener;
    }

    public DatabaseAdapter getDatabaseAdapter() {
        return db;
    }

    public RaidConfig getRaidConfig() {
        return raidConfig;
    }

    public TriggersConfig getTriggerConfig() {
        return triggerConfig;
    }

    private void reloadTriggers() {
        if (registeredTriggerListeners != null) // Unregister
            for (TriggerListener listener : registeredTriggerListeners)
                listener.unregisterListener();
        registeredTriggerListeners = new HashSet<>();

        if (triggerConfig.getOmenConfig().isEnabled())
            registerTrigger(new VillageAbsorbOmenListener(this), true);
        if (triggerConfig.getDropInLavaConfig().isEnabled())
            registerTrigger(new DropInLavaTriggerListener(this), true);
        if (triggerConfig.getTimeBombConfig().isEnabled())
            registerTrigger(new TimeBombTriggerListener(this), false);
    }

    private void registerTrigger(TriggerListener listener, boolean registerEvents) {
        if (registerEvents)
            getServer().getPluginManager().registerEvents(listener, this);
        registeredTriggerListeners.add(listener);
    }

    private void loadMessages() {
        messages = new YamlConfiguration();
        try {
            messages.load(new InputStreamReader(this.getResource("messages.yml")));
        } catch (IOException | InvalidConfigurationException e) {
            this.log("Unable to load messages!", Level.FAIL);
        }
    }

    private void testConfig() {
        int totalWaves = this.getConfig().getInt("raid.waves");
        ConfigurationSection section = this.getConfig().getConfigurationSection("raid.mobs");
        for (String mob : section.getKeys(false)) {
            if (section.getIntegerList(mob).size() < totalWaves) {
                this.log(this.messages.getString("config.invalid-mob-array-length"), Level.FAIL);
                return;
            }
        }
        for (int i = 0; i < totalWaves; i++) {
            final int wave = i;
            int totalRaiders = section.getKeys(false).stream().map(key -> section.getIntegerList(key).get(wave))
                    .reduce(0, (x, y) -> x + y);
            if (totalRaiders == 0) {
                this.log(this.messages.getString("config.zero-raider-wave"), Level.FAIL);
                return;
            }
        }
    }

}
