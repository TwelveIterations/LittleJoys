package net.blay09.mods.littlejoys.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStartDropRushPacket;
import net.blay09.mods.littlejoys.network.protocol.ClientboundGoldRushPacket;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStopDropRushPacket;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerClientboundPacket(ClientboundGoldRushPacket.TYPE,
                ClientboundGoldRushPacket.class,
                ClientboundGoldRushPacket.STREAM_CODEC,
                ClientboundGoldRushPacket::handle);

        networking.registerClientboundPacket(ClientboundStartDropRushPacket.TYPE,
                ClientboundStartDropRushPacket.class,
                ClientboundStartDropRushPacket.STREAM_CODEC,
                ClientboundStartDropRushPacket::handle);

        networking.registerClientboundPacket(ClientboundStopDropRushPacket.TYPE,
                ClientboundStopDropRushPacket.class,
                ClientboundStopDropRushPacket.STREAM_CODEC,
                ClientboundStopDropRushPacket::handle);
    }
}
