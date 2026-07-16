package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.blessing.Blessings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectUtil.class)
public class MobEffectUtilMixin {

    @Inject(method = "formatDuration", at = @At("HEAD"), cancellable = true)
    private static void formatDuration(MobEffectInstance instance, float scale, float tickrate, CallbackInfoReturnable<Component> cir) {
        final var blessing = Blessings.byEffect(instance.getEffect());
        if (blessing.isPresent() && instance.getAmplifier() < 255) { // we treat 255 as infinite
            final var usesLeft = instance.getAmplifier() + 1;
            final var defaultUses = blessing.get().defaultUses();
            final var remainingRatio = (float) usesLeft / defaultUses;
            final var description = remainingRatio >= 0.75f ? "brilliant"
                    : remainingRatio >= 0.5f ? "radiant"
                    : remainingRatio >= 0.25f ? "glowing"
                    : "fading";
            cir.setReturnValue(Component.translatable("effect.littlejoys.uses_left." + description));
        }
    }
}
