package net.blay09.mods.littlejoys.fabric.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.Block;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.tag.ModBlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture, (block) -> block.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(ModBlockTags.DIG_SPOTS).add(ModBlocks.digSpot.asBlock());
        tag(ModBlockTags.FISHING_SPOTS).add(ModBlocks.fishingSpot.asBlock());
    }
}
