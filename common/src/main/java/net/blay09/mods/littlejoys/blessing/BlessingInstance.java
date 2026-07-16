package net.blay09.mods.littlejoys.blessing;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public record BlessingInstance(ServerPlayer player, Blessing blessing, MobEffectInstance effect) {

    public int usesLeft() {
        return effect.getAmplifier() + 1;
    }

    public boolean hasUsesLeft() {
        return usesLeft() > 0;
    }

    public boolean hasUsesLeft(int uses) {
        return usesLeft() >= uses;
    }

    public boolean is(Blessing blessing) {
        return this.blessing == blessing;
    }

    public boolean consumeUse() {
        return consumeUses(1);
    }

    public boolean consumeUses(int uses) {
        if (uses <= 0) {
            return false;
        }

        final var updatedUsesLeft = Math.max(0, usesLeft() - uses);
        player.removeEffect(blessing.effect());
        if (updatedUsesLeft > 0) {
            BlessingManager.addBlessing(player, blessing, updatedUsesLeft);
        }

        return true;
    }
}
