package net.blay09.mods.littlejoys.handler;

import net.blay09.mods.littlejoys.entity.DropRushItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DropRushInstance {
    private final UUID playerId;
    private final BlockPos pos;
    private final ResourceKey<LootTable> lootTable;
    private final int maxTicks;

    private final List<DropRushItemEntity> entities = new ArrayList<>();
    private final List<ItemStack> drops = new ArrayList<>();
    private int ticksPerDrop;

    private int ticksPassed;
    private int dropCooldownTicks;

    public DropRushInstance(UUID playerId, BlockPos pos, ResourceKey<LootTable> lootTable, int maxTicks) {
        this.playerId = playerId;
        this.pos = pos;
        this.lootTable = lootTable;
        this.maxTicks = maxTicks;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ResourceKey<LootTable> getLootTable() {
        return lootTable;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    public int getTicksPerDrop() {
        return ticksPerDrop;
    }

    public void setTicksPerDrop(int ticksPerDrop) {
        this.ticksPerDrop = ticksPerDrop;
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

    public void addDrop(ItemStack itemStack) {
        drops.add(itemStack);
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public void addEntity(DropRushItemEntity entity) {
        entities.add(entity);
    }

    public List<DropRushItemEntity> getEntities() {
        return entities;
    }
}
