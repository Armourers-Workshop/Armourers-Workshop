package moe.plushie.armourers_workshop.core.item.impl;

import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPaintToolPicker {

    default ActionResultType usePickTool(ItemUseContext context) {
        if (shouldUsePickTool(context)) {
            World world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Direction dir = context.getClickedFace();
            return usePickTool(world, pos, dir, world.getBlockEntity(pos), context);
        }
        return ActionResultType.PASS;
    }

    ActionResultType usePickTool(World world, BlockPos pos, Direction dir, TileEntity tileEntity, ItemUseContext context);

    default boolean shouldUsePickTool(ItemUseContext context) {
        return true;
    }
}
