package moe.plushie.armourers_workshop.core.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IPaintPicker {

    default boolean pickColor(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return false;
        }
        return pickColor(context.getLevel(), context.getClickedPos(), context.getItemInHand(), context.getPlayer());
    }

    default boolean pickColor(World worldIn, BlockPos blockPos, ItemStack itemStack, @Nullable PlayerEntity player) {
        TileEntity tileEntity = worldIn.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintProvider) {
            IPaintColor color = ((IPaintProvider) tileEntity).getColor();
            ColorUtils.setColor(itemStack, color);
            return true;
        }
        return false;
    }
}
