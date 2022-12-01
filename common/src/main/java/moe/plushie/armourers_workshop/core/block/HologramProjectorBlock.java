package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.api.common.IBlockEntityProvider;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class HologramProjectorBlock extends AbstractAttachedHorizontalBlock implements IBlockEntityProvider {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public HologramProjectorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL).setValue(LIT, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.HOLOGRAM_PROJECTOR.create(level, blockPos, blockState);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        HologramProjectorBlockEntity tileEntity = getTileEntity(level, blockPos);
        if (tileEntity != null) {
            tileEntity.updateBlockStates();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, LIT);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return MenuManager.openMenu(ModMenuTypes.HOLOGRAM_PROJECTOR, level.getBlockEntity(blockPos), player);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.is(blockState2.getBlock())) {
            return;
        }
        HologramProjectorBlockEntity tileEntity = getTileEntity(level, blockPos);
        if (tileEntity != null) {
            DataSerializers.dropContents(level, blockPos, tileEntity);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    private HologramProjectorBlockEntity getTileEntity(Level level, BlockPos blockPos) {
        BlockEntity tileEntity = level.getBlockEntity(blockPos);
        if (tileEntity instanceof HologramProjectorBlockEntity) {
            return (HologramProjectorBlockEntity) tileEntity;
        }
        return null;
    }
}
