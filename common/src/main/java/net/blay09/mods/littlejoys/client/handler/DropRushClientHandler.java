package net.blay09.mods.littlejoys.client.handler;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStopDropRushPacket;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class DropRushClientHandler {

    private static final int INTRO_TICKS = 48;
    private static final int LOOP_TICKS = 200;
    private static final int OUTRO_TICKS = 98;

    private static boolean dropRushActive;
    private static int dropRushTicksPassed;
    private static int dropRushMaxTicks;

    public static void initialize() {
        ClientTickCallback.ClientLevelTick.BEFORE.register(level -> {
            if (dropRushActive) {
                dropRushTicksPassed++;
                final var minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    if (dropRushTicksPassed == INTRO_TICKS) {
                        level.playSound(minecraft.player, minecraft.player, ModSounds.dropRush.value(), SoundSource.PLAYERS, 1f, 1f);
                    } else if (dropRushTicksPassed > INTRO_TICKS && (dropRushTicksPassed - INTRO_TICKS) % LOOP_TICKS == 0 && dropRushMaxTicks - dropRushTicksPassed > 5) {
                        level.playSound(minecraft.player, minecraft.player, ModSounds.dropRush.value(), SoundSource.PLAYERS, 1f, 1f);
                    } else if (dropRushTicksPassed == dropRushMaxTicks - OUTRO_TICKS) {
                        level.playSound(minecraft.player, minecraft.player, ModSounds.dropRushStop.value(), SoundSource.PLAYERS, 1f, 1f);
                        //level.playLocalSound(minecraft.player.getX(), minecraft.player.getY(), minecraft.player.getZ(), ModSounds.dropRushStop.get(), SoundSource.NEUTRAL, 1f, 1f, false);
                    }
                }
            }
        });

        ClientLifecycleCallback.DisconnectedFromServer.EVENT.register(client
                -> stopDropRush(ClientboundStopDropRushPacket.Reason.DISCONNECT));
    }

    public static void startDropRush(int ticks) {
        final var minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.player != null) {
            dropRushActive = true;
            dropRushTicksPassed = 0;
            dropRushMaxTicks = ticks;
            minecraft.level.playSound(minecraft.player, minecraft.player, ModSounds.dropRushStart.value(), SoundSource.PLAYERS, 1f, 1f);
        }
    }

    public static void stopDropRush(ClientboundStopDropRushPacket.Reason reason) {
        final var minecraft = Minecraft.getInstance();
        dropRushActive = false;
        minecraft.getSoundManager().stop(Identifier.fromNamespaceAndPath(LittleJoys.MOD_ID, "drop_rush"), SoundSource.PLAYERS);
        minecraft.getSoundManager().stop(Identifier.fromNamespaceAndPath(LittleJoys.MOD_ID, "drop_rush_stop"), SoundSource.PLAYERS);
        if (reason == ClientboundStopDropRushPacket.Reason.FULL_CLEAR && minecraft.level != null && minecraft.player != null) {
            minecraft.level.playSound(minecraft.player, minecraft.player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1f, 1f);
        }
    }
}
