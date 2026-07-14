package net.blay09.mods.littlejoys.entity;

import net.blay09.mods.littlejoys.particle.ModParticles;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class FallenStarEntity extends Entity {

    private static final EntityDataAccessor<Long> DATA_LANDING_TARGET = SynchedEntityData.defineId(FallenStarEntity.class, EntityDataSerializers.LONG);
    private static final long NO_LANDING_TARGET = Long.MAX_VALUE;
    private static final String TAG_LIGHT_POS = "LightPos";
    private static final String TAG_LANDING_TARGET = "LandingTarget";
    private static final String TAG_SOURCE_PLAYER = "SourcePlayer";
    private static final String TAG_FALL_TICKS = "FallTicks";
    private static final int FALL_DURATION_TICKS = 80;
    private static final float LAND_SOUND_VOLUME = 0.5f;

    private boolean landed;
    private int fallTicks;
    private @Nullable Vec3 startVec;
    private @Nullable BlockPos lightPos;
    private @Nullable UUID sourcePlayerId;

    public FallenStarEntity(EntityType<? extends FallenStarEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FallenStarEntity(Level level, double posX, double posY, double posZ, BlockPos landingTarget) {
        this(ModEntities.fallenStar.value(), level);
        setPos(posX, posY, posZ);
        setLandingTarget(landingTarget);
    }

    @Override
    public void tick() {
        super.tick();

        xo = getX();
        yo = getY();
        zo = getZ();

        final var landingTarget = getLandingTarget();
        if (landingTarget != null && !landed) {
            moveTowardsLandingTarget(landingTarget);
        } else if (!onGround()) {
            if (!isNoGravity()) {
                applyGravity();
            }
            move(MoverType.SELF, getDeltaMovement());
        } else {
            setDeltaMovement(Vec3.ZERO);
        }

        rotateTowardsClosestPlayer();

        if (level().isClientSide()) {
            spawnParticles();
        } else if (onGround() && !landed && level() instanceof ServerLevel serverLevel) {
            land(serverLevel, blockPosition());
        }
    }

    private void spawnParticles() {
        if (!landed && !onGround()) {
            for (int i = 0; i < 4; i++) {
                final var progress = (i + Math.random()) / 4.0;
                final var x = Mth.lerp(progress, xo, getX()) + (Math.random() - 0.5) * 0.18;
                final var y = Mth.lerp(progress, yo, getY()) + 0.1f + (Math.random() - 0.5) * 0.18;
                final var z = Mth.lerp(progress, zo, getZ()) + (Math.random() - 0.5) * 0.18;
                level().addParticle(ModParticles.fallenStarTrail.value(), x, y, z, (Math.random() - 0.5) * 0.015, 0.015f, (Math.random() - 0.5) * 0.015);
            }
        } else if (tickCount % 30 == 0) {
            level().addParticle(ModParticles.fallenStar.value(), getX() - 0.25 + Math.random() * 0.5, getY() + 1.25f, getZ() - 0.25 + Math.random() * 0.5, 0f, 0.01f, 0f);
        }
    }

    private void moveTowardsLandingTarget(BlockPos landingTarget) {
        if (startVec == null) {
            startVec = position();
        }

        fallTicks++;
        final var target = getImpactTarget(landingTarget);
        final var progress = Math.min(1f, fallTicks / (float) FALL_DURATION_TICKS);
        final var nextPos = startVec.lerp(target, progress);
        setDeltaMovement(nextPos.subtract(position()));
        setPos(nextPos);

        if (progress >= 1f) {
            stopAtLandingTarget();
            if (level() instanceof ServerLevel serverLevel) {
                land(serverLevel, landingTarget);
            }
        }
    }

    private Vec3 getImpactTarget(BlockPos landingTarget) {
        return Vec3.atBottomCenterOf(landingTarget);
    }

    private void land(ServerLevel serverLevel, BlockPos pos) {
        stopAtLandingTarget();
        if (serverLevel.getBlockState(pos).canBeReplaced()) {
            if (serverLevel.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, LightBlock.MAX_LEVEL), 3)) {
                lightPos = pos;
            }
        }
        playImpactEffects(serverLevel, pos);
        playPlayerAwareSound(serverLevel, Vec3.atCenterOf(pos), ModSounds.fallenStarLand, SoundSource.NEUTRAL, LAND_SOUND_VOLUME, 1f);
    }

    public void playPlayerAwareSound(ServerLevel serverLevel, Vec3 pos, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
        serverLevel.playSound(null, pos.x(), pos.y(), pos.z(), sound, source, volume, pitch);

        if (sourcePlayerId != null) {
            final var player = serverLevel.getServer().getPlayerList().getPlayer(sourcePlayerId);
            if (player != null && player.level() == serverLevel) {
                final var audibleRange = sound.value().getRange(volume);
                if (player.distanceToSqr(pos) > audibleRange * audibleRange) {
                    final var playerPos = player.position();
                    final var soundPos = playerPos.add(pos.subtract(playerPos).normalize().scale(12));
                    player.connection.send(new ClientboundSoundPacket(sound, source, soundPos.x(), soundPos.y(), soundPos.z(), 0.2f, pitch, getRandom().nextLong()));
                }
            }
        }
    }

    private void stopAtLandingTarget() {
        landed = true;
        setDeltaMovement(Vec3.ZERO);
    }

    private void rotateTowardsClosestPlayer() {
        final var closestPlayer = level().getNearestPlayer(this, -1);
        if (closestPlayer == null) {
            return;
        }

        final var deltaX = closestPlayer.getX() - getX();
        final var deltaZ = closestPlayer.getZ() - getZ();
        if (deltaX * deltaX + deltaZ * deltaZ < 0.0001) {
            return;
        }

        final var targetYRot = (float) (Mth.atan2(deltaZ, deltaX) * Mth.RAD_TO_DEG) - 90f;
        setYRot(Mth.rotLerp(0.1f, getYRot(), targetYRot));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public void playerTouch(Player player) {
        if (level() instanceof ServerLevel serverLevel) {
            playCollectionEffects(serverLevel);
            discard();
        }
    }

    private void playCollectionEffects(ServerLevel level) {
        level.sendParticles(ModParticles.fallenStarTrail.value(), getX(), getY() + 0.5f, getZ(), 12, 0.25f, 0.35f, 0.25f, 0.02f);
        level.playSound(null, getX(), getY(), getZ(), ModSounds.fallenStarBlessing, SoundSource.AMBIENT, 1f, 1f);
    }

    private void playImpactEffects(ServerLevel level, BlockPos pos) {
        final var impactPos = Vec3.atBottomCenterOf(pos);
        level.sendParticles(ModParticles.fallenStarTrail.value(), impactPos.x(), impactPos.y() + 0.25f, impactPos.z(), 32, 0.45f, 0.25f, 0.45f, 0.08f);
        level.sendParticles(ParticleTypes.POOF, impactPos.x(), impactPos.y() + 0.05f, impactPos.z(), 12, 0.35f, 0.05f, 0.35f, 0.03f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_LANDING_TARGET, NO_LANDING_TARGET);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) {
            removePlacedLight();
        }
        super.remove(reason);
    }

    private void removePlacedLight() {
        if (lightPos != null && level() instanceof ServerLevel serverLevel && serverLevel.getBlockState(lightPos).is(Blocks.LIGHT)) {
            serverLevel.setBlock(lightPos, Blocks.AIR.defaultBlockState(), LightBlock.UPDATE_ALL);
            lightPos = null;
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (lightPos != null) {
            output.putLong(TAG_LIGHT_POS, lightPos.asLong());
        }
        final var landingTarget = getLandingTarget();
        if (landingTarget != null) {
            output.putLong(TAG_LANDING_TARGET, landingTarget.asLong());
        }
        if (sourcePlayerId != null) {
            output.putString(TAG_SOURCE_PLAYER, sourcePlayerId.toString());
        }
        output.putInt(TAG_FALL_TICKS, fallTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        lightPos = input.getLong(TAG_LIGHT_POS).map(BlockPos::of).orElse(null);
        if (lightPos != null) {
            stopAtLandingTarget();
        }
        setLandingTarget(input.getLong(TAG_LANDING_TARGET).map(BlockPos::of).orElse(null));
        sourcePlayerId = input.getString(TAG_SOURCE_PLAYER).map(UUID::fromString).orElse(null);
        fallTicks = input.getIntOr(TAG_FALL_TICKS, 0);
    }

    public @Nullable BlockPos getLandingTarget() {
        final long landingTarget = getEntityData().get(DATA_LANDING_TARGET);
        return landingTarget != NO_LANDING_TARGET ? BlockPos.of(landingTarget) : null;
    }

    public void setLandingTarget(@Nullable BlockPos landingTarget) {
        getEntityData().set(DATA_LANDING_TARGET, landingTarget != null ? landingTarget.asLong() : NO_LANDING_TARGET);
    }

    public void setSourcePlayerId(@Nullable UUID sourcePlayerId) {
        this.sourcePlayerId = sourcePlayerId;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        if (!onGround()) {
            return true;
        } else {
            double size = getBoundingBox().getSize();
            if (Double.isNaN(size)) {
                size = 1;
            }

            size *= (double) 256 * getViewScale();
            return distance < size * size;
        }
    }
}
