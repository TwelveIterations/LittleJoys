package net.blay09.mods.littlejoys.api;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public interface InternalMethods {
    <T extends EventCondition> void registerEventCondition(ResourceLocation identifier, Class<T> clazz, MapCodec<T> codec, Function<FriendlyByteBuf, T> networkDeserializer);

    Optional<BlockPos> findFishingSpot(ServerLevel level, BlockPos pos);

    int claimFishingSpot(ServerLevel level, BlockPos pos);

    void consumeFishingSpot(@Nullable Player player, ServerLevel level, BlockPos pos);
}
