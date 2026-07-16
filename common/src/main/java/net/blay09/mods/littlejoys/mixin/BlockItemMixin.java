package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.blessing.StarOfProsperity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "place", at = @At("RETURN"))
    private void place(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue().consumesAction() && placeContext.getPlayer() instanceof ServerPlayer player) {
            StarOfProsperity.tryBonemealPlacedBlock(player, placeContext.getClickedPos());
        }
    }
}
