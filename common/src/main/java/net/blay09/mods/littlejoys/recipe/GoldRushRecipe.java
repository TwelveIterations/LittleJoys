package net.blay09.mods.littlejoys.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.recipe.condition.EventConditionRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record GoldRushRecipe(EventCondition eventCondition,
                             float chanceMultiplier,
                             ResourceKey<LootTable> lootTable,
                             float seconds,
                             float maxDropsPerSecond,
                             Weight weight) implements Recipe<RecipeInput>, WeightedEntry {

    @Override
    public RecipeType<GoldRushRecipe> getType() {
        return ModRecipeTypes.goldRushRecipeType;
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
    public RecipeSerializer<GoldRushRecipe> getSerializer() {
        return ModRecipeTypes.goldRushRecipeSerializer;
    }

    @Override
    public Weight getWeight() {
        return weight;
    }

    public static class Serializer implements RecipeSerializer<GoldRushRecipe> {

        private static final MapCodec<GoldRushRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EventConditionRegistry.CODEC.fieldOf("eventCondition").forGetter(GoldRushRecipe::eventCondition),
                Codec.FLOAT.fieldOf("chanceMultiplier").orElse(1f).forGetter(GoldRushRecipe::chanceMultiplier),
                ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(GoldRushRecipe::lootTable),
                Codec.FLOAT.fieldOf("seconds").orElse(7f).forGetter(GoldRushRecipe::seconds),
                Codec.FLOAT.fieldOf("maxDropsPerSecond").orElse(-1f).forGetter(GoldRushRecipe::maxDropsPerSecond),
                Weight.CODEC.fieldOf("weight").orElse(Weight.of(1)).forGetter(GoldRushRecipe::weight)
        ).apply(instance, GoldRushRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, GoldRushRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        private static GoldRushRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            final var eventCondition = EventConditionRegistry.conditionFromNetwork(buf);
            final var chanceMultiplier = buf.readFloat();
            final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
            final var seconds = buf.readFloat();
            final var maxDropsPerSecond = buf.readFloat();
            final var weight = Weight.of(buf.readVarInt());
            return new GoldRushRecipe(eventCondition, chanceMultiplier, lootTable, seconds, maxDropsPerSecond, weight);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, GoldRushRecipe recipe) {
            EventConditionRegistry.conditionToNetwork(buf, recipe.eventCondition);
            buf.writeFloat(recipe.chanceMultiplier);
            buf.writeResourceKey(recipe.lootTable);
            buf.writeFloat(recipe.seconds);
            buf.writeFloat(recipe.maxDropsPerSecond);
            buf.writeVarInt(recipe.weight.asInt());
        }

        @Override
        public MapCodec<GoldRushRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GoldRushRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
