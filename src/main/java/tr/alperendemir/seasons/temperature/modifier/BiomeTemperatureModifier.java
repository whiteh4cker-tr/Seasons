package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import static org.bukkit.block.Biome.DESERT;
import static org.bukkit.block.Biome.ICE_SPIKES;

public class BiomeTemperatureModifier implements tr.alperendemir.seasons.temperature.modifier.TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        Biome biome = player.getWorld().getBiome(player.getLocation());
        // You'll need a more comprehensive biome temperature mapping
        return currentTemperature;
    }
}