package moe.plushie.armourers_workshop.library.block;

import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import moe.plushie.armourers_workshop.library.tileentity.GlobalSkinLibraryTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class GlobalSkinLibraryBlock extends AbstractHorizontalBlock {

    public GlobalSkinLibraryBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlobalSkinLibraryTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        if (ModContainerTypes.open(ModContainerTypes.SKIN_LIBRARY_GLOBAL, player, world, pos)) {
            return ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
    }
}
