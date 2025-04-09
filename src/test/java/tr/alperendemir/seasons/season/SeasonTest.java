package tr.alperendemir.seasons.season;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeasonTest {
    
    @Test
    void testSeasonEnumValues() {
        // Test that Season enum has the correct values
        SeasonManager.Season[] seasons = SeasonManager.Season.values();
        
        assertEquals(4, seasons.length, "There should be exactly 4 seasons");
        assertEquals(SeasonManager.Season.SPRING, seasons[0], "First season should be SPRING");
        assertEquals(SeasonManager.Season.SUMMER, seasons[1], "Second season should be SUMMER");
        assertEquals(SeasonManager.Season.AUTUMN, seasons[2], "Third season should be AUTUMN");
        assertEquals(SeasonManager.Season.WINTER, seasons[3], "Fourth season should be WINTER");
    }
    
    @Test
    void testSeasonCycling() {
        // Test cycling through seasons in the correct order
        SeasonManager.Season[] seasons = SeasonManager.Season.values();
        
        for (int i = 0; i < seasons.length; i++) {
            int nextIndex = (i + 1) % seasons.length;
            
            // This simulates the logic in SeasonManager.changeSeason()
            SeasonManager.Season nextSeason = SeasonManager.Season.values()[nextIndex];
            
            switch (seasons[i]) {
                case SPRING:
                    assertEquals(SeasonManager.Season.SUMMER, nextSeason, "After SPRING should come SUMMER");
                    break;
                case SUMMER:
                    assertEquals(SeasonManager.Season.AUTUMN, nextSeason, "After SUMMER should come AUTUMN");
                    break;
                case AUTUMN:
                    assertEquals(SeasonManager.Season.WINTER, nextSeason, "After AUTUMN should come WINTER");
                    break;
                case WINTER:
                    assertEquals(SeasonManager.Season.SPRING, nextSeason, "After WINTER should come SPRING");
                    break;
            }
        }
    }
    
    @Test
    void testSeasonNames() {
        // Test that season names are correct and match expected string values
        assertEquals("SPRING", SeasonManager.Season.SPRING.name(), "SPRING enum should have name 'SPRING'");
        assertEquals("SUMMER", SeasonManager.Season.SUMMER.name(), "SUMMER enum should have name 'SUMMER'");
        assertEquals("AUTUMN", SeasonManager.Season.AUTUMN.name(), "AUTUMN enum should have name 'AUTUMN'");
        assertEquals("WINTER", SeasonManager.Season.WINTER.name(), "WINTER enum should have name 'WINTER'");
    }
} 