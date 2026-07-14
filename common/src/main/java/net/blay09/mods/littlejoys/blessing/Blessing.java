package net.blay09.mods.littlejoys.blessing;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public enum Blessing {
    STAR_OF_ABUNDANCE("star_of_abundance", () -> ModMobEffects.starOfAbundance, 1),
    STAR_OF_VITALITY("star_of_vitality", () -> ModMobEffects.starOfVitality, 1);

    private final String id;
    private final Supplier<Holder<MobEffect>> effect;
    private final int defaultUses;

    Blessing(String id, Supplier<Holder<MobEffect>> effect, int defaultUses) {
        this.id = id;
        this.effect = effect;
        this.defaultUses = defaultUses;
    }

    public String id() {
        return id;
    }

    public Holder<MobEffect> effect() {
        return effect.get();
    }

    public int defaultUses() {
        return defaultUses;
    }

    public static Optional<Blessing> byId(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        final var normalizedId = id.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(it -> it.id.equals(normalizedId))
                .findFirst();
    }
}
