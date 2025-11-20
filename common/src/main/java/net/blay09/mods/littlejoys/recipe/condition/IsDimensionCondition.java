package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public record IsDimensionCondition(Identifier dimension) implements EventCondition {

    public static final MapCodec<IsDimensionCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("dimension").forGetter(IsDimensionCondition::dimension)
    ).apply(instance, IsDimensionCondition::new));

    @Override
    public boolean test(EventContext context) {
        return context.level().dimension().identifier().equals(dimension);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeIdentifier(dimension);
    }

    public static IsDimensionCondition fromNetwork(FriendlyByteBuf buf) {
        final var dimension = buf.readIdentifier();
        return new IsDimensionCondition(dimension);
    }
}
