package net.blay09.mods.littlejoys.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.littlejoys.entity.DropRushItemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Objects;
import java.util.UUID;

public class DropRushItemRenderer extends ItemEntityRenderer {
    public DropRushItemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntityRenderState renderState, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        final var player = Minecraft.getInstance().player;
        final var target = renderState instanceof DropRushItemEntityRenderState state ? state.getTarget() : null;
        final var mine = player != null && Objects.equals(target, player.getUUID());
        if (mine) {
            super.render(renderState, poseStack, buffer, packedLight);
        } else {
            // TODO RenderSystem.setShaderColor(1f, 1f, 1f, 0.25f);
            super.render(renderState, poseStack, buffer, packedLight);
            // TODO Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        }
    }

    @Override
    public ItemEntityRenderState createRenderState() {
        return new DropRushItemEntityRenderState();
    }

    @Override
    public void extractRenderState(ItemEntity itemEntity, ItemEntityRenderState renderState, float partialTicks) {
        super.extractRenderState(itemEntity, renderState, partialTicks);
        if (itemEntity instanceof DropRushItemEntity dropRushItemEntity && renderState instanceof DropRushItemEntityRenderState dropRushItemEntityRenderState) {
            dropRushItemEntityRenderState.setTarget(dropRushItemEntity.getTarget());
        }
    }

    private static class DropRushItemEntityRenderState extends ItemEntityRenderState {
        private UUID target;

        public UUID getTarget() {
            return target;
        }

        public void setTarget(UUID target) {
            this.target = target;
        }
    }
}
