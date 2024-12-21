package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
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
            startSpringEffects();
        }
        initializeFlowerTypes();
    }

    public void startSpringEffects() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::spawnBabyAnimals, 0L, 6000L); // 6000 ticks = 5 minutes
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        // Sky color change is not possible without ProtocolLib
    }

    private void initializeFlowerTypes() {
        flowerTypes.addAll(Arrays.asList(
                Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM,
                Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP,
                Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY,
                Material.PINK_PETALS
        ));
    }

    private void spawnBabyAnimals() {
        for (World world : Bukkit.getWorlds()) {
            if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SPRING) {
                for (LivingEntity entity : world.getLivingEntities()) {
                    if (babyAnimals.contains(entity.getType())) {
                        spawnBabies(entity);
                    }
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
            LivingEntity baby = (LivingEntity) world.spawnEntity(loc, entity.getType());
            if (baby instanceof Ageable) {
                ((Ageable) baby).setBaby();
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.SPRING) {
            Chunk chunk = event.getChunk();

            if (event.isNewChunk()) {
                sprinkleFlowers(chunk);
            }

            removeSnowAndIce(chunk);
        }
    }

    private void sprinkleFlowers(Chunk chunk) {
        Random random = new Random();
        for (int i = 0; i < 64; i++) { // Increased density of flowers
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
            Block block = chunk.getBlock(x, y, z);

            if (canPlaceFlower(block)) {
                Material flowerType = flowerTypes.toArray(new Material[0])[random.nextInt(flowerTypes.size())];
                block.getRelative(0, 1, 0).setType(flowerType, false);
            }
        }
    }

    private void removeSnowAndIce(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getWorld().getMaxHeight() - 1; y >= 0; y--) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.getType() == Material.SNOW || block.getType() == Material.ICE || block.getType() == Material.FROSTED_ICE || block.getType() == Material.SNOW_BLOCK) {
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
    }

    private boolean canPlaceFlower(Block block) {
        return block.getType() == Material.GRASS_BLOCK;
    }
}