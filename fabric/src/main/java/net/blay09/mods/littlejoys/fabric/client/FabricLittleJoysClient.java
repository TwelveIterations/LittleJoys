package net.blay09.mods.littlejoys.fabric.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ClientModInitializer;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.client.LittleJoysClient;

public class FabricLittleJoysClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(LittleJoys.MOD_ID, FabricLoadContext.INSTANCE, LittleJoysClient::initialize);
    }
}
