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

import java.util.List;

public class FishingSpotLootModifier implements BalmLootModifier {

    private static final ThreadLocal<Boolean> isApplyingLoot = ThreadLocal.withInitial(() -> false);

    @Override
    public void apply(LootContext context, List<ItemStack> list) {
        if (isApplyingLoot.get()) {
            return;
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
                    isApplyingLoot.set(true);
                    try {
                        lootTable.getRandomItems(context, list::add);
                    } finally {
                        isApplyingLoot.set(false);
                    }
                }
            });
        }
    }

}
