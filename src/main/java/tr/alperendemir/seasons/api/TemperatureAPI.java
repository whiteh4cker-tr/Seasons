package tr.alperendemir.seasons.api;

import org.bukkit.entity.Player;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.player.PlayerTemperature;

public class TemperatureAPI {

    public double getPlayerTemperature(Player player) {
        PlayerTemperature playerTemperature = Seasons.getInstance().getPlayerManager().getPlayerTemperature(player.getUniqueId());
        if (playerTemperature != null) {
            return playerTemperature.getTemperature();
        }
        return Double.NaN; // Or a default value
    }
}