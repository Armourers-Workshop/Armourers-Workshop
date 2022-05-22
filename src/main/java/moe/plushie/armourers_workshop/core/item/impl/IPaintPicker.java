package moe.plushie.armourers_workshop.core.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPaintPicker {

    default boolean pickColor(ItemUseContext context) {
        if (shouldPickColor(context)) {
            return pickColor(context.getLevel(), context.getClickedPos(), context.getClickedFace(), context);
        }
        return false;
    }

    default boolean pickColor(World worldIn, BlockPos blockPos, Direction direction, ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        TileEntity tileEntity = worldIn.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintProvider) {
            IPaintColor color = ((IPaintProvider) tileEntity).getColor();
            ColorUtils.setColor(itemStack, color);
            return true;
        }
        return false;
    }

    default boolean shouldPickColor(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        return player == null || player.isShiftKeyDown();
    }

}
