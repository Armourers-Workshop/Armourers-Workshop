package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.color.PaintColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;

import javax.annotation.Nullable;
import java.awt.*;

public abstract class ColoredItem extends Item {

    public ColoredItem(Item.Properties properties) {
        super(properties);
    }

    public static void setColor(ItemStack itemStack, PaintColor color) {
        itemStack.getOrCreateTag().putInt(AWConstants.NBT.COLOR, color.getValue());
    }

    @Nullable
    public static PaintColor getColor(ItemStack itemStack) {
        int color = getColorValue(itemStack);
        if (color != 0) {
            return PaintColor.of(color);
        }
        return null;
    }

    public static int getColorValue(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        if (!(itemStack.getItem() instanceof BottleItem)) {
            return 0;
        }
        CompoundNBT tag = itemStack.getTag();
        if (tag != null && tag.contains(AWConstants.NBT.COLOR)) {
            INBT nbt = tag.get(AWConstants.NBT.COLOR);
            if (nbt instanceof NumberNBT) {
                return ((NumberNBT) nbt).getAsInt();
            }
            if (nbt instanceof StringNBT) {
                Color color = ColorUtils.parseColor(nbt.getAsString());
                tag.putInt(AWConstants.NBT.COLOR, color.getRGB());
                return color.getRGB();
            }
            tag.remove(AWConstants.NBT.COLOR);
        }
        return 0;
    }

    public static ISkinPaintType getPaintType(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return SkinPaintTypes.NONE;
        }
        int color = getColorValue(itemStack);
        return PaintColor.getPaintType(color);
    }


    public static IItemColor getColorProvider(int tintIndex) {
        return (itemStack, tintIndex1) -> {
            if (tintIndex == tintIndex1) {
                PaintColor paintColor = getColor(itemStack);
                if (paintColor != null) {
                    return ColorUtils.getDisplayRGB(paintColor) | 0xff000000;
                }
            }
            return 0xffffffff;
        };
    }
}
