package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
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

    private final Random random = new Random();

    public SummerEffects(Seasons plugin) {
        this.plugin = plugin;
        startSummerEffects();
    }

    public void startSummerEffects() {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    private double getRandomOffset() {
        return -2 + (4 * random.nextDouble()); // Random offset between -2 and 2
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Entity entity = event.getEntity();
            if (jungleAnimals.contains(entity.getType())) {
                // Check if the entity was already spawned by this logic
                if (!entity.hasMetadata("summerSpawned")) {
                    // Mark the original entity immediately to prevent re-triggering
                    entity.setMetadata("summerSpawned", new FixedMetadataValue(plugin, true));

                    // 30% chance to spawn one additional entity
                    if (random.nextInt(100) < 30) {
                        Location loc = entity.getLocation();
                        World world = entity.getWorld();

                        // Spawn one additional entity
                        LivingEntity newEntity = (LivingEntity) world.spawnEntity(
                                loc.clone().add(getRandomOffset(), 0, getRandomOffset()), entity.getType()
                        );

                        // Add metadata to the newly spawned entity
                        newEntity.setMetadata("summerSpawned", new FixedMetadataValue(plugin, true));
                    }
                }
            }
            if (entity.getType() == EntityType.ZOMBIE) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(plugin, () -> entity.getWorld().spawnEntity(event.getLocation(), EntityType.HUSK));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockGrow(BlockGrowEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Block block = event.getBlock();
            if (isCrop(block.getType()) && isExposedToSky(block)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (block.getState() instanceof org.bukkit.block.data.Ageable) {
                        org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) block.getState().getBlockData();
                        if (ageable.getAge() < ageable.getMaximumAge()) {
                            ageable.setAge(ageable.getAge() + 1);
                            block.setBlockData(ageable);
                        }
                    }
                }, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER && event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SUMMER) {
            Chunk chunk = event.getChunk();
            removeFlowers(chunk);
            spawnBerryBushes(chunk);
        }
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
        for (int i = 0; i < 8; i++) { // Reduced from 16 to 8, adjust as needed
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);

            // Check if y is valid
            if (y < 0 || y > 254) {
                continue; // Skip this iteration if y is invalid
            }

            Block block = chunk.getBlock(x, y, z);

            // Add a probability check here (e.g., 25% chance)
            if (block.getType() == Material.GRASS_BLOCK && random.nextInt(100) < 25) {
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
}