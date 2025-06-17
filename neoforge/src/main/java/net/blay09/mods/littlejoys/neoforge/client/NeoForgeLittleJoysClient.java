package net.blay09.mods.littlejoys.neoforge.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.client.LittleJoysClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = LittleJoys.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeLittleJoysClient {

    public NeoForgeLittleJoysClient(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        BalmClient.initializeMod(LittleJoys.MOD_ID, context, LittleJoysClient::initialize);
    }
}
