package net.blay09.mods.littlejoys.api;

import net.minecraft.network.FriendlyByteBuf;

public interface EventCondition {
    boolean test(EventContext context);
    void toNetwork(FriendlyByteBuf buf);
}
