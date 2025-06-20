package net.blay09.mods.littlejoys.recipe.condition;

import net.blay09.mods.littlejoys.api.EventContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record EventContextImpl(Level level, BlockPos pos, BlockState state, Player player) implements EventContext {
}
