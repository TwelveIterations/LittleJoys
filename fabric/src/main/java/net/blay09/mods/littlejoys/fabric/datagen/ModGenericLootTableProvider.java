package net.blay09.mods.littlejoys.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.blay09.mods.littlejoys.LittleJoys.id;

public class ModGenericLootTableProvider extends SimpleFabricLootTableProvider {

    public ModGenericLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ALL_PARAMS);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(key(id("fishing_spot/water")), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL).setWeight(5))
                        .add(LootItem.lootTableItem(Items.PUFFERFISH).setWeight(4))
                        .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS)
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                        .add(LootItem.lootTableItem(Items.ECHO_SHARD).setWeight(2))
                        .add(LootItem.lootTableItem(Items.TURTLE_SCUTE).setWeight(2))
                        .add(LootItem.lootTableItem(Items.HEART_OF_THE_SEA).setWeight(1))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .add(LootItem.lootTableItem(Items.RAW_GOLD)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .add(LootItem.lootTableItem(Items.TOTEM_OF_UNDYING))
                        .add(LootItem.lootTableItem(Items.DIAMOND))
                        .add(LootItem.lootTableItem(Items.EMERALD))
                )
        );

        output.accept(key(id("drop_rush/sweet_berry_bush")), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.SWEET_BERRIES)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                )
        );

        output.accept(key(id("dig_spot/basalt")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );

        output.accept(key(id("dig_spot/blackstone")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );

        output.accept(key(id("dig_spot/bone_block")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))))
        );

        output.accept(key(id("dig_spot/clay")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/crimson_nylium")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );

        output.accept(key(id("dig_spot/dirt")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/end_stone")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(endDigSpot())
        );

        output.accept(key(id("dig_spot/grass_block")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/gravel")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/ice")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/magma_block")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );

        output.accept(key(id("dig_spot/moss_block")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/mud")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/mycelium")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/netherrack")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );

        output.accept(key(id("dig_spot/podzol")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/red_sand")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/red_sandstone")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/sand")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/sandstone")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/snow_block")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/soul_sand")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );

        output.accept(key(id("dig_spot/soul_soil")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );

        output.accept(key(id("dig_spot/stone")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/terracotta")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(overworldDigSpot())
        );

        output.accept(key(id("dig_spot/warped_nylium")), LootTable.lootTable()
                .withPool(genericDigSpot())
                .withPool(netherDigSpot())
        );
    }

    private static ResourceKey<LootTable> key(Identifier id) {
        return ResourceKey.create(Registries.LOOT_TABLE, id);
    }

    private static LootPool.Builder genericDigSpot() {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.DIAMOND).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                .add(LootItem.lootTableItem(Items.BOOK).setWeight(4).apply(EnchantRandomlyFunction.randomEnchantment()))
                .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))));
    }

    private static LootPool.Builder overworldDigSpot() {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6))))
                .add(LootItem.lootTableItem(Items.BONE).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                .add(LootItem.lootTableItem(Items.ENDER_PEARL))
                .add(LootItem.lootTableItem(Items.CARROT).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                .add(LootItem.lootTableItem(Items.POTATO).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))));
    }

    private static LootPool.Builder netherDigSpot() {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(Items.BLAZE_ROD).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(Items.MAGMA_CREAM).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))));
    }

    private static LootPool.Builder endDigSpot() {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(Items.CHORUS_FRUIT).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                .add(LootItem.lootTableItem(Items.SHULKER_SHELL));
    }

}
