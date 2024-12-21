package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;

public class WaterTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        if (player.isInWater()) {
            return currentTemperature - 2;
        } else {
            return currentTemperature;
        }
    }
}