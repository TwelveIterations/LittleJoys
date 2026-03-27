package net.blay09.mods.littlejoys.mixin;

import net.blay09.mods.littlejoys.entity.DropRushItemEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private ClientLevel level;

    @Inject(method = "handleTakeItemEntity", at = @At("HEAD"))
    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        final var entity = level.getEntity(packet.getItemId());
        if (entity instanceof DropRushItemEntity itemEntity) {
            itemEntity.setPickedUp(true);
        }
    }
}
