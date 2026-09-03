package net.blay09.mods.littlejoys.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.registry.condition.LittleJoysRules;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record GoldRushEvent(ShogiEffect<?> eventCondition,
                            float chanceMultiplier,
                            ResourceKey<LootTable> lootTable,
                            float seconds,
                            float maxDropsPerSecond,
                            int weight) {

    public static final Codec<GoldRushEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LittleJoysRules.SCOPE.getEffectCodec().fieldOf("eventCondition").forGetter(GoldRushEvent::eventCondition),
            Codec.FLOAT.fieldOf("chanceMultiplier").orElse(1f).forGetter(GoldRushEvent::chanceMultiplier),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(GoldRushEvent::lootTable),
            Codec.FLOAT.fieldOf("seconds").orElse(7f).forGetter(GoldRushEvent::seconds),
            Codec.FLOAT.fieldOf("maxDropsPerSecond").orElse(-1f).forGetter(GoldRushEvent::maxDropsPerSecond),
            Codec.INT.fieldOf("weight").orElse(1).forGetter(GoldRushEvent::weight)
    ).apply(instance, GoldRushEvent::new));

}
