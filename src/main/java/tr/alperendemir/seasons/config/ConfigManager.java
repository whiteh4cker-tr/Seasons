package tr.alperendemir.seasons.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tr.alperendemir.seasons.Seasons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigManager {

    private final Seasons plugin;
    private FileConfiguration config;
    private FileConfiguration seasonsConfig;

    public ConfigManager() {
        plugin = Seasons.getInstance();
        saveDefaultConfig();
        config = plugin.getConfig();
        loadSeasonsConfig();
    }

    public void saveDefaultConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultConfigStream = plugin.getResource("config.yml");
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
            config.setDefaults(defaultConfig);
        }
    }

    private void loadSeasonsConfig() {
        File seasonsConfigFile = new File(plugin.getDataFolder(), "seasons.yml");
        if (!seasonsConfigFile.exists()) {
            plugin.saveResource("seasons.yml", false);
        }
        seasonsConfig = YamlConfiguration.loadConfiguration(seasonsConfigFile);
    }

    public void saveSeasonsConfig() {
        File seasonsConfigFile = new File(plugin.getDataFolder(), "seasons.yml");
        try {
            seasonsConfig.save(seasonsConfigFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save seasons.yml!");
            e.printStackTrace();
        }
    }

    public int getSeasonDuration() {
        return config.getInt("season-duration", 15); // Default to 1 minute
    }

    public String getCurrentSeasonName() {
        return seasonsConfig.getString("current-season", "SPRING"); // Get from seasons.yml
    }

    public FileConfiguration getSeasonsConfig() {
        return seasonsConfig;
    }

    // --- Other config options ---

    public boolean isTemperatureInFahrenheit() {
        return config.getBoolean("temperature-in-fahrenheit", false);
    }

    public double getColdEffectThreshold() {
        return config.getDouble("cold-effect-threshold", -10.0);
    }

    public double getHeatEffectThreshold() {
        return config.getDouble("heat-effect-threshold", 50.0);
    }

    public boolean areVisualEffectsEnabled() {
        return config.getBoolean("visual-effects.enabled", true);
    }

    public int getTemperatureUpdateInterval() {
        return config.getInt("temperature-update-interval", 2);
    }

    // --- Temperature Effects ---
    public boolean isColdSlownessEnabled() {
        return config.getBoolean("temperature-effects.cold.slowness.enabled", true);
    }

    public double getColdSlownessAmplifierPerDegree() {
        return config.getDouble("temperature-effects.cold.slowness.amplifier-per-degree", 0.2);
    }

    public int getColdSlownessMaxAmplifier() {
        return config.getInt("temperature-effects.cold.slowness.max-amplifier", 5);
    }

    public boolean isColdHungerEnabled() {
        return config.getBoolean("temperature-effects.cold.hunger.enabled", true);
    }

    public double getColdHungerAmplifierPerDegree() {
        return config.getDouble("temperature-effects.cold.hunger.amplifier-per-degree", 0.2);
    }

    public int getColdHungerMaxAmplifier() {
        return config.getInt("temperature-effects.cold.hunger.max-amplifier", 5);
    }

    public boolean isFreezingDamageEnabled() {
        return config.getBoolean("temperature-effects.cold.freezing-damage.enabled", true);
    }

    public double getFreezingDamageThresholdOffset() {
        return config.getDouble("temperature-effects.cold.freezing-damage.threshold-offset", -10.0);
    }

    public double getFreezingDamagePerDegree() {
        return config.getDouble("temperature-effects.cold.freezing-damage.damage-per-degree", 1.0);
    }

    public boolean isHeatSlownessEnabled() {
        return config.getBoolean("temperature-effects.heat.slowness.enabled", true);
    }

    public double getHeatSlownessAmplifierPerDegree() {
        return config.getDouble("temperature-effects.heat.slowness.amplifier-per-degree", 0.2);
    }

    public int getHeatSlownessMaxAmplifier() {
        return config.getInt("temperature-effects.heat.slowness.max-amplifier", 5);
    }

    public boolean isHeatHungerEnabled() {
        return config.getBoolean("temperature-effects.heat.hunger.enabled", true);
    }

    public double getHeatHungerAmplifierPerDegree() {
        return config.getDouble("temperature-effects.heat.hunger.amplifier-per-degree", 0.2);
    }

    public int getHeatHungerMaxAmplifier() {
        return config.getInt("temperature-effects.heat.hunger.max-amplifier", 5);
    }

    public boolean isHeatBurningEnabled() {
        return config.getBoolean("temperature-effects.heat.burning.enabled", true);
    }

    public int getHeatBurningTicksPerDegree() {
        return config.getInt("temperature-effects.heat.burning.ticks-per-degree", 4);
    }

    public int getHeatBurningMaxTicks() {
        return config.getInt("temperature-effects.heat.burning.max-ticks", 100);
    }

    public String getWinterWaterFreezingOption() {
        return config.getString("season-settings.winter.water-freezing", "disabled");
    }
}