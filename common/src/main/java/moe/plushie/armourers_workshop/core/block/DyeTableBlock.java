package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.core.blockentity.DyeTableBlockEntity;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DyeTableBlock extends AbstractHorizontalBlock implements AbstractBlockEntityProvider {

    public DyeTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity abi$createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.DYE_TABLE.get().create(level, blockPos, blockState);
    }

    @Override
    protected OpenInteractionResult abi$useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return player.openMenu(ModMenuTypes.DYE_TABLE, level, blockPos);
    }

    @Override
    protected void abi$onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        var blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            DataSerializers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockEntity.getItem(9));
        }
        super.abi$onRemove(state, level, blockPos, newState, p_196243_5_);
    }

    private DyeTableBlockEntity getBlockEntity(Level level, BlockPos blockPos) {
        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof DyeTableBlockEntity blockEntity1) {
            return blockEntity1;
        }
        return null;
    }
}
