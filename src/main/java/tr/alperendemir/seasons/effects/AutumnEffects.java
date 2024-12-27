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
            Chunk chunk = event.getChunk();
            // Spawn big patches of mushrooms (consider performance)
            spawnMushroomPatches(chunk);
            // Remove the sweet berry bushes
            removeSweetBerryBushes(chunk);
        }
    }

    private void spawnMushroomPatches(Chunk chunk) {
        Random random = new Random();

        // Number of mushroom patches
        for (int i = 0; i < 2; i++) {
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
            Block block = chunk.getBlock(x, y, z);

            if (block.getType() == Material.GRASS_BLOCK) {
                Material mushroomType = random.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM;

                // Create a sparse patch of mushrooms
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        // 50% chance to place a mushroom on each block in the patch
                        if (random.nextInt(100) < 30) {
                            Block relative = block.getRelative(dx, 0, dz);
                            if (relative.getRelative(0, 1, 0).getType() == Material.AIR) {
                                relative.getRelative(0, 1, 0).setType(mushroomType, false);
                            }
                        }
                    }
                }
            }
        }
    }


    private void removeSweetBerryBushes(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.getType() == Material.SWEET_BERRY_BUSH) {
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
    }

}