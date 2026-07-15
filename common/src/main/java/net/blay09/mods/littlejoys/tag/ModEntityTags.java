package net.blay09.mods.littlejoys.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModEntityTags {
    public static final TagKey<EntityType<?>> CALMED_BY_SERENITY = TagKey.create(Registries.ENTITY_TYPE, id("calmed_by_serenity"));
}
