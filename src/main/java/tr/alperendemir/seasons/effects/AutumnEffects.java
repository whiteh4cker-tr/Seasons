package tr.alperendemir.seasons.effects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
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

    public AutumnEffects(Seasons plugin) {
        this.plugin = plugin;
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.AUTUMN) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            startAutumnEffects();
        }
    }

    public void startAutumnEffects() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::spawnFallingLeaves, 0L, 20L); // Every second
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.AUTUMN) {
            LivingEntity entity = event.getEntity();

            // Increase spawn rate for Mooshrooms, Frogs, and Foxes
            if (autumnAnimals.contains(entity.getType())) {
                if (new Random().nextInt(100) < 30) { // 30% chance to boost spawn
                    // Spawn an extra entity of the same type nearby
                    Location loc = entity.getLocation();
                    World world = entity.getWorld();
                    for (int i = 0; i < 2; i++) { // Spawn 2 extra, adjust as needed
                        world.spawnEntity(loc.clone().add(getRandomOffset(), 0, getRandomOffset()), entity.getType());
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

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.AUTUMN) {
            Chunk chunk = event.getChunk();
            // Spawn big patches of mushrooms (consider performance)
            spawnMushroomPatches(chunk);
            // Remove the sweet berry bushes
            removeSweetBerryBushes(chunk);
        }
    }

    private void spawnFallingLeaves() {
        for (World world : Bukkit.getWorlds()) {
            if (plugin.getSeasonManager().getCurrentSeason() == SeasonManager.Season.AUTUMN) {
                for (Player player : world.getPlayers()) {
                    if (isLocationSuitableForFallingLeaves(player.getLocation())) {
                        spawnLeafParticles(player);
                    }
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

        // Spawn falling leaf particle (using DUST_COLOR_TRANSITION with two colors for transition)
        Color fromColor = getRandomLeafColor();
        Color toColor = Color.WHITE; // Or another color for transition effect
        world.spawnParticle(Particle.DUST, particleLoc, 1, new Particle.DustOptions(fromColor, 1));
    }

    private Color getRandomLeafColor() {
        Random random = new Random();
        int choice = random.nextInt(4); // 0, 1, 2, 3

        switch (choice) {
            case 0:
                return Color.ORANGE;
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.fromRGB(139, 69, 19); // Brown
            default:
                return Color.GREEN;
        }
    }

    private boolean isLocationSuitableForFallingLeaves(Location loc) {
        // Check if the player is in an area where leaves should fall
        // Example: Check for nearby leaf blocks
        for (int x = -5; x <= 5; x++) {
            for (int y = 0; y <= 10; y++) {
                for (int z = -5; z <= 5; z++) {
                    Block block = loc.getBlock().getRelative(x, y, z);
                    if (isLeaf(block.getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void spawnMushroomPatches(Chunk chunk) {
        Random random = new Random();
        for (int i = 0; i < 5; i++) { // Reduced density of mushroom patches
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
            Block block = chunk.getBlock(x, y, z);

            if (block.getType() == Material.GRASS_BLOCK) {
                Material mushroomType = random.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM;
                // Create a small patch of mushrooms
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        Block relative = block.getRelative(dx, 0, dz);
                        if (relative.getRelative(0, 1, 0).getType() == Material.AIR) {
                            relative.getRelative(0, 1, 0).setType(mushroomType, false);
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

    private boolean isLeaf(Material material) {
        return material.toString().contains("LEAVES");
    }

}