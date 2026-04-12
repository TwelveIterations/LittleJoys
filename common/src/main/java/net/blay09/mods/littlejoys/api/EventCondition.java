package net.blay09.mods.littlejoys.api;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface EventCondition {
    boolean test(EventContext context);
    void toNetwork(RegistryFriendlyByteBuf buf);
}
