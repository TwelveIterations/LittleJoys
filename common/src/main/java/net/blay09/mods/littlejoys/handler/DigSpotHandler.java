package net.blay09.mods.littlejoys.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.block.entity.DigSpotBlockEntity;
import net.blay09.mods.littlejoys.recipe.DigSpotRecipe;
import net.blay09.mods.littlejoys.recipe.ModRecipeTypes;
import net.blay09.mods.littlejoys.recipe.condition.EventContextImpl;
import net.blay09.mods.littlejoys.stats.ModStats;
import net.blay09.mods.littlejoys.tag.ModPoiTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class DigSpotHandler {

    private static final RandomSource random = RandomSource.create();
    private static final String DIG_SPOT_COOLDOWN = "digSpotCooldown";

    public static void initialize() {
        Balm.getEvents().onEvent(UseBlockEvent.class, event -> {
            final var player = event.getPlayer();
            final var tool = player.getItemInHand(event.getHand());
            if (!(tool.getItem() instanceof ShovelItem) || !player.mayBuild() || player.isSpectator()) {
                return;
            }

            final var level = event.getLevel();
            final var pos = event.getHitResult().getBlockPos();
            final var digSpotPos = pos.above();
            if (!level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_SHOVEL)
                    || !level.getBlockState(digSpotPos).is(ModBlocks.digSpot)
                    || !level.mayInteract(player, pos)) {
                return;
            }

            if (level instanceof ServerLevel serverLevel) {
                final var digSpotState = level.getBlockState(digSpotPos);
                Block.dropResources(digSpotState, level, digSpotPos, level.getBlockEntity(digSpotPos), player, tool);
                level.removeBlock(digSpotPos, false);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(pos)),
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 20, 0.25, 0.05, 0.25, 0.1);
                level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1f, 1f);
                tool.hurtAndBreak(1, player, it -> it.broadcastBreakEvent(event.getHand()));
            }
            event.setResult(InteractionResult.sidedSuccess(level.isClientSide));
            event.setCanceled(!level.isClientSide);
        });

        Balm.getEvents().onTickEvent(TickType.ServerPlayer, TickPhase.End, (player) -> {
            final var playerData = Balm.getHooks().getPersistentData(player);
            final var littleJoysData = playerData.getCompound(LittleJoys.MOD_ID);
            playerData.put(LittleJoys.MOD_ID, littleJoysData);
            final var cooldown = littleJoysData.getInt(DIG_SPOT_COOLDOWN);
            if (cooldown > 0) {
                littleJoysData.putInt(DIG_SPOT_COOLDOWN, cooldown - 1);
            } else {
                final var level = (ServerLevel) player.level();
                final var poiManager = level.getPoiManager();
                final var centerPos = getOriginForNextSpawn(player);
                final var checkRange = LittleJoysConfig.getActive().digSpots.minimumDistanceBetween;
                final var spawnRange = LittleJoysConfig.getActive().digSpots.spawnDistance;
                final var digSpotInRange = poiManager.getInRange(it -> it.is(ModPoiTypeTags.DIG_SPOTS), centerPos, checkRange, PoiManager.Occupancy.ANY).findAny();
                if (digSpotInRange.isEmpty()) {
                    final var foundSurfacePos = getVerticallyNearRandomOffsetPos(level, centerPos, spawnRange);
                    if (foundSurfacePos.isEmpty()) {
                        littleJoysData.putInt(DIG_SPOT_COOLDOWN, 20);
                        return;
                    }

                    final var surfacePos = foundSurfacePos.get();
                    final var aboveSurfacePos = surfacePos.above();

                    final var totalSpots = ChunkLimitManager.get(level).getTotalDigSpotsInChunk(aboveSurfacePos);
                    final var maxSpots = LittleJoysConfig.getActive().digSpots.totalLimitPerChunk;
                    if (maxSpots > 0 && totalSpots >= maxSpots) {
                        // If we have exceeded the total, don't bother re-checking until 10 seconds have passed
                        littleJoysData.putInt(DIG_SPOT_COOLDOWN, 200);
                        return;
                    }

                    if (!level.getBlockState(aboveSurfacePos).canBeReplaced()) {
                        // If this position was bad, try again in a second
                        littleJoysData.putInt(DIG_SPOT_COOLDOWN, 20);
                        return;
                    }

                    if (createDigSpot(level, aboveSurfacePos, player)) {
                        littleJoysData.putInt(DIG_SPOT_COOLDOWN, Math.round(LittleJoysConfig.getActive().digSpots.spawnIntervalSeconds * 20));
                    } else {
                        // Cool down for a second if we failed
                        littleJoysData.putInt(DIG_SPOT_COOLDOWN, 20);
                    }
                } else {
                    // If we have one in range, don't bother re-checking until 10 seconds have passed
                    littleJoysData.putInt(DIG_SPOT_COOLDOWN, 200);
                }
            }
        });
    }

    public static boolean createDigSpot(ServerLevel level, BlockPos pos, ServerPlayer player) {
        return findRecipe(level, pos, player).map(recipe -> {
            createDigSpot(level, pos, recipe);
            return true;
        }).orElse(false);
    }

    public static void createDigSpot(ServerLevel level, BlockPos pos, DigSpotRecipe recipe) {
        level.setBlock(pos, ModBlocks.digSpot.defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof DigSpotBlockEntity digSpot) {
            digSpot.setRecipeId(recipe.identifier());
        }
        ChunkLimitManager.get(level).trackDigSpot(pos);
    }

    private static BlockPos getOriginForNextSpawn(Player player) {
        final var projectForwardDistance = LittleJoysConfig.getActive().digSpots.projectForwardDistance;
        final var forwardDirection = player.getDirection();
        return player.blockPosition().relative(forwardDirection, projectForwardDistance);
    }

    private static Optional<BlockPos> getVerticallyNearRandomOffsetPos(ServerLevel level, BlockPos origin, int spawnRange) {
        BlockPos bestPos = null;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            final var offsetX = random.nextInt(spawnRange + spawnRange) - spawnRange;
            final var offsetZ = random.nextInt(spawnRange + spawnRange) - spawnRange;
            final var randomOffsetPos = new BlockPos(origin.getX() + offsetX, origin.getY(), origin.getZ() + offsetZ);
            if (!level.isLoaded(randomOffsetPos)) {
                continue;
            }

            final var surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, randomOffsetPos).below();
            final var surfaceDist = Math.abs(surfacePos.getY() - origin.getY());
            if (bestPos == null || surfaceDist < bestDist) {
                bestPos = surfacePos;
                bestDist = surfaceDist;
            }
        }
        return Optional.ofNullable(bestPos);
    }

    private static Optional<DigSpotRecipe> findRecipe(ServerLevel level, BlockPos pos, ServerPlayer player) {
        final var recipeManager = level.getRecipeManager();
        final var recipes = recipeManager.getAllRecipesFor(ModRecipeTypes.digSpotRecipeType);
        final var candidates = new ArrayList<DigSpotRecipe>();
        for (final var recipe : recipes) {
            if (isValidRecipeFor(recipe, level, pos, player)) {
                candidates.add(recipe);
            }
        }
        return WeightedRandom.getRandomItem(random, candidates);
    }

    private static boolean isValidRecipeFor(DigSpotRecipe recipe, ServerLevel level, BlockPos pos, ServerPlayer player) {
        final var context = new EventContextImpl(level, pos, level.getBlockState(pos), player);
        return recipe.eventCondition().test(context);
    }

    public static Optional<DigSpotRecipe> recipeById(ServerLevel level, @Nullable ResourceLocation recipeId) {
        final var recipeManager = level.getRecipeManager();
        if (recipeId == null) {
            return Optional.empty();
        }
        final var recipe = recipeManager.byKey(recipeId).orElse(null);
        if (recipe instanceof DigSpotRecipe digSpotRecipe) {
            return Optional.of(digSpotRecipe);
        }
        return Optional.empty();
    }

    public static void digSpotConsumed(Player player) {
        final var playerData = Balm.getHooks().getPersistentData(player);
        final var littleJoysData = playerData.getCompound(LittleJoys.MOD_ID);
        playerData.put(LittleJoys.MOD_ID, littleJoysData);
        littleJoysData.putInt(DIG_SPOT_COOLDOWN, Math.round(LittleJoysConfig.getActive().digSpots.afterDiggingCooldownSeconds * 20));

        player.awardStat(ModStats.digSpotsDug);
    }
}
