package net.blay09.mods.littlejoys.handler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.advancement.ModAdvancements;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.blessing.StarOfFate;
import net.blay09.mods.littlejoys.entity.DropRushItemEntity;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStartDropRushPacket;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStopDropRushPacket;
import net.blay09.mods.littlejoys.registry.DropRushEvent;
import net.blay09.mods.littlejoys.registry.ModDynamicRegistries;
import net.blay09.mods.littlejoys.stats.ModStats;
import net.blay09.mods.shogi.context.MutableShogiContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Optional;

public class DropRushHandler {
    private static final RandomSource random = RandomSource.create();

    private static final int DROP_TICKS = 50;

    private static final Table<ResourceKey<Level>, BlockPos, DropRushInstance> activeDropRushes = HashBasedTable.create();

    public static void initialize() {
        BlockCallback.Break.Before.EVENT.register((level, pos, state, blockEntity, player) -> {
            if (!LittleJoysConfig.getActive().dropRush.enabled) {
                return true;
            }

            if (player != null && player.getAbilities().instabuild) {
                return true;
            }

            if (player == null || Balm.hooks().isFakePlayer(player)) {
                return true;
            }

            final var hasSilkTouch = level.registryAccess().lookup(Registries.ENCHANTMENT)
                    .flatMap(it -> it.get(Enchantments.SILK_TOUCH))
                    .map(it -> EnchantmentHelper.getEnchantmentLevel(it, player) > 0)
                    .orElse(false);
            if (hasSilkTouch) {
                return true;
            }

            if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return true;
            }

            rollForDropRush(serverLevel, pos, state, serverPlayer);
            return true;
        });

        ServerTickCallback.ServerLevelTick.BEFORE.register(level -> {
            for (final var dropRush : activeDropRushes.row(level.dimension()).values()) {
                dropRush.setTicksPassed(dropRush.getTicksPassed() + 1);
                dropRush.setDropCooldownTicks(dropRush.getDropCooldownTicks() - 1);
                final var dropsLeft = dropRush.getDrops();
                if (dropRush.getDropCooldownTicks() <= 0 && !dropsLeft.isEmpty()) {
                    final var nextDropItemStack = dropsLeft.removeLast();
                    spawnDropRushItem(level, dropRush, nextDropItemStack);
                    dropRush.setDropCooldownTicks(dropRush.getTicksPerDrop());
                } else if (dropRush.getTicksPassed() >= DROP_TICKS) {
                    for (final var itemStack : dropsLeft) {
                        spawnDropRushItem(level, dropRush, itemStack);
                    }
                }

                dropRush.getEntities().removeIf(DropRushItemEntity::isPickedUp);
                if (dropRush.getEntities().isEmpty()) {
                    final var player = level.getPlayerByUUID(dropRush.getPlayerId());
                    if (player != null) {
                        player.awardStat(ModStats.dropRushesCompleted);
                        if (player instanceof ServerPlayer serverPlayer) {
                            ModAdvancements.awardDropRushComplete(serverPlayer);
                        }
                        Balm.networking().sendTo(player, new ClientboundStopDropRushPacket(ClientboundStopDropRushPacket.Reason.FULL_CLEAR));
                    }
                } else if (dropRush.getTicksPassed() >= dropRush.getMaxTicks()) {
                    final var player = level.getPlayerByUUID(dropRush.getPlayerId());
                    if (player != null) {
                        Balm.networking().sendTo(player, new ClientboundStopDropRushPacket(ClientboundStopDropRushPacket.Reason.TIME_UP));
                    }
                }
            }
            activeDropRushes.values().removeIf(it -> it.getTicksPassed() >= it.getMaxTicks() || it.getEntities().isEmpty());
        });
    }

    public static boolean rollForDropRush(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return rollEvent(level, pos, state, player, false).map(eventHolder -> {
            startDropRush(level, pos, player, eventHolder);
            return true;
        }).orElse(false);
    }

    public static boolean startDropRush(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return rollEvent(level, pos, state, player, true).map(eventHolder -> {
            startDropRush(level, pos, player, eventHolder);
            return true;
        }).orElse(false);
    }

    public static void startDropRush(ServerLevel level, BlockPos pos, ServerPlayer player, Holder.Reference<DropRushEvent> eventHolder) {
        final var event = eventHolder.value();
        final var dropRushInstance = new DropRushInstance(
                player.getUUID(),
                pos,
                event.lootTable(),
                (int) Math.floor(20 * event.seconds()));
        final var lootParamsBuilder = (new LootParams.Builder(level))
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
        final var lootTableId = event.lootTable();
        final var lootParams = lootParamsBuilder.withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(pos))
                .create(LootContextParamSets.BLOCK);
        final var lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableId);
        for (int i = 0; i < event.rolls(); i++) {
            lootTable.getRandomItems(lootParams).forEach(dropRushInstance::addDrop);
        }
        dropRushInstance.setTicksPerDrop(Math.max(DROP_TICKS / Math.max(1, dropRushInstance.getDrops().size()), 1));
        Balm.networking().sendTo(player, new ClientboundStartDropRushPacket(dropRushInstance.getMaxTicks()));
        player.awardStat(ModStats.dropRushesTriggered);
        activeDropRushes.put(level.dimension(), pos, dropRushInstance);
    }

    private static void spawnDropRushItem(Level level, DropRushInstance dropRush, ItemStack itemStack) {
        final float deltaX = (random.nextFloat() - 0.5f) * 0.7f;
        final float deltaY = random.nextFloat() * 0.5f + 0.2f;
        final float deltaZ = (random.nextFloat() - 0.5f) * 0.7f;
        final var x = dropRush.getPos().getX() + 0.5f;
        final var y = dropRush.getPos().getY() + 0.25f;
        final var z = dropRush.getPos().getZ() + 0.5f;
        final var itemEntity = new DropRushItemEntity(level, x, y, z, itemStack, deltaX, deltaY, deltaZ);
        itemEntity.setPickUpDelay(20);
        itemEntity.setUnlimitedLifetime();
        itemEntity.setActualLifetime(dropRush.getMaxTicks() - dropRush.getTicksPassed());
        itemEntity.setTarget(dropRush.getPlayerId());
        level.addFreshEntity(itemEntity);
        dropRush.addEntity(itemEntity);
    }

    private static Optional<Holder.Reference<DropRushEvent>> rollEvent(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player, boolean force) {
        final var events = level.registryAccess().lookupOrThrow(ModDynamicRegistries.DROP_RUSH);
        final var candidates = new ArrayList<Holder.Reference<DropRushEvent>>();
        final var baseChance = LittleJoysConfig.getActive().dropRush.baseChance;
        final var effectiveBaseChance = force ? baseChance : StarOfFate.applyChanceBonus(player, baseChance);
        final var roll = random.nextFloat();
        for (final var eventHolder : events.listElements().toList()) {
            if (isValidEventFor(eventHolder, level, pos, state, player) && (force || roll <= effectiveBaseChance * eventHolder.value().chanceMultiplier())) {
                candidates.add(eventHolder);
            }
        }
        return WeightedRandom.getRandomItem(random, candidates, it -> it.value().weight());
    }

    private static boolean isValidEventFor(Holder.Reference<DropRushEvent> eventHolder, ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        final var context = MutableShogiContext.of(player)
                .withLevel(level)
                .withBlockPos(pos)
                .withBlockState(state)
                .withItemStack(player.getMainHandItem());
        return eventHolder.value().eventCondition().test(context);
    }

}
