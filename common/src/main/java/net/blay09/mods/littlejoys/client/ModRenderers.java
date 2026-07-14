package net.blay09.mods.littlejoys.client;

import net.blay09.mods.balm.client.particle.BalmParticleProviderRegistrar;
import net.blay09.mods.balm.client.renderer.entity.BalmEntityRendererRegistrar;
import net.blay09.mods.littlejoys.client.entity.DropRushItemRenderer;
import net.blay09.mods.littlejoys.client.entity.FallenStarRenderer;
import net.blay09.mods.littlejoys.entity.ModEntities;
import net.blay09.mods.littlejoys.particle.ModParticles;
import net.minecraft.client.particle.SuspendedTownParticle;

public class ModRenderers {

    public static void initialize(BalmEntityRendererRegistrar renderers) {
        renderers.register(ModEntities.dropRushItem, DropRushItemRenderer::new);
        renderers.register(ModEntities.fallenStar, FallenStarRenderer::new);
    }

    public static void initialize(BalmParticleProviderRegistrar renderers) {
        renderers.register(ModParticles.goldRush, SuspendedTownParticle.HappyVillagerProvider::new);
        renderers.register(ModParticles.fallenStar, SuspendedTownParticle.HappyVillagerProvider::new);
        renderers.register(ModParticles.fishingSpot, SuspendedTownParticle.HappyVillagerProvider::new);
    }

}
