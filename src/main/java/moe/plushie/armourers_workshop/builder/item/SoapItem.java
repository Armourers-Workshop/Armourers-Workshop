package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.tileentity.BoundingBoxTileEntity;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.item.impl.IPaintApplier;
import moe.plushie.armourers_workshop.utils.BlockPaintUpdater;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoapItem extends FlavouredItem implements IPaintApplier {

    public SoapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (applyColor(context)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return super.useOn(context);
    }


    @Override
    public IPaintUpdater createPaintUpdater(ItemUseContext context) {
        return new BlockPaintUpdater(this);
    }

    @Override
    public IPaintColor getMixedColor(IPaintable target, Direction direction, ItemStack itemStack, ItemUseContext context) {
        if (target instanceof BoundingBoxTileEntity) {
            return PaintColor.CLEAR;
        }
        return PaintColor.WHITE;
    }

    @Override
    public boolean isFullMode(World worldIn, BlockPos blockPos, ItemStack itemStack, ItemUseContext context) {
        return false;
    }
}
