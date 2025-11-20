package net.blay09.mods.littlejoys.block.entity;

import net.blay09.mods.balm.world.level.block.entity.OnLoadHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DigSpotBlockEntity extends BlockEntity implements OnLoadHandler {

    private ResourceKey<Recipe<?>> recipeId;
    private BlockState stateBelow;

    public DigSpotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.digSpot.value(), pos, state);
    }

    @Override
    public void onLoad() {
        if (level != null) {
            stateBelow = level.getBlockState(worldPosition.below());
        }
    }

    public BlockState getStateBelow() {
        return stateBelow;
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
