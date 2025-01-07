# Seasons
 Seasons plugin - Add temperature and seasons to your world.

![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/seasonsplus?style=flat)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/seasonsplus?style=flat&label=Modrinth%20Downloads)](https://modrinth.com/plugin/seasonsplus)
[![Spiget Downloads](https://img.shields.io/spiget/downloads/121435?style=flat&label=Spigot%20Downloads)](https://www.spigotmc.org/resources/seasons.121435/)
[![Hangar Views](https://img.shields.io/hangar/views/SeasonsPlus?style=flat&label=Hangar%20Views)](https://hangar.papermc.io/icecubetr/SeasonsPlus)
![GitHub License](https://img.shields.io/github/license/whiteh4cker-tr/Seasons?style=flat)
[![CodeFactor](https://www.codefactor.io/repository/github/whiteh4cker-tr/seasons/badge)](https://www.codefactor.io/repository/github/whiteh4cker-tr/seasons)

<big>Supported Platforms</big><br>
[![spigot software](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/compact-minimal/supported/spigot_vector.svg)](https://www.spigotmc.org/)
[![paper software](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/supported/paper_vector.svg)](https://papermc.io/)
[![purpur software](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/supported/purpur_vector.svg)](https://purpurmc.org/)

![features](https://i.imgur.com/JFPNAPj.png)

-   Temperature
-   Freezing (to death)

-   Burning (to death)
-   Hunger

-   Slowness
-   Visible cold breath

-   PlaceholderAPI support: `%seasons_current%`
-   Custom season events

![alt text](https://i.imgur.com/vqd7ImQ.png)

![summer](https://i.imgur.com/QoD9qoQ.png)

-   No rain
-   Berry bushes everywhere

-   Faster crop growth
-   Husks will spawn instead of zombies

-   Removes flowers that were spawned during the Spring season when a chunk loads.
-   Increases the spawn rate of jungle animals (Parrots, Ocelots, and Pandas) by 30%.

![autumn](https://i.imgur.com/0ePOSSK.png)

-   Gives mobs a 20% chance to spawn with a Carved Pumpkin on their head.
-   Removes any Sweet Berry Bushes that might have spawned in previous seasons.

-   Increases the spawn rate of Mooshrooms, Frogs, and Foxes by 30%.
-   Spawns mushroom patches.

![winter](https://imgur.com/4vpOXyN.png)

-   Skeletons that spawn naturally during Winter will be replaced with Strays.
-   Crops and plants will not grow if they are exposed to the sky (no solid block above them).

-   Has a 20% chance to display an aurora borealis effect in the sky each night.
-   The effect consists of FIREWORK_SPARK particles spawning high in the sky around players.

-   Increases the spawn rate of Wolves, Foxes, and Polar Bears by 50%.

![spring](https://imgur.com/l1dXOqs.png)

-   Adds a variety of flowers to newly generated chunks
-   Increases the spawn rate of Sheep, Cows, Pigs, Rabbits, and Chickens by 30%.

![modifiers](https://imgur.com/424WENZ.png)

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

![config](https://imgur.com/WuW0y71.png)
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
# Configure the duration of each season in minutes.
season-duration: 15

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
