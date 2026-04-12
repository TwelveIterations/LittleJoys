package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record AlwaysCondition() implements EventCondition {

    public static final AlwaysCondition INSTANCE = new AlwaysCondition();
    public static final MapCodec<AlwaysCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(EventContext context) {
        return true;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
    }

    public static AlwaysCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return INSTANCE;
    }
}
