package net.blay09.mods.littlejoys.handler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BreakBlockEvent;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.entity.DropRushItemEntity;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStartDropRushPacket;
import net.blay09.mods.littlejoys.network.protocol.ClientboundStopDropRushPacket;
import net.blay09.mods.littlejoys.recipe.DropRushRecipe;
import net.blay09.mods.littlejoys.recipe.ModRecipeTypes;
import net.blay09.mods.littlejoys.recipe.WeightedRecipeHolder;
import net.blay09.mods.littlejoys.recipe.condition.EventContextImpl;
import net.blay09.mods.littlejoys.stats.ModStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
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
        Balm.getEvents().onEvent(BreakBlockEvent.class, event -> {
            if (event.getPlayer().getAbilities().instabuild) {
                return;
            }

            if (Balm.getHooks().isFakePlayer(event.getPlayer())) {
                return;
            }

            final var hasSilkTouch = event.getLevel().registryAccess().registry(Registries.ENCHANTMENT)
                    .flatMap(it -> it.getHolder(Enchantments.SILK_TOUCH))
                    .map(it -> EnchantmentHelper.getEnchantmentLevel(it, event.getPlayer()) > 0)
                    .orElse(false);
            if (hasSilkTouch) {
                return;
            }

            final var level = event.getLevel();
            final var player = event.getPlayer();
            if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return;
            }

            handleDropRushChance(serverLevel, event.getPos(), event.getState(), serverPlayer);
        });

        Balm.getEvents().onTickEvent(TickType.ServerLevel, TickPhase.Start, level -> {
            for (final var dropRush : activeDropRushes.row(level.dimension()).values()) {
                dropRush.setTicksPassed(dropRush.getTicksPassed() + 1);
                dropRush.setDropCooldownTicks(dropRush.getDropCooldownTicks() - 1);
                final var dropsLeft = dropRush.getDrops();
                if (dropRush.getDropCooldownTicks() <= 0 && !dropsLeft.isEmpty()) {
                    final var nextDropItemStack = dropsLeft.remove(dropsLeft.size() - 1);
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
                        Balm.getNetworking().sendTo(player, new ClientboundStopDropRushPacket(ClientboundStopDropRushPacket.Reason.FULL_CLEAR));
                    }
                } else if (dropRush.getTicksPassed() >= dropRush.getMaxTicks()) {
                    final var player = level.getPlayerByUUID(dropRush.getPlayerId());
                    if (player != null) {
                        Balm.getNetworking().sendTo(player, new ClientboundStopDropRushPacket(ClientboundStopDropRushPacket.Reason.TIME_UP));
                    }
                }
            }
            activeDropRushes.values().removeIf(it -> it.getTicksPassed() >= it.getMaxTicks() || it.getEntities().isEmpty());
        });
    }

    public static void handleDropRushChance(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        findRecipe(level, pos, state, player).ifPresent(recipeHolder -> {
            final var recipe = recipeHolder.value();
            final var dropRushInstance = new DropRushInstance(
                    player.getUUID(),
                    pos,
                    state,
                    recipe.lootTable(),
                    (int) Math.floor(20 * recipe.seconds()));
            final var lootParamsBuilder = (new LootParams.Builder(level))
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
            final var lootTableId = recipe.lootTable();
            if (lootTableId != BuiltInLootTables.EMPTY) {
                final var lootParams = lootParamsBuilder.withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(pos))
                        .create(LootContextParamSets.BLOCK);
                final var lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableId);
                for (int i = 0; i < recipe.rolls(); i++) {
                    lootTable.getRandomItems(lootParams).forEach(dropRushInstance::addDrop);
                }
            }
            dropRushInstance.setTicksPerDrop(Math.max(DROP_TICKS / Math.max(1, dropRushInstance.getDrops().size()), 1));
            Balm.getNetworking().sendTo(player, new ClientboundStartDropRushPacket(dropRushInstance.getMaxTicks()));
            player.awardStat(ModStats.dropRushesTriggered);
            activeDropRushes.put(level.dimension(), pos, dropRushInstance);
        });
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

    private static Optional<RecipeHolder<DropRushRecipe>> findRecipe(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        final var recipeManager = level.getRecipeManager();
        final var recipes = recipeManager.getAllRecipesFor(ModRecipeTypes.dropRushRecipeType);
        final var candidates = new ArrayList<WeightedRecipeHolder<DropRushRecipe>>();
        final var baseChance = LittleJoysConfig.getActive().dropRush.baseChance;
        final var roll = random.nextFloat();
        for (final var recipeHolder : recipes) {
            if (isValidRecipeFor(recipeHolder, level, pos, state, player) && roll <= baseChance * recipeHolder.value().chanceMultiplier()) {
                candidates.add(new WeightedRecipeHolder<>(recipeHolder));
            }
        }
        return WeightedRandom.getRandomItem(random, candidates).map(WeightedRecipeHolder::recipeHolder);
    }

    private static boolean isValidRecipeFor(RecipeHolder<DropRushRecipe> recipe, ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        final var context = new EventContextImpl(level, pos, state, player);
        return recipe.value().eventCondition().test(context);
    }

}
