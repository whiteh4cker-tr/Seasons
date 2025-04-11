package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.season.SeasonManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AutumnEffects implements Listener {

    private final Seasons plugin;
    private final Set<EntityType> autumnAnimals = new HashSet<>(Arrays.asList(
            EntityType.MOOSHROOM, EntityType.FROG, EntityType.FOX
    ));

    private final Random random = new Random();

    public AutumnEffects(Seasons plugin) {
        this.plugin = plugin;
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.AUTUMN) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            startAutumnEffects();
        }
    }

    public void startAutumnEffects() {
        // No effects for now
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.AUTUMN) {
            LivingEntity entity = event.getEntity();

            if (autumnAnimals.contains(entity.getType())) {
                // Check if the entity was already spawned by this logic
                if (!entity.hasMetadata("autumnSpawned")) {
                    // Mark the original entity immediately to prevent re-triggering
                    entity.setMetadata("autumnSpawned", new FixedMetadataValue(plugin, true));

                    // 30% chance to spawn one additional entity
                    if (random.nextInt(100) < 30) {
                        Location loc = entity.getLocation();
                        World world = entity.getWorld();

                        // Spawn one additional entity
                        LivingEntity newEntity = (LivingEntity) world.spawnEntity(
                                loc.clone().add(getRandomOffset(), 0, getRandomOffset()), entity.getType()
                        );

                        // Add metadata to the newly spawned entity
                        newEntity.setMetadata("autumnSpawned", new FixedMetadataValue(plugin, true));
                    }
                }
            }

            // Add pumpkin to mobs' heads
            if (new Random().nextInt(100) < 20) { // 20% chance
                if (entity.getEquipment() != null) {
                    ItemStack pumpkin = new ItemStack(Material.CARVED_PUMPKIN);
                    entity.getEquipment().setHelmet(pumpkin);
                }
            }
        }
    }

    // Helper method to get a random offset for spawning
    private double getRandomOffset() {
        Random random = new Random();
        return random.nextDouble() * 6 - 3; // Returns a value between -3 and +3
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.AUTUMN) {
            // Don't process the chunk immediately - schedule it for later to avoid blocking the server thread
            final Chunk chunk = event.getChunk();
            
            // Schedule the task to run later, giving the server time to finish its own chunk processing
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    if (chunk != null && chunk.isLoaded()) {
                        // Spawn big patches of mushrooms (consider performance)
                        spawnMushroomPatches(chunk);
                        // Remove the sweet berry bushes - use a separate delayed task for this
                        scheduleBerryBushRemoval(chunk);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Error in autumn chunk processing: " + e.getMessage());
                }
            }, 2L); // Add a short delay to ensure server's chunk processing is complete
        }
    }
    
    private void scheduleBerryBushRemoval(Chunk chunk) {
        // Process berry bush removal in small batches to prevent server lag
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                if (chunk != null && chunk.isLoaded()) {
                    removeSweetBerryBushes(chunk);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error removing berry bushes: " + e.getMessage());
            }
        }, 1L);
    }

    private void spawnMushroomPatches(Chunk chunk) {
        if (chunk == null || !chunk.isLoaded()) {
            return;
        }
        
        Random random = new Random();

        // Number of mushroom patches - reduced to 1 for performance
        for (int i = 0; i < 1; i++) {
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            
            World world = chunk.getWorld();
            if (world == null) {
                return;
            }
            
            int y = world.getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);

            // Ensure y is within the valid range (0 to 254)
            if (y < 0 || y > 254) {
                continue; // Skip this iteration if y is invalid
            }

            Block block = chunk.getBlock(x, y, z);
            if (block == null) {
                continue;
            }

            if (block.getType() == Material.GRASS_BLOCK) {
                Material mushroomType = random.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM;

                // Create a sparse patch of mushrooms
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        // 30% chance to place a mushroom on each block in the patch
                        if (random.nextInt(100) < 30) {
                            try {
                                Block relative = block.getRelative(dx, 0, dz);
                                if (relative != null) {
                                    Block above = relative.getRelative(0, 1, 0);
                                    if (above != null && above.getType() == Material.AIR) {
                                        above.setType(mushroomType, false);
                                    }
                                }
                            } catch (Exception e) {
                                // Skip if there's an error with this block
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeSweetBerryBushes(Chunk chunk) {
        if (chunk == null || !chunk.isLoaded()) {
            return;
        }
        
        try {
            World world = chunk.getWorld();
            if (world == null) {
                return;
            }
            
            // Process in smaller batches to avoid overwhelming the server
            // Process only a fraction of the blocks per tick, focusing on where berry bushes typically grow
            int maxY = Math.min(world.getMaxHeight(), 255);
            
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    // Focus on the top 64 blocks where berry bushes typically spawn
                    int startY = Math.max(0, world.getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z) - 16);
                    int endY = Math.min(startY + 64, maxY);
                    
                    for (int y = startY; y < endY; y++) {
                        try {
                            Block block = chunk.getBlock(x, y, z);
                            if (block != null && block.getType() == Material.SWEET_BERRY_BUSH) {
                                block.setType(Material.AIR, false);
                            }
                        } catch (Exception e) {
                            // Skip this block if there's an error
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error in berry bush removal: " + e.getMessage());
        }
    }

}