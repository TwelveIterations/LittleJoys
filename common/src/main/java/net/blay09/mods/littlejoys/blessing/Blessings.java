package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class Blessings {
    public static final ResourceKey<Registry<Blessing>> REGISTRY_KEY = ResourceKey.createRegistryKey(id("blessing"));
    private static Registry<Blessing> registry;
    private static Map<Holder<MobEffect>, Blessing> byEffect = Map.of();
    private static boolean effectLookupInitialized;

    public static Blessing STAR_OF_ABUNDANCE;
    public static Blessing STAR_OF_FATE;
    public static Blessing STAR_OF_PROSPERITY;
    public static Blessing STAR_OF_SERENITY;

    public static void initialize(BalmRegistrar registrar) {
        registry = registrar.createCustomRegistry(REGISTRY_KEY);

        STAR_OF_ABUNDANCE = registrar.register(key("star_of_abundance"), it -> new Blessing(it, () -> ModMobEffects.starOfAbundance, 77)).value();
        STAR_OF_FATE = registrar.register(key("star_of_fate"), it -> new Blessing(it, () -> ModMobEffects.starOfFate, 77)).value();
        STAR_OF_PROSPERITY = registrar.register(key("star_of_prosperity"), it -> new Blessing(it, () -> ModMobEffects.starOfProsperity, 77)).value();
        STAR_OF_SERENITY = registrar.register(key("star_of_serenity"), it -> new Blessing(it, () -> ModMobEffects.starOfSerenity, 77)).value();

        byEffect = Map.of();
        effectLookupInitialized = false;
    }

    private static ResourceKey<Blessing> key(String identifier) {
        return ResourceKey.create(REGISTRY_KEY, id(identifier));
    }

    public static Iterable<Blessing> all() {
        return registry;
    }

    public static Blessing random(RandomSource random) {
        return registry.getRandom(random)
                .orElseThrow(() -> new IllegalStateException("No blessings have been registered"))
                .value();
    }

    public static Optional<Blessing> byId(Identifier id) {
        return registry.getOptional(id);
    }

    public static Optional<Blessing> byEffect(Holder<MobEffect> effect) {
        if (!effectLookupInitialized) {
            initializeEffectLookup();
        }

        return Optional.ofNullable(byEffect.get(effect));
    }

    private static void initializeEffectLookup() {
        final var result = new HashMap<Holder<MobEffect>, Blessing>();
        var complete = true;
        for (final var blessing : registry) {
            final var effect = blessing.effect();
            if (effect == null) {
                complete = false;
                continue;
            }

            result.put(effect, blessing);
        }

        byEffect = Map.copyOf(result);
        effectLookupInitialized = complete;
    }
}
