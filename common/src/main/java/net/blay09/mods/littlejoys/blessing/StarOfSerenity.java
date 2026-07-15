package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class StarOfSerenity {
    public static void initialize() {
        LivingEntityCallback.Fall.Before.EVENT.register(StarOfSerenity::computeFallDamage);
    }

    public static boolean preventSweetBerryBushDamage(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_SERENITY)) {
            return false;
        }

        activeBlessing.consumeUse();
        return true;
    }

    private static float computeFallDamage(LivingEntity entity, float fallDamage) {
        if (!(entity instanceof ServerPlayer player) || fallDamage <= 0f) {
            return fallDamage;
        }

        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_SERENITY)) {
            return fallDamage;
        }

        activeBlessing.consumeUse();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
        return Math.max(0f, fallDamage - 8f);
    }

}
