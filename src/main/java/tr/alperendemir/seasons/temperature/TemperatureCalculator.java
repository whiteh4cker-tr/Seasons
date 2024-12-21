package tr.alperendemir.seasons.temperature;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.temperature.modifier.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemperatureCalculator {

    private final List<TemperatureModifier> modifiers;

    public TemperatureCalculator() {
        modifiers = new ArrayList<>();
        modifiers.add(new SeasonTemperatureModifier());
        modifiers.add(new BiomeTemperatureModifier());
        modifiers.add(new ArmorTemperatureModifier());
        modifiers.add(new WeatherTemperatureModifier());
        modifiers.add(new TimeTemperatureModifier());
        modifiers.add(new WaterTemperatureModifier());
        modifiers.add(new FoodTemperatureModifier());
        modifiers.add(new HeightTemperatureModifier());
        modifiers.add(new SprintingTemperatureModifier());
        modifiers.add(new BlockLightTemperatureModifier());
    }

    public double calculateTemperature(Player player) {
        // Calculate base temperature based on biome
        double baseTemperature = getBaseTemperatureForBiome(player.getWorld().getBiome(player.getLocation()));

        // Apply modifiers in order
        double finalTemperature = baseTemperature;
        for (TemperatureModifier modifier : modifiers) {
            finalTemperature = modifier.modifyTemperature(player, finalTemperature);
        }

        return finalTemperature;
    }

    private double getBaseTemperatureForBiome(Biome biome) {
        // Use a Map for biome temperatures (you can also load these from config)
        Map<Biome, Double> biomeTemperatures = new HashMap<>();
        biomeTemperatures.put(Biome.DESERT, 40.0);
        biomeTemperatures.put(Biome.BADLANDS, 40.0);
        biomeTemperatures.put(Biome.SNOWY_PLAINS, -5.0);
        biomeTemperatures.put(Biome.ICE_SPIKES, -5.0);
        biomeTemperatures.put(Biome.OCEAN, 15.0);
        biomeTemperatures.put(Biome.DEEP_OCEAN, 15.0);
        biomeTemperatures.put(Biome.FOREST, 20.0);
        biomeTemperatures.put(Biome.PLAINS, 20.0);
        // ... add more biomes and their temperatures ...

        return biomeTemperatures.getOrDefault(biome, 25.0); // Default temperature 25.0
    }
}