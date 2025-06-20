package net.blay09.mods.littlejoys.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface FishingSpotHolder {
    Player littlejoys$getPlayerOwner();
    Optional<BlockPos> getFishingSpot();
    void setFishingSpot(BlockPos fishingSpot);
}
