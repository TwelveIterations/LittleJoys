package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.blay09.mods.littlejoys.recipe.FluidIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public record AboveFluidSourceCondition(FluidIngredient fluid, boolean allowWaterlogged) implements EventCondition {

    public static final MapCodec<AboveFluidSourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FluidIngredient.CODEC.fieldOf("fluid").orElse(FluidIngredient.WATER).forGetter(AboveFluidSourceCondition::fluid),
            Codec.BOOL.optionalFieldOf("allowWaterlogged", false).forGetter(AboveFluidSourceCondition::allowWaterlogged)
    ).apply(instance, AboveFluidSourceCondition::new));

    @Override
    public boolean test(EventContext context) {
        final var groundState = context.level().getBlockState(context.pos().below());
        if (!allowWaterlogged && groundState.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false)) {
            return false;
        }

        final var fluidState = groundState.getFluidState();
        return fluid.test(fluidState) && fluidState.isSource();
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        fluid.toNetwork(buf);
        buf.writeBoolean(allowWaterlogged);
    }

    public static AboveFluidSourceCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return new AboveFluidSourceCondition(FluidIngredient.fromNetwork(buf), buf.readBoolean());
    }
}
