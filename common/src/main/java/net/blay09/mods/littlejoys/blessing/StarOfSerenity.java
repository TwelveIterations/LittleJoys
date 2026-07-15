package net.blay09.mods.littlejoys.blessing;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class StarOfSerenity {

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

}
