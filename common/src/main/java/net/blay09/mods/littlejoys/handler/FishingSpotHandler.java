package net.blay09.mods.littlejoys.handler;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.block.entity.FishingSpotBlockEntity;
import net.blay09.mods.littlejoys.mixin.RecipeManagerAccessor;
import net.blay09.mods.littlejoys.particle.ModParticles;
import net.blay09.mods.littlejoys.recipe.FishingSpotRecipe;
import net.blay09.mods.littlejoys.recipe.ModRecipeTypes;
import net.blay09.mods.littlejoys.recipe.condition.EventContextImpl;
import net.blay09.mods.littlejoys.stats.ModStats;
import net.blay09.mods.littlejoys.tag.ModPoiTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class FishingSpotHandler {

    private static final RandomSource random = RandomSource.create();
    private static final String FISHING_SPOT_COOLDOWN = "fishingSpotCooldown";

    public static void initialize() {
        ServerTickCallback.ServerPlayerTick.AFTER.register(player -> {
            final var playerData = Balm.hooks().getPersistentData(player);
            final var littleJoysData = playerData.getCompoundOrEmpty(LittleJoys.MOD_ID);
            playerData.put(LittleJoys.MOD_ID, littleJoysData);
            final var cooldown = littleJoysData.getIntOr(FISHING_SPOT_COOLDOWN, 0);
            if (cooldown > 0) {
                littleJoysData.putInt(FISHING_SPOT_COOLDOWN, cooldown - 1);
            } else {
                final var level = player.level();
                final var poiManager = level.getPoiManager();
                final var centerPos = getOriginForNextSpawn(player);
                final var checkRange = LittleJoysConfig.getActive().fishingSpots.minimumDistanceBetween;
                final var spawnRange = LittleJoysConfig.getActive().fishingSpots.spawnDistance;
                final var fishingSpotInRange = poiManager.getInRange(it -> it.is(ModPoiTypeTags.FISHING_SPOTS),
                        centerPos,
                        checkRange,
                        PoiManager.Occupancy.ANY).findAny();
                if (fishingSpotInRange.isEmpty()) {
                    final var offsetX = random.nextInt(spawnRange + spawnRange) - spawnRange;
                    final var offsetZ = random.nextInt(spawnRange + spawnRange) - spawnRange;
                    final var randomOffsetPos = new BlockPos(centerPos.getX() + offsetX, centerPos.getX(), centerPos.getZ() + offsetZ);
                    final var surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, randomOffsetPos).below();
                    final var aboveSurfacePos = surfacePos.above();

                    final var totalSpots = ChunkLimitManager.get(level).getTotalFishingSpotsInChunk(aboveSurfacePos);
                    final var maxSpots = LittleJoysConfig.getActive().fishingSpots.totalLimitPerChunk;
                    if (maxSpots > 0 && totalSpots >= maxSpots) {
                        // If we have exceeded the total, don't bother re-checking until 10 seconds have passed
                        littleJoysData.putInt(FISHING_SPOT_COOLDOWN, 200);
                        return;
                    }

                    if (!level.getBlockState(aboveSurfacePos).canBeReplaced()) {
                        // If this position was bad, try again in a second
                        littleJoysData.putInt(FISHING_SPOT_COOLDOWN, 20);
                        return;
                    }

                    if(createFishingSpot(level, aboveSurfacePos, player)) {
                        littleJoysData.putInt(FISHING_SPOT_COOLDOWN, Math.round(LittleJoysConfig.getActive().fishingSpots.spawnIntervalSeconds * 20));
                    } else {
                        // Cool down for a second if we failed
                        littleJoysData.putInt(FISHING_SPOT_COOLDOWN, 20);
                    }
                } else {
                    // If we have one in range, don't bother re-checking until 10 seconds have passed
                    littleJoysData.putInt(FISHING_SPOT_COOLDOWN, 200);
                }
            }
        });
    }

    public static boolean createFishingSpot(ServerLevel level, BlockPos pos, ServerPlayer player) {
        return findRecipe(level, pos, player).map(recipeHolder -> {
            createFishingSpot(level, pos, recipeHolder);
            return true;
        }).orElse(false);
    }

    public static void createFishingSpot(ServerLevel level, BlockPos pos, RecipeHolder<FishingSpotRecipe> recipeHolder) {
        level.setBlock(pos, ModBlocks.fishingSpot.defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof FishingSpotBlockEntity fishingSpot) {
            fishingSpot.setRecipeId(recipeHolder.id());
        }
        ChunkLimitManager.get(level).trackFishingSpot(pos);
    }

    private static BlockPos getOriginForNextSpawn(Player player) {
        final var projectForwardDistance = LittleJoysConfig.getActive().fishingSpots.projectForwardDistance;
        final var forwardDirection = player.getDirection();
        return player.blockPosition().relative(forwardDirection, projectForwardDistance);
    }

    private static Optional<RecipeHolder<FishingSpotRecipe>> findRecipe(ServerLevel level, BlockPos pos, ServerPlayer player) {
        final var recipeManager = level.getServer().getRecipeManager();
        final var recipeMap = ((RecipeManagerAccessor) recipeManager).getRecipes();
        final var recipes = recipeMap.byType(ModRecipeTypes.fishingSpot.type());
        final var candidates = new ArrayList<RecipeHolder<FishingSpotRecipe>>();
        for (final var recipe : recipes) {
            if (isValidRecipeFor(recipe, level, pos, player)) {
                candidates.add(recipe);
            }
        }
        return WeightedRandom.getRandomItem(random, candidates, it -> it.value().weight());
    }

    private static boolean isValidRecipeFor(RecipeHolder<FishingSpotRecipe> recipe, ServerLevel level, BlockPos pos, ServerPlayer player) {
        final var context = new EventContextImpl(level, pos, level.getBlockState(pos), player);
        return recipe.value().eventCondition().test(context);
    }

    @SuppressWarnings("unchecked")
    private static Optional<RecipeHolder<FishingSpotRecipe>> recipeById(ServerLevel level, @Nullable ResourceKey<Recipe<?>> recipeId) {
        final var recipeManager = level.recipeAccess();
        if (recipeId == null) {
            return Optional.empty();
        }
        final var recipeHolder = recipeManager.byKey(recipeId).orElse(null);
        if (recipeHolder != null && recipeHolder.value() instanceof FishingSpotRecipe) {
            return Optional.of((RecipeHolder<FishingSpotRecipe>) recipeHolder);
        }
        return Optional.empty();
    }

    public static Optional<RecipeHolder<FishingSpotRecipe>> resolveRecipe(ServerLevel level, BlockPos pos, @Nullable ResourceKey<Recipe<?>> recipeId, ServerPlayer player) {
        final var optRecipe = FishingSpotHandler.recipeById(level, recipeId);
        if (optRecipe.isPresent() && FishingSpotHandler.isValidRecipeFor(optRecipe.get(), level, pos, player)) {
            return optRecipe;
        }
        return FishingSpotHandler.findRecipe(level, pos, player);
    }

    public static Optional<BlockPos> findFishingSpot(ServerLevel serverLevel, BlockPos pos) {
        final var poiManager = serverLevel.getPoiManager();
        final var range = LittleJoysConfig.getActive().fishingSpots.fishingRangeTolerance;
        return poiManager.findClosest(it -> it.is(ModPoiTypeTags.FISHING_SPOTS), pos, range, PoiManager.Occupancy.ANY);
    }

    public static int claimFishingSpot(ServerLevel level, BlockPos pos) {
        level.sendParticles(ModParticles.goldRush.value(),
                pos.getX() + 0.5f,
                pos.getY() + 0.5f,
                pos.getZ() + 0.5f,
                2,
                0.25f,
                0.25f,
                0.25f,
                0f);
        if (LittleJoysConfig.getActive().fishingSpots.secondsUntilLured < 0) {
            return -1;
        }

        return Math.round(LittleJoysConfig.getActive().fishingSpots.secondsUntilLured * 20);
    }

    public static void consumeFishingSpot(@Nullable Player player, ServerLevel level, BlockPos pos) {
        final var x = pos.getX() + 0.5f;
        final var y = pos.getY() + 0.5f;
        final var z = pos.getZ() + 0.5f;
        level.sendParticles(ParticleTypes.CLOUD, x, y, z, 8, 0.25f, 0.25f, 0.25f, 0f);
        level.destroyBlock(pos, false);
        if (player != null) {
            player.awardStat(ModStats.fishingSpotsFished);

            final var playerData = Balm.hooks().getPersistentData(player);
            final var littleJoysData = playerData.getCompoundOrEmpty(LittleJoys.MOD_ID);
            playerData.put(LittleJoys.MOD_ID, littleJoysData);
            littleJoysData.putInt(FISHING_SPOT_COOLDOWN, Math.round(LittleJoysConfig.getActive().fishingSpots.afterFishingCooldownSeconds * 20));
        }
    }
}
