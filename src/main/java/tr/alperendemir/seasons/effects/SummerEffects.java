package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.season.SeasonManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SummerEffects implements Listener {

    private final Seasons plugin;
    private final Set<EntityType> jungleAnimals = new HashSet<>(Arrays.asList(
            EntityType.PARROT, EntityType.OCELOT, EntityType.PANDA
    ));

    public SummerEffects(Seasons plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("SummerEffects constructor called.");
        startSummerEffects();
    }

    public void startSummerEffects() {
        // Now it's safe to check the season and register events.
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("SummerEffects events registered.");
            // Start other tasks here if necessary
            Bukkit.getScheduler().runTaskTimer(plugin, this::spawnFallingLeaves, 0L, 20L); // Every second
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            World world = event.getWorld();
            // Placeholder for setting water and sky color to light blue using ProtocolLib
            updateWorldForSummer(world);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Entity entity = event.getEntity();
            if (entity.getType() == EntityType.ZOMBIE) {
                event.setCancelled(true); // Cancel zombie spawn
                entity.getWorld().spawnEntity(event.getLocation(), EntityType.HUSK);
            } else if (jungleAnimals.contains(entity.getType())) {
                // Increase spawn chance for jungle animals
                if (new Random().nextInt(100) < 30) { // 30% chance to boost spawn
                    event.setCancelled(false); // Ensure they can spawn
                }
            }
        }
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Block block = event.getBlock();
            if (isCrop(block.getType()) && isExposedToSky(block)) {
                // Apply a growth boost
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (block.getState() instanceof org.bukkit.block.data.Ageable) {
                        org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) block.getState().getBlockData();
                        if (ageable.getAge() < ageable.getMaximumAge()) {
                            ageable.setAge(ageable.getAge() + 1);
                            block.setBlockData(ageable);
                        }
                    }
                }, 1L); // 1 tick delay
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER && event.toWeatherState()) {
            // Cancel rain events
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Chunk chunk = event.getChunk();
            removeFlowers(chunk);
            // Spawn berry bushes (consider performance impact)
            spawnBerryBushes(chunk);
        }
    }


    private void spawnFallingLeaves() {
        for (World world : Bukkit.getWorlds()) {
            if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
                for (Player player : world.getPlayers()) {
                    spawnLeafParticles(player);
                }
            }
        }
    }

    private void spawnLeafParticles(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();
        Random random = new Random();

        // Random offset within 10 blocks
        double x = loc.getX() + random.nextDouble() * 20 - 10;
        double y = loc.getY() + 10 + random.nextDouble() * 5; // 10-15 blocks above
        double z = loc.getZ() + random.nextDouble() * 20 - 10;

        Location particleLoc = new Location(world, x, y, z);

        // Spawn falling leaf particle
        world.spawnParticle(Particle.FALLING_WATER, particleLoc, 1, 0, 0, 0, 0);
    }

    private void removeFlowers(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getWorld().getMaxHeight() - 1; y >= 0; y--) {
                    Block block = chunk.getBlock(x, y, z);
                    if (isFlower(block.getType())) {
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
    }

    private boolean isFlower(Material material) {
        return material == Material.DANDELION || material == Material.POPPY ||
                material == Material.BLUE_ORCHID || material == Material.ALLIUM ||
                material == Material.AZURE_BLUET || material == Material.RED_TULIP ||
                material == Material.ORANGE_TULIP || material == Material.WHITE_TULIP ||
                material == Material.PINK_TULIP || material == Material.OXEYE_DAISY ||
                material == Material.CORNFLOWER || material == Material.LILY_OF_THE_VALLEY ||
                material == Material.PINK_PETALS;
    }

    private void spawnBerryBushes(Chunk chunk) {
        Random random = new Random();
        for (int i = 0; i < 16; i++) { // Reduced density
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
            Block block = chunk.getBlock(x, y, z);

            if (block.getType() == Material.GRASS_BLOCK) {
                block.getRelative(0, 1, 0).setType(Material.SWEET_BERRY_BUSH, false);
            }
        }
    }

    private boolean isExposedToSky(Block block) {
        return block.getLightFromSky() > 0;
    }

    private boolean isCrop(Material material) {
        Set<Material> cropTypes = new HashSet<>(Arrays.asList(
                Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
                Material.MELON_STEM, Material.PUMPKIN_STEM
        ));
        return cropTypes.contains(material);
    }

    private void updateWorldForSummer(World world) {
        // Placeholder: Update world for summer effects
        // This will likely involve ProtocolLib for visual changes
    }
}