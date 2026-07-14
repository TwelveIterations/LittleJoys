package net.blay09.mods.littlejoys.entity;

import net.blay09.mods.littlejoys.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class FallenStarEntity extends Entity {

    private static final String TAG_PLACED_LIGHT_POS = "PlacedLightPos";

    private boolean landed;
    private @Nullable BlockPos placedLightPos;

    public FallenStarEntity(EntityType<? extends FallenStarEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FallenStarEntity(Level level, double posX, double posY, double posZ, double deltaX, double deltaY, double deltaZ) {
        this(ModEntities.fallenStar.value(), level);
        setPos(posX, posY, posZ);
        setDeltaMovement(deltaX, deltaY, deltaZ);
    }

    @Override
    public void tick() {
        super.tick();

        xo = getX();
        yo = getY();
        zo = getZ();

        if (!isNoGravity()) {
            applyGravity();
        }

        move(MoverType.SELF, getDeltaMovement());
        rotateTowardsClosestPlayer();

        final var airDrag = getAirDrag();
        var drag = airDrag;
        if (onGround()) {
            drag *= level().getBlockState(getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction();
        }

        setDeltaMovement(getDeltaMovement().multiply(drag, airDrag, drag));
        if (onGround() && getDeltaMovement().y < 0) {
            setDeltaMovement(getDeltaMovement().multiply(1, -0.5, 1));
        }

        if (level().isClientSide()) {
            if (!onGround()) {
                level().addParticle(ModParticles.fallenStar.value(), getX(), getY() + 0.1f, getZ(), 0f, 0.02f, 0f);
            } else if (tickCount % 30 == 0) {
                level().addParticle(ModParticles.fallenStar.value(), getX() - 0.25 + Math.random() * 0.5, getY() + 1.25f, getZ() - 0.25 + Math.random() * 0.5, 0f, 0.01f, 0f);
            }
        } else if (onGround() && !landed && level() instanceof ServerLevel serverLevel) {
            landed = true;
            final var pos = blockPosition();
            if (serverLevel.getBlockState(pos).canBeReplaced()) {
                if (serverLevel.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, LightBlock.MAX_LEVEL), 3)) {
                    placedLightPos = pos;
                }
            }
            serverLevel.playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_FALL, SoundSource.NEUTRAL, 1f, 1f);
        }
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
        level.sendParticles(ModParticles.fallenStar.value(), getX(), getY() + 0.5f, getZ(), 12, 0.25f, 0.35f, 0.25f, 0.02f);
        level.playSound(null, this, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.AMBIENT, 1f, 1f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) {
            removePlacedLight();
        }
        super.remove(reason);
    }

    private void removePlacedLight() {
        if (placedLightPos != null && level() instanceof ServerLevel serverLevel && serverLevel.getBlockState(placedLightPos).is(Blocks.LIGHT)) {
            serverLevel.setBlock(placedLightPos, Blocks.AIR.defaultBlockState(), LightBlock.UPDATE_ALL);
            placedLightPos = null;
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (placedLightPos != null) {
            output.putLong(TAG_PLACED_LIGHT_POS, placedLightPos.asLong());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        placedLightPos = input.getLong(TAG_PLACED_LIGHT_POS).map(BlockPos::of).orElse(null);
        if (placedLightPos != null) {
            landed = true;
        }
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
