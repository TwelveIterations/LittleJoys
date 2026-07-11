package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public record IsStatePropertyCondition(String property, String value) implements EventCondition {

    public static final MapCodec<IsStatePropertyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("property").forGetter(IsStatePropertyCondition::property),
            Codec.STRING.fieldOf("value").forGetter(IsStatePropertyCondition::value)
    ).apply(instance, IsStatePropertyCondition::new));

    @Override
    public boolean test(EventContext context) {
        final var state = context.state();
        final var property = state.getBlock().getStateDefinition().getProperty(this.property);
        return property != null && hasPropertyValue(state, property, value);
    }

    private static <T extends Comparable<T>> boolean hasPropertyValue(BlockState state, Property<T> property, String value) {
        final var expectedValue = property.getValue(value);
        return expectedValue.isPresent() && state.getValue(property).equals(expectedValue.get());
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(property);
        buf.writeUtf(value);
    }

    public static IsStatePropertyCondition fromNetwork(FriendlyByteBuf buf) {
        final var property = buf.readUtf();
        final var value = buf.readUtf();
        return new IsStatePropertyCondition(property, value);
    }
}
