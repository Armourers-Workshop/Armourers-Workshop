package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

@Available("[16, 26)")
@SuppressWarnings({"deprecation", "NullableProblems"})
public class AbstractBlockImpl extends AbstractBlockImplA {

    protected AbstractBlockImpl(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateShape(BlockState blockState, OpenDirection direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2, Object context) {
        return super.updateShape(blockState, AbstractDirection.unwrap(direction), blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public final BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        return updateShape(blockState, AbstractDirection.wrap(direction), blockState2, levelAccessor, blockPos, blockPos2, null);
    }

    @Override
    protected boolean skipRendering(BlockState blockState, BlockState blockState2, OpenDirection direction, Object context) {
        return super.skipRendering(blockState, blockState2, AbstractDirection.unwrap(direction));
    }

    @Override
    public final boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return skipRendering(blockState, blockState2, AbstractDirection.wrap(direction), null);
    }

    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, Object context) {
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl);
    }

    @Override
    public final void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        neighborChanged(blockState, level, blockPos, block, blockPos2, bl, null);
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, Object context) {
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public final void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        onRemove(blockState, level, blockPos, blockState2, bl, null);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
        return super.getOcclusionShape(blockState, blockGetter, blockPos);
    }

    @Override
    public final VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return getOcclusionShape(blockState, blockGetter, blockPos, null);
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, Object context) {
        super.entityInside(blockState, level, blockPos, entity);
    }

    @Override
    public final void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        entityInside(blockState, level, blockPos, entity, null);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
        return super.propagatesSkylightDown(blockState, blockGetter, blockPos);
    }

    @Override
    public final boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return propagatesSkylightDown(blockState, blockGetter, blockPos, null);
    }

    @Override
    protected int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
        return super.getLightBlock(blockState, blockGetter, blockPos);
    }

    @Override
    public final int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return getLightBlock(blockState, blockGetter, blockPos, null);
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context) {
        return super.getSignal(blockState, blockGetter, blockPos, AbstractDirection.unwrap(direction));
    }

    @Override
    public final int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return getSignal(blockState, blockGetter, blockPos, AbstractDirection.wrap(direction), null);
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context) {
        return super.getDirectSignal(blockState, blockGetter, blockPos, AbstractDirection.unwrap(direction));
    }

    @Override
    public final int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return getDirectSignal(blockState, blockGetter, blockPos, AbstractDirection.wrap(direction), null);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, OpenDirection direction, Object context) {
        return super.getAnalogOutputSignal(blockState, level, blockPos);
    }

    @Override
    public final int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return getAnalogOutputSignal(blockState, level, blockPos, OpenDirection.NORTH, null);
    }
}
