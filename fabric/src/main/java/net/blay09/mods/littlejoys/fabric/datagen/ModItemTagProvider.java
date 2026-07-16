package net.blay09.mods.littlejoys.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.blay09.mods.littlejoys.tag.ModItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        valueLookupBuilder(ModItemTags.CAN_TRIGGER_GOLDRUSH).addOptionalTag(ItemTags.PICKAXES);
        getOrCreateRawBuilder(ModItemTags.CANNOT_TRIGGER_GOLDRUSH).addOptionalTag(Identifier.fromNamespaceAndPath("justhammers", "hammer"));
        valueLookupBuilder(ModItemTags.STAR_OF_ABUNDANCE_ITEMS)
                .add(Items.ARROW)
                .add(Items.BEEF)
                .add(Items.BONE)
                .add(Items.CHICKEN)
                .add(Items.COD)
                .add(Items.COOKED_BEEF)
                .add(Items.COOKED_CHICKEN)
                .add(Items.COOKED_COD)
                .add(Items.COOKED_MUTTON)
                .add(Items.COOKED_PORKCHOP)
                .add(Items.COOKED_RABBIT)
                .add(Items.COOKED_SALMON)
                .add(Items.ENDER_PEARL)
                .add(Items.FEATHER)
                .add(Items.GHAST_TEAR)
                .add(Items.GLOW_INK_SAC)
                .add(Items.GLOWSTONE_DUST)
                .add(Items.GUNPOWDER)
                .add(Items.INK_SAC)
                .add(Items.LEATHER)
                .add(Items.MAGMA_CREAM)
                .add(Items.MUTTON)
                .add(Items.NAUTILUS_SHELL)
                .add(Items.PHANTOM_MEMBRANE)
                .add(Items.PORKCHOP)
                .add(Items.PUFFERFISH)
                .add(Items.RABBIT)
                .add(Items.RABBIT_FOOT)
                .add(Items.RABBIT_HIDE)
                .add(Items.ROTTEN_FLESH)
                .add(Items.SALMON)
                .add(Items.SLIME_BALL)
                .add(Items.SPIDER_EYE)
                .add(Items.STICK)
                .add(Items.SUGAR)
                .add(Items.TROPICAL_FISH);
        builder(ModItemTags.STAR_OF_ABUNDANCE_ITEMS).addOptionalTag(ItemTags.WOOL);
        final var rawBuilder = getOrCreateRawBuilder(ModItemTags.STAR_OF_ABUNDANCE_ITEMS);
        rawBuilder.addElement(Identifier.withDefaultNamespace("string"));
        rawBuilder.addOptionalTag(Identifier.fromNamespaceAndPath("c", "foods/raw_meat"));
    }
}
