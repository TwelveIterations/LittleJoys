package net.blay09.mods.littlejoys.registry.condition;

import com.mojang.datafixers.util.Either;
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

import static net.blay09.mods.littlejoys.LittleJoys.id;

public record CanSpawnDigSpot(HolderSet<Block> block) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("can_spawn_dig_spot");
    public static final MapCodec<CanSpawnDigSpot> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HolderSetCodec.create(Registries.BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec(), false).fieldOf("block").forGetter(CanSpawnDigSpot::block)
    ).apply(instance, CanSpawnDigSpot::new));

    @Override
    public Either<Boolean, Throwable> apply(ShogiContext context) {
        return Either.left(context.requireLevel().getBlockState(context.requireBlockPos().below()).is(block));
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }
}
