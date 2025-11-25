package moe.plushie.armourers_workshop.core.data.paint;

import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPaintToolPicker {

    default OpenInteractionResult usePickTool(IUseOnContext context) {
        if (shouldUsePickTool(context)) {
            var level = context.level();
            var pos = context.clickedPos();
            var facing = (OpenDirection) context.clickedFace();
            return usePickTool(level, pos, facing, level.getBlockEntity(pos), context);
        }
        return OpenInteractionResult.PASS;
    }

    OpenInteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, IUseOnContext context);

    default boolean shouldUsePickTool(IUseOnContext context) {
        return true;
    }
}
