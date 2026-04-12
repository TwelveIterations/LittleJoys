package net.blay09.mods.littlejoys.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModAdvancements {

    public static final Identifier root = id("littlejoys/root");
    public static final Identifier digSpot = id("littlejoys/dig_spot");
    public static final Identifier fishingSpot = id("littlejoys/fishing_spot");
    public static final Identifier dropRushComplete = id("littlejoys/drop_rush_complete");
    public static final Identifier goldRush = id("littlejoys/gold_rush");

    public static void awardDigSpot(ServerPlayer player) {
        awardRootAnd(player, digSpot);
    }

    public static void awardFishingSpot(ServerPlayer player) {
        awardRootAnd(player, fishingSpot);
    }

    public static void awardDropRushComplete(ServerPlayer player) {
        awardRootAnd(player, dropRushComplete);
    }

    public static void awardGoldRush(ServerPlayer player) {
        awardRootAnd(player, goldRush);
    }

    private static void awardRootAnd(ServerPlayer player, Identifier id) {
        award(player, root);
        award(player, id);
    }

    private static void award(ServerPlayer player, Identifier id) {
        final var server = player.level().getServer();
        final AdvancementHolder advancement = server.getAdvancements().get(id);
        if (advancement == null) {
            return;
        }

        final var playerAdvancements = player.getAdvancements();
        final var progress = playerAdvancements.getOrStartProgress(advancement);
        final List<String> remainingCriteria = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remainingCriteria::add);
        for (final var criterion : remainingCriteria) {
            playerAdvancements.award(advancement, criterion);
        }
    }
}
