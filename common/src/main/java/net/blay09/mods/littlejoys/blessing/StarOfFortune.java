package net.blay09.mods.littlejoys.blessing;

import net.minecraft.server.level.ServerPlayer;

public class StarOfFortune {

    public static float applyChanceBonus(ServerPlayer player, float chance) {
        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_FORTUNE)) {
            return chance;
        }

        activeBlessing.consumeUse();
        return chance * 7f;
    }

}
