package moe.plushie.armourers_workshop.core.item.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPaintToolPicker {

    default InteractionResult usePickTool(UseOnContext context) {
        if (shouldUsePickTool(context)) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Direction dir = context.getClickedFace();
            return usePickTool(level, pos, dir, level.getBlockEntity(pos), context);
        }
        return InteractionResult.PASS;
    }

    InteractionResult usePickTool(Level level, BlockPos pos, Direction dir, BlockEntity tileEntity, UseOnContext context);

    default boolean shouldUsePickTool(UseOnContext context) {
        return true;
    }
}
