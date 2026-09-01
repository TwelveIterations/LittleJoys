package net.blay09.mods.littlejoys.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.littlejoys.entity.FallenStarEntity;
import net.blay09.mods.littlejoys.item.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class FallenStarRenderer extends EntityRenderer<FallenStarEntity, FallenStarRenderer.FallenStarRenderState> {

    private final ItemModelResolver itemModelResolver;
    private @Nullable ItemStack fallenStarStack;

    public FallenStarRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemModelResolver = context.getItemModelResolver();
        shadowRadius = 0f;
        shadowStrength = 0f;
    }

    @Override
    public FallenStarRenderState createRenderState() {
        return new FallenStarRenderState();
    }

    @Override
    public void extractRenderState(FallenStarEntity fallenStar, FallenStarRenderState renderState, float partialTicks) {
        super.extractRenderState(fallenStar, renderState, partialTicks);
        renderState.bobOffset = 0f;
        renderState.yRot = Mth.rotLerp(partialTicks, fallenStar.yRotO, fallenStar.getYRot());
        if (fallenStarStack == null) {
            fallenStarStack = ModItems.fallenStar.createStack();
        }
        itemModelResolver.updateForNonLiving(renderState.item, fallenStarStack, ItemDisplayContext.GROUND, fallenStar);
    }

    @Override
    public void submit(FallenStarRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.item.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        final var modelBounds = renderState.item.getModelBoundingBox();
        final var scale = 2f;
        final var yOffset = (float) -modelBounds.minY * scale + 0.0625f;
        final var bob = Mth.sin(renderState.ageInTicks / 10f) * 0.1f + 0.1f;
        poseStack.translate(0f, renderState.bobOffset + bob + yOffset, 0f);
        poseStack.rotate(Axis.YP.rotationDegrees(-renderState.yRot));
        poseStack.scale(scale, scale, scale);
        renderState.item.submit(poseStack, nodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
        poseStack.popPose();
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
    }

    public static class FallenStarRenderState extends EntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public float bobOffset;
        public float yRot;
    }
}
