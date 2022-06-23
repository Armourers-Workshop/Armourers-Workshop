package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.core.tileentity.DyeTableTileEntity;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class DyeTableBlock extends AbstractHorizontalBlock {

    public DyeTableBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DyeTableTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        if (ModContainerTypes.open(ModContainerTypes.DYE_TABLE, player, world, pos)) {
            return ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        DyeTableTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), tileEntity.getItem(9));
        }
        super.onRemove(state, world, pos, newState, p_196243_5_);
    }

    private DyeTableTileEntity getTileEntity(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof DyeTableTileEntity) {
            return (DyeTableTileEntity) tileEntity;
        }
        return null;
    }
}
