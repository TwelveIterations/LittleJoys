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
    }
}
