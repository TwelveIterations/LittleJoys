package net.blay09.mods.littlejoys.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.registry.condition.LittleJoysRules;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record DigSpotEvent(ShogiEffect<?> condition, ResourceKey<LootTable> lootTable,
                           int weight) {

    public static final Codec<DigSpotEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LittleJoysRules.SCOPE.getEffectCodec().fieldOf("condition").forGetter(DigSpotEvent::condition),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(DigSpotEvent::lootTable),
            Codec.INT.fieldOf("weight").orElse(1).forGetter(DigSpotEvent::weight)
    ).apply(instance, DigSpotEvent::new));

}
