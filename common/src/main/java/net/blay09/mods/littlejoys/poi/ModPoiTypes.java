package net.blay09.mods.littlejoys.poi;

import net.blay09.mods.balm.world.entity.ai.village.poi.BalmPoiTypeRegistrar;
import net.blay09.mods.littlejoys.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.Set;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModPoiTypes {

    public static final ResourceKey<PoiType> DIG_SPOT = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id("dig_spot"));
    public static final ResourceKey<PoiType> FISHING_SPOT = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id("fishing_spot"));

    public static Holder<PoiType> digSpot;
    public static Holder<PoiType> fishingSpot;

    public static void initialize(BalmPoiTypeRegistrar worldGen) {
        digSpot = worldGen.register(DIG_SPOT.identifier().getPath(), () -> new PoiType(Set.of(ModBlocks.digSpot.defaultBlockState()), 1, 1));
        fishingSpot = worldGen.register(FISHING_SPOT.identifier().getPath(), () -> new PoiType(Set.of(ModBlocks.fishingSpot.defaultBlockState()), 1, 1));
    }
}
