package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.blessing.StarOfSerenity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
    private void canAttack(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (StarOfSerenity.shouldIgnoreTarget((Mob) (Object) this, target)) {
            cir.setReturnValue(false);
        }
    }
}
