package tr.alperendemir.seasons.player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tr.alperendemir.seasons.Seasons;

public class PlayerTemperature {

    private final Player player;
    private double temperature;

    public PlayerTemperature(Player player, double temperature) {
        this.player = player;
        this.temperature = temperature;
    }

    public Player getPlayer() {
        return player;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void updateActionBar() {
        String unit = Seasons.getInstance().getConfigManager().isTemperatureInFahrenheit() ? "°F" : "°C";
        String formattedTemperature = String.format("%.1f%s", temperature, unit);
        String message = getColoredTemperature(temperature, formattedTemperature);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private String getColoredTemperature(double temperature, String formattedTemperature) {
        ChatColor color;
        if (temperature < 5) {
            color = ChatColor.DARK_BLUE;
        } else if (temperature >= 5 && temperature <= 40) {
            color = ChatColor.GREEN;
        } else {
            color = ChatColor.RED;
        }
        return color + "[  " + formattedTemperature + "  ]" + ChatColor.RESET;
    }
}