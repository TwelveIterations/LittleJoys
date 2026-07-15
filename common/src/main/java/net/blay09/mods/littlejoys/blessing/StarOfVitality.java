package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;

public class StarOfVitality {

    public static void initialize() {
        LivingEntityCallback.Heal.Before.EVENT.register(StarOfVitality::computeHeal);
    }

    private static float computeHeal(LivingEntity entity, float healAmount) {
        if (!(entity instanceof ServerPlayer player) || healAmount <= 0f) {
            return healAmount;
        }

        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_VITALITY)) {
            return healAmount;
        }

        final var missingHealth = player.getMaxHealth() - player.getHealth();
        final var baseHeal = Math.min(healAmount, missingHealth);
        final var missingHealthAfterBaseHeal = missingHealth - baseHeal;
        if (missingHealthAfterBaseHeal <= 0f) {
            return healAmount;
        }

        final var usesLeft = activeBlessing.usesLeft();
        final var extraHeal = Math.min(Math.min(healAmount, missingHealthAfterBaseHeal), usesLeft * 2f);
        if (extraHeal <= 0f) {
            return healAmount;
        }

        activeBlessing.consumeUses((int) Math.ceil(extraHeal / 2f));
        return healAmount + extraHeal;
    }

    public static void eatExtraFood(LivingEntity entity, FoodData foodData, FoodProperties foodProperties) {
        if (!(entity instanceof ServerPlayer player) || foodProperties.nutrition() <= 0) {
            return;
        }

        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_VITALITY)) {
            return;
        }

        final var foodBeforeExtra = foodData.getFoodLevel();
        if (foodBeforeExtra >= 20) {
            return;
        }

        foodData.eat(foodProperties);
        activeBlessing.consumeUses(foodData.getFoodLevel() - foodBeforeExtra);
    }
}
