package tr.alperendemir.seasons.data;

import org.bukkit.configuration.file.YamlConfiguration;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.player.PlayerTemperature;
import tr.alperendemir.seasons.season.SeasonManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class DataManager {

    private final File playerDataFolder;
    private final File seasonsDataFile;
    private final Seasons plugin;

    public DataManager() {
        plugin = Seasons.getInstance();
        playerDataFolder = new File(plugin.getDataFolder(), "players");
        seasonsDataFile = new File(plugin.getDataFolder(), "seasons.yml");

        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        saveDefaultSeasonsConfig(); // Handle default seasons config
    }

    private void saveDefaultSeasonsConfig() {
        if (!seasonsDataFile.exists()) {
            plugin.saveResource("seasons.yml", false); // Copy from resources if it doesn't exist
        }

        // Load the seasons file after saving
        YamlConfiguration seasonsConfig = YamlConfiguration.loadConfiguration(seasonsDataFile);

        // Get the default config from the JAR
        InputStream defaultSeasonsConfigStream = plugin.getResource("seasons.yml");
        if (defaultSeasonsConfigStream != null) {
            YamlConfiguration defaultSeasonsConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultSeasonsConfigStream));
            seasonsConfig.setDefaults(defaultSeasonsConfig); // Set defaults from the JAR file
        }
    }

    // ... rest of your DataManager methods (loadPlayerData, savePlayerData, etc.) ...

    public PlayerTemperature loadPlayerData(UUID playerId) {
        File playerFile = new File(playerDataFolder, playerId + ".yml");
        if (!playerFile.exists()) {
            // Create a new PlayerTemperature with default values
            return new PlayerTemperature(Seasons.getInstance().getServer().getPlayer(playerId), 25.0); // 25.0 is a default temperature
        }

        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
        double temperature = playerData.getDouble("temperature", 25.0);

        return new PlayerTemperature(Seasons.getInstance().getServer().getPlayer(playerId), temperature);
    }

    public void savePlayerData(UUID playerId, PlayerTemperature playerTemperature) {
        File playerFile = new File(playerDataFolder, playerId + ".yml");
        YamlConfiguration playerData = new YamlConfiguration();

        playerData.set("temperature", playerTemperature.getTemperature());

        try {
            playerData.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SeasonManager.Season getCurrentSeason() {
        YamlConfiguration seasonsData = YamlConfiguration.loadConfiguration(seasonsDataFile);
        String seasonName = seasonsData.getString("current-season");

        if (seasonName == null) {
            return SeasonManager.Season.SPRING; // Default season
        }

        try {
            return SeasonManager.Season.valueOf(seasonName.toUpperCase());
        } catch (IllegalArgumentException e) {
            Seasons.getInstance().getLogger().warning("Invalid season name in seasons.yml: " + seasonName);
            return SeasonManager.Season.SPRING; // Default season
        }
    }

    public void setCurrentSeason(SeasonManager.Season season) {
        YamlConfiguration seasonsData = YamlConfiguration.loadConfiguration(seasonsDataFile);
        seasonsData.set("current-season", season.name());

        try {
            seasonsData.save(seasonsDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}