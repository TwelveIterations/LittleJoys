package net.blay09.mods.littlejoys.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FishingSpotBlockEntity extends BlockEntity {

    private ResourceKey<Recipe<?>> recipeId;

    public FishingSpotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.fishingSpot.value(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.storeNullable("recipe", ResourceKey.codec(Registries.RECIPE), recipeId);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        recipeId = input.read("recipe", ResourceKey.codec(Registries.RECIPE)).orElse(null);
    }

    public ResourceKey<Recipe<?>> getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(ResourceKey<Recipe<?>> recipeId) {
        this.recipeId = recipeId;
    }
}
