package net.blay09.mods.littlejoys.blessing;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.mixin.LivingEntityAccessor;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.blay09.mods.littlejoys.tag.ModEntityTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;

public class StarOfSerenity {
    public static void initialize() {
        LivingEntityCallback.Fall.Before.EVENT.register(StarOfSerenity::computeFallDamage);
        ServerTickCallback.ServerPlayerTick.AFTER.register(StarOfSerenity::tickPlayer);
    }

    public static boolean preventSweetBerryBushDamage(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        final var activeBlessing = getSerenityBlessing(player);
        if (activeBlessing == null) {
            return false;
        }

        activeBlessing.consumeUse();
        return true;
    }

    public static boolean preventCactusDamage(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        final var activeBlessing = getSerenityBlessing(player);
        if (activeBlessing == null) {
            return false;
        }

        activeBlessing.consumeUse();
        return true;
    }

    public static boolean shouldIgnoreTarget(LivingEntity entity, LivingEntity target) {
        return entity.is(ModEntityTags.CALMED_BY_SERENITY)
                && target instanceof ServerPlayer player && getSerenityBlessing(player) != null;
    }

    private static void tickPlayer(ServerPlayer player) {
        final var activeBlessing = getSerenityBlessing(player);
        if (activeBlessing == null) {
            return;
        }

        tryExtinguishPlayerOnFire(player, activeBlessing);
        calmNearbyMobs(player, activeBlessing);
    }

    private static void tryExtinguishPlayerOnFire(ServerPlayer player, BlessingInstance activeBlessing) {
        if (!player.isOnFire() || player.tickCount % 20 != 0) {
            return;
        }

        final var random = player.level().getRandom();
        if (random.nextFloat() >= LittleJoysConfig.getActive().blessings.starOfSerenityExtinguishChance) {
            return;
        }

        player.clearFire();
        activeBlessing.consumeUse();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
    }

    private static void calmNearbyMobs(ServerPlayer player, BlessingInstance activeBlessing) {
        final var searchArea = new AABB(player.blockPosition()).inflate(16);
        for (final var mob : player.level().getEntitiesOfClass(PathfinderMob.class, searchArea, Entity::isAlive)) {
            if (!mob.is(ModEntityTags.CALMED_BY_SERENITY)) {
                continue;
            }

            if (mob.tickCount % 20 == 0) {
                if (mob instanceof Animal animal) {
                    if (calmPanic(animal)) {
                        activeBlessing.consumeUse();
                    }
                    if (calmAggressiveWolf(animal)) {
                        activeBlessing.consumeUse();
                    }
                } else {
                    if (calmMob(player, mob)) {
                        activeBlessing.consumeUse();
                    }
                }
            }
        }
    }

    private static boolean calmPanic(Animal animal) {
        if (animal instanceof PathfinderMob mob && !animal.isOnFire() && animal.getLastDamageSource() != null) {
            final var lastDamageSource = animal.getLastDamageSource();
            if (lastDamageSource.is(DamageTypeTags.PANIC_CAUSES) || lastDamageSource.is(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)) {
                mob.getNavigation().stop();
                mob.stopInPlace();
                ((LivingEntityAccessor) mob).setLastDamageSource(null);
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
            wolf.stopInPlace();
            return true;
        }
        return false;
    }

    private static boolean calmMob(ServerPlayer player, Mob mob) {
        if (mob.getTarget() == player) {
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();
            mob.stopInPlace();
            return true;
        }
        return false;
    }

    private static float computeFallDamage(LivingEntity entity, float fallDamage) {
        if (!(entity instanceof ServerPlayer player) || fallDamage <= 0f) {
            return fallDamage;
        }

        final var activeBlessing = getSerenityBlessing(player);
        if (activeBlessing == null) {
            return fallDamage;
        }

        activeBlessing.consumeUse();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
        return Math.max(0f, fallDamage - 8f);
    }

    private static BlessingInstance getSerenityBlessing(ServerPlayer player) {
        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        return activeBlessing != null && activeBlessing.is(Blessings.STAR_OF_SERENITY) ? activeBlessing : null;
    }

}
