package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.season.SeasonManager;

public class SeasonTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        SeasonManager.Season season = Seasons.getInstance().getSeasonManager().getCurrentSeason();
        double seasonModifier = 0;
        switch (season) {
            case SPRING:
                seasonModifier = 5.0;
                break;
            case SUMMER:
                seasonModifier = 15.0;
                break;
            case AUTUMN:
                seasonModifier = -5.0;
                break;
            case WINTER:
                seasonModifier = -15.0;
                break;
        }

        return currentTemperature + seasonModifier;
    }
}