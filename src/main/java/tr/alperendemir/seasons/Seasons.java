package tr.alperendemir.seasons;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import tr.alperendemir.seasons.api.TemperatureAPI;
import tr.alperendemir.seasons.config.ConfigManager;
import tr.alperendemir.seasons.data.DataManager;
import tr.alperendemir.seasons.player.PlayerManager;
import tr.alperendemir.seasons.season.SeasonManager;
import tr.alperendemir.seasons.placeholder.SeasonsExpansion;

public class Seasons extends JavaPlugin {

    private static Seasons instance;
    private ConfigManager configManager;
    private SeasonManager seasonManager;
    private PlayerManager playerManager;
    private DataManager dataManager;
    private TemperatureAPI temperatureAPI;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager();
        getLogger().info("ConfigManager created."); // Debug log

        dataManager = new DataManager();
        getLogger().info("DataManager created."); // Debug log

        seasonManager = new SeasonManager(this);
        getLogger().info("SeasonManager created."); // Debug log

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            this.seasonManager.start();
        }, 20L); // Delay by 1 second

        // Schedule activation of effects to ensure everything is initialized
        Bukkit.getScheduler().runTask(this, () -> {
            seasonManager.activateCurrentSeasonEffects();
        });

        playerManager = new PlayerManager();
        getLogger().info("PlayerManager created."); // Debug log

        temperatureAPI = new TemperatureAPI();
        getLogger().info("TemperatureAPI created."); // Debug log

        // Register listeners
        playerManager.registerEvents();

        setupPlaceholders();

        getLogger().info("Seasons plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Unregister listeners to prevent memory leaks
        if (playerManager != null) {
            HandlerList.unregisterAll(playerManager);
        }

        getLogger().info("Seasons plugin has been disabled!");
    }

    public static Seasons getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public TemperatureAPI getTemperatureAPI() {
        return temperatureAPI;
    }

    private void setupPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SeasonsExpansion(this).register();
            getLogger().info("PlaceholderAPI found - Registering placeholders");
        }
    }
}