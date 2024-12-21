package tr.alperendemir.seasons.temperature.modifier;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorTemperatureModifier implements TemperatureModifier {
    @Override
    public double modifyTemperature(Player player, double currentTemperature) {
        double armorModifier = 0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null) {
                switch (armor.getType()) {
                    case LEATHER_HELMET:
                    case LEATHER_CHESTPLATE:
                    case LEATHER_LEGGINGS:
                    case LEATHER_BOOTS:
                        armorModifier += 2; // Updated to +2°C per piece
                        break;
                    case IRON_HELMET:
                    case IRON_CHESTPLATE:
                    case IRON_LEGGINGS:
                    case IRON_BOOTS:
                    case GOLDEN_HELMET:
                    case GOLDEN_CHESTPLATE:
                    case GOLDEN_LEGGINGS:
                    case GOLDEN_BOOTS:
                    case DIAMOND_HELMET:
                    case DIAMOND_CHESTPLATE:
                    case DIAMOND_LEGGINGS:
                    case DIAMOND_BOOTS:
                        armorModifier += 1.25; // +1.25°C per piece
                        break;
                    case NETHERITE_HELMET:
                    case NETHERITE_CHESTPLATE:
                    case NETHERITE_LEGGINGS:
                    case NETHERITE_BOOTS:
                        armorModifier += 0.75; // +0.75°C per piece
                        break;
                    default:
                        break;
                }
            }
        }
        return currentTemperature + armorModifier;
    }
}
