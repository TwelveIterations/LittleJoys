package net.blay09.mods.littlejoys.loot;

import net.blay09.mods.balm.world.level.storage.loot.BalmLootModifier;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.blessing.BlessingManager;
import net.blay09.mods.littlejoys.blessing.Blessings;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.blay09.mods.littlejoys.tag.ModBlockTags;
import net.blay09.mods.littlejoys.tag.ModItemTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StarOfAbundanceLootModifier implements BalmLootModifier {

    @Override
    public void apply(LootContext context, List<ItemStack> list, @Nullable ResourceKey<LootTable> lootTableId) {
        final var state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        final var entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (state == null || !state.is(ModBlockTags.STAR_OF_ABUNDANCE_BLOCKS) || !(entity instanceof ServerPlayer player)) {
            return;
        }

        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_ABUNDANCE)) {
            return;
        }

        final var validStacks = new ArrayList<ItemStack>();
        for (final var stack : list) {
            if (!stack.isEmpty() && stack.is(ModItemTags.STAR_OF_ABUNDANCE_ITEMS)) {
                validStacks.add(stack);
            }
        }

        if (validStacks.isEmpty()) {
            return;
        }

        final var random = context.getRandom();
        final var duplicateChance = LittleJoysConfig.getActive().blessings.starOfAbundanceUseChance;
        if (random.nextFloat() < duplicateChance) {
            final var duplicate = validStacks.get(random.nextInt(validStacks.size())).copy();
            list.add(duplicate);
            activeBlessing.consumeUse();
            context.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
        }
    }
}
