package net.blay09.mods.littlejoys.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
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
        builder(ModItemTags.CAN_TRIGGER_GOLDRUSH).addOptionalTag(ItemTags.PICKAXES);
        getOrCreateRawBuilder(ModItemTags.CANNOT_TRIGGER_GOLDRUSH).addOptionalTag(Identifier.fromNamespaceAndPath("justhammers", "hammer"));
        builder(ModItemTags.STAR_OF_ABUNDANCE_ITEMS)
                .add(ItemIds.COAL)
                .add(ItemIds.DIAMOND)
                .add(ItemIds.EMERALD)
                .add(ItemIds.LAPIS_LAZULI)
                .add(ItemIds.QUARTZ)
                .add(ItemIds.RAW_COPPER)
                .add(ItemIds.RAW_GOLD)
                .add(ItemIds.RAW_IRON)
                .add(BlockItemIds.REDSTONE_DUST);
        final var rawBuilder = getOrCreateRawBuilder(ModItemTags.STAR_OF_ABUNDANCE_ITEMS);
        rawBuilder.addOptionalTag(Identifier.fromNamespaceAndPath("c", "gems"));
        rawBuilder.addOptionalTag(Identifier.fromNamespaceAndPath("c", "raw_materials"));
    }
}
