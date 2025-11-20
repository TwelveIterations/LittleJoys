package net.blay09.mods.littlejoys.client.handler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.handler.GoldRushInstance;
import net.blay09.mods.littlejoys.particle.ModParticles;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class GoldRushClientHandler {

    private static final RandomSource random = RandomSource.create();

    private static final Table<ResourceKey<Level>, BlockPos, GoldRushInstance> activeGoldRushes = HashBasedTable.create();

    public static void initialize() {
        ClientTickCallback.ClientLevelTick.BEFORE.register(level -> {
            for (final var goldRush : activeGoldRushes.row(level.dimension()).values()) {
                final var pos = goldRush.getPos();
                final var x = pos.getX();
                final var y = pos.getY();
                final var z = pos.getZ();
                final var randomOffsetX = random.nextFloat();
                final var randomOffsetY = random.nextFloat();
                final var randomOffsetZ = random.nextFloat();
                final var state = level.getBlockState(pos);
                if (state.isViewBlocking(level, pos)) {
                    for (final var direction : Direction.values()) {
                        final var offsetX = direction.getAxis() == Direction.Axis.X ? 0.5f + 0.6f * direction.getAxisDirection().getStep() : randomOffsetX;
                        final var offsetY = direction.getAxis() == Direction.Axis.Y ? 0.5f + 0.6f * direction.getAxisDirection().getStep() : randomOffsetY;
                        final var offsetZ = direction.getAxis() == Direction.Axis.Z ? 0.5f + 0.6f * direction.getAxisDirection().getStep() : randomOffsetZ;
                        final var offsetPos = pos.relative(direction);
                        if (!level.getBlockState(offsetPos).isViewBlocking(level, offsetPos)) {
                            level.addParticle(ModParticles.goldRush.value(), x + offsetX, y + offsetY, z + offsetZ, 0f, 0f, 0f);
                        }
                    }
                } else {
                    level.addParticle(ModParticles.goldRush.value(),
                            x + randomOffsetX,
                            y + randomOffsetY,
                            z + randomOffsetZ,
                            0f,
                            0f,
                            0f);
                }
                if (goldRush.getTicksPassed() % 160 == 0) {
                    level.playLocalSound(pos, ModSounds.goldRush.value(), SoundSource.BLOCKS, 0.5f, 1f, false);
                }
                goldRush.setTicksPassed(goldRush.getTicksPassed() + 1);
            }
        });

        ClientLifecycleCallback.DisconnectedFromServer.EVENT.register(client -> {
            activeGoldRushes.clear();
            Minecraft.getInstance().getSoundManager().stop(Identifier.fromNamespaceAndPath(LittleJoys.MOD_ID, "gold_rush"), SoundSource.BLOCKS);
        });
    }

    public static void addActiveGoldRush(BlockPos pos) {
        final var level = Minecraft.getInstance().level;
        if (level != null) {
            activeGoldRushes.put(level.dimension(), pos, new GoldRushInstance(pos, level.getBlockState(pos), Optional.empty(), -1, -1, null));
        }
    }

    public static void removeActiveGoldRush(BlockPos pos) {
        final var minecraft = Minecraft.getInstance();
        final var level = minecraft.level;
        if (level != null) {
            activeGoldRushes.remove(level.dimension(), pos);
            if (activeGoldRushes.isEmpty()) {
                minecraft.getSoundManager().stop(Identifier.fromNamespaceAndPath(LittleJoys.MOD_ID, "gold_rush"), SoundSource.BLOCKS);
            }
        }
    }
}
