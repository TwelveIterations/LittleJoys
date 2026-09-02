package net.blay09.mods.littlejoys.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.recipe.condition.LittleJoysRules;
import net.blay09.mods.shogi.effect.EmptyEffect;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record FishingSpotRecipe(ShogiEffect<?> eventCondition, ResourceKey<LootTable> lootTable,
                                int weight) implements Recipe<RecipeInput> {

    @Override
    public RecipeType<FishingSpotRecipe> getType() {
        return ModRecipeTypes.fishingSpot.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipeTypes.fishingSpot.bookCategory();
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<FishingSpotRecipe> getSerializer() {
        return ModRecipeTypes.fishingSpot.serializer();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    private static final MapCodec<FishingSpotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LittleJoysRules.SCOPE.getEffectCodec().fieldOf("eventCondition").forGetter(FishingSpotRecipe::eventCondition),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(FishingSpotRecipe::lootTable),
            Codec.INT.fieldOf("weight").orElse(1).forGetter(FishingSpotRecipe::weight)
    ).apply(instance, FishingSpotRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FishingSpotRecipe> STREAM_CODEC = StreamCodec.of(FishingSpotRecipe::toNetwork,
            FishingSpotRecipe::fromNetwork);

    private static FishingSpotRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
        final var weight = buf.readVarInt();
        return new FishingSpotRecipe(EmptyEffect.INSTANCE, lootTable, weight);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, FishingSpotRecipe recipe) {
        buf.writeResourceKey(recipe.lootTable);
        buf.writeVarInt(recipe.weight);
    }

    public static RecipeSerializer<FishingSpotRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
