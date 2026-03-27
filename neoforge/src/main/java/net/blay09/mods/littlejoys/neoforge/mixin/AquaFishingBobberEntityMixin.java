package net.blay09.mods.littlejoys.neoforge.mixin;

import com.teammetallurgy.aquaculture.entity.AquaFishingBobberEntity;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AquaFishingBobberEntity.class)
public abstract class AquaFishingBobberEntityMixin extends FishingHook {

    public AquaFishingBobberEntityMixin(EntityType<? extends FishingHook> entityType, Level level) {
        super(entityType, level);
    }
    // Set order = 900 so catchingFish is still being called and not canceled by other mods
    @Inject(method = "catchingFish", at = @At("HEAD"), order = 900)
    private void catchingFish(BlockPos pos, CallbackInfo ci) {
        try {
            if (level() instanceof ServerLevel serverLevel) {
                final var fishingHookAccessor = (FishingHookAccessor) this;
                final var fishingSpotHolder = (FishingSpotHolder) this;
                if (fishingSpotHolder.littlejoys$getFishingSpot().isEmpty()) {
                    FishingSpotHandler.findFishingSpot(serverLevel, pos).ifPresent(fishingSpotPos -> {
                        fishingSpotHolder.littlejoys$setFishingSpot(fishingSpotPos);
                        int configuredTimeUntilLured = FishingSpotHandler.claimFishingSpot(serverLevel, fishingSpotPos);
                        final var timeUntilLured = fishingHookAccessor.getTimeUntilLured();
                        final var newTimeUntilLured = configuredTimeUntilLured >= 0 ? Mth.clamp(timeUntilLured, 1, configuredTimeUntilLured) : timeUntilLured;
                        if (timeUntilLured != newTimeUntilLured) {
                            fishingHookAccessor.setTimeUntilLured(newTimeUntilLured);
                        }
                    });
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("LittleJoys crashed during catchingFish hook", e);
        }
    }

    @Inject(method = "retrieve", at = @At("RETURN"))
    private void retrieve(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
        try {
            if (level() instanceof ServerLevel serverLevel) {
                final var fishingHookAccessor = (FishingHookAccessor) this;
                final var fishingSpotHolder = (FishingSpotHolder) this;
                final var fishingSpot = fishingSpotHolder.littlejoys$getFishingSpot();
                if (fishingSpot.isPresent() && fishingHookAccessor.getNibble() > 0) {
                    FishingSpotHandler.consumeFishingSpot(getPlayerOwner(), serverLevel, fishingSpot.get());
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("LittleJoys crashed during retrieve hook", e);
        }
    }
}
