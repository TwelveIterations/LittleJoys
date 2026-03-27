package net.blay09.mods.littlejoys.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface FishingSpotHolder {
    @Nullable Player littlejoys$getPlayerOwner();
    Optional<BlockPos> littlejoys$getFishingSpot();
    void littlejoys$setFishingSpot(BlockPos fishingSpot);
    boolean littlejoys$shouldSkipRewards();
    void littlejoys$setSkipRewards(boolean skipRewards);
}
