package net.blay09.mods.littlejoys.registry;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModDynamicRegistries {

    public static final ResourceKey<Registry<DigSpotEvent>> DIG_SPOT = ResourceKey.createRegistryKey(id("dig_spot"));
    public static final ResourceKey<Registry<FishingSpotEvent>> FISHING_SPOT = ResourceKey.createRegistryKey(id("fishing_spot"));
    public static final ResourceKey<Registry<GoldRushEvent>> GOLD_RUSH = ResourceKey.createRegistryKey(id("gold_rush"));
    public static final ResourceKey<Registry<DropRushEvent>> DROP_RUSH = ResourceKey.createRegistryKey(id("drop_rush"));

    public static void initialize(BalmRegistrar registrar) {
        registrar.createDynamicRegistry(DIG_SPOT, DigSpotEvent.CODEC);
        registrar.createDynamicRegistry(FISHING_SPOT, FishingSpotEvent.CODEC);
        registrar.createDynamicRegistry(GOLD_RUSH, GoldRushEvent.CODEC);
        registrar.createDynamicRegistry(DROP_RUSH, DropRushEvent.CODEC);
    }
}
