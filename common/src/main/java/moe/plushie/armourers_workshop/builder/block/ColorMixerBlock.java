package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.blockentity.ColorMixerBlockEntity;
import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ColorMixerBlock extends AbstractHorizontalBlock implements AbstractBlockEntityProvider {

    public ColorMixerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity abi$createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.COLOR_MIXER.get().create(level, blockPos, blockState);
    }

    @Override
    protected void abi$createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected BlockState abi$getStateForPlacement(BlockPlaceContext context) {
        for (var direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
            } else {
                return this.defaultBlockState().setValue(FACING, direction.getOpposite());
            }
        }
        return null;
    }

    @Override
    protected OpenInteractionResult abi$useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return player.openMenu(ModMenuTypes.COLOR_MIXER, level, blockPos);
    }

    @Override
    protected int abi$getModelTintColor(BlockState blockState, @Nullable BlockGetter level, @Nullable BlockPos blockPos, int layerIndex) {
        if (level == null || blockPos == null || layerIndex != 1) {
            return 0xffffffff;
        }
        if (level.getBlockEntity(blockPos) instanceof ColorMixerBlockEntity blockEntity) {
            return blockEntity.color().argb() | 0xff000000;
        }
        return 0xffffffff;
    }
}
