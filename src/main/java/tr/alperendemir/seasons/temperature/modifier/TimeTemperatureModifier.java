package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class TimeTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        World world = player.getWorld();
        long time = world.getTime();

        double timeModifier = 0; // Initialize the modifier to 0

        // Adjust temperature based on time of day
        if (time > 0 && time < 12000) {
            // Day (adjust between 0 and 12000 instead of 6000 and 18000)
            timeModifier = 5.0;
        } else {
            // Night (no modifier needed, as it defaults to 0)
            timeModifier = -5;
        }

        return currentTemperature + timeModifier; // Apply the modifier
    }
}