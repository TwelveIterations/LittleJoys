package net.blay09.mods.littlejoys.sound;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static Holder<SoundEvent> blessingUsed;
    public static Holder<SoundEvent> goldRush;
    public static Holder<SoundEvent> dropRushStart;
    public static Holder<SoundEvent> dropRush;
    public static Holder<SoundEvent> dropRushStop;
    public static Holder<SoundEvent> fallenStar;
    public static Holder<SoundEvent> fallenStarBlessing;
    public static Holder<SoundEvent> fallenStarLand;

    public static void initialize(BalmRegistrar.Scoped<SoundEvent> sounds) {
        blessingUsed = sounds.register("blessing_used", SoundEvent::createVariableRangeEvent).asHolder();
        goldRush = sounds.register("gold_rush", SoundEvent::createVariableRangeEvent).asHolder();
        dropRushStart = sounds.register("drop_rush_start", SoundEvent::createVariableRangeEvent).asHolder();
        dropRush = sounds.register("drop_rush", SoundEvent::createVariableRangeEvent).asHolder();
        dropRushStop = sounds.register("drop_rush_stop", SoundEvent::createVariableRangeEvent).asHolder();
        fallenStar = sounds.register("fallen_star", SoundEvent::createVariableRangeEvent).asHolder();
        fallenStarBlessing = sounds.register("fallen_star_blessing", SoundEvent::createVariableRangeEvent).asHolder();
        fallenStarLand = sounds.register("fallen_star_land", SoundEvent::createVariableRangeEvent).asHolder();
    }
}
