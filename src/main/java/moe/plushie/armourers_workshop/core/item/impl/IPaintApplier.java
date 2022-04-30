package moe.plushie.armourers_workshop.core.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumMap;

public interface IPaintApplier {

    boolean isFullMode(World worldIn, BlockPos blockPos, ItemStack itemStack, @Nullable PlayerEntity player);


    default boolean applyColor(ItemUseContext context) {
        return applyColor(context.getLevel(), context.getClickedPos(), context.getClickedFace(), context.getItemInHand(), context.getPlayer());
    }

    default boolean applyColor(World worldIn, BlockPos blockPos, Direction direction, ItemStack itemStack, @Nullable PlayerEntity player) {
        TileEntity tileEntity = worldIn.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintable) {
            IPaintable paintable = (IPaintable) tileEntity;
            // we will fill to all block faces when in full mode.
            Direction[] directions = {direction};
            if (isFullMode(worldIn, blockPos, itemStack, player)) {
                directions = Direction.values();
            }
            // we must commit all changes at one to avoid triggering block updates repeated.
            EnumMap<Direction, IPaintColor> colors = new EnumMap<>(Direction.class);
            for (Direction direction1 : directions) {
                if (shouldApplyColor(worldIn, paintable, direction1, itemStack, player)) {
                    colors.put(direction1, getMixedColor(worldIn, paintable, direction1, itemStack, player));
                }
            }
            if (!colors.isEmpty()) {
                paintable.setColors(colors);
            }
            return true;
        }
        return false;
    }

    default IPaintColor getMixedColor(World worldIn, IPaintable paintable, Direction direction, ItemStack itemStack, @Nullable PlayerEntity player) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            return paintColor;
        }
        return PaintColor.WHITE;
    }

    default boolean shouldApplyColor(World worldIn, IPaintable paintable, Direction direction, ItemStack itemStack, @Nullable PlayerEntity player) {
        return true;
    }
}
