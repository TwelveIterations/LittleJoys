package net.blay09.mods.littlejoys;

import net.blay09.mods.littlejoys.api.InternalMethods;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class InternalMethodsImpl implements InternalMethods {
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
