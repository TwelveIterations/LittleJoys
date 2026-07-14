package net.blay09.mods.littlejoys;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;

import java.util.Objects;

@Config(LittleJoys.MOD_ID)
public class LittleJoysConfig {

    public static void initialize() {
        Balm.config().registerConfig(LittleJoysConfig.class);
    }

    public static LittleJoysConfig getActive() {
        return Objects.requireNonNull(Balm.config().getActiveConfig(LittleJoysConfig.class));
    }

    public DropRush dropRush = new DropRush();
    public GoldRush goldRush = new GoldRush();
    public FishingSpots fishingSpots = new FishingSpots();
    public DigSpots digSpots = new DigSpots();
    public FallenStars fallenStars = new FallenStars();
    public StardewFishing stardewFishing = new StardewFishing();

    public static class FishingSpots {
        @Comment("If disabled, fishing spots will not spawn.")
        public boolean enabled = true;

        @Comment("The minimum distance between fishing spots, preventing them from spawning too close together.")
        public int minimumDistanceBetween = 128;

        @Comment("The distance fishing spots will spawn from the player.")
        public int spawnDistance = 8;

        @Comment("The seconds that must pass after a fishing spot appeared before another fishing spot can appear for a player.")
        public float spawnIntervalSeconds = 300;

        @Comment("The seconds that must pass after fishing a fishing spot before another fishing spot can appear for a player.")
        public float afterFishingCooldownSeconds = 600;

        @Comment("The maximum distance a bobber can be from a fishing spot to still trigger it.")
        public int fishingRangeTolerance = 3;

        @Comment("Fishing spots will lure fish quicker than regular water. Set to -1 to disable.")
        public float secondsUntilLured = 2f;

        @Comment("The limit of fishing spots that can spawn in a given chunk overall. Once this many fishing spots have spawned, the chunk will never spawn any again. Set to -1 to disable.")
        public int totalLimitPerChunk = -1;

        @Comment("The offset applied to the spawn area in the direction the player is facing. Set to 0 to center it around the player.")
        public int projectForwardDistance = 4;
    }

    public static class DigSpots {
        @Comment("If disabled, dig spots will not spawn.")
        public boolean enabled = true;

        @Comment("The minimum distance between dig spots, preventing them from spawning too close together.")
        public int minimumDistanceBetween = 128;

        @Comment("The distance dig spots will spawn from the player.")
        public int spawnDistance = 32;

        @Comment("The seconds that must pass after a dig spot appeared before another dig spot can appear for a player.")
        public float spawnIntervalSeconds = 300;

        @Comment("The seconds that must pass after digging a dig spot before another dig spot can appear for a player.")
        public float afterDiggingCooldownSeconds = 600;

        @Comment("The limit of dig spots that can spawn in a given chunk overall. Once this many dig spots have spawned, the chunk will never spawn any again. Set to -1 to disable.")
        public int totalLimitPerChunk = 1;

        @Comment("The offset applied to the spawn area in the direction the player is facing. Set to 0 to center it around the player.")
        public int projectForwardDistance = 25;
    }

    public static class FallenStars {
        @Comment("If disabled, fallen stars will not spawn.")
        public boolean enabled = true;

        @Comment("The chance per roll for a fallen star to start falling once the player cooldown has elapsed.")
        public float chancePerRoll = 0.2f;

        @Comment("The minimum distance between fallen stars, preventing them from spawning too close together.")
        public int minimumDistanceBetween = 128;

        @Comment("The maximum distance fallen stars can spawn in front of the player.")
        public int spawnRange = 64;

        @Comment("The seconds that must pass after a fallen star appeared before another fallen star can appear for a player.")
        public float cooldownSeconds = 10800;

        @Comment("The seconds that must pass before another fallen star chance is rolled for a player.")
        public float rollIntervalSeconds = 900;
    }

    public static class StardewFishing {
        @Comment("The treasure chest chance bonus applied when fishing at a fishing spot.")
        public float fishingSpotTreasureChestChanceBonus = 0f;

        @Comment("The golden chest chance bonus applied when fishing at a fishing spot.")
        public float fishingSpotGoldenChestChanceBonus = 0f;

        @Comment("If enabled, Little Joy's own fishing spot rewards will be skipped when Stardew Fishing minigames take over.")
        public boolean skipFishingSpotRewards = false;
    }

    public static class DropRush {
        @Comment("If disabled, drop rushes will not trigger.")
        public boolean enabled = true;

        @Comment("The base chance for a drop rush to occur.")
        public float baseChance = 0.02f;
    }

    public static class GoldRush {
        @Comment("If disabled, gold rushes will not trigger.")
        public boolean enabled = true;

        @Comment("The base chance for a gold rush to occur.")
        public float baseChance = 0.02f;
    }
}
