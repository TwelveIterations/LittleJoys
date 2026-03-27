package net.blay09.mods.littlejoys;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.api.InternalMethods;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.recipe.condition.EventConditionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class InternalMethodsImpl implements InternalMethods {
    @Override
    public <T extends EventCondition> void registerEventCondition(Identifier identifier, Class<T> clazz, MapCodec<T> codec, Function<FriendlyByteBuf, T> networkDeserializer) {
        EventConditionRegistry.registerCondition(identifier, clazz, codec, networkDeserializer);
    }

    @Override
    public Optional<BlockPos> findFishingSpot(ServerLevel level, BlockPos pos) {
        return FishingSpotHandler.findFishingSpot(level, pos);
    }

    @Override
    public int claimFishingSpot(ServerLevel level, BlockPos pos) {
        return FishingSpotHandler.claimFishingSpot(level, pos);
    }

    @Override
    public void consumeFishingSpot(@Nullable Player player, ServerLevel level, BlockPos pos) {
        FishingSpotHandler.consumeFishingSpot(player, level, pos);
    }
}
