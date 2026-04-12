package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record AndCondition(List<EventCondition> conditions) implements EventCondition {

    public static final MapCodec<AndCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EventConditionRegistry.LIST_CODEC.fieldOf("conditions").forGetter(AndCondition::conditions)
    ).apply(instance, AndCondition::new));

    @Override
    public boolean test(EventContext context) {
        for (EventCondition condition : conditions) {
            if (!condition.test(context)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        buf.writeCollection(conditions, (FriendlyByteBuf buffer, EventCondition condition) -> EventConditionRegistry.conditionToNetwork((RegistryFriendlyByteBuf) buffer, condition));
    }

    public static AndCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        final var conditions = buf.readCollection(ArrayList::new, (FriendlyByteBuf buffer) -> EventConditionRegistry.conditionFromNetwork((RegistryFriendlyByteBuf) buffer));
        return new AndCondition(conditions);
    }
}
