package net.blay09.mods.littlejoys.loot;

import net.blay09.mods.balm.world.level.storage.loot.BalmLootModifier;
import net.blay09.mods.littlejoys.LittleJoysConfig;
import net.blay09.mods.littlejoys.blessing.BlessingManager;
import net.blay09.mods.littlejoys.blessing.Blessings;
import net.blay09.mods.littlejoys.handler.FishingSpotHolder;
import net.blay09.mods.littlejoys.sound.ModSounds;
import net.blay09.mods.littlejoys.tag.ModBlockTags;
import net.blay09.mods.littlejoys.tag.ModEntityTags;
import net.blay09.mods.littlejoys.tag.ModItemTags;
import net.minecraft.resources.Identifier;
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

    private static final Identifier FISHING_LOOT_TABLE = Identifier.withDefaultNamespace("gameplay/fishing");

    @Override
    public void apply(LootContext context, List<ItemStack> list, @Nullable ResourceKey<LootTable> lootTableId) {
        final var lootSource = getLootSource(context, lootTableId);
        if (lootSource == null) {
            return;
        }

        final var player = getPlayer(context);
        if (player == null) {
            return;
        }

        final var activeBlessing = BlessingManager.getActiveBlessing(player);
        if (activeBlessing == null || !activeBlessing.is(Blessings.STAR_OF_ABUNDANCE.value())) {
            return;
        }

        final var validStacks = getValidStacks(list);
        if (validStacks.isEmpty()) {
            return;
        }

        final var random = context.getRandom();
        final var duplicateChance = getDuplicateChance(lootSource);
        if (random.nextFloat() < duplicateChance) {
            final var duplicate = validStacks.get(random.nextInt(validStacks.size())).copy();
            list.add(duplicate);
            activeBlessing.consumeUse();
            context.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.blessingUsed, SoundSource.PLAYERS, 0.5f, (float) (0.9 + Math.random() * 0.2));
        }
    }

    private static StarOfAbundanceLootModifier.@Nullable AbundanceSource getLootSource(LootContext context, @Nullable ResourceKey<LootTable> lootTableId) {
        if (lootTableId != null && lootTableId.identifier().equals(FISHING_LOOT_TABLE)) {
            return AbundanceSource.FISHING;
        }

        final var state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        if (state != null && state.is(ModBlockTags.STAR_OF_ABUNDANCE_BLOCKS)) {
            return AbundanceSource.BLOCK_DROP;
        }

        final var entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity != null && entity.is(ModEntityTags.STAR_OF_ABUNDANCE_MOBS)) {
            return AbundanceSource.MOB_DROP;
        }

        return null;
    }

    private static @Nullable ServerPlayer getPlayer(LootContext context) {
        final var lastDamagePlayer = context.getOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER);
        if (lastDamagePlayer instanceof ServerPlayer player) {
            return player;
        }

        final var attackingEntity = context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
        if (attackingEntity instanceof ServerPlayer player) {
            return player;
        }

        final var entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof ServerPlayer player) {
            return player;
        }

        if (entity instanceof FishingSpotHolder fishingSpotHolder && fishingSpotHolder.littlejoys$getPlayerOwner() instanceof ServerPlayer player) {
            return player;
        }

        return null;
    }

    private static List<ItemStack> getValidStacks(List<ItemStack> list) {
        final var validStacks = new ArrayList<ItemStack>();
        for (final var stack : list) {
            if (!stack.isEmpty() && stack.is(ModItemTags.STAR_OF_ABUNDANCE_ITEMS)) {
                validStacks.add(stack);
            }
        }

        return validStacks;
    }

    private static float getDuplicateChance(AbundanceSource lootSource) {
        final var blessings = LittleJoysConfig.getActive().blessings;
        return switch (lootSource) {
            case BLOCK_DROP -> blessings.starOfAbundanceBlockDropChance;
            case FISHING -> blessings.starOfAbundanceFishingDropChance;
            case MOB_DROP -> blessings.starOfAbundanceMobDropChance;
        };
    }

    private enum AbundanceSource {
        BLOCK_DROP,
        FISHING,
        MOB_DROP
    }
}
