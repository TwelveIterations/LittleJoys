package net.blay09.mods.littlejoys.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModDamageTypeTags {
    public static final TagKey<DamageType> PREVENTED_BY_STAR_OF_SERENITY = TagKey.create(Registries.DAMAGE_TYPE, id("prevented_by_star_of_serenity"));
}
