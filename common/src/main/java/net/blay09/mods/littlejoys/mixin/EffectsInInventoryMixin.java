package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.blessing.Blessings;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {

    @Inject(method = "getEffectName", at = @At("HEAD"), cancellable = true)
    private void getEffectName(MobEffectInstance instance, CallbackInfoReturnable<Component> cir) {
        if (Blessings.byEffect(instance.getEffect()).isPresent()) {
            cir.setReturnValue(instance.getEffect().value().getDisplayName());
        }
    }
}
