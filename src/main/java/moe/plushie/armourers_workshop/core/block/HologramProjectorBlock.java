package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.core.tileentity.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class HologramProjectorBlock extends AbstractHorizontalFaceBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public HologramProjectorBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(LIT, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos targetPos, boolean p_220069_6_) {
        HologramProjectorTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null) {
            tileEntity.updateBlockStates();
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HologramProjectorTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        if (ModContainerTypes.open(ModContainerTypes.HOLOGRAM_PROJECTOR, player, world, pos)) {
            return ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        HologramProjectorTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity != null) {
            InventoryHelper.dropContents(world, pos, tileEntity);
        }
        super.onRemove(state, world, pos, newState, p_196243_5_);
    }

    private HologramProjectorTileEntity getTileEntity(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof HologramProjectorTileEntity) {
            return (HologramProjectorTileEntity) tileEntity;
        }
        return null;
    }
}
