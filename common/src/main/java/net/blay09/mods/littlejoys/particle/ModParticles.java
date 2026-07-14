package net.blay09.mods.littlejoys.particle;

import net.blay09.mods.balm.core.particles.BalmParticleTypeRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;

public class ModParticles {
    public static Holder<SimpleParticleType> goldRush;
    public static Holder<SimpleParticleType> fallenStar;
    public static Holder<SimpleParticleType> fallenStarTrail;
    public static Holder<SimpleParticleType> fishingSpot;

    public static void initialize(BalmParticleTypeRegistrar particles) {
        goldRush = particles.register("gold_rush", true).asHolder();
        fallenStar = particles.register("fallen_star", true).asHolder();
        fallenStarTrail = particles.register("fallen_star_trail", true).asHolder();
        fishingSpot = particles.register("fishing_spot", true).asHolder();
    }
}
