package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.effect.CustomMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModMobEffects {

    public static Holder<MobEffect> starOfAbundance;
    public static Holder<MobEffect> starOfFortune;
    public static Holder<MobEffect> starOfVitality;

    public static void initialize(BalmRegistrar.Scoped<MobEffect> effects) {
        starOfAbundance = effects.register("star_of_abundance", _ -> new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xFFD35A));
        starOfFortune = effects.register("star_of_fortune", _ -> new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xFFEF8A));
        starOfVitality = effects.register("star_of_vitality", _ -> new CustomMobEffect(MobEffectCategory.BENEFICIAL, 0xF05F6D));
    }
}
