package net.blay09.mods.littlejoys.poi;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.Set;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModPoiTypes {

    public static ResourceKey<PoiType> DIG_SPOT = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id("dig_spot"));
    public static ResourceKey<PoiType> FISHING_SPOT = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id("fishing_spot"));

    public static DeferredObject<PoiType> digSpot;
    public static DeferredObject<PoiType> fishingSpot;

    public static void initialize(BalmWorldGen worldGen) {
        digSpot = worldGen.registerPoiType(DIG_SPOT.location(), () -> new PoiType(Set.of(ModBlocks.digSpot.defaultBlockState()), 1, 1));
        fishingSpot = worldGen.registerPoiType(FISHING_SPOT.location(), () -> new PoiType(Set.of(ModBlocks.fishingSpot.defaultBlockState()), 1, 1));
    }
}
