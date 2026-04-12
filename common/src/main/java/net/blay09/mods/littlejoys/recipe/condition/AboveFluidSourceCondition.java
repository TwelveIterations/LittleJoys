package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.blay09.mods.littlejoys.recipe.FluidIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record AboveFluidSourceCondition(FluidIngredient fluid) implements EventCondition {

    public static final MapCodec<AboveFluidSourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FluidIngredient.CODEC.fieldOf("fluid").orElse(FluidIngredient.WATER).forGetter(AboveFluidSourceCondition::fluid)
    ).apply(instance, AboveFluidSourceCondition::new));

    @Override
    public boolean test(EventContext context) {
        final var groundState = context.level().getFluidState(context.pos().below());
        return fluid.test(groundState) && groundState.isSource();
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        fluid.toNetwork(buf);
    }

    public static AboveFluidSourceCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return new AboveFluidSourceCondition(FluidIngredient.fromNetwork(buf));
    }
}
