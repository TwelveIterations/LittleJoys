package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;

public record IsStateCondition(BlockState state) implements EventCondition {

    public static final MapCodec<IsStateCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(IsStateCondition::state)
    ).apply(instance, IsStateCondition::new));

    @Override
    public boolean test(EventContext context) {
        return context.state().equals(state);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(BlockState.CODEC, state);
    }

    public static IsStateCondition fromNetwork(FriendlyByteBuf buf) {
        final var state = buf.readLenientJsonWithCodec(BlockState.CODEC);
        return new IsStateCondition(state);
    }
}
