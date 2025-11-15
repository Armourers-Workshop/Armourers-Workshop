package moe.plushie.armourers_workshop.core.data.paint;

import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPaintToolPicker {

    default OpenInteractionResult usePickTool(UseOnContext context) {
        if (shouldUsePickTool(context)) {
            var level = context.getLevel();
            var pos = context.getClickedPos();
            var facing = AbstractDirection.wrap(context.getClickedFace());
            return usePickTool(level, pos, facing, level.getBlockEntity(pos), context);
        }
        return OpenInteractionResult.PASS;
    }

    OpenInteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, UseOnContext context);

    default boolean shouldUsePickTool(UseOnContext context) {
        return true;
    }
}
