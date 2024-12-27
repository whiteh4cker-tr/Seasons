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
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.WINTER) {
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
            if (event.isNewChunk()) {
                sprinkleFlowers(chunk);
            }
        }
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