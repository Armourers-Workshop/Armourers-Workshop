package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.builder.blockentity.ColorMixerBlockEntity;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.compatibility.core.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class ColorMixerBlock extends AbstractHorizontalBlock implements AbstractBlockEntityProvider, IBlockTintColorProvider {

    public ColorMixerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.COLOR_MIXER.get().create(level, blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
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
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return ModMenuTypes.COLOR_MIXER.get().openMenu(player, level.getBlockEntity(blockPos));
    }

    @Override
    public int getTintColor(BlockState blockState, BlockGetter reader, BlockPos blockPos, int index) {
        if (reader == null || blockPos == null || index != 1) {
            return 0xffffffff;
        }
        if (reader.getBlockEntity(blockPos) instanceof ColorMixerBlockEntity blockEntity) {
            return blockEntity.getColor().getRGB() | 0xff000000;
        }
        return 0xffffffff;
    }
}
