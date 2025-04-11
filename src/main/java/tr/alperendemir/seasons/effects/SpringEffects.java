package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.season.SeasonManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SpringEffects implements Listener {

    private final Seasons plugin;
    private final Set<Material> flowerTypes = new HashSet<>();
    private final Set<EntityType> springAnimals = new HashSet<>(Arrays.asList(
            EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.RABBIT, EntityType.CHICKEN
    ));

    private final Random random = new Random();

    public SpringEffects(Seasons plugin) {
        this.plugin = plugin;
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SPRING) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        initializeFlowerTypes();
    }


    private void initializeFlowerTypes() {
        flowerTypes.addAll(Arrays.asList(
                Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM,
                Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP,
                Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY,
                Material.PINK_PETALS
        ));
    }

    private double getRandomOffset() {
        return random.nextDouble() * 6 - 3; // Returns a value between -3 and +3
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SPRING) {
            Entity entity = event.getEntity();
            // Check if the entity has metadata indicating it was custom spawned
            if (entity.hasMetadata("springSpawned")) {
                return; // Ignore entities spawned by this plugin
            }

            if (springAnimals.contains(entity.getType())) {
                // Check if the entity was already spawned by this logic
                if (!entity.hasMetadata("springSpawned")) {
                    // Mark the original entity immediately to prevent re-triggering
                    entity.setMetadata("springSpawned", new FixedMetadataValue(plugin, true));

                    // 30% chance to spawn one additional entity
                    if (random.nextInt(100) < 30) {
                        Location loc = entity.getLocation();
                        World world = entity.getWorld();

                        // Spawn one additional entity
                        LivingEntity newEntity = (LivingEntity) world.spawnEntity(
                                loc.clone().add(getRandomOffset(), 0, getRandomOffset()), entity.getType()
                        );

                        // Add metadata to the newly spawned entity
                        newEntity.setMetadata("springSpawned", new FixedMetadataValue(plugin, true));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SPRING) {
            Chunk chunk = event.getChunk();
            
            try {
                // Check if winter water freezing was enabled
                String winterFreezingOption = plugin.getConfigManager().getWinterWaterFreezingOption();
                if (!winterFreezingOption.equalsIgnoreCase("disabled")) {
                    // If winter freezing was enabled, we should melt ice in spring
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (chunk.isLoaded()) {
                            meltIce(chunk, winterFreezingOption);
                        }
                    }, 1L);
                }
                
                // Add flowers only to new chunks
                if (event.isNewChunk()) {
                    sprinkleFlowers(chunk);
                }
            } catch (Exception e) {
                // No warning printed
            }
        }
    }

    private void meltIce(Chunk chunk, String previousWinterFreezingOption) {
        if (chunk == null || !chunk.isLoaded()) {
            return;
        }
        
        try {
            World world = chunk.getWorld();
            if (world == null) {
                return;
            }
            
            int maxHeight = Math.min(world.getMaxHeight(), 255);
            // Use same step size as was used in winter for consistency
            int step = previousWinterFreezingOption.equalsIgnoreCase("full") ? 1 : 2;
            
            for (int x = 0; x < 16; x += step) {
                for (int z = 0; z < 16; z += step) {
                    for (int y = 0; y < maxHeight; y += step) {
                        Block block = chunk.getBlock(x, y, z);
                        
                        // Skip if block is null
                        if (block == null) {
                            continue;
                        }
                        
                        // If it's ice, convert back to water
                        if (block.getType() == Material.ICE) {
                            // Check if it has water below or adjacent to determine if it should be water
                            if (hasAdjacentWater(block)) {
                                block.setType(Material.WATER, false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // No warning printed
        }
    }
    
    private boolean hasAdjacentWater(Block block) {
        // Check below first (most likely place for water)
        Block below = block.getRelative(0, -1, 0);
        if (below != null && (below.getType() == Material.WATER || below.getType() == Material.ICE)) {
            return true;
        }
        
        // Check adjacent blocks
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // Skip the center block
                
                Block adjacent = block.getRelative(dx, 0, dz);
                if (adjacent != null && (adjacent.getType() == Material.WATER || adjacent.getType() == Material.ICE)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private void sprinkleFlowers(Chunk chunk) {
        Random random = new Random();
        for (int i = 0; i < 32; i++) { // Reduced iteration count
            int x = random.nextInt(16);
            int z = random.nextInt(16);

            // Find the first non-air block from the top
            for (int y = chunk.getWorld().getMaxHeight() - 1; y >= 0; y--) {
                Block block = chunk.getBlock(x, y, z);
                if (block.getType() != Material.AIR) {
                    if (canPlaceFlower(block) && random.nextInt(100) < 25) { // Probability check
                        Material flowerType = flowerTypes.toArray(new Material[0])[random.nextInt(flowerTypes.size())];
                        block.getRelative(0, 1, 0).setType(flowerType, false);
                    }
                    break; // Move to the next x,z once a suitable block is found
                }
            }
        }
    }

    private boolean canPlaceFlower(Block block) {
        return block.getType() == Material.GRASS_BLOCK;
    }
}