package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.player.PlayerTemperature;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoodTemperatureModifier implements TemperatureModifier, Listener {

    private final Map<UUID, Long> lastFoodConsumptionTime = new HashMap<>();

    public FoodTemperatureModifier() {
        Seasons.getInstance().getServer().getPluginManager().registerEvents(this, Seasons.getInstance());
    }

    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        long lastConsumption = lastFoodConsumptionTime.getOrDefault(player.getUniqueId(), 0L);
        if (System.currentTimeMillis() - lastConsumption < 10000) { // 10 seconds
            return currentTemperature + 2; // Increase temperature for 10 seconds after eating
        }
        return currentTemperature;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        // Example foods that might warm you up
        switch (event.getItem().getType()) {
            case COOKED_BEEF:
            case COOKED_CHICKEN:
            case COOKED_COD:
            case COOKED_MUTTON:
            case COOKED_PORKCHOP:
            case COOKED_RABBIT:
            case COOKED_SALMON:
            case PUMPKIN_PIE:
            case BAKED_POTATO:
                lastFoodConsumptionTime.put(player.getUniqueId(), System.currentTimeMillis());
                PlayerTemperature playerTemperature = Seasons.getInstance().getPlayerManager().getPlayerTemperature(player.getUniqueId());
                if (playerTemperature != null) {
                    double newTemperature = modifyTemperature(player, playerTemperature.getTemperature());
                    playerTemperature.setTemperature(newTemperature);
                }
                break;
            default:
                break;
        }
    }
}
