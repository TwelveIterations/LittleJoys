package net.blay09.mods.littlejoys.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.littlejoys.recipe.condition.LittleJoysRules;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record GoldRushRecipe(ShogiEffect<?> eventCondition,
                             float chanceMultiplier,
                             ResourceKey<LootTable> lootTable,
                             float seconds,
                             float maxDropsPerSecond,
                             int weight) implements Recipe<RecipeInput> {

    @Override
    public RecipeType<GoldRushRecipe> getType() {
        return ModRecipeTypes.goldRush.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipeTypes.goldRush.bookCategory();
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
    public RecipeSerializer<GoldRushRecipe> getSerializer() {
        return ModRecipeTypes.goldRush.serializer();
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

    private static final MapCodec<GoldRushRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LittleJoysRules.EVENT_CONDITIONS.getEffectCodec().fieldOf("eventCondition").forGetter(GoldRushRecipe::eventCondition),
            Codec.FLOAT.fieldOf("chanceMultiplier").orElse(1f).forGetter(GoldRushRecipe::chanceMultiplier),
            ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(GoldRushRecipe::lootTable),
            Codec.FLOAT.fieldOf("seconds").orElse(7f).forGetter(GoldRushRecipe::seconds),
            Codec.FLOAT.fieldOf("maxDropsPerSecond").orElse(-1f).forGetter(GoldRushRecipe::maxDropsPerSecond),
            Codec.INT.fieldOf("weight").orElse(1).forGetter(GoldRushRecipe::weight)
    ).apply(instance, GoldRushRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, GoldRushRecipe> STREAM_CODEC = StreamCodec.of(GoldRushRecipe::toNetwork, GoldRushRecipe::fromNetwork);

    private static GoldRushRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        final var chanceMultiplier = buf.readFloat();
        final var lootTable = buf.readResourceKey(Registries.LOOT_TABLE);
        final var seconds = buf.readFloat();
        final var maxDropsPerSecond = buf.readFloat();
        final var weight = buf.readVarInt();
        return new GoldRushRecipe(LittleJoysRules.UNSYNCED_EVENT_CONDITION, chanceMultiplier, lootTable, seconds, maxDropsPerSecond, weight);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, GoldRushRecipe recipe) {
        buf.writeFloat(recipe.chanceMultiplier);
        buf.writeResourceKey(recipe.lootTable);
        buf.writeFloat(recipe.seconds);
        buf.writeFloat(recipe.maxDropsPerSecond);
        buf.writeVarInt(recipe.weight);
    }

    public static RecipeSerializer<GoldRushRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
