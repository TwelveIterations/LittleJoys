package net.blay09.mods.littlejoys.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class DigSpotBlockEntity extends BlockEntity {

    private @Nullable ResourceKey<Recipe<?>> recipeId;

    public DigSpotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.digSpot.value(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.storeNullable("recipe", ResourceKey.codec(Registries.RECIPE), recipeId);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        recipeId = input.read("recipe", ResourceKey.codec(Registries.RECIPE)).orElse(null);
    }

    public @Nullable ResourceKey<Recipe<?>> getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(ResourceKey<Recipe<?>> recipeId) {
        this.recipeId = recipeId;
    }
}
