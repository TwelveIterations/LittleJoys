package net.blay09.mods.littlejoys.client;

import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.littlejoys.client.handler.DropRushClientHandler;
import net.blay09.mods.littlejoys.client.handler.GoldRushClientHandler;

public class LittleJoysClient {
    public static void initialize(BalmClientRegistrars registrars) {
        registrars.entityRenderers(ModRenderers::initialize);
        registrars.particleProviders(ModRenderers::initialize);

        DropRushClientHandler.initialize();
        GoldRushClientHandler.initialize();
    }
}
