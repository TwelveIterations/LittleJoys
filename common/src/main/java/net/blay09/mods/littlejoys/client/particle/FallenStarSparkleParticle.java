package net.blay09.mods.littlejoys.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FallenStarSparkleParticle extends SingleQuadParticle {

    private FallenStarSparkleParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, x, y, z, xd, yd, zd, sprite);
        hasPhysics = false;
        friction = 0.96f;
        quadSize = 0.12f + random.nextFloat() * 0.08f;
        lifetime = 24 + random.nextInt(18);
        setColor(1f, 0.82f + random.nextFloat() * 0.12f, 0.25f + random.nextFloat() * 0.15f);
        setAlpha(0.95f);
        setParticleSpeed(xd, yd, zd);
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        final var ageProgress = Mth.clamp(age / (float) lifetime, 0f, 1f);
        setAlpha(0.95f * (1f - ageProgress));
    }

    @Override
    public float getQuadSize(float partialTicks) {
        final var ageProgress = Mth.clamp((age + partialTicks) / lifetime, 0f, 1f);
        final var twinkle = 0.75f + 0.25f * Mth.sin((age + partialTicks) * 0.6f);
        return quadSize * twinkle * (1f - ageProgress * 0.35f);
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
            return new FallenStarSparkleParticle(level, x, y, z, xAux, yAux, zAux, sprite.get(random));
        }
    }
}
