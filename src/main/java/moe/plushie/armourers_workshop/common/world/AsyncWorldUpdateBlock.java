package moe.plushie.armourers_workshop.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class AsyncWorldUpdateBlock extends AsyncWorldUpdate {

    private final IBlockState state;
    private boolean onlyReplaceable;
    private TileEntity tileEntity;

    public AsyncWorldUpdateBlock(IBlockState state, BlockPos pos, World world) {
        this(state, pos, world.provider.getDimension());
    }

    public AsyncWorldUpdateBlock(IBlockState state, BlockPos pos, int dimensionId) {
        super(pos, dimensionId);
        this.state = state;
        onlyReplaceable = false;
    }

    public AsyncWorldUpdateBlock setTileEntity(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
        return this;
    }

    public AsyncWorldUpdateBlock setOnlyReplaceable(boolean onlyReplaceable) {
        this.onlyReplaceable = onlyReplaceable;
        return this;
    }

    public IBlockState getState() {
        return state;
    }

    @Override
    public void doUpdate(World world) {
        boolean canPlace = true;
        if (onlyReplaceable) {
            IBlockState target = world.getBlockState(pos);
            if (!target.getBlock().isReplaceable(world, pos)) {
                canPlace = false;
            }
            if (target.getBlock() == state.getBlock()) {
                canPlace = true;
            }
        }
        if (canPlace) {
            world.setBlockState(pos, state, 2);
            if (tileEntity != null) {
                world.setTileEntity(getPos(), tileEntity);
            }
        }
    }

    @Override
    public String toString() {
        return "AsyncWorldUpdateBlock [state=" + state + ", onlyReplaceable=" + onlyReplaceable + ", tileEntity=" + tileEntity + ", pos=" + pos + ", dimensionId=" + dimensionId + ", delay=" + delay + "]";
    }
}
