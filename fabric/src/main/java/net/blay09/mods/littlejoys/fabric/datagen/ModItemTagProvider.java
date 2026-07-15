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
                .add(ItemIds.ARROW)
                .add(ItemIds.BEEF)
                .add(ItemIds.BONE)
                .add(ItemIds.CHICKEN)
                .add(ItemIds.COD)
                .add(ItemIds.COOKED_BEEF)
                .add(ItemIds.COOKED_CHICKEN)
                .add(ItemIds.COOKED_COD)
                .add(ItemIds.COOKED_MUTTON)
                .add(ItemIds.COOKED_PORKCHOP)
                .add(ItemIds.COOKED_RABBIT)
                .add(ItemIds.COOKED_SALMON)
                .add(ItemIds.ENDER_PEARL)
                .add(ItemIds.FEATHER)
                .add(ItemIds.GHAST_TEAR)
                .add(ItemIds.GLOW_INK_SAC)
                .add(ItemIds.GLOWSTONE_DUST)
                .add(ItemIds.GUNPOWDER)
                .add(ItemIds.INK_SAC)
                .add(ItemIds.LEATHER)
                .add(ItemIds.MAGMA_CREAM)
                .add(ItemIds.MUTTON)
                .add(ItemIds.NAUTILUS_SHELL)
                .add(ItemIds.PHANTOM_MEMBRANE)
                .add(ItemIds.PORKCHOP)
                .add(ItemIds.PUFFERFISH)
                .add(ItemIds.RABBIT)
                .add(ItemIds.RABBIT_FOOT)
                .add(ItemIds.RABBIT_HIDE)
                .add(ItemIds.ROTTEN_FLESH)
                .add(ItemIds.SALMON)
                .add(ItemIds.SLIME_BALL)
                .add(ItemIds.SPIDER_EYE)
                .add(ItemIds.STICK)
                .add(ItemIds.SUGAR)
                .add(ItemIds.TROPICAL_FISH);
        builder(ModItemTags.STAR_OF_ABUNDANCE_ITEMS).addOptionalTag(ItemTags.WOOL);
        final var rawBuilder = getOrCreateRawBuilder(ModItemTags.STAR_OF_ABUNDANCE_ITEMS);
        rawBuilder.addElement(Identifier.withDefaultNamespace("string"));
        rawBuilder.addOptionalTag(Identifier.fromNamespaceAndPath("c", "foods/raw_meat"));
    }
}
