package net.blay09.mods.littlejoys.client;

import net.blay09.mods.balm.client.particle.BalmParticleProviderRegistrar;
import net.blay09.mods.balm.client.renderer.chunk.BalmBlockRenderTypeRegistrar;
import net.blay09.mods.balm.client.renderer.entity.BalmEntityRendererRegistrar;
import net.blay09.mods.littlejoys.client.entity.DropRushItemRenderer;
import net.blay09.mods.littlejoys.entity.ModEntities;
import net.blay09.mods.littlejoys.particle.ModParticles;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class ModRenderers {

    public static void initialize(BalmEntityRendererRegistrar renderers) {
        renderers.register(ModEntities.dropRushItem, DropRushItemRenderer::new);
    }

    public static void initialize(BalmParticleProviderRegistrar renderers) {
        renderers.register(ModParticles.goldRush, SuspendedTownParticle.HappyVillagerProvider::new);
        renderers.register(ModParticles.fishingSpot, SuspendedTownParticle.HappyVillagerProvider::new);
    }

    public static void initialize(BalmBlockRenderTypeRegistrar renderers) {
        renderers.setRenderLayer(ModBlocks.digSpot, ChunkSectionLayer.CUTOUT);
        renderers.setRenderLayer(ModBlocks.fishingSpot, ChunkSectionLayer.CUTOUT);
    }
}
