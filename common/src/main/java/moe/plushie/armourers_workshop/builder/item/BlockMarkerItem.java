package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;

public class BlockMarkerItem extends FlavouredItem {

    public BlockMarkerItem(Properties properties) {
        super(properties);
    }

    @Override
    protected OpenInteractionResult abi$useOn(IUseOnContext context) {
        var level = context.level();
        var blockPos = context.clickedPos();
        var blockState = level.getBlockState(blockPos);
        if (blockState.hasProperty(SkinCubeBlock.MARKER)) {
            var direction = OptionalDirection.of((OpenDirection) context.clickedFace());
            if (direction.equals(SkinCubeBlock.getMarker(blockState))) {
                direction = OptionalDirection.NONE;
            }
            level.setBlock(blockPos, SkinCubeBlock.setMarker(blockState, direction), Constants.BlockFlags.BLOCK_UPDATE);
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.PASS;
    }
}
