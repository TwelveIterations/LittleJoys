package net.blay09.mods.littlejoys.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModItemTags {
    public static final TagKey<Item> CAN_TRIGGER_GOLDRUSH = TagKey.create(Registries.ITEM, id("can_trigger_goldrush"));
    public static final TagKey<Item> CANNOT_TRIGGER_GOLDRUSH = TagKey.create(Registries.ITEM, id("cannot_trigger_goldrush"));
    public static final TagKey<Item> STAR_OF_ABUNDANCE_ITEMS = TagKey.create(Registries.ITEM, id("star_of_abundance_items"));
}
