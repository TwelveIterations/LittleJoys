package net.blay09.mods.littlejoys.api;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class LittleJoysAPI {
    public static final String MOD_ID = "littlejoys";

    private static InternalMethods internalMethods;

    /**
     * Internal use only.
     */
    @ApiStatus.Internal
    public static void __setupAPI(InternalMethods internalMethods) {
        LittleJoysAPI.internalMethods = internalMethods;
    }

    public static <T extends EventCondition> void registerEventCondition(ResourceLocation identifier, Class<T> clazz, MapCodec<T> codec, Function<FriendlyByteBuf, T> networkDeserializer) {
        internalMethods.registerEventCondition(identifier, clazz, codec, networkDeserializer);
    }

    public static Optional<BlockPos> findFishingSpot(ServerLevel level, BlockPos pos) {
        return internalMethods.findFishingSpot(level, pos);
    }

    public static int claimFishingSpot(ServerLevel level, BlockPos pos) {
        return internalMethods.claimFishingSpot(level, pos);
    }

    public static void consumeFishingSpot(@Nullable Player player, ServerLevel level, BlockPos pos) {
        internalMethods.consumeFishingSpot(player, level, pos);
    }

}
