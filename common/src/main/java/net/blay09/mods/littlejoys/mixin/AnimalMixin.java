package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.blessing.StarOfProsperity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public class AnimalMixin {
    @Inject(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER))
    private void spawnChildFromBreeding(ServerLevel level, Animal partner, CallbackInfo ci) {
        StarOfProsperity.trySpawnTwinOffspring(level, (Animal) (Object) this, partner);
    }
}
