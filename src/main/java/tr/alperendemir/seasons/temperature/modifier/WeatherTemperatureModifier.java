package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

public class WeatherTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        if (player.getWorld().isClearWeather()) {
            return currentTemperature + 2;
        } else if (player.getWorld().hasStorm()) {
            return currentTemperature - 2;
        } else {
            return currentTemperature;
        }
    }
}