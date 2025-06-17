package net.blay09.mods.littlejoys.fabric.datagen;

import net.blay09.mods.littlejoys.poi.ModPoiTypes;
import net.blay09.mods.littlejoys.tag.ModPoiTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModPoiTypeTagProvider extends KeyTagProvider<PoiType> {
    public ModPoiTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, provider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(ModPoiTypeTags.DIG_SPOTS).add(ModPoiTypes.DIG_SPOT);
        tag(ModPoiTypeTags.FISHING_SPOTS).add(ModPoiTypes.FISHING_SPOT);
    }
}
