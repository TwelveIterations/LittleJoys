package net.blay09.mods.littlejoys.block.entity;

import net.minecraft.core.BlockPos;
import net.blay09.mods.littlejoys.registry.DigSpotEvent;
import net.blay09.mods.littlejoys.registry.ModDynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class DigSpotBlockEntity extends BlockEntity {

    private @Nullable ResourceKey<DigSpotEvent> eventKey;

    public DigSpotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.digSpot.value(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.storeNullable("event", ResourceKey.codec(ModDynamicRegistries.DIG_SPOT), eventKey);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        eventKey = input.read("event", ResourceKey.codec(ModDynamicRegistries.DIG_SPOT)).orElse(null);
    }

    public @Nullable ResourceKey<DigSpotEvent> getEventKey() {
        return eventKey;
    }

    public void setEventKey(ResourceKey<DigSpotEvent> eventKey) {
        this.eventKey = eventKey;
    }
}
