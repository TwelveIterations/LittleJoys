package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.recipe.FluidIngredient;
import net.blay09.mods.shogi.Shogi;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public final class LittleJoysRules {
    public static final ShogiScope EVENT_CONDITIONS = Shogi.scope(id("event_conditions"), scope -> {
        scope.setDefaultNamespaces(List.of("littlejoys", "shogi"));
        scope.registerEffect(AboveFluidSource.IDENTIFIER, AboveFluidSource.MAP_CODEC);
        scope.registerEffect(AboveState.IDENTIFIER, AboveState.MAP_CODEC, List.of("state"));
        scope.registerEffect(IsTool.IDENTIFIER, IsTool.MAP_CODEC, List.of("item"));
    });
    public static final ShogiEffect<Boolean> UNSYNCED_EVENT_CONDITION = ShogiEffect.simple(id("unsynced_event_condition"), () -> false);

    private LittleJoysRules() {
    }

    public static void initialize() {
        EVENT_CONDITIONS.identifier();
    }

    public record AboveFluidSource(FluidIngredient fluid, boolean allowWaterlogged) implements ShogiEffect<Boolean> {
        public static final Identifier IDENTIFIER = id("above_fluid_source");
        public static final MapCodec<AboveFluidSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidIngredient.CODEC.fieldOf("fluid").orElse(FluidIngredient.WATER).forGetter(AboveFluidSource::fluid),
                Codec.BOOL.optionalFieldOf("allowWaterlogged", false).forGetter(AboveFluidSource::allowWaterlogged)
        ).apply(instance, AboveFluidSource::new));

        @Override
        public Either<Boolean, Throwable> apply(ShogiContext context) {
            final var groundState = context.requireLevel().getBlockState(context.requireBlockPos().below());
            if (!allowWaterlogged && groundState.getValueOrElse(BlockStateProperties.WATERLOGGED, false)) {
                return Either.left(false);
            }

            final var fluidState = groundState.getFluidState();
            return Either.left(fluid.test(fluidState) && fluidState.isSource());
        }

        @Override
        public Identifier identifier() {
            return IDENTIFIER;
        }
    }

    public record AboveState(BlockState state) implements ShogiEffect<Boolean> {
        public static final Identifier IDENTIFIER = id("above_state");
        public static final MapCodec<AboveState> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockState.CODEC.fieldOf("state").forGetter(AboveState::state)
        ).apply(instance, AboveState::new));

        @Override
        public Either<Boolean, Throwable> apply(ShogiContext context) {
            return Either.left(context.requireLevel().getBlockState(context.requireBlockPos().below()).equals(state));
        }

        @Override
        public Identifier identifier() {
            return IDENTIFIER;
        }
    }

    public record IsTool(HolderSet<Item> item) implements ShogiEffect<Boolean> {
        public static final Identifier IDENTIFIER = id("is_tool");
        public static final MapCodec<IsTool> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false).fieldOf("item").forGetter(IsTool::item)
        ).apply(instance, IsTool::new));

        @Override
        public Either<Boolean, Throwable> apply(ShogiContext context) {
            return Either.left(context.itemStack().is(item::contains));
        }

        @Override
        public Identifier identifier() {
            return IDENTIFIER;
        }
    }
}
