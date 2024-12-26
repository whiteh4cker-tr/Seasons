package tr.alperendemir.seasons.player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tr.alperendemir.seasons.Seasons;

public class PlayerTemperature {

    private final Player player;
    private double temperature; // Temperature in Celsius

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
        double displayTemperature = temperature; // Default to Celsius
        if (unit.equals("°F")) {
            // Convert Celsius to Fahrenheit
            displayTemperature = celsiusToFahrenheit(temperature);
        }
        String formattedTemperature = String.format("%.1f%s", displayTemperature, unit);
        String message = getColoredTemperature(displayTemperature, formattedTemperature, unit);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private String getColoredTemperature(double temperature, String formattedTemperature, String unit) {
        ChatColor color;
        if (unit.equals("°F")) {
            // Fahrenheit ranges
            if (temperature < 41) { // 5°C = 41°F
                color = ChatColor.DARK_BLUE;
            } else if (temperature >= 41 && temperature <= 104) { // 5°C = 41°F, 40°C = 104°F
                color = ChatColor.GREEN;
            } else {
                color = ChatColor.RED;
            }
        } else {
            // Celsius ranges
            if (temperature < 5) {
                color = ChatColor.DARK_BLUE;
            } else if (temperature >= 5 && temperature <= 40) {
                color = ChatColor.GREEN;
            } else {
                color = ChatColor.RED;
            }
        }
        return color + "[  " + formattedTemperature + "  ]" + ChatColor.RESET;
    }

    private double celsiusToFahrenheit(double celsius) {
        // Conversion formula: F = (C * 9/5) + 32
        return (celsius * 9 / 5) + 32;
    }
}