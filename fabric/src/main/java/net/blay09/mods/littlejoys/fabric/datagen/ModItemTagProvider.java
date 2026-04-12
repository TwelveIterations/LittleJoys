package net.blay09.mods.littlejoys.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.blay09.mods.littlejoys.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        valueLookupBuilder(ModItemTags.CAN_TRIGGER_GOLDRUSH).addOptionalTag(ItemTags.PICKAXES);
        getOrCreateRawBuilder(ModItemTags.CANNOT_TRIGGER_GOLDRUSH).addOptionalTag(Identifier.fromNamespaceAndPath("justhammers", "hammer"));
    }
}
