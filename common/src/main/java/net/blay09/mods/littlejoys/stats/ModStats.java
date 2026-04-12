package net.blay09.mods.littlejoys.stats;

import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModStats {
    public static final Identifier digSpotsDug = id("dig_spots");
    public static final Identifier fishingSpotsFished = id("fishing_spots");
    public static final Identifier goldRushesTriggered = id("gold_rushes");
    public static final Identifier dropRushesTriggered = id("drop_rushes");
    public static final Identifier dropRushesCompleted = id("drop_rushes_completed");

    public static void initialize(BalmCustomStatRegistrar stats) {
        stats.register(digSpotsDug, StatFormatter.DEFAULT);
        stats.register(fishingSpotsFished, StatFormatter.DEFAULT);
        stats.register(goldRushesTriggered, StatFormatter.DEFAULT);
        stats.register(dropRushesTriggered, StatFormatter.DEFAULT);
        stats.register(dropRushesCompleted, StatFormatter.DEFAULT);
    }
}
