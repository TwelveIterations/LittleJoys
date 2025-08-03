package net.blay09.mods.littlejoys.forge.compat;

import com.bonker.stardewfishing.server.event.StardewMinigameEndedEvent;
import net.blay09.mods.littlejoys.api.LittleJoysAPI;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StardewFishingSupport {
    public StardewFishingSupport() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onStardewMinigameEnded(StardewMinigameEndedEvent event) {
        final var player = event.getPlayer();
        final var hook = event.getHook();
        if (event.wasSuccessful() && hook instanceof FishingSpotHolder fishingSpotHolder && player.level() instanceof ServerLevel serverLevel) {
            fishingSpotHolder.getFishingSpot().ifPresent(fishingSpot ->
                    LittleJoysAPI.consumeFishingSpot(player, serverLevel, fishingSpot));
        }
    }
}
