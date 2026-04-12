package net.blay09.mods.littlejoys.stats;

import net.blay09.mods.balm.api.stats.BalmStats;
import net.minecraft.resources.ResourceLocation;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModStats {
    public static final ResourceLocation digSpotsDug = id("dig_spots");
    public static final ResourceLocation fishingSpotsFished = id("fishing_spots");
    public static final ResourceLocation goldRushesTriggered = id("gold_rushes");
    public static final ResourceLocation dropRushesTriggered = id("drop_rushes");
    public static final ResourceLocation dropRushesCompleted = id("drop_rushes_completed");

    public static void initialize(BalmStats stats) {
        stats.registerCustomStat(digSpotsDug);
        stats.registerCustomStat(fishingSpotsFished);
        stats.registerCustomStat(goldRushesTriggered);
        stats.registerCustomStat(dropRushesTriggered);
        stats.registerCustomStat(dropRushesCompleted);
    }
}
