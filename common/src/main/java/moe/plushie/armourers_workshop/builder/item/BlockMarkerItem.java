package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class BlockMarkerItem extends FlavouredItem {

    public BlockMarkerItem(Properties properties) {
        super(properties);
    }

    @Override
    protected OpenInteractionResult abi$useOn(UseOnContext context) {
        var level = context.getLevel();
        var blockPos = context.getClickedPos();
        var blockState = level.getBlockState(blockPos);
        if (blockState.hasProperty(SkinCubeBlock.MARKER)) {
            var direction = OptionalDirection.of(context.getClickedFace());
            if (direction.equals(SkinCubeBlock.getMarker(blockState))) {
                direction = OptionalDirection.NONE;
            }
            level.setBlock(blockPos, SkinCubeBlock.setMarker(blockState, direction), Constants.BlockFlags.BLOCK_UPDATE);
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.PASS;
    }
}
