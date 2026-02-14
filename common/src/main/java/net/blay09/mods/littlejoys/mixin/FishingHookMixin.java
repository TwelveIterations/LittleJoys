package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Entity implements FishingSpotHolder {

    @Shadow
    private int nibble;

    @Shadow
    private int timeUntilLured;

    @Unique
    @Nullable
    private BlockPos littlejoys$fishingSpot;

    @Unique
    private boolean littlejoys$skipRewards;

    public FishingHookMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // Set order = 900 so catchingFish is still being called and not canceled by other mods
    @Inject(method = "catchingFish", at = @At("HEAD"), order = 900)
    private void catchingFish(BlockPos pos, CallbackInfo ci) {
        if (level() instanceof ServerLevel serverLevel
                && littlejoys$fishingSpot == null
                ) {
            FishingSpotHandler.findFishingSpot(serverLevel, pos).ifPresent(fishingSpotPos -> {
                littlejoys$fishingSpot = fishingSpotPos;
                int configuredTimeUntilLured = FishingSpotHandler.claimFishingSpot(serverLevel, fishingSpotPos);
                if (configuredTimeUntilLured >= 0) {
                    timeUntilLured = Mth.clamp(timeUntilLured, 1, configuredTimeUntilLured);
                }
            });
        }
    }

    @Inject(method = "retrieve", at = @At("RETURN"))
    private void retrieve(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
        if (level() instanceof ServerLevel serverLevel) {
            if (littlejoys$fishingSpot != null && nibble > 0) {
                FishingSpotHandler.consumeFishingSpot(((FishingHook) (Object) this).getPlayerOwner(), serverLevel, littlejoys$fishingSpot);
            }
        }
    }

    @Override
    public Optional<BlockPos> getFishingSpot() {
        return Optional.ofNullable(littlejoys$fishingSpot);
    }

    @Override
    public void setFishingSpot(BlockPos fishingSpot) {
        littlejoys$fishingSpot = fishingSpot;
    }

    @Override
    public Player littlejoys$getPlayerOwner() {
        return ((FishingHook) (Object) this).getPlayerOwner();
    }

    @Override
    public boolean littlejoys$shouldSkipRewards() {
        return littlejoys$skipRewards;
    }

    @Override
    public void littlejoys$setSkipRewards(boolean skipRewards) {
        littlejoys$skipRewards = skipRewards;
    }
}
