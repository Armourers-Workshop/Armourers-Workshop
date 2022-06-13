package moe.plushie.armourers_workshop.core.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPaintApplier {

    boolean isFullMode(World worldIn, BlockPos blockPos, ItemStack itemStack, ItemUseContext context);

    IPaintUpdater createPaintUpdater(ItemUseContext context);

    default boolean applyColor(ItemUseContext context) {
        if (shouldApplyColor(context)) {
            IPaintUpdater updater = createPaintUpdater(context);
            updater.begin(context);
            if (applyColor(context.getLevel(), context.getClickedPos(), context.getClickedFace(), updater, context)) {
                updater.commit(context);
                return true;
            }
        }
        return false;
    }

    default boolean applyColor(World world, BlockPos blockPos, Direction direction, IPaintUpdater updater, ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        TileEntity tileEntity = world.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintable) {
            IPaintable target = (IPaintable) tileEntity;
            // we will fill to all block faces when in full mode.
            Direction[] directions = {direction};
            if (isFullMode(world, blockPos, itemStack, context)) {
                directions = Direction.values();
            }
            for (Direction direction1 : directions) {
                if (target.shouldChangeColor(direction1)) {
                    updater.add(target, direction1, getMixedColor(target, direction1, itemStack, context));
                }
            }
            return true;
        }
        return false;
    }

    default IPaintColor getMixedColor(IPaintable target, Direction direction, ItemStack itemStack, ItemUseContext context) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            return paintColor;
        }
        return PaintColor.WHITE;
    }

    default boolean shouldApplyColor(ItemUseContext context) {
        // by default, applying colors only execute on the client side,
        // and then send the final color to the server side.
        return context.getLevel().isClientSide();
    }

    interface IPaintUpdater {

        void begin(ItemUseContext context);

        void add(IPaintable target, Direction direction, IPaintColor newColor);

        void commit(ItemUseContext context);
    }
}
