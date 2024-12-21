package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;

public interface TemperatureModifier {
    double modifyTemperature(Player player, double currentTemperature);
}
