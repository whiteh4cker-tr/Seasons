package tr.alperendemir.seasons.temperature;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
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
        biomeTemperatures.put(Biome.BADLANDS, 30.0);
        biomeTemperatures.put(Biome.SNOWY_PLAINS, -5.0);
        biomeTemperatures.put(Biome.ICE_SPIKES, -5.0);
        biomeTemperatures.put(Biome.OCEAN, 15.0);
        biomeTemperatures.put(Biome.DEEP_OCEAN, 15.0);
        biomeTemperatures.put(Biome.FOREST, 20.0);
        biomeTemperatures.put(Biome.PLAINS, 20.0);
        biomeTemperatures.put(Biome.DEEP_COLD_OCEAN, 5.0);
        biomeTemperatures.put(Biome.COLD_OCEAN, 7.0);
        biomeTemperatures.put(Biome.WARM_OCEAN, 25.0);
        biomeTemperatures.put(Biome.LUKEWARM_OCEAN, 25.0);
        biomeTemperatures.put(Biome.DEEP_LUKEWARM_OCEAN, 20.0);
        biomeTemperatures.put(Biome.DEEP_FROZEN_OCEAN, -7.0);
        biomeTemperatures.put(Biome.FROZEN_OCEAN, -5.0);
        biomeTemperatures.put(Biome.MUSHROOM_FIELDS, 15.0);
        biomeTemperatures.put(Biome.JAGGED_PEAKS, 0.0);
        biomeTemperatures.put(Biome.FROZEN_PEAKS, -10.0);
        biomeTemperatures.put(Biome.STONY_PEAKS, 5.0);
        biomeTemperatures.put(Biome.MEADOW, 18.0);
        biomeTemperatures.put(Biome.CHERRY_GROVE, 18.0);
        biomeTemperatures.put(Biome.SNOWY_SLOPES, -5.0);
        biomeTemperatures.put(Biome.WINDSWEPT_HILLS, 10.0);
        biomeTemperatures.put(Biome.WINDSWEPT_GRAVELLY_HILLS, 10.0);
        biomeTemperatures.put(Biome.WINDSWEPT_FOREST, 10.0);
        biomeTemperatures.put(Biome.FLOWER_FOREST, 22.0);
        biomeTemperatures.put(Biome.TAIGA, 20.0);
        biomeTemperatures.put(Biome.OLD_GROWTH_PINE_TAIGA, 20.0);
        biomeTemperatures.put(Biome.SNOWY_TAIGA, 0.0);
        biomeTemperatures.put(Biome.BIRCH_FOREST, 20.0);
        biomeTemperatures.put(Biome.OLD_GROWTH_BIRCH_FOREST, 20.0);
        biomeTemperatures.put(Biome.DARK_FOREST, 10.0);
        biomeTemperatures.put(Biome.JUNGLE, 25.0);
        biomeTemperatures.put(Biome.SPARSE_JUNGLE, 22.0);
        biomeTemperatures.put(Biome.BAMBOO_JUNGLE, 25.0);
        biomeTemperatures.put(Biome.RIVER, 25.0);
        biomeTemperatures.put(Biome.FROZEN_RIVER, -5.0);
        biomeTemperatures.put(Biome.SWAMP, 25.0);
        biomeTemperatures.put(Biome.MANGROVE_SWAMP, 25.0);
        biomeTemperatures.put(Biome.BEACH, 20.0);
        biomeTemperatures.put(Biome.SNOWY_BEACH, 0.0);
        biomeTemperatures.put(Biome.STONY_SHORE, 10.0);
        biomeTemperatures.put(Biome.SUNFLOWER_PLAINS, 25.0);
        biomeTemperatures.put(Biome.SAVANNA, 30.0);
        biomeTemperatures.put(Biome.SAVANNA_PLATEAU, 28.0);
        biomeTemperatures.put(Biome.WINDSWEPT_SAVANNA, 24.0);
        biomeTemperatures.put(Biome.WOODED_BADLANDS, 28.0);
        biomeTemperatures.put(Biome.ERODED_BADLANDS, 28.0);
        // ... add more biomes and their temperatures ...

        return biomeTemperatures.getOrDefault(biome, 25.0); // Default temperature 25.0
    }
}