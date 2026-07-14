package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.blessing.Blessing;
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
        for (final var blessing : Blessing.values()) {
            if (instance.getEffect().equals(blessing.effect())) {
                if (instance.getAmplifier() < 255) { // we treat 255 as infinite
                    cir.setReturnValue(Component.translatable("effect.littlejoys.uses_left", instance.getAmplifier()));
                }
                return;
            }
        }
    }
}
