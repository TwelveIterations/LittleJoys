package net.blay09.mods.littlejoys.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.recipe.condition.EventConditionRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record DigSpotRecipe(EventCondition eventCondition, ResourceKey<LootTable> lootTable,
                            Weight weight) implements Recipe<RecipeInput>, WeightedEntry {

    @Override
    public RecipeType<DigSpotRecipe> getType() {
        return ModRecipeTypes.digSpotRecipeType;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<DigSpotRecipe> getSerializer() {
        return ModRecipeTypes.digSpotRecipeSerializer;
    }

    @Override
    public Weight getWeight() {
        return weight;
    }

    public static class Serializer implements RecipeSerializer<DigSpotRecipe> {

        private static final MapCodec<DigSpotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EventConditionRegistry.CODEC.fieldOf("eventCondition").forGetter(DigSpotRecipe::eventCondition),
                ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(DigSpotRecipe::lootTable),
                Weight.CODEC.fieldOf("weight").orElse(Weight.of(1)).forGetter(DigSpotRecipe::weight)
        ).apply(instance, DigSpotRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, DigSpotRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork,
                Serializer::fromNetwork);

        private static DigSpotRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            final var eventCondition = EventConditionRegistry.conditionFromNetwork(buf);
            final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
            final var weight = Weight.of(buf.readInt());
            return new DigSpotRecipe(eventCondition, lootTable, weight);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, DigSpotRecipe recipe) {
            EventConditionRegistry.conditionToNetwork(buf, recipe.eventCondition);
            buf.writeResourceKey(recipe.lootTable);
            buf.writeInt(recipe.weight.asInt());
        }

        @Override
        public MapCodec<DigSpotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DigSpotRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
