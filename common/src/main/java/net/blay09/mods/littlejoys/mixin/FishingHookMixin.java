package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Nullable
    private BlockPos littlejoys_fishingSpot;

    public FishingHookMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "catchingFish", at = @At("HEAD"))
    private void catchingFish(BlockPos pos, CallbackInfo ci) {
        if (level() instanceof ServerLevel serverLevel
                && littlejoys_fishingSpot == null
                && timeUntilLured > 40) {
            FishingSpotHandler.findFishingSpot(serverLevel, pos).ifPresent(fishingSpotPos -> {
                littlejoys_fishingSpot = fishingSpotPos;
                int configuredTimeUntilLured = FishingSpotHandler.claimFishingSpot(serverLevel, fishingSpotPos);
                if (configuredTimeUntilLured >= 0) {
                    timeUntilLured = Math.max(1, configuredTimeUntilLured);
                }
            });
        }
    }

    @Inject(method = "retrieve", at = @At("RETURN"))
    private void retrieve(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
        if (level() instanceof ServerLevel serverLevel) {
            if (littlejoys_fishingSpot != null && nibble > 0) {
                FishingSpotHandler.consumeFishingSpot(((FishingHook) (Object) this).getPlayerOwner(), serverLevel, littlejoys_fishingSpot);
            }
        }
    }

    @Override
    public Optional<BlockPos> getFishingSpot() {
        return Optional.ofNullable(littlejoys_fishingSpot);
    }

    @Override
    public void setFishingSpot(BlockPos fishingSpot) {
        littlejoys_fishingSpot = fishingSpot;
    }
}
