package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.effect.CustomMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModMobEffects {

    public static Holder<MobEffect> starOfAbundance;
    public static Holder<MobEffect> starOfFate;
    public static Holder<MobEffect> starOfProsperity;
    public static Holder<MobEffect> starOfSerenity;

    public static void initialize(BalmRegistrar.Scoped<MobEffect> effects) {
        starOfAbundance = effects.register("star_of_abundance", _ -> new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xFFD35A));
        starOfFate = effects.register("star_of_fate", _ -> new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xFFEF8A));
        starOfProsperity = effects.register("star_of_prosperity", _ -> new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xF05F6D));
        starOfSerenity = effects.register("star_of_serenity", _ -> new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xA7E7FF));
    }
}
