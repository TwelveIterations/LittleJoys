package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record NotCondition(EventCondition condition) implements EventCondition {

    public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EventConditionRegistry.CODEC.fieldOf("condition").forGetter(NotCondition::condition)
    ).apply(instance, NotCondition::new));

    @Override
    public boolean test(EventContext context) {
        return !condition.test(context);
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        EventConditionRegistry.conditionToNetwork(buf, condition);
    }

    public static NotCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return new NotCondition(EventConditionRegistry.conditionFromNetwork(buf));
    }
}
