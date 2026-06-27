package net.blay09.mods.littlejoys.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface InternalMethods {
    Optional<BlockPos> findFishingSpot(ServerLevel level, BlockPos pos);

    int claimFishingSpot(ServerLevel level, BlockPos pos);

    void consumeFishingSpot(@Nullable Player player, ServerLevel level, BlockPos pos);
}
