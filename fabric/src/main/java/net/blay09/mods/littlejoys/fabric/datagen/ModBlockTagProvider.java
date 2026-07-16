package net.blay09.mods.littlejoys.fabric.datagen;

import net.blay09.mods.littlejoys.block.ModBlocks;
import net.blay09.mods.littlejoys.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        valueLookupBuilder(ModBlockTags.DIG_SPOTS).add(ModBlocks.digSpot.asBlock());
        valueLookupBuilder(ModBlockTags.FISHING_SPOTS).add(ModBlocks.fishingSpot.asBlock());
        valueLookupBuilder(ModBlockTags.STAR_OF_ABUNDANCE_BLOCKS).add(Blocks.NETHER_QUARTZ_ORE);
        final var starOfAbundanceValidBlocks = getOrCreateRawBuilder(ModBlockTags.STAR_OF_ABUNDANCE_BLOCKS);
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("coal_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("copper_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("diamond_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("emerald_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("gold_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("iron_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("lapis_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.withDefaultNamespace("redstone_ores"));
        starOfAbundanceValidBlocks.addOptionalTag(Identifier.fromNamespaceAndPath("c", "ores"));
    }
}
