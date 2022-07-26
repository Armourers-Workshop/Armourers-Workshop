package moe.plushie.armourers_workshop.library.block;

import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModMenus;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.library.blockentity.SkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SkinLibraryBlock extends AbstractHorizontalBlock implements EntityBlock {

    public SkinLibraryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new SkinLibraryBlockEntity();
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (this == ModBlocks.SKIN_LIBRARY.get()) {
            MenuManager.openMenu(ModMenus.SKIN_LIBRARY, player, level, blockPos);
        }
        if (this == ModBlocks.SKIN_LIBRARY_CREATIVE.get()) {
            MenuManager.openMenu(ModMenus.SKIN_LIBRARY_CREATIVE, player, level, blockPos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        SkinLibraryBlockEntity tileEntity = ObjectUtils.safeCast(world.getBlockEntity(pos), SkinLibraryBlockEntity.class);
        if (tileEntity != null) {
            DataSerializers.dropContents(world, pos, tileEntity.getInventory());
        }
        super.onRemove(state, world, pos, newState, p_196243_5_);
    }
}
