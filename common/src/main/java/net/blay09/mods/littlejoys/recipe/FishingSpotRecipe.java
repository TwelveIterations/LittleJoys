package net.blay09.mods.littlejoys.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.recipe.condition.EventConditionRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record FishingSpotRecipe(EventCondition eventCondition, ResourceKey<LootTable> lootTable, int weight) implements Recipe<RecipeInput> {

    @Override
    public RecipeType<FishingSpotRecipe> getType() {
        return ModRecipeTypes.fishingSpotRecipeType;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipeTypes.fishingSpotRecipeBookCategory;
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
    public RecipeSerializer<FishingSpotRecipe> getSerializer() {
        return ModRecipeTypes.fishingSpotRecipeSerializer;
    }
    
    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<FishingSpotRecipe> {
        private static final MapCodec<FishingSpotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EventConditionRegistry.CODEC.fieldOf("eventCondition").forGetter(FishingSpotRecipe::eventCondition),
                ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(FishingSpotRecipe::lootTable),
                Codec.INT.fieldOf("weight").orElse(1).forGetter(FishingSpotRecipe::weight)
        ).apply(instance, FishingSpotRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FishingSpotRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork,
                Serializer::fromNetwork);

        private static FishingSpotRecipe fromNetwork(FriendlyByteBuf buf) {
            final var eventCondition = EventConditionRegistry.conditionFromNetwork(buf);
            final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
            final var weight = buf.readVarInt();
            return new FishingSpotRecipe(eventCondition, lootTable, weight);
        }

        private static void toNetwork(FriendlyByteBuf buf, FishingSpotRecipe recipe) {
            EventConditionRegistry.conditionToNetwork(buf, recipe.eventCondition);
            buf.writeResourceKey(recipe.lootTable);
            buf.writeVarInt(recipe.weight);
        }

        @Override
        public MapCodec<FishingSpotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FishingSpotRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
