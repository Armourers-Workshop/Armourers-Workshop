package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class AbstractBlockImpl extends AbstractBlockImplA {
   protected AbstractBlockImpl(Properties properties) {
      super(properties);
   }

   protected BlockState updateShape(
      BlockState blockState,
      OpenDirection direction,
      BlockState blockState2,
      LevelAccessor levelAccessor,
      BlockPos blockPos,
      BlockPos blockPos2,
      Object context
   ) {
      return this.updateShape(blockState, AbstractDirection.unwrap(direction), blockState2, levelAccessor, blockPos, blockPos2);
   }

   public final BlockState updateShape(
      BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2
   ) {
      return blockState;
   }

   protected boolean skipRendering(BlockState blockState, BlockState blockState2, OpenDirection direction, Object context) {
      return super.skipRendering(blockState, blockState2, AbstractDirection.unwrap(direction));
   }

   public final boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
      return this.skipRendering(blockState, blockState2, AbstractDirection.wrap(direction), null);
   }

   protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, Object context) {
      super.neighborChanged(blockState, level, blockPos, block, null, bl);
   }

   protected void neighborChanged(
      BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable OpenOrientation orientation, boolean movedByPiston
   ) {
      this.neighborChanged(blockState, level, blockPos, block, (BlockPos)null, movedByPiston, null);
   }

   public final void neighborChanged(
      BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable Orientation orientation, boolean movedByPiston
   ) {
      this.neighborChanged(blockState, level, blockPos, block, (OpenOrientation)null, movedByPiston);
   }

   protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, Object context) {
   }

   protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
      // 26.2 calls this after the chunk has installed the replacement state.
      this.onRemove(state, level, pos, level.getBlockState(pos), movedByPiston, null);
   }

   protected VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
      return super.getOcclusionShape(blockState);
   }

   protected VoxelShape getOcclusionShape(BlockState blockState) {
      return this.getOcclusionShape(blockState, null, null, null);
   }

   protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, Object context) {
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
      this.entityInside(state, level, pos, entity, null);
   }

   protected boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
      return super.propagatesSkylightDown(blockState);
   }

   protected boolean propagatesSkylightDown(BlockState state) {
      return this.propagatesSkylightDown(state, null, null, null);
   }

   protected int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context) {
      return super.getLightDampening(blockState);
   }

   protected final int getLightDampening(BlockState state) {
      return this.getLightBlock(state, null, null, null);
   }

   protected int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context) {
      return super.getSignal(blockState, blockGetter, blockPos, AbstractDirection.unwrap(direction));
   }

   public final int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return this.getSignal(blockState, blockGetter, blockPos, AbstractDirection.wrap(direction), null);
   }

   protected int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context) {
      return super.getDirectSignal(blockState, blockGetter, blockPos, AbstractDirection.unwrap(direction));
   }

   public final int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return this.getDirectSignal(blockState, blockGetter, blockPos, AbstractDirection.wrap(direction), null);
   }

   protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, OpenDirection direction, Object context) {
      return super.getAnalogOutputSignal(blockState, level, blockPos, AbstractDirection.unwrap(direction));
   }

   protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, Direction direction) {
      return this.getAnalogOutputSignal(blockState, level, blockPos, AbstractDirection.wrap(direction), null);
   }
}
