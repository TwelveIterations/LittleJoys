package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;

public record AboveStateCondition(BlockState state) implements EventCondition {

    public static final MapCodec<AboveStateCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(AboveStateCondition::state)
    ).apply(instance, AboveStateCondition::new));

    @Override
    public boolean test(EventContext context) {
        final var groundState = context.level().getBlockState(context.pos().below());
        return groundState.equals(state);
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        buf.writeJsonWithCodec(BlockState.CODEC, state);
    }

    public static AboveStateCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        final var state = buf.readJsonWithCodec(BlockState.CODEC);
        return new AboveStateCondition(state);
    }
}
