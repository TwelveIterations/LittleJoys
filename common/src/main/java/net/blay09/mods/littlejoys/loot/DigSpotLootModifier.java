package net.blay09.mods.littlejoys.loot;

import net.blay09.mods.balm.world.level.storage.loot.BalmLootModifier;
import net.blay09.mods.littlejoys.block.entity.DigSpotBlockEntity;
import net.blay09.mods.littlejoys.handler.DigSpotHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DigSpotLootModifier implements BalmLootModifier {

    private static final ThreadLocal<Boolean> isApplyingLoot = ThreadLocal.withInitial(() -> false);

    @Override
    public void apply(LootContext context, List<ItemStack> list, @Nullable ResourceKey<LootTable> lootTableId) {
        if (isApplyingLoot.get()) {
            return;
        }

        final var level = context.getLevel();
        final var vec = context.getOptionalParameter(LootContextParams.ORIGIN);
        final var state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        final var blockEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (state == null || vec == null || !(blockEntity instanceof DigSpotBlockEntity digSpot)) {
            return;
        }

        DigSpotHandler.recipeById(level, digSpot.getRecipeId()).ifPresent(recipe -> {
            final var recipeLootTableId = recipe.lootTable();
            final var lootTable = level.getServer().reloadableRegistries().getLootTable(recipeLootTableId);
            isApplyingLoot.set(true);
            try {
                lootTable.getRandomItems(context, list::add);
            } finally {
                isApplyingLoot.set(false);
            }
        });
    }

}
