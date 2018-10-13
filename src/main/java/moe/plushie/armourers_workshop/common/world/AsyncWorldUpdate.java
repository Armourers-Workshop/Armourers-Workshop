package moe.plushie.armourers_workshop.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class AsyncWorldUpdate {
    
    private int delay;
    private final IBlockState state;
    private final BlockPos pos;
    private final int dimensionId;
    private TileEntity tileEntity;
    
    public AsyncWorldUpdate(IBlockState state, BlockPos pos, World world) {
        this(state, pos, world.provider.getDimension());
    }
    
    public AsyncWorldUpdate(IBlockState state, BlockPos pos, int dimensionId) {
        this.state = state;
        this.pos = pos;
        this.dimensionId = dimensionId;
    }
    
    public AsyncWorldUpdate setDelay(int delay) {
        this.delay = delay;
        return this;
    }
    
    public AsyncWorldUpdate setTileEntity(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
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
    
    public IBlockState getState() {
        return state;
    }
    
    public BlockPos getPos() {
        return pos;
    }
    
    public int getDimensionId() {
        return dimensionId;
    }

    public void doUpdate(World world) {
        world.setBlockState(pos, state, 3);
        if (tileEntity != null) {
            world.setTileEntity(getPos(), tileEntity);
        }
    }
}
