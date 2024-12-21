# Seasons
 Seasons plugin - Add temperature and seasons to your world.

## Features

-   Temperature
-   Freezing (to death)

-   Burning (to death)
-   Hunger

-   Slowness
-   Visible cold breath

-   Custom season events

![alt text](https://i.imgur.com/vqd7ImQ.png)

## Summer

-   No rain
-   Berry bushes everywhere

-   Faster crop growth
-   Husks will spawn instead of zombies

-   Increases the spawn rate of jungle animals (Parrots, Ocelots, and Pandas) by 30%.
-   Removes flowers that were spawned during the Spring season when a chunk loads.

## Spring

-   Increases the spawn rate of baby animals
-   Specifically targets: Sheep, Cows, Pigs, Rabbits, and Chickens.

-   Adds a variety of flowers to newly generated chunks
-   Removes any existing snow (layers and blocks), ice, and frosted ice blocks when a chunk loads

-   Spawns baby animals every 5 minutes

## Winter

-   Skeletons that spawn naturally during Winter will be replaced with Strays.
-   Increases the spawn rate of Wolves, Foxes, and Polar Bears by 50%.

-   Crops and plants will not grow if they are exposed to the sky (no solid block above them).
-   Has a 20% chance to display an aurora borealis effect in the sky each night.

-   The effect consists of FIREWORK_SPARK particles spawning high in the sky around players.
-   Snow layers in loaded chunks are removed when the season changes from Winter to another season (with a 1-minute delay).

## Autumn

-   Increases the spawn rate of Mushrooms, Frogs, and Foxes by 30%.
-   Gives mobs a 20% chance to spawn with a Carved Pumpkin on their head.

-   Removes any Sweet Berry Bushes that might have spawned in previous seasons.

## Temperature modifiers

-   Armor
-   Biome

-   Block light
-   Food

-   Height
-   Season

-   Sprinting
-   Time

-   Water
-   Weather

## config.yml
```
# Seasons Plugin - Configuration File

# Temperature Unit
# Set to 'true' to use Fahrenheit, 'false' to use Celsius.
temperature-in-fahrenheit: false

# Temperature Effect Thresholds
# Players will start experiencing negative effects when their temperature goes below the cold threshold or above the heat threshold.
cold-effect-threshold: -10.0
heat-effect-threshold: 50.0

# Visual Effects
# Enable or disable visual effects like sweating or visible breath.
visual-effects:
  enabled: true

# Temperature Update Interval
# How often (in seconds) the plugin should recalculate player temperatures.
temperature-update-interval: 2

# Season Settings
# Configure the duration of each season in Minecraft days.
# Note: 1 Minecraft day is 20 minutes in real time by default.
season-duration: 7

# The world time when the current season started.
# This is automatically managed by the plugin, DO NOT EDIT.
season-start-time: 16350

# Temperature Effects
# Configure the effects applied to players when they are too cold or too hot.
temperature-effects:
  cold:
    slowness:
      enabled: true
      amplifier-per-degree: 0.2 # How much the slowness amplifier increases per degree below the threshold
      max-amplifier: 5
    hunger:
      enabled: true
      amplifier-per-degree: 0.2
      max-amplifier: 5
    freezing-damage:
      enabled: true
      threshold-offset: -10 # How many degrees below the cold threshold that damage starts
      damage-per-degree: 1 # How much damage to apply per degree below the freezing damage threshold
  heat:
    slowness:
      enabled: true
      amplifier-per-degree: 0.2
      max-amplifier: 5
    hunger:
      enabled: true
      amplifier-per-degree: 0.2
      max-amplifier: 5
    burning:
      enabled: true
      ticks-per-degree: 4 # How many ticks of fire to apply per degree above the threshold (20 ticks = 1 second)
      max-ticks: 100
```
## seasons.yml
```
# Default Season Configuration

current-season: SPRING
```