package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;

public class BlockLightTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        int lightLevel = player.getLocation().getBlock().getLightLevel();
        if (lightLevel > 10) {
            return currentTemperature + (lightLevel - 10) * 0.2; // Increase 0.2 degrees per light level above 10
        }
        return currentTemperature;
    }
}
