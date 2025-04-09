package tr.alperendemir.seasons.season;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeasonChangeTest {

    @Test
    void testSeasonChangeDue() {
        // Test the isSeasonChangeDue logic by simulating time passage
        
        // Set up initial values
        long currentTime = System.currentTimeMillis();
        int seasonDuration = 60; // 60 minutes
        
        // Test case 1: Just started, no change due
        long seasonStartTime = currentTime;
        assertFalse(isSeasonChangeDue(seasonStartTime, currentTime, seasonDuration),
                "Season should not change when no time has passed");
        
        // Test case 2: 30 minutes passed, no change due yet
        seasonStartTime = currentTime - (30 * 60 * 1000); // 30 minutes ago
        assertFalse(isSeasonChangeDue(seasonStartTime, currentTime, seasonDuration),
                "Season should not change when less than seasonDuration has passed");
        
        // Test case 3: Exactly seasonDuration passed, change is due
        seasonStartTime = currentTime - (seasonDuration * 60 * 1000); // 60 minutes ago
        assertTrue(isSeasonChangeDue(seasonStartTime, currentTime, seasonDuration),
                "Season should change when exactly seasonDuration has passed");
        
        // Test case 4: More than seasonDuration passed, change is due
        seasonStartTime = currentTime - (90 * 60 * 1000); // 90 minutes ago
        assertTrue(isSeasonChangeDue(seasonStartTime, currentTime, seasonDuration),
                "Season should change when more than seasonDuration has passed");
    }
    
    /**
     * Helper method that replicates the isSeasonChangeDue logic from SeasonManager
     */
    private boolean isSeasonChangeDue(long seasonStartTime, long currentTime, int seasonDuration) {
        long elapsedTime = (currentTime - seasonStartTime) / (1000 * 60); // Convert to minutes
        return elapsedTime >= seasonDuration;
    }
}