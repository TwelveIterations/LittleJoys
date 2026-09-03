package net.blay09.mods.littlejoys.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.registry.condition.LittleJoysRules;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record DropRushEvent(ShogiEffect<?> eventCondition, float chanceMultiplier, ResourceKey<LootTable> lootTable,
                            int rolls, float seconds, int range, int weight) {

    public static final Codec<DropRushEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LittleJoysRules.SCOPE.getEffectCodec().fieldOf("eventCondition").forGetter(DropRushEvent::eventCondition),
            Codec.FLOAT.fieldOf("chanceMultiplier").orElse(1f).forGetter(DropRushEvent::chanceMultiplier),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(DropRushEvent::lootTable),
            Codec.INT.fieldOf("rolls").orElse(8).forGetter(DropRushEvent::rolls),
            Codec.FLOAT.fieldOf("seconds").orElse(12.5f).forGetter(DropRushEvent::seconds),
            Codec.INT.fieldOf("range").orElse(8).forGetter(DropRushEvent::range),
            Codec.INT.fieldOf("weight").orElse(1).forGetter(DropRushEvent::weight)
    ).apply(instance, DropRushEvent::new));

}
