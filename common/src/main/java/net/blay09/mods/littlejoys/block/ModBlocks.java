package net.blay09.mods.littlejoys.block;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static DeferredBlock digSpot;
    public static DeferredBlock fishingSpot;

    public static void initialize(BalmBlockRegistrar blocks) {
        blocks.enableBlockDescriptionPrefixForItems();

        digSpot = blocks.register("dig_spot", DigSpotBlock::new, it -> it).withDefaultItem().asDeferredBlock();
        fishingSpot = blocks.register("fishing_spot", FishingSpotBlock::new, BlockBehaviour.Properties::noLootTable).withDefaultItem().asDeferredBlock();
    }

}
