package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.BonemealableBlock;

public class StarOfProsperity {

    public static void initialize() {
        LivingEntityCallback.Heal.Before.EVENT.register(StarOfProsperity::computeHeal);
    }

    private static float computeHeal(LivingEntity entity, float healAmount) {
        if (!(entity instanceof ServerPlayer player) || healAmount <= 0f) {
            return healAmount;
        }

        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_PROSPERITY)) {
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
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_PROSPERITY)) {
            return;
        }

        final var foodBeforeExtra = foodData.getFoodLevel();
        if (foodBeforeExtra >= 20) {
            return;
        }

        foodData.eat(foodProperties);
        activeBlessing.consumeUses(foodData.getFoodLevel() - foodBeforeExtra);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
    }

    public static void tryBonemealPlacedBlock(ServerPlayer player, BlockPos pos) {
        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_PROSPERITY)) {
            return;
        }

        final var level = player.level();
        final var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BonemealableBlock bonemealableBlock)) {
            return;
        }

        final var random = level.getRandom();
        if (random.nextFloat() >= LittleJoysConfig.getActive().blessings.starOfProsperityBonemealChance) {
            return;
        }

        if (!bonemealableBlock.isValidBonemealTarget(level, pos, state) || !bonemealableBlock.isBonemealSuccess(level, random, pos, state)) {
            return;
        }

        bonemealableBlock.performBonemeal(level, random, pos, state);
        activeBlessing.consumeUse();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
    }

    public static void trySpawnTwinOffspring(ServerLevel level, Animal parent, Animal partner) {
        final var random = level.getRandom();
        if (random.nextFloat() >= LittleJoysConfig.getActive().blessings.starOfProsperityTwinChance) {
            return;
        }

        BlessingInstance activeBlessing = null;
        final var nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, parent.getBoundingBox().inflate(8), player -> {
            final var blessing = BlessingManager.getActiveBlessing(player);
            return blessing != null && blessing.is(Blessings.STAR_OF_PROSPERITY);
        });
        if (!nearbyPlayers.isEmpty()) {
            activeBlessing = BlessingManager.getActiveBlessing(nearbyPlayers.getFirst());
        }
        if (activeBlessing == null) {
            return;
        }

        final var twin = parent.getBreedOffspring(level, partner);
        if (twin != null) {
            twin.setBaby(true);
            twin.snapTo(parent.getX(), parent.getY(), parent.getZ(), 0f, 0f);
            level.addFreshEntityWithPassengers(twin);
            activeBlessing.consumeUse();
            level.playSound(null, parent.getX(), parent.getY(), parent.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
        }
    }
}
