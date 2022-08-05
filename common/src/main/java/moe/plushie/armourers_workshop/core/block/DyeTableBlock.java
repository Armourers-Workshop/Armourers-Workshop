package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.api.common.IBlockEntityProvider;
import moe.plushie.armourers_workshop.core.blockentity.DyeTableBlockEntity;
import moe.plushie.armourers_workshop.init.ModBlockEntities;
import moe.plushie.armourers_workshop.init.ModMenus;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DyeTableBlock extends AbstractHorizontalBlock implements IBlockEntityProvider {

    public DyeTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return ModBlockEntities.DYE_TABLE.get().create();
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (MenuManager.openMenu(ModMenus.DYE_TABLE, player, level, blockPos)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        DyeTableBlockEntity tileEntity = getTileEntity(level, blockPos);
        if (tileEntity != null) {
            DataSerializers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), tileEntity.getItem(9));
        }
        super.onRemove(state, level, blockPos, newState, p_196243_5_);
    }

    private DyeTableBlockEntity getTileEntity(Level level, BlockPos blockPos) {
        BlockEntity tileEntity = level.getBlockEntity(blockPos);
        if (tileEntity instanceof DyeTableBlockEntity) {
            return (DyeTableBlockEntity) tileEntity;
        }
        return null;
    }
}
