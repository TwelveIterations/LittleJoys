package net.blay09.mods.littlejoys.recipe;

import net.blay09.mods.balm.world.item.crafting.BalmRecipeTypeRegistrar;
import net.blay09.mods.balm.world.item.crafting.DeferredRecipeType;
import net.minecraft.world.item.crafting.RecipeInput;

public class ModRecipeTypes {

    public static DeferredRecipeType<RecipeInput, DigSpotRecipe> digSpot;
    public static DeferredRecipeType<RecipeInput, FishingSpotRecipe> fishingSpot;
    public static DeferredRecipeType<RecipeInput, GoldRushRecipe> goldRush;
    public static DeferredRecipeType<RecipeInput, DropRushRecipe> dropRush;

    public static void initialize(BalmRecipeTypeRegistrar recipes) {
        digSpot = recipes.register("dig_spot", DigSpotRecipe.class)
                .withSerializer(DigSpotRecipe.Serializer::new)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        fishingSpot = recipes.register("fishing_spot", FishingSpotRecipe.class)
                .withSerializer(FishingSpotRecipe.Serializer::new)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        goldRush = recipes.register("gold_rush", GoldRushRecipe.class)
                .withSerializer(GoldRushRecipe.Serializer::new)
                .withRecipeBookCategory()
                .asDeferredRecipeType();

        dropRush = recipes.register("drop_rush", DropRushRecipe.class)
                .withSerializer(DropRushRecipe.Serializer::new)
                .withRecipeBookCategory()
                .asDeferredRecipeType();
    }
}
