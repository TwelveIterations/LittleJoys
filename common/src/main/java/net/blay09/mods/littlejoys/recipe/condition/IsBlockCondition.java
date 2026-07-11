package net.blay09.mods.littlejoys.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.Block;

public record IsBlockCondition(Block block) implements EventCondition {

    public static final MapCodec<IsBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(IsBlockCondition::block)
    ).apply(instance, IsBlockCondition::new));

    @Override
    public boolean test(EventContext context) {
        return context.state().getBlock() == block;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(block));
    }

    public static IsBlockCondition fromNetwork(FriendlyByteBuf buf) {
        final var block = BuiltInRegistries.BLOCK.get(buf.readResourceLocation());
        return new IsBlockCondition(block);
    }
}
