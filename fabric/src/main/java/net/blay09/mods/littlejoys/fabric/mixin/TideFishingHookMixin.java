package net.blay09.mods.littlejoys.fabric.mixin;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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

@Mixin(TideFishingHook.class)
public abstract class TideFishingHookMixin extends Entity implements FishingSpotHolder {

    @Shadow(remap = false)
    private int nibble;

    @Shadow(remap = false)
    private int timeUntilLured;

    @Nullable
    private BlockPos littlejoys_fishingSpot;

    public TideFishingHookMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "catchingFish", at = @At("HEAD"), remap = false)
    private void catchingFish(BlockPos pos, CallbackInfo ci) {
        try {
            if (level() instanceof ServerLevel serverLevel) {
                final var fishingSpotHolder = (FishingSpotHolder) this;
                if (fishingSpotHolder.getFishingSpot().isEmpty() && timeUntilLured > 40) {
                    FishingSpotHandler.findFishingSpot(serverLevel, pos).ifPresent(fishingSpotPos -> {
                        fishingSpotHolder.setFishingSpot(fishingSpotPos);
                        int configuredTimeUntilLured = FishingSpotHandler.claimFishingSpot(serverLevel, fishingSpotPos);
                        if (configuredTimeUntilLured >= 0) {
                            timeUntilLured = Math.max(1, configuredTimeUntilLured);
                        }
                    });
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("LittleJoys crashed during catchingFish hook", e);
        }
    }

    @Inject(method = "retrieve(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;)I", at = @At("RETURN"))
    private void retrieve(ItemStack itemStack, ServerLevel level, Player player, CallbackInfoReturnable<Integer> ci) {
        try {
            final var fishingSpotHolder = (FishingSpotHolder) this;
            final var fishingSpot = fishingSpotHolder.getFishingSpot();
            if (fishingSpot.isPresent() && nibble > 0) {
                FishingSpotHandler.consumeFishingSpot(player, level, fishingSpot.get());
            }
        } catch (Throwable e) {
            throw new RuntimeException("LittleJoys crashed during retrieve hook", e);
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

    @Override
    public Player littlejoys$getPlayerOwner() {
        return ((TideFishingHook) (Object) this).getPlayerOwner();
    }
}
