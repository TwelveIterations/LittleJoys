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

public class FallenStarParticle extends SingleQuadParticle {

    private static final float RAINBOW_TRAIL_CHANCE = 0.08f;
    private static final ColorStop[] TRAIL_GRADIENT = {
            new ColorStop(0f, 1f, 0.95f, 0.15f),
            new ColorStop(0.5f, 0.65f, 0.2f, 1f),
            new ColorStop(1f, 0.2f, 0.55f, 1f)
    };
    private static final ColorStop[] RAINBOW_TRAIL_GRADIENT = {
            new ColorStop(0f, 1f, 0.15f, 0.15f),
            new ColorStop(0.2f, 1f, 0.6f, 0.1f),
            new ColorStop(0.4f, 1f, 0.95f, 0.15f),
            new ColorStop(0.6f, 0.2f, 0.9f, 0.25f),
            new ColorStop(0.8f, 0.2f, 0.55f, 1f),
            new ColorStop(1f, 0.65f, 0.2f, 1f)
    };

    private final ColorStop[] gradient;

    private FallenStarParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, x, y, z, xd, yd, zd, sprite);
        gradient = random.nextFloat() < RAINBOW_TRAIL_CHANCE ? RAINBOW_TRAIL_GRADIENT : TRAIL_GRADIENT;
        hasPhysics = false;
        friction = 0.85f;
        quadSize = 0.35f + random.nextFloat() * 0.15f;
        lifetime = 20 + random.nextInt(10);
        setAlpha(0.9f);
        updateColor();
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
    public void tick() {
        super.tick();
        updateColor();
    }

    private void updateColor() {
        final var ageProgress = Mth.clamp(age / (float) lifetime, 0f, 1f);
        for (int i = 1; i < gradient.length; i++) {
            final var from = gradient[i - 1];
            final var to = gradient[i];
            if (ageProgress <= to.progress()) {
                final var progress = (ageProgress - from.progress()) / (to.progress() - from.progress());
                setGradientColor(from, to, progress);
                return;
            }
        }
        setGradientColor(gradient[gradient.length - 1]);
    }

    private void setGradientColor(ColorStop from, ColorStop to, float progress) {
        setColor(
                Mth.lerp(progress, from.red(), to.red()),
                Mth.lerp(progress, from.green(), to.green()),
                Mth.lerp(progress, from.blue(), to.blue()));
    }

    private void setGradientColor(ColorStop color) {
        setColor(color.red(), color.green(), color.blue());
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

    private record ColorStop(float progress, float red, float green, float blue) {
    }
}
