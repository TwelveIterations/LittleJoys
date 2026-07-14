package net.blay09.mods.littlejoys.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class FallenStarParticle extends SingleQuadParticle {

    private FallenStarParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, x, y, z, xd, yd, zd, sprite);
        hasPhysics = false;
        friction = 0.85f;
        quadSize = 0.35f + random.nextFloat() * 0.15f;
        lifetime = 20 + random.nextInt(10);
        setAlpha(0.9f);
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        final var ageProgress = (age + partialTicks) / lifetime;
        return quadSize * (1f - ageProgress * 0.5f);
    }

    @Override
    public int getLightCoords(float partialTicks) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new FallenStarParticle(level, x, y, z, xAux, yAux, zAux, sprite.get(random));
        }
    }
}
