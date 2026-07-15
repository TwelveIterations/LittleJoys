package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;

public class StarOfSerenity {
    public static void initialize() {
        LivingEntityCallback.Fall.Before.EVENT.register(StarOfSerenity::computeFallDamage);
        ServerTickCallback.ServerPlayerTick.AFTER.register(StarOfSerenity::calmNearbyAnimals);
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

    private static void calmNearbyAnimals(ServerPlayer player) {
        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_SERENITY)) {
            return;
        }

        final var searchArea = new AABB(player.blockPosition()).inflate(16);
        for (final var animal : player.level().getEntitiesOfClass(Animal.class, searchArea, Entity::isAlive)) {
            if (calmPanic(animal)) {
                activeBlessing.consumeUse();
            }
            if (calmAggressiveWolf(animal)) {
                activeBlessing.consumeUse();
            }
        }
    }

    private static boolean calmPanic(Animal animal) {
        if (animal instanceof PathfinderMob mob && !animal.isOnFire() && animal.getLastDamageSource() != null) {
            final var lastDamageSource = animal.getLastDamageSource();
            if (lastDamageSource.is(DamageTypeTags.PANIC_CAUSES) || lastDamageSource.is(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)) {
                mob.getNavigation().stop();
                return true;
            }
        }
        return false;
    }

    private static boolean calmAggressiveWolf(Animal animal) {
        if (animal instanceof Wolf wolf && wolf.isAngry()) {
            wolf.stopBeingAngry();
            wolf.setAggressive(false);
            wolf.getNavigation().stop();
            return true;
        }
        return false;
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
