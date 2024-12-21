package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;

public class HeightTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        int y = player.getLocation().getBlockY();
        if (y > 100) {
            return currentTemperature - (y - 100) * 0.1; // Decrease 0.1 degree per block above 100
        }
        return currentTemperature;
    }
}