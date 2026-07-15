package net.blay09.mods.littlejoys.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.blay09.mods.littlejoys.blessing.StarOfSerenity;
import net.blay09.mods.littlejoys.handler.DropRushHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {
    @Inject(method = "useWithoutItem", at = @At("RETURN"))
    public void useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() == InteractionResult.CONSUME && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            DropRushHandler.rollForDropRush(serverLevel, pos, state, serverPlayer);
        }
    }

    @WrapOperation(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean entityInside(Entity pEntity, ServerLevel pLevel, DamageSource damageSource, float amount, Operation<Boolean> original, BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        return !StarOfSerenity.preventSweetBerryBushDamage(pEntity) && original.call(pEntity, pLevel, damageSource, amount);
    }
}
