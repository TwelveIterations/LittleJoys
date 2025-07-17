package net.blay09.mods.littlejoys.handler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BreakBlockEvent;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.mixin.RecipeManagerAccessor;
import net.blay09.mods.littlejoys.network.protocol.ClientboundGoldRushPacket;
import net.blay09.mods.littlejoys.recipe.GoldRushRecipe;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Optional;

public class GoldRushHandler {
    private static final RandomSource random = RandomSource.create();

    private static final Table<ResourceKey<Level>, BlockPos, GoldRushInstance> activeGoldRushes = HashBasedTable.create();

    public static void initialize() {
        Balm.getEvents().onEvent(BreakBlockEvent.class, event -> {
            if (event.getPlayer().getAbilities().instabuild) {
                return;
            }

            if (Balm.getHooks().isFakePlayer(event.getPlayer())) {
                return;
            }

            final var hasSilkTouch = event.getLevel().registryAccess().lookup(Registries.ENCHANTMENT)
                    .flatMap(it -> it.get(Enchantments.SILK_TOUCH))
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

            var activeGoldRush = activeGoldRushes.get(level.dimension(), event.getPos());
            if (activeGoldRush == null) {
                activeGoldRush = rollForGoldRush(serverLevel, event.getPos(), event.getState(), serverPlayer).orElse(null);
            }
            if (activeGoldRush != null) {
                if (activeGoldRush.getDropCooldownTicks() <= 0) {
                    final var pos = activeGoldRush.getPos();
                    final var lootParamsBuilder = (new LootParams.Builder(((ServerLevel) level)))
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
                    final var lootTableId = activeGoldRush.getLootTable();
                    if (lootTableId.isPresent()) {
                        final var lootParams = lootParamsBuilder.withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(pos))
                                .create(LootContextParamSets.BLOCK);
                        final var lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableId.get());
                        lootTable.getRandomItems(lootParams).forEach((itemStack) -> Block.popResource(level, pos, itemStack));
                    }
                    activeGoldRush.setDropCooldownTicks(activeGoldRush.getTicksPerDrop());
                }
                event.setCanceled(true);
            }
        });

        Balm.getEvents().onTickEvent(TickType.ServerLevel, TickPhase.Start, level -> {
            for (final var goldRush : activeGoldRushes.row(level.dimension()).values()) {
                goldRush.setTicksPassed(goldRush.getTicksPassed() + 1);
                goldRush.setDropCooldownTicks(goldRush.getDropCooldownTicks() - 1);
                if (goldRush.getTicksPassed() >= goldRush.getMaxTicks()) {
                    if (level.getBlockState(goldRush.getPos()).equals(goldRush.getInitialState())) {
                        level.destroyBlock(goldRush.getPos(), true, goldRush.getPlayer());
                    }
                    Balm.getNetworking().sendToAll(level.getServer(), new ClientboundGoldRushPacket(goldRush.getPos(), false));
                }
            }
            activeGoldRushes.values().removeIf(it -> it.getTicksPassed() >= it.getMaxTicks());
        });
    }

    public static Optional<GoldRushInstance> rollForGoldRush(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return rollRecipe(level, pos, state, player, false).map(recipe -> startGoldRush(level, pos, state, player, recipe));
    }

    public static boolean isInGoldRush(ServerLevel level, BlockPos pos) {
        return activeGoldRushes.contains(level.dimension(), pos);
    }

    public static Optional<GoldRushInstance> startGoldRush(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        return rollRecipe(level, pos, state, player, true).map(recipe -> startGoldRush(level, pos, state, player, recipe));
    }

    public static GoldRushInstance startGoldRush(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player, RecipeHolder<GoldRushRecipe> recipeHolder) {
        final var recipe = recipeHolder.value();
        final var activeGoldRush = new GoldRushInstance(pos,
                state,
                Optional.of(recipe.lootTable()),
                (int) Math.floor(20 * recipe.seconds()),
                recipe.maxDropsPerSecond() == -1 ? 0 : (int) Math.floor(20 / recipe.maxDropsPerSecond()),
                player);
        Balm.getNetworking().sendToTracking(level, pos, new ClientboundGoldRushPacket(pos, true));
        player.awardStat(ModStats.goldRushesTriggered);
        activeGoldRushes.put(level.dimension(), pos, activeGoldRush);
        return activeGoldRush;
    }

    private static Optional<RecipeHolder<GoldRushRecipe>> rollRecipe(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player, boolean force) {
        final var recipeManager = level.getServer().getRecipeManager();
        final var recipeMap = ((RecipeManagerAccessor) recipeManager).getRecipes();
        final var recipes = recipeMap.byType(ModRecipeTypes.goldRushRecipeType);
        final var candidates = new ArrayList<WeightedRecipeHolder<GoldRushRecipe>>();
        final var baseChance = LittleJoysConfig.getActive().goldRush.baseChance;
        final var roll = random.nextFloat();
        for (final var recipeHolder : recipes) {
            if (isValidRecipeFor(recipeHolder, level, pos, state, player) && (force || roll <= baseChance * recipeHolder.value().chanceMultiplier())) {
                candidates.add(new WeightedRecipeHolder<>(recipeHolder));
            }
        }
        return WeightedRandom.getRandomItem(random, candidates).map(WeightedRecipeHolder::recipeHolder);
    }

    private static boolean isValidRecipeFor(RecipeHolder<GoldRushRecipe> recipe, ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        final var context = new EventContextImpl(level, pos, state, player);
        return recipe.value().eventCondition().test(context);
    }
}
