package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.littlejoys.advancement.ModAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class BlessingManager {

    private static final RandomSource random = RandomSource.create();

    public static void initialize() {
        LivingEntityCallback.Heal.Before.EVENT.register(BlessingManager::computeHeal);
    }

    public static Blessing applyRandomBlessing(ServerPlayer player) {
        final var blessings = Blessing.values();
        final var blessing = blessings[random.nextInt(blessings.length)];
        applyBlessing(player, blessing);
        return blessing;
    }

    public static void applyBlessing(ServerPlayer player, Blessing blessing) {
        setActiveBlessing(player, blessing, blessing.defaultUses());
        ModAdvancements.awardFallenStarBlessing(player);
    }

    public static Optional<Blessing> getActiveBlessing(ServerPlayer player) {
        for (final var blessing : Blessing.values()) {
            final var effect = player.getEffect(blessing.effect());
            if (effect != null && effect.getAmplifier() > 0) {
                return Optional.of(blessing);
            }
        }

        return Optional.empty();
    }

    public static int getUsesLeft(ServerPlayer player) {
        final var activeBlessing = getActiveBlessing(player);
        if (activeBlessing.isEmpty()) {
            return 0;
        }

        final var effect = player.getEffect(activeBlessing.get().effect());
        return effect != null ? effect.getAmplifier() : 0;
    }

    public static boolean consumeUse(ServerPlayer player) {
        return consumeUses(player, 1);
    }

    public static boolean consumeUses(ServerPlayer player, int uses) {
        if (uses <= 0) {
            return false;
        }

        final var activeBlessing = getActiveBlessing(player);
        if (activeBlessing.isEmpty()) {
            clearBlessing(player);
            return false;
        }

        final var blessing = activeBlessing.get();
        final var usesLeft = getUsesLeft(player);
        final var updatedUsesLeft = Math.max(0, usesLeft - uses);
        player.removeEffect(blessing.effect());
        if (updatedUsesLeft > 0) {
            addBlessingEffect(player, blessing, updatedUsesLeft);
        }

        return true;
    }

    public static void clearBlessing(ServerPlayer player) {
        removeBlessingEffects(player);
    }

    private static void setActiveBlessing(ServerPlayer player, Blessing blessing, int usesLeft) {
        if (usesLeft <= 0) {
            clearBlessing(player);
            return;
        }

        for (final var otherBlessing : Blessing.values()) {
            if (otherBlessing != blessing) {
                player.removeEffect(otherBlessing.effect());
            }
        }

        player.removeEffect(blessing.effect());
        addBlessingEffect(player, blessing, usesLeft);
    }

    private static void addBlessingEffect(ServerPlayer player, Blessing blessing, int usesLeft) {
        player.addEffect(new MobEffectInstance(blessing.effect(), -1, usesLeft, true, false, true));
    }

    private static void removeBlessingEffects(ServerPlayer player) {
        for (final var blessing : Blessing.values()) {
            player.removeEffect(blessing.effect());
        }
    }

    private static float computeHeal(LivingEntity entity, float healAmount) {
        if (!(entity instanceof ServerPlayer player) || healAmount <= 0f) {
            return healAmount;
        }

        final var activeBlessing = getActiveBlessing(player);
        if (activeBlessing.filter(it -> it == Blessing.STAR_OF_VITALITY).isEmpty()) {
            return healAmount;
        }

        final var missingHealth = player.getMaxHealth() - player.getHealth();
        final var baseHeal = Math.min(healAmount, missingHealth);
        final var missingHealthAfterBaseHeal = missingHealth - baseHeal;
        if (missingHealthAfterBaseHeal <= 0f) {
            return healAmount;
        }

        final var usesLeft = getUsesLeft(player);
        final var extraHeal = Math.min(Math.min(healAmount, missingHealthAfterBaseHeal), usesLeft * 2f);
        if (extraHeal <= 0f) {
            return healAmount;
        }

        consumeUses(player, (int) Math.ceil(extraHeal / 2f));
        return healAmount + extraHeal;
    }
}
