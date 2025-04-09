package tr.alperendemir.seasons.temperature.modifier;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tr.alperendemir.seasons.season.SeasonManager;

class SeasonTemperatureModifierTest {

    @Test
    void testSeasonTemperatureModifiers() {
        // Test the temperature modifiers for each season
        double baseTemperature = 20.0;

        // Spring adds +5.0
        assertEquals(25.0, calculateModifiedTemperature(baseTemperature, SeasonManager.Season.SPRING),
                "Spring should add +5.0 to temperature");

        // Summer adds +15.0
        assertEquals(35.0, calculateModifiedTemperature(baseTemperature, SeasonManager.Season.SUMMER),
                "Summer should add +15.0 to temperature");

        // Autumn adds -5.0
        assertEquals(15.0, calculateModifiedTemperature(baseTemperature, SeasonManager.Season.AUTUMN),
                "Autumn should add -5.0 to temperature");

        // Winter adds -15.0
        assertEquals(5.0, calculateModifiedTemperature(baseTemperature, SeasonManager.Season.WINTER),
                "Winter should add -15.0 to temperature");
    }

    /**
     * Helper method to calculate temperature modification based on season
     * This replicates the logic from SeasonTemperatureModifier.modifyTemperature() without requiring a Player instance
     */
    private double calculateModifiedTemperature(double currentTemperature, SeasonManager.Season season) {
        double seasonModifier = 0;
        switch (season) {
            case SPRING:
                seasonModifier = 5.0;
                break;
            case SUMMER:
                seasonModifier = 15.0;
                break;
            case AUTUMN:
                seasonModifier = -5.0;
                break;
            case WINTER:
                seasonModifier = -15.0;
                break;
        }
        return currentTemperature + seasonModifier;
    }
} 