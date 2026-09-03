package net.blay09.mods.littlejoys.handler;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.EventPhases;
import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.advancement.ModAdvancements;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.block.entity.DigSpotBlockEntity;
import net.blay09.mods.littlejoys.registry.DigSpotEvent;
import net.blay09.mods.littlejoys.registry.ModDynamicRegistries;
import net.blay09.mods.shogi.context.MutableShogiContext;
import net.blay09.mods.littlejoys.stats.ModStats;
import net.blay09.mods.littlejoys.tag.ModBlockTags;
import net.blay09.mods.littlejoys.tag.ModPoiTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class DigSpotHandler {

    private static final RandomSource random = RandomSource.create();
    private static final String DIG_SPOT_COOLDOWN = "digSpotCooldown";

    public static void initialize() {
        BlockCallback.Break.Before.EVENT.register(EventPhases.LOWEST, (level, pos, state, blockEntity, player) -> {
            if (player != null && player.getAbilities().instabuild) {
                return true;
            }

            if (!(level instanceof ServerLevel) || player == null || Balm.hooks().isFakePlayer(player)) {
                return true;
            }

            if (level.getBlockState(pos.above()).is(ModBlockTags.DIG_SPOTS)) {
                DigSpotHandler.digSpotConsumed(player);
            }

            return true;
        });

        ServerTickCallback.ServerPlayerTick.AFTER.register(player -> {
            if (!LittleJoysConfig.getActive().digSpots.enabled) {
                return;
            }

            final var playerData = Balm.hooks().getPersistentData(player);
            final var littleJoysData = playerData.getCompoundOrEmpty(LittleJoys.MOD_ID);
            playerData.put(LittleJoys.MOD_ID, littleJoysData);
            final var cooldown = littleJoysData.getIntOr(DIG_SPOT_COOLDOWN, 0);
            if (cooldown > 0) {
                littleJoysData.putInt(DIG_SPOT_COOLDOWN, cooldown - 1);
            } else {
                final var level = player.level();
                final var poiManager = level.getPoiManager();
                final var centerPos = getOriginForNextSpawn(player);
                final var checkRange = LittleJoysConfig.getActive().digSpots.minimumDistanceBetween;
                final var spawnRange = LittleJoysConfig.getActive().digSpots.spawnDistance;
                final var digSpotInRange = poiManager.getInRange(it -> it.is(ModPoiTypeTags.DIG_SPOTS), centerPos, checkRange, PoiManager.Occupancy.ANY).findAny();
                if (digSpotInRange.isEmpty()) {
                    final var surfacePos = getVerticallyNearRandomOffsetPos(level, centerPos, spawnRange);
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
        return findEvent(level, pos, player).map(eventHolder -> {
            createDigSpot(level, pos, eventHolder);
            return true;
        }).orElse(false);
    }

    public static void createDigSpot(ServerLevel level, BlockPos pos, Holder.Reference<DigSpotEvent> eventHolder) {
        level.setBlock(pos, ModBlocks.digSpot.defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof DigSpotBlockEntity digSpot) {
            digSpot.setEventKey(eventHolder.key());
        }
        ChunkLimitManager.get(level).trackDigSpot(pos);
    }

    private static BlockPos getOriginForNextSpawn(Player player) {
        final var projectForwardDistance = LittleJoysConfig.getActive().digSpots.projectForwardDistance;
        final var forwardDirection = player.getDirection();
        return player.blockPosition().relative(forwardDirection, projectForwardDistance);
    }

    private static BlockPos getVerticallyNearRandomOffsetPos(ServerLevel level, BlockPos origin, int spawnRange) {
        BlockPos bestPos = null;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            final var offsetX = random.nextInt(spawnRange + spawnRange) - spawnRange;
            final var offsetZ = random.nextInt(spawnRange + spawnRange) - spawnRange;
            final var randomOffsetPos = new BlockPos(origin.getX() + offsetX, origin.getY(), origin.getZ() + offsetZ);
            final var surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, randomOffsetPos).below();
            final var surfaceDist = Math.abs(surfacePos.getY() - origin.getY());
            if (bestPos == null || surfaceDist < bestDist) {
                bestPos = surfacePos;
                bestDist = surfaceDist;
            }
        }
        return bestPos;
    }

    private static Optional<Holder.Reference<DigSpotEvent>> findEvent(ServerLevel level, BlockPos pos, ServerPlayer player) {
        final var events = level.registryAccess().lookupOrThrow(ModDynamicRegistries.DIG_SPOT);
        final var candidates = new ArrayList<Holder.Reference<DigSpotEvent>>();
        for (final var eventHolder : events.listElements().toList()) {
            if (isValidEventFor(eventHolder, level, pos, player)) {
                candidates.add(eventHolder);
            }
        }
        return WeightedRandom.getRandomItem(random, candidates, it -> it.value().weight());
    }

    private static boolean isValidEventFor(Holder.Reference<DigSpotEvent> eventHolder, ServerLevel level, BlockPos pos, ServerPlayer player) {
        final var context = MutableShogiContext.of(player)
                .withLevel(level)
                .withBlockPos(pos)
                .withBlockState(level.getBlockState(pos))
                .withItemStack(player.getMainHandItem());
        return eventHolder.value().eventCondition().test(context);
    }

    public static Optional<DigSpotEvent> eventByKey(ServerLevel level, @Nullable ResourceKey<DigSpotEvent> eventKey) {
        if (eventKey == null) {
            return Optional.empty();
        }
        return level.registryAccess().lookupOrThrow(ModDynamicRegistries.DIG_SPOT).get(eventKey).map(Holder::value);
    }

    public static void digSpotConsumed(Player player) {
        final var playerData = Balm.hooks().getPersistentData(player);
        final var littleJoysData = playerData.getCompoundOrEmpty(LittleJoys.MOD_ID);
        playerData.put(LittleJoys.MOD_ID, littleJoysData);
        littleJoysData.putInt(DIG_SPOT_COOLDOWN, Math.round(LittleJoysConfig.getActive().digSpots.afterDiggingCooldownSeconds * 20));

        player.awardStat(ModStats.digSpotsDug);
        if (player instanceof ServerPlayer serverPlayer) {
            ModAdvancements.awardDigSpot(serverPlayer);
        }
    }
}
