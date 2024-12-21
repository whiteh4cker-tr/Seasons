package tr.alperendemir.seasons.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.config.ConfigManager;
import tr.alperendemir.seasons.temperature.TemperatureCalculator;

public class TemperatureEffects {

    private final TemperatureCalculator temperatureCalculator;
    private final ConfigManager configManager;

    public TemperatureEffects() {
        temperatureCalculator = new TemperatureCalculator();
        this.configManager = Seasons.getInstance().getConfigManager();
    }

    public void applyEffects(Player player, double temperature) {
        double coldThreshold = configManager.getColdEffectThreshold();
        double heatThreshold = configManager.getHeatEffectThreshold();

        if (temperature <= coldThreshold) {
            applyColdEffects(player, temperature, coldThreshold);
        } else if (temperature >= heatThreshold) {
            applyHeatEffects(player, temperature, heatThreshold);
        }

        if (configManager.areVisualEffectsEnabled()) {
            applyVisualEffects(player, temperature);
        }
    }

    private void applyColdEffects(Player player, double temperature, double coldThreshold) {
        // Slowness effect
        if (configManager.isColdSlownessEnabled()) {
            int amplifier = calculateAmplifier(temperature, coldThreshold, configManager.getColdSlownessAmplifierPerDegree(), configManager.getColdSlownessMaxAmplifier());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, amplifier));
        }

        // Hunger effect
        if (configManager.isColdHungerEnabled()) {
            int amplifier = calculateAmplifier(temperature, coldThreshold, configManager.getColdHungerAmplifierPerDegree(), configManager.getColdHungerMaxAmplifier());
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, amplifier));
        }

        // Freezing damage
        if (configManager.isFreezingDamageEnabled()) {
            double damageThreshold = coldThreshold + configManager.getFreezingDamageThresholdOffset();
            if (temperature < damageThreshold) {
                double damage = Math.abs(temperature - damageThreshold) * configManager.getFreezingDamagePerDegree();
                player.damage(damage);
            }
        }
    }

    private void applyHeatEffects(Player player, double temperature, double heatThreshold) {
        // Slowness effect
        if (configManager.isHeatSlownessEnabled()) {
            int amplifier = calculateAmplifier(temperature, heatThreshold, configManager.getHeatSlownessAmplifierPerDegree(), configManager.getHeatSlownessMaxAmplifier());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, amplifier));
        }

        // Hunger effect
        if (configManager.isHeatHungerEnabled()) {
            int amplifier = calculateAmplifier(temperature, heatThreshold, configManager.getHeatHungerAmplifierPerDegree(), configManager.getHeatHungerMaxAmplifier());
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, amplifier));
        }

        // Burning effect
        if (configManager.isHeatBurningEnabled()) {
            int ticks = calculateEffectDuration(temperature, heatThreshold, configManager.getHeatBurningTicksPerDegree(), configManager.getHeatBurningMaxTicks());
            player.setFireTicks(ticks);
        }
    }

    private int calculateAmplifier(double temperature, double threshold, double amplifierPerDegree, int maxAmplifier) {
        int amplifier = (int) Math.round(Math.abs(temperature - threshold) * amplifierPerDegree);
        return Math.min(amplifier, maxAmplifier);
    }

    private int calculateEffectDuration(double temperature, double threshold, double ticksPerDegree, int maxTicks) {
        int ticks = (int) Math.round(Math.abs(temperature - threshold) * ticksPerDegree);
        return Math.min(ticks, maxTicks);
    }

    private void applyVisualEffects(Player player, double temperature) {
        double airTemperature = temperatureCalculator.calculateTemperature(player);

        if (temperature >= 40) {
            // TODO: Implement sweating visual effect (e.g., particle effects)
            //player.sendMessage("You are sweating!");
        }

        if (airTemperature < 5) { // Message if below 5 degrees
            //player.sendMessage("Your breath is visible in the cold air!");

            if (airTemperature < 0) { // Particle effect only if below 0 degrees
                spawnSnowflakeParticles(player);
            }
        }
    }

    private void spawnSnowflakeParticles(Player player) {
        Location loc = player.getEyeLocation();
        int particles = 100; // Number of particles
        double radius = 0.5; // Radius around the player's head

        for (int i = 0; i < particles; i++) {
            double angle = 2 * Math.PI * i / particles;
            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);

            Vector offset = new Vector(xOffset, 0, zOffset); // Add randomness to the vertical direction
            Location particleLoc = loc.clone().add(offset);

            player.spawnParticle(Particle.SNOWFLAKE, particleLoc, 1, 0, 0, 0, 0);
        }
    }
}