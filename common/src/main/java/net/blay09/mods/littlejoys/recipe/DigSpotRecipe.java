package net.blay09.mods.littlejoys.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.api.EventCondition;
import net.blay09.mods.littlejoys.recipe.condition.EventConditionRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record DigSpotRecipe(EventCondition eventCondition, ResourceKey<LootTable> lootTable,
                            int weight) implements Recipe<RecipeInput> {

    @Override
    public RecipeType<DigSpotRecipe> getType() {
        return ModRecipeTypes.digSpot.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipeTypes.digSpot.bookCategory();
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
    public RecipeSerializer<DigSpotRecipe> getSerializer() {
        return ModRecipeTypes.digSpot.serializer();
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

    private static final MapCodec<DigSpotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EventConditionRegistry.CODEC.fieldOf("eventCondition").forGetter(DigSpotRecipe::eventCondition),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(DigSpotRecipe::lootTable),
            Codec.INT.fieldOf("weight").orElse(1).forGetter(DigSpotRecipe::weight)
    ).apply(instance, DigSpotRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, DigSpotRecipe> STREAM_CODEC = StreamCodec.of(DigSpotRecipe::toNetwork,
            DigSpotRecipe::fromNetwork);

    private static DigSpotRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        final var eventCondition = EventConditionRegistry.conditionFromNetwork(buf);
        final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
        final var weight = buf.readInt();
        return new DigSpotRecipe(eventCondition, lootTable, weight);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, DigSpotRecipe recipe) {
        EventConditionRegistry.conditionToNetwork(buf, recipe.eventCondition);
        buf.writeResourceKey(recipe.lootTable);
        buf.writeInt(recipe.weight);
    }

    public static RecipeSerializer<DigSpotRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
