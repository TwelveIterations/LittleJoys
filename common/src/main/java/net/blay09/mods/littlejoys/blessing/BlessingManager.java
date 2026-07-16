package net.blay09.mods.littlejoys.blessing;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import org.jspecify.annotations.Nullable;

public class BlessingManager {

    private static final RandomSource random = RandomSource.create();

    public static Blessing applyRandomBlessing(ServerPlayer player) {
        final var blessing = Blessings.random(random);
        applyBlessing(player, blessing);
        return blessing;
    }

    public static void applyBlessing(ServerPlayer player, Blessing blessing) {
        setActiveBlessing(player, blessing, blessing.defaultUses());
    }

    public static @Nullable BlessingInstance getActiveBlessing(ServerPlayer player) {
        for (final var effect : player.getActiveEffects()) {
            final var blessing = Blessings.byEffect(effect.getEffect());
            if (blessing.isPresent()) {
                return new BlessingInstance(player, blessing.get(), effect);
            }
        }

        return null;
    }

    public static void setActiveBlessing(ServerPlayer player, Blessing blessing, int usesLeft) {
        removeBlessings(player);
        addBlessing(player, blessing, usesLeft);
    }

    public static void addBlessing(ServerPlayer player, Blessing blessing, int usesLeft) {
        if (usesLeft > 0) {
            player.addEffect(new MobEffectInstance(blessing.effect(), -1, usesLeft - 1, false, false, false));
        }
    }

    public static void removeBlessings(ServerPlayer player) {
        for (final var blessing : Blessings.all()) {
            player.removeEffect(blessing.effect());
        }
    }

}
