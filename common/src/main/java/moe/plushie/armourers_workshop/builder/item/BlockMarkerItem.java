package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMarkerItem extends FlavouredItem {

    public BlockMarkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var blockPos = context.getClickedPos();
        var blockState = level.getBlockState(blockPos);
        if (blockState.hasProperty(SkinCubeBlock.MARKER)) {
            var direction = OptionalDirection.of(context.getClickedFace());
            if (direction.equals(SkinCubeBlock.getMarker(blockState))) {
                direction = OptionalDirection.NONE;
            }
            level.setBlock(blockPos, SkinCubeBlock.setMarker(blockState, direction), Constants.BlockFlags.BLOCK_UPDATE);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
