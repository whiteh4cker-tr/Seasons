package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.season.SeasonManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WinterEffects implements Listener {

    private final Seasons plugin;
    private final Set<EntityType> winterEntities = new HashSet<>(Arrays.asList(
            EntityType.WOLF, EntityType.FOX, EntityType.POLAR_BEAR
    ));

    private final Random random = new Random();

    public WinterEffects(Seasons plugin) {
        this.plugin = plugin;
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.WINTER) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            startWinterEffects();
        }
    }

    public void startWinterEffects() {
        // Schedule the task to check for aurora borealis
        Bukkit.getScheduler().runTaskTimer(plugin, this::displayAuroraBorealis, 0L, 1200L); // Check every minute
    }

    private void displayAuroraBorealis() {
        for (World world : Bukkit.getWorlds()) {
            if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.WINTER) {
                if (isNight(world) && new Random().nextInt(100) < 20) { // 20% chance each night
                    for (Player player : world.getPlayers()) {
                        spawnAuroraParticles(player);
                    }
                }
            }
        }
    }

    private void spawnAuroraParticles(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();
        Random random = new Random();

        for (int i = 0; i < 50; i++) { // Number of particles
            double x = loc.getX() + random.nextDouble() * 40 - 20; // Within 20 blocks
            double y = loc.getY() + 20 + random.nextDouble() * 10; // 20-30 blocks above
            double z = loc.getZ() + random.nextDouble() * 40 - 20; // Within 20 blocks

            Location particleLoc = new Location(world, x, y, z);
            world.spawnParticle(Particle.FIREWORK, particleLoc, 0, 0, 0, 0, 0);
        }
    }

    private boolean isNight(World world) {
        long time = world.getTime();
        return time > 13000 && time < 23000;
    }

    private double getRandomOffset() {
        return random.nextDouble() * 6 - 3; // Returns a value between -3 and +3
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.WINTER) {
            Entity entity = event.getEntity();
            // Check if the entity has metadata indicating it was custom spawned
            if (entity.hasMetadata("winterSpawned")) {
                return; // Ignore entities spawned by this plugin
            }

            if (winterEntities.contains(entity.getType())) {
                // Check if the entity was already spawned by this logic
                if (!entity.hasMetadata("winterSpawned")) {
                    // Mark the original entity immediately to prevent re-triggering
                    entity.setMetadata("winterSpawned", new FixedMetadataValue(plugin, true));

                    // 50% chance to spawn one additional entity
                    if (random.nextInt(100) < 50) {
                        Location loc = entity.getLocation();
                        World world = entity.getWorld();

                        // Spawn one additional entity
                        LivingEntity newEntity = (LivingEntity) world.spawnEntity(
                                loc.clone().add(getRandomOffset(), 0, getRandomOffset()), entity.getType()
                        );

                        // Add metadata to the newly spawned entity
                        newEntity.setMetadata("winterSpawned", new FixedMetadataValue(plugin, true));
                    }
                }
            }

            if (entity.getType() == EntityType.SKELETON) {
                event.setCancelled(true); // Cancel skeleton spawn

                // Spawn a stray instead (no need to store the Stray in a variable)
                entity.getWorld().spawnEntity(event.getLocation(), EntityType.STRAY);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockForm(BlockFormEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.WINTER) {
            Block block = event.getBlock();
            if (block.getType() == Material.WATER) {
                if (isExposedToSky(block)) {
                    event.setCancelled(true);
                    block.setType(Material.ICE, false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockGrow(BlockGrowEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.WINTER) {
            Block block = event.getBlock();
            // Check if the block is a crop/plant and does not have a block above it
            if (isCrop(block.getType()) && isExposedToSky(block)) {
                event.setCancelled(true); // Prevent growth
            }
        }
    }

    private boolean isExposedToSky(Block block) {
        return block.getLightFromSky() > 0;
    }

    private boolean isCrop(Material material) {
        // Add more crop types if necessary
        Set<Material> cropTypes = new HashSet<>(Arrays.asList(
                Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
                Material.MELON_STEM, Material.PUMPKIN_STEM
        ));
        return cropTypes.contains(material);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.WINTER) {
            try {
                Chunk chunk = event.getChunk();
                if (chunk != null && chunk.isLoaded()) {
                    // Use scheduled task to avoid blocking the main thread
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (chunk.isLoaded()) {
                            applyWinterEffectsToChunk(chunk);
                        }
                    }, 1L); // Slight delay to ensure chunk is fully processed
                }
            } catch (Exception e) {
                // No warning printed
            }
        }
    }

    private void applyWinterEffectsToChunk(Chunk chunk) {
        if (chunk == null || !chunk.isLoaded()) {
            return;
        }
        
        try {
            World world = chunk.getWorld();
            if (world == null) {
                return;
            }
            
            // Get the water freezing option from config
            String freezeOption = plugin.getConfigManager().getWinterWaterFreezingOption();
            
            // If freezing is disabled, don't process ice
            if (freezeOption.equalsIgnoreCase("disabled")) {
                return;
            }
            
            int maxHeight = Math.min(world.getMaxHeight(), 255);
            int step = freezeOption.equalsIgnoreCase("full") ? 1 : 2; // Full = check every block, partial = every other block
            
            // Process blocks based on config setting
            for (int x = 0; x < 16; x += step) {
                for (int z = 0; z < 16; z += step) {
                    for (int y = 0; y < maxHeight; y += step) {
                        Block block = chunk.getBlock(x, y, z);
                        
                        // Skip if block is null
                        if (block == null) {
                            continue;
                        }
                        
                        // Skip if the block is a container (e.g., chest)
                        if (isContainer(block.getType())) {
                            continue;
                        }
                        
                        // Surface water to ice conversion logic
                        if (block.getType() == Material.WATER) {
                            // In full mode, convert all water to ice regardless of sky exposure
                            // In partial mode, only convert exposed water
                            if (freezeOption.equalsIgnoreCase("full") || isExposedToSky(block)) {
                                block.setType(Material.ICE, false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // No warning printed
        }
    }
    
    private boolean isContainer(Material material) {
        return material == Material.CHEST 
            || material == Material.TRAPPED_CHEST 
            || material == Material.BARREL
            || material == Material.FURNACE
            || material == Material.BREWING_STAND;
    }

}