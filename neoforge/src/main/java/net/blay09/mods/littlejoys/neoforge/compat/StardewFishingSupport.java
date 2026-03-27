package net.blay09.mods.littlejoys.neoforge.compat;

import com.bonker.stardewfishing.server.event.StardewMinigameStartedEvent;
import net.blay09.mods.littlejoys.LittleJoysConfig;
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
            fishingSpotHolder.littlejoys$getFishingSpot().ifPresent(fishingSpot ->
                    LittleJoysAPI.consumeFishingSpot(event.getEntity(), serverLevel, fishingSpot));
        }
    }

    @SubscribeEvent
    public void onStardewMinigameStarted(StardewMinigameStartedEvent event) {
        final var fishingHook = event.getHook();
        if (fishingHook instanceof FishingSpotHolder fishingSpotHolder && fishingSpotHolder.littlejoys$getFishingSpot().isPresent()) {
            final var config = LittleJoysConfig.getActive().stardewFishing;
            event.setTreasureChanceBonus(event.getTreasureChanceBonus() + config.fishingSpotTreasureChestChanceBonus);
            event.setGoldenChanceBonus(event.getGoldenChanceBonus() + config.fishingSpotGoldenChestChanceBonus);
            if (config.skipFishingSpotRewards) {
                fishingSpotHolder.littlejoys$setSkipRewards(true);
            }
        }
    }
}
