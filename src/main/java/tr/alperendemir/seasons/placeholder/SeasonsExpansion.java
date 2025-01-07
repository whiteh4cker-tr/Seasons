package tr.alperendemir.seasons.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import tr.alperendemir.seasons.Seasons;

public class SeasonsExpansion extends PlaceholderExpansion {
    private final Seasons plugin;

    public SeasonsExpansion(Seasons plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "seasons";
    }

    @Override
    public @NotNull String getAuthor() {
        return "alperendemir";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("current")) {
            return plugin.getSeasonManager().getCurrentSeason().name();
        }
        
        return null;
    }
}
