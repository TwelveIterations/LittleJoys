package net.blay09.mods.littlejoys.fabric.datagen;

import net.blay09.mods.littlejoys.tag.ModEntityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityTypeIds;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends FabricTagsProvider.EntityTypeTagsProvider {
    public ModEntityTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        final var calmedBySerenity = builder(ModEntityTags.CALMED_BY_SERENITY);
        calmedBySerenity.add(EntityTypeIds.SLIME);
        calmedBySerenity.add(EntityTypeIds.MAGMA_CUBE);

        final var starOfAbundanceMobs = builder(ModEntityTags.STAR_OF_ABUNDANCE_MOBS);
        starOfAbundanceMobs.add(EntityTypeIds.BLAZE);
        starOfAbundanceMobs.add(EntityTypeIds.BOGGED);
        starOfAbundanceMobs.add(EntityTypeIds.BREEZE);
        starOfAbundanceMobs.add(EntityTypeIds.CAVE_SPIDER);
        starOfAbundanceMobs.add(EntityTypeIds.PIG);
        starOfAbundanceMobs.add(EntityTypeIds.COW);
        starOfAbundanceMobs.add(EntityTypeIds.CHICKEN);
        starOfAbundanceMobs.add(EntityTypeIds.CREEPER);
        starOfAbundanceMobs.add(EntityTypeIds.DROWNED);
        starOfAbundanceMobs.add(EntityTypeIds.ELDER_GUARDIAN);
        starOfAbundanceMobs.add(EntityTypeIds.ENDERMAN);
        starOfAbundanceMobs.add(EntityTypeIds.GHAST);
        starOfAbundanceMobs.add(EntityTypeIds.GLOW_SQUID);
        starOfAbundanceMobs.add(EntityTypeIds.GUARDIAN);
        starOfAbundanceMobs.add(EntityTypeIds.HOGLIN);
        starOfAbundanceMobs.add(EntityTypeIds.HUSK);
        starOfAbundanceMobs.add(EntityTypeIds.MAGMA_CUBE);
        starOfAbundanceMobs.add(EntityTypeIds.MOOSHROOM);
        starOfAbundanceMobs.add(EntityTypeIds.PHANTOM);
        starOfAbundanceMobs.add(EntityTypeIds.SHEEP);
        starOfAbundanceMobs.add(EntityTypeIds.RABBIT);
        starOfAbundanceMobs.add(EntityTypeIds.SALMON);
        starOfAbundanceMobs.add(EntityTypeIds.SHULKER);
        starOfAbundanceMobs.add(EntityTypeIds.SKELETON);
        starOfAbundanceMobs.add(EntityTypeIds.SLIME);
        starOfAbundanceMobs.add(EntityTypeIds.SPIDER);
        starOfAbundanceMobs.add(EntityTypeIds.SQUID);
        starOfAbundanceMobs.add(EntityTypeIds.STRAY);
        starOfAbundanceMobs.add(EntityTypeIds.WITCH);
        starOfAbundanceMobs.add(EntityTypeIds.WITHER_SKELETON);
        starOfAbundanceMobs.add(EntityTypeIds.ZOMBIE);
    }
}
