package moe.plushie.armourers_workshop.library.block;

import moe.plushie.armourers_workshop.compat.core.block.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GlobalSkinLibraryBlock extends AbstractHorizontalBlock implements AbstractBlockEntityProvider {

    public GlobalSkinLibraryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity abi$createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.SKIN_LIBRARY_GLOBAL.get().create(level, blockPos, blockState);
    }

    @Override
    protected OpenInteractionResult abi$useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return player.openMenu(ModMenuTypes.SKIN_LIBRARY_GLOBAL, level, blockPos);
    }
}
