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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public record CanSpawnFishingSpot(HolderSet<Fluid> fluid) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("can_spawn_fishing_spot");
    public static final MapCodec<CanSpawnFishingSpot> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HolderSetCodec.create(Registries.FLUID, BuiltInRegistries.FLUID.holderByNameCodec(), false).fieldOf("fluid").forGetter(CanSpawnFishingSpot::fluid)
    ).apply(instance, CanSpawnFishingSpot::new));

    @Override
    public Either<Boolean, Throwable> apply(ShogiContext context) {
        final var level = context.requireLevel();
        final var pos = context.requireBlockPos();
        final var state = level.getBlockState(pos.below());
        final var fluidState = state.getFluidState();
        return Either.left(fluidState.is(fluid) && fluidState.isSource()
                && !state.getValueOrElse(BlockStateProperties.WATERLOGGED, false)
                && level.canSeeSky(pos));
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }
}
