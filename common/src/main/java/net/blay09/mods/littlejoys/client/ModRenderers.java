package net.blay09.mods.littlejoys.client;

import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.littlejoys.client.entity.DropRushItemRenderer;
import net.blay09.mods.littlejoys.entity.ModEntities;
import net.blay09.mods.littlejoys.particle.ModParticles;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.client.renderer.RenderType;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.entity.EntityType;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModRenderers {

    public static void initialize(BalmRenderers renderers) {
        // Note: To support cutout rendering on all loaders, you must additionally specify `"render_type": "minecraft:cutout"` in your block model JSON.
        renderers.setBlockRenderType(() -> ModBlocks.digSpot, ChunkSectionLayer.CUTOUT);
        renderers.setBlockRenderType(() -> ModBlocks.fishingSpot, ChunkSectionLayer.CUTOUT);

        renderers.registerEntityRenderer(id("drop_rush_item"), () -> (EntityType) ModEntities.dropRushItem.get(), DropRushItemRenderer::new);

        renderers.registerParticleProvider(id("gold_rush"), () -> ModParticles.goldRush, SuspendedTownParticle.HappyVillagerProvider::new);
        renderers.registerParticleProvider(id("fishing_spot"), () -> ModParticles.fishingSpot, SuspendedTownParticle.HappyVillagerProvider::new);
    }
}
