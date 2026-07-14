package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.advancement.ModAdvancements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class BlessingManager {

    private static final RandomSource random = RandomSource.create();
    private static final String TAG_BLESSING = "blessing";
    private static final String TAG_ACTIVE_BLESSING = "active";
    private static final String TAG_USES_LEFT = "usesLeft";

    public static void initialize() {
        ServerTickCallback.ServerPlayerTick.AFTER.register(BlessingManager::syncBlessingEffect);
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
        syncBlessingEffect(player);
        ModAdvancements.awardFallenStarBlessing(player);
    }

    public static Optional<Blessing> getActiveBlessing(ServerPlayer player) {
        final var blessingData = getBlessingData(player);
        final var usesLeft = blessingData.getIntOr(TAG_USES_LEFT, 0);
        if (usesLeft <= 0) {
            return Optional.empty();
        }

        return Blessing.byId(blessingData.getString(TAG_ACTIVE_BLESSING).orElse(""));
    }

    public static int getUsesLeft(ServerPlayer player) {
        return getBlessingData(player).getIntOr(TAG_USES_LEFT, 0);
    }

    public static boolean consumeUse(ServerPlayer player) {
        return consumeUses(player, 1);
    }

    public static boolean consumeUses(ServerPlayer player, int uses) {
        if (uses <= 0) {
            return false;
        }

        final var blessingData = getBlessingData(player);
        final var usesLeft = blessingData.getIntOr(TAG_USES_LEFT, 0);
        if (usesLeft <= 0 || getActiveBlessing(player).isEmpty()) {
            clearBlessing(player);
            return false;
        }

        blessingData.putInt(TAG_USES_LEFT, Math.max(0, usesLeft - uses));
        syncBlessingEffect(player);
        return true;
    }

    public static void clearBlessing(ServerPlayer player) {
        final var blessingData = getBlessingData(player);
        blessingData.remove(TAG_ACTIVE_BLESSING);
        blessingData.putInt(TAG_USES_LEFT, 0);
        removeBlessingEffects(player);
    }

    private static void setActiveBlessing(ServerPlayer player, Blessing blessing, int usesLeft) {
        final var blessingData = getBlessingData(player);
        blessingData.putString(TAG_ACTIVE_BLESSING, blessing.id());
        blessingData.putInt(TAG_USES_LEFT, usesLeft);
    }

    private static void syncBlessingEffect(ServerPlayer player) {
        final var activeBlessing = getActiveBlessing(player);
        if (activeBlessing.isEmpty()) {
            removeBlessingEffects(player);
            return;
        }

        final var blessing = activeBlessing.get();
        for (final var otherBlessing : Blessing.values()) {
            if (otherBlessing != blessing) {
                player.removeEffect(otherBlessing.effect());
            }
        }

        final var currentEffect = player.getEffect(blessing.effect());
        if (currentEffect == null || !currentEffect.isInfiniteDuration()) {
            player.addEffect(new MobEffectInstance(blessing.effect(), -1, 0, true, false, true));
        }
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

    private static CompoundTag getBlessingData(ServerPlayer player) {
        final var playerData = Balm.hooks().getPersistentData(player);
        final var littleJoysData = playerData.getCompoundOrEmpty(LittleJoys.MOD_ID);
        final var blessingData = littleJoysData.getCompoundOrEmpty(TAG_BLESSING);
        littleJoysData.put(TAG_BLESSING, blessingData);
        playerData.put(LittleJoys.MOD_ID, littleJoysData);
        return blessingData;
    }
}
