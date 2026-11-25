package net.blay09.mods.littlejoys.registry.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Map;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public record CanTriggerDropRush(HolderSet<Block> block, Map<String, String> properties) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("can_trigger_drop_rush");
    public static final MapCodec<CanTriggerDropRush> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HolderSetCodec.create(Registries.BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec(), false).fieldOf("block").forGetter(CanTriggerDropRush::block),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("properties", Map.of()).forGetter(CanTriggerDropRush::properties)
    ).apply(instance, CanTriggerDropRush::new));

    @Override
    public Either<Boolean, Throwable> apply(ShogiContext context) {
        final var state = context.requireLevel().getBlockState(context.requireBlockPos());
        return Either.left(state.is(block) && BlockPropertiesUtil.matches(context.requireBlockState(), properties));
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }
}
