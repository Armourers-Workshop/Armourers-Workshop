package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.blockentity.BoundingBoxBlockEntity;
import moe.plushie.armourers_workshop.compat.core.block.AbstractBlock;
import moe.plushie.armourers_workshop.compat.core.block.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BoundingBoxBlock extends AbstractBlock implements AbstractBlockEntityProvider {

    public BoundingBoxBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity abi$createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.BOUNDING_BOX.get().create(level, blockPos, blockState);
    }

    @Override
    protected boolean abi$dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected void abi$setPlacedBy(BlockState blockState, Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    @Override
    protected OpenInteractionResult abi$onAttack(Level level, BlockPos blockPos, BlockState blockState, Direction direction, Player player, OpenInteractionHand hand) {
        //
        if (level.getBlockEntity(blockPos) instanceof BoundingBoxBlockEntity blockEntity && blockEntity.isValid() && blockEntity.hasColors()) {
            blockEntity.clearArmourerTextureColors();
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.FAIL;
    }

    @Override
    protected boolean abi$propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        // a transparent block, should not blocking the transmission of light
        return true;
    }

    @Override
    protected VoxelShape abi$getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (blockGetter.getBlockEntity(blockPos) instanceof BoundingBoxBlockEntity blockEntity && blockEntity.isValid()) {
            return Shapes.block();
        }
        return Shapes.empty();
    }

    @Override
    protected RenderShape abi$getRenderShape(BlockState state) {
        if (ModDebugger.boundingBox) {
            return RenderShape.MODEL;
        }
        return RenderShape.INVISIBLE;
    }
}
