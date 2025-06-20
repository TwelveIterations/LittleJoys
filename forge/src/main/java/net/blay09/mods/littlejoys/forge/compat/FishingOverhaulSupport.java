package net.blay09.mods.littlejoys.forge.compat;

import net.blay09.mods.littlejoys.api.LittleJoysAPI;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FishingOverhaulSupport {
    public FishingOverhaulSupport() {
        MinecraftForge.EVENT_BUS.register(this);
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
