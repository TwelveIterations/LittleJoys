package net.blay09.mods.littlejoys.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModBlockTags {
    public static final TagKey<Block> DIG_SPOTS = TagKey.create(Registries.BLOCK, id("dig_spots"));
    public static final TagKey<Block> FISHING_SPOTS = TagKey.create(Registries.BLOCK, id("fishing_spots"));
    public static final TagKey<Block> STAR_OF_ABUNDANCE_BLOCKS = TagKey.create(Registries.BLOCK, id("star_of_abundance_blocks"));
}
