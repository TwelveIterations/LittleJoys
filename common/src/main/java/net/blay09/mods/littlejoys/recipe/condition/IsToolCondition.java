package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.world.item.Item;

public record IsToolCondition(HolderSet<Item> item) implements EventCondition {
    public static final MapCodec<IsToolCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false).fieldOf("item")
                    .forGetter(IsToolCondition::item)
    ).apply(instance, IsToolCondition::new));

    @Override
    public boolean test(EventContext context) {
        return context.toolItem().is(item::contains);
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        ByteBufCodecs.holderSet(Registries.ITEM).encode(buf, item);
    }

    public static IsToolCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return new IsToolCondition(ByteBufCodecs.holderSet(Registries.ITEM).decode(buf));
    }
}
