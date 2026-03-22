package net.blay09.mods.littlejoys.fabric.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.digSpot.asBlock());
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.fishingSpot.asBlock());
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }

}
