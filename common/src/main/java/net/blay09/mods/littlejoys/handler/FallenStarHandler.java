package net.blay09.mods.littlejoys.handler;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.blessing.StarOfFortune;
import net.blay09.mods.littlejoys.entity.FallenStarEntity;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class FallenStarHandler {

    private static final RandomSource random = RandomSource.create();
    private static final String FALLEN_STAR_COOLDOWN = "fallenStarCooldown";
    private static final int FALLING_STAR_HORIZONTAL_OFFSET = 24;
    private static final int FALLING_STAR_VERTICAL_OFFSET = 128;

    public static void initialize() {
        ServerTickCallback.ServerPlayerTick.AFTER.register(player -> {
            if (!LittleJoysConfig.getActive().fallenStars.enabled) {
                return;
            }

            final var level = player.level();
            final var pos = player.blockPosition();
            if (level.environmentAttributes().getValue(EnvironmentAttributes.STAR_BRIGHTNESS, pos) <= 0f) {
                return;
            }

            final var config = LittleJoysConfig.getActive().fallenStars;
            final var playerData = Balm.hooks().getPersistentData(player);
            final var littleJoysData = playerData.getCompoundOrEmpty(LittleJoys.MOD_ID);
            if (!littleJoysData.contains(FALLEN_STAR_COOLDOWN)) {
                littleJoysData.putInt(FALLEN_STAR_COOLDOWN, (int) Math.round(72000 * Math.random()));
            }
            playerData.put(LittleJoys.MOD_ID, littleJoysData);

            final var cooldown = littleJoysData.getIntOr(FALLEN_STAR_COOLDOWN, 0);
            if (cooldown > 0) {
                littleJoysData.putInt(FALLEN_STAR_COOLDOWN, cooldown - 1);
                return;
            }

            if (random.nextFloat() >= config.chancePerRoll) {
                littleJoysData.putInt(FALLEN_STAR_COOLDOWN, Math.round(config.rollIntervalSeconds * 20));
                return;
            }

            if (hasFallenStarInRange(level, player.blockPosition(), config.minimumDistanceBetween)) {
                littleJoysData.putInt(FALLEN_STAR_COOLDOWN, 200);
                return;
            }

            final var targetPos = findTargetPos(level, player);
            if (targetPos.isPresent()) {
                startFallingStar(level, targetPos.get(), player);
                littleJoysData.putInt(FALLEN_STAR_COOLDOWN, Math.round(config.cooldownSeconds * 20));
            } else {
                littleJoysData.putInt(FALLEN_STAR_COOLDOWN, 20);
            }
        });
    }

    public static boolean startFallingStar(ServerLevel level, Player player) {
        final var targetPos = findTargetPos(level, player);
        if (targetPos.isEmpty()) {
            return false;
        }

        startFallingStar(level, targetPos.get(), player);
        return true;
    }

    public static void startFallingStar(ServerLevel level, BlockPos pos) {
        startFallingStar(level, pos, null);
    }

    public static void startFallingStar(ServerLevel level, BlockPos pos, @Nullable Player player) {
        final var offsetX = random.nextBoolean() ? FALLING_STAR_HORIZONTAL_OFFSET : -FALLING_STAR_HORIZONTAL_OFFSET;
        final var offsetZ = random.nextBoolean() ? FALLING_STAR_HORIZONTAL_OFFSET : -FALLING_STAR_HORIZONTAL_OFFSET;
        final var fallenStar = new FallenStarEntity(level, pos.getX() + 0.5f + offsetX, pos.getY() + FALLING_STAR_VERTICAL_OFFSET, pos.getZ() + 0.5f + offsetZ, pos);
        if (player != null) {
            fallenStar.setSourcePlayerId(player.getUUID());
        }
        level.addFreshEntity(fallenStar);

        if (player != null) {
            fallenStar.playPlayerAwareSound(level, fallenStar.position(), ModSounds.fallenStar, SoundSource.AMBIENT, 1f, 1f);
        }
    }

    private static Optional<BlockPos> findTargetPos(ServerLevel level, Player player) {
        final var config = LittleJoysConfig.getActive().fallenStars;
        for (int i = 0; i < 5; i++) {
            final var candidate = getRandomPosInFront(level, player, config.spawnRange);
            if (isValidTarget(level, candidate)) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    private static BlockPos getRandomPosInFront(ServerLevel level, Player player, int spawnRange) {
        final var radius = Math.max(1, spawnRange / 2);
        final var origin = player.blockPosition().relative(player.getDirection(), radius);
        final var offsetX = random.nextInt(radius + radius) - radius;
        final var offsetZ = random.nextInt(radius + radius) - radius;
        final var randomOffsetPos = new BlockPos(origin.getX() + offsetX, origin.getY(), origin.getZ() + offsetZ);
        return level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, randomOffsetPos);
    }

    private static boolean isValidTarget(ServerLevel level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        if (!state.canBeReplaced()) {
            return false;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!level.canSeeSky(pos.offset(x, 0, z))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean hasFallenStarInRange(ServerLevel level, BlockPos pos, int range) {
        final var bounds = new AABB(pos).inflate(range);
        return !level.getEntitiesOfClass(FallenStarEntity.class, bounds, Entity::isAlive).isEmpty();
    }
}
