package net.blay09.mods.littlejoys.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static Holder<BlockEntityType<DigSpotBlockEntity>> digSpot;
    public static Holder<BlockEntityType<FishingSpotBlockEntity>> fishingSpot;

    public static void initialize(BalmBlockEntityTypeRegistrar blockEntities) {
        digSpot = blockEntities.register("dig_spot", DigSpotBlockEntity::new, ModBlocks.digSpot).asHolder();
        fishingSpot = blockEntities.register("fishing_spot", FishingSpotBlockEntity::new, ModBlocks.fishingSpot).asHolder();
    }
}
