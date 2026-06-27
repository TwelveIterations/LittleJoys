package net.blay09.mods.littlejoys.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class LittleJoysAPI {
    public static final String MOD_ID = "littlejoys";

    private static InternalMethods internalMethods;

    /**
     * Internal use only.
     */
    public static void __setupAPI(InternalMethods internalMethods) {
        LittleJoysAPI.internalMethods = internalMethods;
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
