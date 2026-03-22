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

public record DropRushRecipe(EventCondition eventCondition, float chanceMultiplier, ResourceKey<LootTable> lootTable,
                             int rolls, float seconds, int range, int weight) implements Recipe<RecipeInput> {

    @Override
    public RecipeType<DropRushRecipe> getType() {
        return ModRecipeTypes.dropRush.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipeTypes.dropRush.bookCategory();
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
    public RecipeSerializer<DropRushRecipe> getSerializer() {
        return ModRecipeTypes.dropRush.serializer();
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

    private static final MapCodec<DropRushRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EventConditionRegistry.CODEC.fieldOf("eventCondition").forGetter(DropRushRecipe::eventCondition),
            Codec.FLOAT.fieldOf("chanceMultiplier").orElse(1f).forGetter(DropRushRecipe::chanceMultiplier),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(DropRushRecipe::lootTable),
            Codec.INT.fieldOf("rolls").orElse(8).forGetter(DropRushRecipe::rolls),
            Codec.FLOAT.fieldOf("seconds").orElse(12.5f).forGetter(DropRushRecipe::seconds),
            Codec.INT.fieldOf("range").orElse(8).forGetter(DropRushRecipe::range),
            Codec.INT.fieldOf("weight").orElse(1).forGetter(DropRushRecipe::weight)
    ).apply(instance, DropRushRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, DropRushRecipe> STREAM_CODEC = StreamCodec.of(DropRushRecipe::toNetwork, DropRushRecipe::fromNetwork);

    private static DropRushRecipe fromNetwork(FriendlyByteBuf buf) {
        final var eventCondition = EventConditionRegistry.conditionFromNetwork(buf);
        final var chance = buf.readFloat();
        final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
        final var rolls = buf.readVarInt();
        final var seconds = buf.readFloat();
        final var range = buf.readVarInt();
        final var weight = buf.readVarInt();
        return new DropRushRecipe(eventCondition, chance, lootTable, rolls, seconds, range, weight);
    }

    private static void toNetwork(FriendlyByteBuf buf, DropRushRecipe recipe) {
        EventConditionRegistry.conditionToNetwork(buf, recipe.eventCondition);
        buf.writeFloat(recipe.chanceMultiplier);
        buf.writeResourceKey(recipe.lootTable);
        buf.writeVarInt(recipe.rolls);
        buf.writeFloat(recipe.seconds);
        buf.writeVarInt(recipe.range);
        buf.writeVarInt(recipe.weight);
    }

    public static RecipeSerializer<DropRushRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
