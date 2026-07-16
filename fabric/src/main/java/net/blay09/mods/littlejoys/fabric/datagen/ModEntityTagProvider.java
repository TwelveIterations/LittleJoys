package net.blay09.mods.littlejoys.fabric.datagen;

import net.blay09.mods.littlejoys.tag.ModEntityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends FabricTagsProvider.EntityTypeTagsProvider {
    public ModEntityTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        final var calmedBySerenity = valueLookupBuilder(ModEntityTags.CALMED_BY_SERENITY);
        calmedBySerenity.add(EntityType.SLIME);
        calmedBySerenity.add(EntityType.MAGMA_CUBE);

        final var starOfAbundanceMobs = valueLookupBuilder(ModEntityTags.STAR_OF_ABUNDANCE_MOBS);
        starOfAbundanceMobs.add(EntityType.BLAZE);
        starOfAbundanceMobs.add(EntityType.BOGGED);
        starOfAbundanceMobs.add(EntityType.BREEZE);
        starOfAbundanceMobs.add(EntityType.CAVE_SPIDER);
        starOfAbundanceMobs.add(EntityType.PIG);
        starOfAbundanceMobs.add(EntityType.COW);
        starOfAbundanceMobs.add(EntityType.CHICKEN);
        starOfAbundanceMobs.add(EntityType.CREEPER);
        starOfAbundanceMobs.add(EntityType.DROWNED);
        starOfAbundanceMobs.add(EntityType.ELDER_GUARDIAN);
        starOfAbundanceMobs.add(EntityType.ENDERMAN);
        starOfAbundanceMobs.add(EntityType.GHAST);
        starOfAbundanceMobs.add(EntityType.GLOW_SQUID);
        starOfAbundanceMobs.add(EntityType.GUARDIAN);
        starOfAbundanceMobs.add(EntityType.HOGLIN);
        starOfAbundanceMobs.add(EntityType.HUSK);
        starOfAbundanceMobs.add(EntityType.MAGMA_CUBE);
        starOfAbundanceMobs.add(EntityType.MOOSHROOM);
        starOfAbundanceMobs.add(EntityType.PHANTOM);
        starOfAbundanceMobs.add(EntityType.SHEEP);
        starOfAbundanceMobs.add(EntityType.RABBIT);
        starOfAbundanceMobs.add(EntityType.SALMON);
        starOfAbundanceMobs.add(EntityType.SHULKER);
        starOfAbundanceMobs.add(EntityType.SKELETON);
        starOfAbundanceMobs.add(EntityType.SLIME);
        starOfAbundanceMobs.add(EntityType.SPIDER);
        starOfAbundanceMobs.add(EntityType.SQUID);
        starOfAbundanceMobs.add(EntityType.STRAY);
        starOfAbundanceMobs.add(EntityType.WITCH);
        starOfAbundanceMobs.add(EntityType.WITHER_SKELETON);
        starOfAbundanceMobs.add(EntityType.ZOMBIE);
    }
}
