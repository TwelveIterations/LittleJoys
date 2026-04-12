package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record CanSeeSkyCondition() implements EventCondition {

    public static final CanSeeSkyCondition INSTANCE = new CanSeeSkyCondition();
    public static final MapCodec<CanSeeSkyCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(EventContext context) {
        return context.level().canSeeSky(context.pos());
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
    }

    public static CanSeeSkyCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return INSTANCE;
    }
}
