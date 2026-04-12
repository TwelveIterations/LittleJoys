package net.blay09.mods.littlejoys.client.handler;

import net.minecraft.core.BlockPos;

class ClientGoldRushInstance {
    private final BlockPos pos;
    private int ticksPassed;

    ClientGoldRushInstance(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getTicksPassed() {
        return ticksPassed;
    }

    public void setTicksPassed(int ticksPassed) {
        this.ticksPassed = ticksPassed;
    }
}
