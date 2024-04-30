package moe.plushie.armourers_workshop.core.block;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.compatibility.core.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
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
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return ModMenuTypes.SKINNING_TABLE.get().openMenu(player, IGlobalPos.create(level, blockPos));
    }
}
