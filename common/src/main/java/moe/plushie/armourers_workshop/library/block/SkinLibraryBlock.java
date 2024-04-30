package moe.plushie.armourers_workshop.library.block;

import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.compatibility.core.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.library.blockentity.SkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SkinLibraryBlock extends AbstractHorizontalBlock implements AbstractBlockEntityProvider {

    public SkinLibraryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.SKIN_LIBRARY.get().create(level, blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (this == ModBlocks.SKIN_LIBRARY_CREATIVE.get()) {
            return ModMenuTypes.SKIN_LIBRARY_CREATIVE.get().openMenu(player, level.getBlockEntity(blockPos));
        }
        if (this == ModBlocks.SKIN_LIBRARY.get()) {
            return ModMenuTypes.SKIN_LIBRARY.get().openMenu(player, level.getBlockEntity(blockPos));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (state.is(newState.getBlock())) {
            return;
        }
        SkinLibraryBlockEntity blockEntity = ObjectUtils.safeCast(level.getBlockEntity(pos), SkinLibraryBlockEntity.class);
        if (blockEntity != null) {
            DataSerializers.dropContents(level, pos, blockEntity.getInventory());
        }
        super.onRemove(state, level, pos, newState, p_196243_5_);
    }
}
