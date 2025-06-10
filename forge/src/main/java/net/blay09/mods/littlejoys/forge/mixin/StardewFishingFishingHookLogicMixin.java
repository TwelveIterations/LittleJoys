package net.blay09.mods.littlejoys.forge.mixin;

import com.bonker.stardewfishing.common.FishingHookLogic;
import net.blay09.mods.littlejoys.api.LittleJoysAPI;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHookLogic.class)
public class StardewFishingFishingHookLogicMixin {
    @Inject(method = "endMinigame", at = @At("HEAD"), remap = false)
    private static void endMinigame(Player player, boolean success, double accuracy, boolean gotChest, @Nullable ItemStack fishingRod, CallbackInfo ci) {
        try {
            if (success && player.fishing instanceof FishingSpotHolder fishingSpotHolder && player.level() instanceof ServerLevel serverLevel) {
                fishingSpotHolder.getFishingSpot().ifPresent(fishingSpot ->
                        LittleJoysAPI.consumeFishingSpot(player, serverLevel, fishingSpot));
            }
        } catch (Throwable e) {
            throw new RuntimeException("LittleJoys crashed during endMinigame hook", e);
        }
    }
}
