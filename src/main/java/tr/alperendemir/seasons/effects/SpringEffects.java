package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import tr.alperendemir.seasons.Seasons;
import tr.alperendemir.seasons.season.SeasonManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SpringEffects implements Listener {

    private final Seasons plugin;
    private final Set<Material> flowerTypes = new HashSet<>();
    private final Set<EntityType> babyAnimals = new HashSet<>(Arrays.asList(
            EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.RABBIT, EntityType.CHICKEN
    ));

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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SPRING) {
            Entity entity = event.getEntity();
            if (babyAnimals.contains(entity.getType())) {
                // 30% chance to spawn extra babies
                if (new Random().nextInt(100) < 30) {
                    // Use a BukkitRunnable to spawn additional babies after the event
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            spawnBabies((LivingEntity) entity);
                        }
                    }.runTaskLater(plugin, 1L); // Delay by 1 tick
                }
            }
        }
    }

    private void spawnBabies(LivingEntity entity) {
        Location loc = entity.getLocation();
        World world = entity.getWorld();
        Random random = new Random();

        int numberOfBabies = random.nextInt(3) + 3; // 3 to 5 babies
        for (int i = 0; i < numberOfBabies; i++) {
            try {
                LivingEntity baby = (LivingEntity) world.spawnEntity(loc, entity.getType());
                if (baby instanceof Ageable) {
                    ((Ageable) baby).setBaby();
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to spawn baby entity: " + e.getMessage());
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