package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.api.extend.IExtendedBlockHandler;
import moe.plushie.armourers_workshop.builder.blockentity.BoundingBoxBlockEntity;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoundingBoxBlock extends Block implements EntityBlock, IExtendedBlockHandler {

    public BoundingBoxBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    @Override
    public InteractionResult attackBlock(Level level, BlockPos blockPos, BlockState blockState, Direction direction, Player player, InteractionHand hand) {
        //
        BoundingBoxBlockEntity blockEntity = ObjectUtils.safeCast(level.getBlockEntity(blockPos), BoundingBoxBlockEntity.class);
        if (blockEntity != null && blockEntity.isValid() && blockEntity.hasColors()) {
            blockEntity.clearArmourerTextureColors();
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new BoundingBoxBlockEntity();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        // a transparent block, should not blocking the transmission of light
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        if (ModDebugger.boundingBox) {
            return RenderShape.MODEL;
        }
        return RenderShape.INVISIBLE;
    }
}
