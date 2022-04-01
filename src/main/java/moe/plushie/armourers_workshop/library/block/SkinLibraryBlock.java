package moe.plushie.armourers_workshop.library.block;

import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import moe.plushie.armourers_workshop.library.tileentity.SkinLibraryTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class SkinLibraryBlock extends AbstractHorizontalBlock {

    public SkinLibraryBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SkinLibraryTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.CONSUME;
        }
        if (this == ModBlocks.SKIN_LIBRARY) {
            ModContainerTypes.open(ModContainerTypes.SKIN_LIBRARY, player, IWorldPosCallable.create(world, pos));
        }
        if (this == ModBlocks.SKIN_LIBRARY_CREATIVE) {
            ModContainerTypes.open(ModContainerTypes.SKIN_LIBRARY_CREATIVE, player, IWorldPosCallable.create(world, pos));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof SkinLibraryTileEntity) {
            InventoryHelper.dropContents(world, pos, ((SkinLibraryTileEntity) tileEntity).getInventory());
        }
        super.onRemove(state, world, pos, newState, p_196243_5_);
    }
}
