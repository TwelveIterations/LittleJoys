package net.blay09.mods.littlejoys.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class GoldRushInstance {
    private final BlockPos pos;
    private final BlockState initialState;
    private final @Nullable ResourceKey<LootTable> lootTable;
    private final int maxTicks;
    private final int ticksPerDrop;
    private final @Nullable Player player;
    private int ticksPassed;
    private int dropCooldownTicks;

    public GoldRushInstance(BlockPos pos, BlockState initialState, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ResourceKey<LootTable>> lootTable, int maxTicks, int ticksPerDrop, @Nullable Player player) {
        this.pos = pos;
        this.initialState = initialState;
        this.lootTable = lootTable.orElse(null);
        this.maxTicks = maxTicks;
        this.ticksPerDrop = ticksPerDrop;
        this.player = player;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getInitialState() {
        return initialState;
    }

    public Optional<ResourceKey<LootTable>> getLootTable() {
        return Optional.ofNullable(lootTable);
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    public int getTicksPerDrop() {
        return ticksPerDrop;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public int getTicksPassed() {
        return ticksPassed;
    }

    public void setTicksPassed(int ticksPassed) {
        this.ticksPassed = ticksPassed;
    }

    public int getDropCooldownTicks() {
        return dropCooldownTicks;
    }

    public void setDropCooldownTicks(int dropCooldownTicks) {
        this.dropCooldownTicks = dropCooldownTicks;
    }
}
