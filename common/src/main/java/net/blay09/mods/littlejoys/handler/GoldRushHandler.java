package net.blay09.mods.littlejoys.handler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.balm.platform.event.callback.ServerTickCallback;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.mixin.RecipeManagerAccessor;
import net.blay09.mods.littlejoys.network.protocol.ClientboundGoldRushPacket;
import net.blay09.mods.littlejoys.recipe.GoldRushRecipe;
import net.blay09.mods.littlejoys.recipe.ModRecipeTypes;
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

    private static final Table<ResourceKey<Level>, BlockPos, GoldRushInstance> activeGoldRushes = Tables.synchronizedTable(HashBasedTable.create());

    public static void initialize() {
        BlockCallback.Break.Before.EVENT.register((level, pos, state, blockEntity, player) -> {
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

            var activeGoldRush = activeGoldRushes.get(serverLevel.dimension(), pos);
            if (activeGoldRush == null) {
                activeGoldRush = rollForGoldRush(serverLevel, pos, state, serverPlayer).orElse(null);
            }
            if (activeGoldRush != null) {
                if (!state.equals(activeGoldRush.getInitialState())) {
                    activeGoldRushes.remove(serverLevel.dimension(), pos);
                    Balm.networking().sendToAll(serverLevel.getServer(), new ClientboundGoldRushPacket(serverLevel.dimension(), pos, false));
                    return true;
                }

                if (activeGoldRush.getDropCooldownTicks() <= 0) {
                    final var goldRushPos = activeGoldRush.getPos();
                    final var lootParamsBuilder = (new LootParams.Builder(serverLevel))
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(goldRushPos))
                            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(goldRushPos));
                    final var lootTableId = activeGoldRush.getLootTable();
                    if (lootTableId.isPresent()) {
                        final var lootParams = lootParamsBuilder.withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(goldRushPos))
                                .create(LootContextParamSets.BLOCK);
                        final var lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableId.get());
                        lootTable.getRandomItems(lootParams).forEach((itemStack) -> Block.popResource(serverLevel, goldRushPos, itemStack));
                    }
                    activeGoldRush.setDropCooldownTicks(activeGoldRush.getTicksPerDrop());
                }
                return false;
            }
            return true;
        });

        ServerTickCallback.ServerLevelTick.BEFORE.register(level -> {
            final var levelGoldRushes = activeGoldRushes.row(level.dimension());
            final var toRemove = new ArrayList<GoldRushInstance>();
            for (final var goldRush : levelGoldRushes.values()) {
                goldRush.setTicksPassed(goldRush.getTicksPassed() + 1);
                goldRush.setDropCooldownTicks(goldRush.getDropCooldownTicks() - 1);
                if (goldRush.getTicksPassed() >= goldRush.getMaxTicks()) {
                    if (level.getBlockState(goldRush.getPos()).equals(goldRush.getInitialState())) {
                        level.destroyBlock(goldRush.getPos(), true, goldRush.getPlayer());
                    }
                    toRemove.add(goldRush);
                }
            }
            toRemove.forEach(goldRush -> {
                Balm.networking().sendToAll(level.getServer(), new ClientboundGoldRushPacket(level.dimension(), goldRush.getPos(), false));
                levelGoldRushes.remove(goldRush.getPos());
            });
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
        player.awardStat(ModStats.goldRushesTriggered);
        activeGoldRushes.put(level.dimension(), pos, activeGoldRush);
        Balm.networking().sendToTracking(level, pos, new ClientboundGoldRushPacket(level.dimension(), pos, true));
        return activeGoldRush;
    }

    private static Optional<RecipeHolder<GoldRushRecipe>> rollRecipe(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player, boolean force) {
        final var recipeManager = level.getServer().getRecipeManager();
        final var recipeMap = ((RecipeManagerAccessor) recipeManager).getRecipes();
        final var recipes = recipeMap.byType(ModRecipeTypes.goldRush.type());
        final var candidates = new ArrayList<RecipeHolder<GoldRushRecipe>>();
        final var baseChance = LittleJoysConfig.getActive().goldRush.baseChance;
        final var roll = random.nextFloat();
        for (final var recipeHolder : recipes) {
            if (isValidRecipeFor(recipeHolder, level, pos, state, player) && (force || roll <= baseChance * recipeHolder.value().chanceMultiplier())) {
                candidates.add(recipeHolder);
            }
        }
        return WeightedRandom.getRandomItem(random, candidates, it -> it.value().weight());
    }

    private static boolean isValidRecipeFor(RecipeHolder<GoldRushRecipe> recipe, ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player) {
        final var context = new EventContextImpl(level, pos, state, player);
        return recipe.value().eventCondition().test(context);
    }
}
