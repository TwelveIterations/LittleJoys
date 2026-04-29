package net.blay09.mods.littlejoys.fabric.datagen;

import net.blay09.mods.littlejoys.poi.ModPoiTypes;
import net.blay09.mods.littlejoys.tag.ModPoiTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.concurrent.CompletableFuture;

public class ModPoiTypeTagProvider extends TagsProvider<PoiType> {
    public ModPoiTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, provider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(ModPoiTypeTags.DIG_SPOTS).add(ModPoiTypes.DIG_SPOT);
        tag(ModPoiTypeTags.FISHING_SPOTS).add(ModPoiTypes.FISHING_SPOT);
    }
}
