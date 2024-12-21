package tr.alperendemir.seasons.season;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.config.ConfigManager;
import tr.alperendemir.seasons.effects.AutumnEffects;
import tr.alperendemir.seasons.effects.SpringEffects;
import tr.alperendemir.seasons.effects.SummerEffects;
import tr.alperendemir.seasons.effects.WinterEffects;

public class SeasonManager {

    private final Seasons plugin;
    private Season currentSeason;
    private SpringEffects springEffects;
    private WinterEffects winterEffects;
    private SummerEffects summerEffects;
    private AutumnEffects autumnEffects;
    private int seasonDuration; // Duration in Minecraft days
    private long seasonStartTime; // Time when the current season started

    public SeasonManager(Seasons plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("SeasonManager constructor called."); // Debug log

        loadConfiguration();
        startSeasonTimer();
        // activateCurrentSeasonEffects(); // Removed this line
    }

    public void activateCurrentSeasonEffects() {
        plugin.getLogger().info("Activating effects for season: " + currentSeason);
        if (currentSeason == Season.SPRING) {
            springEffects = new SpringEffects(plugin);
            springEffects.startSpringEffects();
        } else if (currentSeason == Season.WINTER) {
            winterEffects = new WinterEffects(plugin);
            winterEffects.startWinterEffects();
        } else if (currentSeason == Season.SUMMER) {
            summerEffects = new SummerEffects(plugin);
            summerEffects.startSummerEffects();
        } else if (currentSeason == Season.AUTUMN) {
            autumnEffects = new AutumnEffects(plugin);
            autumnEffects.startAutumnEffects();
        }
    }

    private void loadConfiguration() {
        ConfigManager configManager = plugin.getConfigManager();

        plugin.getLogger().info("SeasonManager loading configuration."); // Debug log

        // Load season duration from config.yml
        seasonDuration = configManager.getSeasonDuration();
        plugin.getLogger().info("Season duration loaded: " + seasonDuration); // Debug log

        // Load the current season from seasons.yml using ConfigManager
        String seasonName = configManager.getCurrentSeasonName();
        plugin.getLogger().info("Current season loaded from config: " + seasonName); // Debug log

        currentSeason = Season.valueOf(seasonName.toUpperCase());

        // Load season start time from config.yml
        seasonStartTime = configManager.getConfig().getLong("season-start-time", 0);

        if (seasonStartTime == 0) {
            // First-time setup or reset
            seasonStartTime = getCurrentWorldTime();
        }
        saveCurrentSeasonData(); // Save data after loading
    }

    public void setSeason(Season newSeason) {
        if (currentSeason == newSeason) {
            return; // Season is already the same
        }

        // Clean up previous season's effects
        if (currentSeason == Season.SPRING && springEffects != null) {
            HandlerList.unregisterAll(springEffects);
            springEffects = null;
        } else if (currentSeason == Season.WINTER && winterEffects != null) {
            HandlerList.unregisterAll(winterEffects);
            winterEffects = null;
        } else if (currentSeason == Season.SUMMER && summerEffects != null) {
            HandlerList.unregisterAll(summerEffects);
            summerEffects = null;
        } else if (currentSeason == Season.AUTUMN && autumnEffects != null) {
            HandlerList.unregisterAll(autumnEffects);
            autumnEffects = null;
        }

        currentSeason = newSeason;
        seasonStartTime = getCurrentWorldTime();
        saveCurrentSeasonData();

        // Activate effects for the new season
        activateCurrentSeasonEffects();
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    private void startSeasonTimer() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (isSeasonChangeDue()) {
                changeSeason();
            }
        }, 0L, 1200L); // Check every minute (1200 ticks)
    }

    private boolean isSeasonChangeDue() {
        long currentTime = getCurrentWorldTime();
        long elapsedTime = currentTime - seasonStartTime;
        long seasonDurationTicks = seasonDuration * 24000L; // Convert days to ticks

        return elapsedTime >= seasonDurationTicks;
    }

    private void changeSeason() {
        // Cycle through the seasons
        int nextSeasonOrdinal = (currentSeason.ordinal() + 1) % Season.values().length;
        setSeason(Season.values()[nextSeasonOrdinal]);
    }

    private long getCurrentWorldTime() {
        // Assuming the main world is the first one loaded by the server
        return Bukkit.getWorlds().get(0).getTime();
    }

    private void saveCurrentSeasonData() {
        // Save to config.yml
        plugin.getConfigManager().getConfig().set("season-start-time", seasonStartTime);
        plugin.saveConfig();

        // Save to seasons.yml
        plugin.getConfigManager().getSeasonsConfig().set("current-season", currentSeason.name());
        plugin.getConfigManager().saveSeasonsConfig();
    }

    public enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }
}