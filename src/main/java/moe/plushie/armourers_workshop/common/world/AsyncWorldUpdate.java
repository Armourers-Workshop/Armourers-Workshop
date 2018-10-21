package moe.plushie.armourers_workshop.common.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AsyncWorldUpdate {

    protected final BlockPos pos;
    protected final int dimensionId;
    protected int delay;

    public AsyncWorldUpdate(BlockPos pos, int dimensionId) {
        this.pos = pos;
        this.dimensionId = dimensionId;
    }

    public AsyncWorldUpdate setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public boolean ready() {
        if (delay > 0) {
            delay--;
            return false;
        }
        return true;
    }

    public int getDelay() {
        return delay;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public abstract void doUpdate(World world);
}
