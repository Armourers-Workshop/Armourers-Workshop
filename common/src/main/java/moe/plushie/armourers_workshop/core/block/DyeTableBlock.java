package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.compatibility.core.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.core.blockentity.DyeTableBlockEntity;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
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
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.DYE_TABLE.get().create(level, blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return ModMenuTypes.DYE_TABLE.get().openMenu(player, level.getBlockEntity(blockPos));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        DyeTableBlockEntity blockEntity = getBlockEntity(level, blockPos);
        if (blockEntity != null) {
            DataSerializers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockEntity.getItem(9));
        }
        super.onRemove(state, level, blockPos, newState, p_196243_5_);
    }

    private DyeTableBlockEntity getBlockEntity(Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof DyeTableBlockEntity) {
            return (DyeTableBlockEntity) blockEntity;
        }
        return null;
    }
}
