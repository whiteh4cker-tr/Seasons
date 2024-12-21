package tr.alperendemir.seasons.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.data.DataManager;
import tr.alperendemir.seasons.effects.TemperatureEffects;
import tr.alperendemir.seasons.temperature.TemperatureCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener {

    private final Map<UUID, PlayerTemperature> players;
    private final TemperatureCalculator temperatureCalculator;
    private final TemperatureEffects temperatureEffects;

    public PlayerManager() {
        players = new HashMap<>();
        temperatureCalculator = new TemperatureCalculator();
        temperatureEffects = new TemperatureEffects();

        // Schedule temperature updates
        Bukkit.getScheduler().runTaskTimer(Seasons.getInstance(), this::updateTemperatures, 0L,
                20L * Seasons.getInstance().getConfigManager().getTemperatureUpdateInterval());
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, Seasons.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        unloadPlayerData(player.getUniqueId());
    }

    private void updateTemperatures() {
        for (PlayerTemperature playerTemperature : players.values()) {
            double newTemperature = temperatureCalculator.calculateTemperature(playerTemperature.getPlayer());
            playerTemperature.setTemperature(newTemperature);
            temperatureEffects.applyEffects(playerTemperature.getPlayer(), newTemperature);
            playerTemperature.updateActionBar();
        }
    }

    public PlayerTemperature getPlayerTemperature(UUID playerId) {
        return players.get(playerId);
    }

    public void loadPlayerData(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;

        DataManager dataManager = Seasons.getInstance().getDataManager();
        PlayerTemperature playerTemperature = dataManager.loadPlayerData(playerId);
        players.put(playerId, playerTemperature);
    }

    public void unloadPlayerData(UUID playerId) {
        PlayerTemperature playerTemperature = players.remove(playerId);
        if (playerTemperature != null) {
            Seasons.getInstance().getDataManager().savePlayerData(playerId, playerTemperature);
        }
    }
}