package net.blay09.mods.littlejoys.client.entity;

import net.blay09.mods.littlejoys.entity.DropRushItemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Objects;

public class DropRushItemRenderer extends ItemEntityRenderer {
    public DropRushItemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(ItemEntity itemEntity, ItemEntityRenderState renderState, float partialTicks) {
        super.extractRenderState(itemEntity, renderState, partialTicks);
        if (itemEntity instanceof DropRushItemEntity dropRushItemEntity) {
            final var player = Minecraft.getInstance().player;
            final var mine = player != null && Objects.equals(dropRushItemEntity.getTarget(), player.getUUID());
            if (mine) {
                renderState.outlineColor = 0x3FFFFFFF;
            }
        }
    }

}
