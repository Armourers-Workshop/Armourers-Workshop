package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.compatibility.core.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SkinningTableBlock extends AbstractHorizontalBlock {

    public SkinningTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return MenuManager.openMenu(ModMenuTypes.SKINNING_TABLE, level, blockPos, player);
    }
}
