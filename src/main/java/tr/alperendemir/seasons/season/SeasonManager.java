package tr.alperendemir.seasons.season;

import org.bukkit.Bukkit;
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
    private int seasonDuration; // Duration in minutes
    private long seasonStartRealTime; // System time when the season started

    public SeasonManager(Seasons plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("SeasonManager constructor called."); // Debug log

        // Load configuration but do not start timers yet
        loadConfiguration();
    }

    /**
     * Starts the season timer and activates effects for the current season.
     * This method should be called after the plugin is fully enabled.
     */
    public void start() {
        plugin.getLogger().info("Starting SeasonManager timers and effects.");
        startSeasonTimer();
        activateCurrentSeasonEffects();
    }

    private void loadConfiguration() {
        ConfigManager configManager = plugin.getConfigManager();

        plugin.getLogger().info("SeasonManager loading configuration."); // Debug log

        // Load season duration from config.yml using ConfigManager's method
        seasonDuration = configManager.getSeasonDuration();
        plugin.getLogger().info("Season duration loaded: " + seasonDuration); // Debug log

        // Load the current season from seasons.yml using ConfigManager
        String seasonName = configManager.getCurrentSeasonName();
        plugin.getLogger().info("Current season loaded from config: " + seasonName); // Debug log

        if (seasonName != null) {
            currentSeason = Season.valueOf(seasonName.toUpperCase());
        } else {
            currentSeason = Season.SPRING; // Default to SPRING if no season is configured
            plugin.getLogger().warning("No season configured in seasons.yml. Defaulting to SPRING.");
        }

        // Initialize season start time based on the configured duration
        seasonStartRealTime = System.currentTimeMillis();
    }

    public void setSeason(Season newSeason) {
        if (currentSeason == newSeason) {
            return; // Season is already the same
        }

        // Clean up previous season's effects
        unregisterSeasonEffects(currentSeason);

        currentSeason = newSeason;
        seasonStartRealTime = System.currentTimeMillis(); // Update to the current time
        saveCurrentSeasonData();

        // Activate effects for the new season
        activateCurrentSeasonEffects();
    }

    private void unregisterSeasonEffects(Season season) {
        switch (season) {
            case SPRING:
                if (springEffects != null) {
                    HandlerList.unregisterAll(springEffects);
                    springEffects = null;
                }
                break;
            case WINTER:
                if (winterEffects != null) {
                    HandlerList.unregisterAll(winterEffects);
                    winterEffects = null;
                }
                break;
            case SUMMER:
                if (summerEffects != null) {
                    HandlerList.unregisterAll(summerEffects);
                    summerEffects = null;
                }
                break;
            case AUTUMN:
                if (autumnEffects != null) {
                    HandlerList.unregisterAll(autumnEffects);
                    autumnEffects = null;
                }
                break;
        }
    }

    public void activateCurrentSeasonEffects() {
        plugin.getLogger().info("Activating effects for season: " + currentSeason);
        switch (currentSeason) {
            case SPRING:
                springEffects = new SpringEffects(plugin);
                break;
            case WINTER:
                winterEffects = new WinterEffects(plugin);
                winterEffects.startWinterEffects();
                break;
            case SUMMER:
                summerEffects = new SummerEffects(plugin);
                summerEffects.startSummerEffects();
                break;
            case AUTUMN:
                autumnEffects = new AutumnEffects(plugin);
                autumnEffects.startAutumnEffects();
                break;
        }
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    private void startSeasonTimer() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (isSeasonChangeDue()) {
                changeSeason();
            }
        }, 0L, 60 * 20L); // Check every minute (60 seconds * 20 ticks/second)
    }

    private boolean isSeasonChangeDue() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - seasonStartRealTime) / (1000 * 60); // Convert to minutes

        return elapsedTime >= seasonDuration;
    }

    private void changeSeason() {
        // Cycle through the seasons
        int nextSeasonOrdinal = (currentSeason.ordinal() + 1) % Season.values().length;
        setSeason(Season.values()[nextSeasonOrdinal]);
    }

    private void saveCurrentSeasonData() {
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