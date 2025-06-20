package net.blay09.mods.littlejoys.loot;

import net.blay09.mods.balm.api.loot.BalmLootModifier;
import net.blay09.mods.littlejoys.block.entity.FishingSpotBlockEntity;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FishingSpotLootModifier implements BalmLootModifier {

    private static final Set<LootContext> activeContexts = new HashSet<>();

    @Override
    public void apply(LootContext context, List<ItemStack> list) {
        synchronized (activeContexts) {
            if (activeContexts.contains(context)) {
                return;
            }
        }

        final var level = context.getLevel();
        final var origin = context.getParamOrNull(LootContextParams.ORIGIN);
        final var entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (origin == null || !(entity instanceof FishingSpotHolder fishingSpotHolder) || !(fishingSpotHolder.littlejoys$getPlayerOwner() instanceof ServerPlayer player)) {
            return;
        }

        final var fishingSpotPos = fishingSpotHolder.getFishingSpot();
        if (fishingSpotPos.isPresent() && level.getBlockEntity(fishingSpotPos.get()) instanceof FishingSpotBlockEntity fishingSpot) {
            FishingSpotHandler.resolveRecipe(level, fishingSpotPos.get(), fishingSpot.getRecipeId(), player).ifPresent(recipe -> {
                final var lootTableId = recipe.lootTable();
                if (lootTableId != BuiltInLootTables.EMPTY) {
                    final var lootTable = level.getServer().getLootData().getLootTable(lootTableId);
                    synchronized (activeContexts) {
                        activeContexts.add(context);
                    }
                    lootTable.getRandomItems(context, list::add);
                    synchronized (activeContexts) {
                        activeContexts.remove(context);
                    }
                }
            });
        }
    }

}
