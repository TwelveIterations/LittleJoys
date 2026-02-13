package net.blay09.mods.littlejoys.neoforge.compat;

import net.blay09.mods.littlejoys.api.LittleJoysAPI;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

public class StardewFishingSupport {

    public StardewFishingSupport() {
        NeoForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onItemFished(ItemFishedEvent event) {
        final var fishingHook = event.getHookEntity();
        if (fishingHook.level() instanceof ServerLevel serverLevel && fishingHook instanceof FishingSpotHolder fishingSpotHolder) {
            fishingSpotHolder.getFishingSpot().ifPresent(fishingSpot ->
                    LittleJoysAPI.consumeFishingSpot(event.getEntity(), serverLevel, fishingSpot));
        }
    }
}
