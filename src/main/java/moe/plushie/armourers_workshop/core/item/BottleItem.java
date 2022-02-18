package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.utils.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class BottleItem extends Item {

    public static final String NBT_KEY_COLOR = "color";

    public BottleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        int color = getColor(itemStack);
        if (color != 0) {
            return PaintColor.getPaintType(color) != SkinPaintTypes.NORMAL;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        super.appendHoverText(itemStack, world, tooltips, flag);
        PaintColor paintColor = getPaintColor(itemStack);
        if (paintColor != null) {
            String hexColor = String.format("#%02x%02x%02x", paintColor.getRed(), paintColor.getGreen(), paintColor.getBlue());
            ITextComponent paintName = TranslateUtils.translate("paintType." + paintColor.getPaintType().getRegistryName());
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.colour", paintColor.getRGB()));
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.hex", hexColor));
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.paintType", paintName));
        } else {
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.empty"));
        }
    }

    public static void setColor(ItemStack itemStack, PaintColor color) {
        itemStack.getOrCreateTag().putInt(NBT_KEY_COLOR, color.getValue());
    }

    public static int getColor(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        if (!(itemStack.getItem() instanceof BottleItem)) {
            return 0;
        }
        CompoundNBT tag = itemStack.getTag();
        if (tag != null && tag.contains(NBT_KEY_COLOR)) {
            INBT nbt = tag.get(NBT_KEY_COLOR);
            if (nbt instanceof NumberNBT) {
                return ((NumberNBT) nbt).getAsInt();
            }
            if (nbt instanceof StringNBT) {
                Color color = ColorUtils.parseColor(nbt.getAsString());
                tag.putInt(NBT_KEY_COLOR, color.getRGB());
                return color.getRGB();
            }
            tag.remove(NBT_KEY_COLOR);
        }
        return 0;
    }

    public static PaintColor getPaintColor(ItemStack itemStack) {
        int color = getColor(itemStack);
        if (color != 0) {
            return PaintColor.of(color);
        }
        return null;
    }

    public static ISkinPaintType getPaintType(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return SkinPaintTypes.NONE;
        }
        int color = getColor(itemStack);
        return PaintColor.getPaintType(color);
    }

}
