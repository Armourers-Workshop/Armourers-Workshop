package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.core.utils.ContainerOpener;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class HologramProjectorBlock extends HorizontalFaceBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public HologramProjectorBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(LIT, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        updatePoweredState(world, p_220069_3_);
    }

//    @Override
//    public void tick(BlockState state, ServerWorld world, BlockPos p_225534_3_, Random p_225534_4_) {
//        if (state.getValue(LIT) && !world.hasNeighborSignal(p_225534_3_)) {
//            world.setBlock(p_225534_3_, state.cycle(LIT), 2);
//        }
//    }

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
        ContainerOpener.open(HologramProjectorContainer.TYPE, player, IWorldPosCallable.create(world, pos));
        return ActionResultType.CONSUME;
    }

    private void updatePoweredState(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof HologramProjectorTileEntity) {
            ((HologramProjectorTileEntity) tileEntity).updatePowerStats();
        }
    }
}
