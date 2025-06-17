package net.blay09.mods.littlejoys.fabric.client;

import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.client.LittleJoysClient;

public class FabricLittleJoysClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(LittleJoys.MOD_ID, EmptyLoadContext.INSTANCE, LittleJoysClient::initialize);
    }
}
