package net.blay09.mods.littlejoys.sound;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static Holder<SoundEvent> goldRush;
    public static Holder<SoundEvent> dropRushStart;
    public static Holder<SoundEvent> dropRush;
    public static Holder<SoundEvent> dropRushStop;

    public static void initialize(BalmRegistrar.Scoped<SoundEvent> sounds) {
        goldRush = sounds.register("gold_rush", SoundEvent::createVariableRangeEvent);
        dropRushStart = sounds.register("drop_rush_start", SoundEvent::createVariableRangeEvent);
        dropRush = sounds.register("drop_rush", SoundEvent::createVariableRangeEvent);
        dropRushStop = sounds.register("drop_rush_stop", SoundEvent::createVariableRangeEvent);
    }
}
