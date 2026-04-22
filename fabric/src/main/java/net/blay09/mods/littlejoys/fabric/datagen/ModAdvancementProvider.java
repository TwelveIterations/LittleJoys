package net.blay09.mods.littlejoys.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModAdvancementProvider extends FabricAdvancementProvider {

    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        final var root = Advancement.Builder.advancement()
                .display(
                        Items.COOKIE,
                        Component.translatable("advancements.littlejoys.root.title"),
                        Component.translatable("advancements.littlejoys.root.description"),
                        ResourceLocation.withDefaultNamespace("gui/advancements/backgrounds/stone"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("trigger", impossible())
                .save(consumer, id("littlejoys/root").toString());

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.ARMS_UP_POTTERY_SHERD,
                        Component.translatable("advancements.littlejoys.dig_spot.title"),
                        Component.translatable("advancements.littlejoys.dig_spot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("trigger", impossible())
                .save(consumer, id("littlejoys/dig_spot").toString());

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.PUFFERFISH,
                        Component.translatable("advancements.littlejoys.fishing_spot.title"),
                        Component.translatable("advancements.littlejoys.fishing_spot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("trigger", impossible())
                .save(consumer, id("littlejoys/fishing_spot").toString());

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.SWEET_BERRIES,
                        Component.translatable("advancements.littlejoys.drop_rush_complete.title"),
                        Component.translatable("advancements.littlejoys.drop_rush_complete.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("trigger", impossible())
                .save(consumer, id("littlejoys/drop_rush_complete").toString());

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.RAW_GOLD,
                        Component.translatable("advancements.littlejoys.gold_rush.title"),
                        Component.translatable("advancements.littlejoys.gold_rush.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("trigger", impossible())
                .save(consumer, id("littlejoys/gold_rush").toString());
    }

    private static Criterion<ImpossibleTrigger.TriggerInstance> impossible() {
        return new Criterion<>(CriteriaTriggers.IMPOSSIBLE, new ImpossibleTrigger.TriggerInstance());
    }
}
