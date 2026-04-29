package net.blay09.mods.littlejoys.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.tag.ModBlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        builder(ModBlockTags.DIG_SPOTS).add(ModBlocks.digSpot.asResourceKey());
        builder(ModBlockTags.FISHING_SPOTS).add(ModBlocks.fishingSpot.asResourceKey());
    }
}
