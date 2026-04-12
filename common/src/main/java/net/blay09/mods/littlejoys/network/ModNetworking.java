package net.blay09.mods.littlejoys.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.network.protocol.ClientboundConfigMessage;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStartDropRushPacket;
import net.blay09.mods.littlejoys.network.protocol.ClientboundGoldRushPacket;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStopDropRushPacket;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerClientboundPacket(ClientboundGoldRushPacket.TYPE,
                ClientboundGoldRushPacket.class,
                ClientboundGoldRushPacket.STREAM_CODEC,
                ClientboundGoldRushPacket::handle);

        networking.registerClientboundPacket(ClientboundStartDropRushPacket.TYPE,
                ClientboundStartDropRushPacket.class,
                ClientboundStartDropRushPacket::encode,
                ClientboundStartDropRushPacket::decode,
                ClientboundStartDropRushPacket::handle);

        networking.registerClientboundPacket(ClientboundStopDropRushPacket.TYPE,
                ClientboundStopDropRushPacket.class,
                ClientboundStopDropRushPacket::encode,
                ClientboundStopDropRushPacket::decode,
                ClientboundStopDropRushPacket::handle);

        SyncConfigMessage.register(ClientboundConfigMessage.TYPE,
                ClientboundConfigMessage.class,
                ClientboundConfigMessage::new,
                LittleJoysConfig.class,
                LittleJoysConfig::new);
    }
}
