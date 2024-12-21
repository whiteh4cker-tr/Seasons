package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;

public class SprintingTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        if (player.isSprinting()) {
            return currentTemperature + 2; // Increase temperature when sprinting
        }
        return currentTemperature;
    }
}