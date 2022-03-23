package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class BottleItem extends ColoredItem {

    public BottleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        if (hasColor(itemStack)) {
            return getPaintType(itemStack) != SkinPaintTypes.NORMAL;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        PaintColor paintColor = getColor(itemStack);
        if (paintColor != null) {
            String hexColor = String.format("#%02x%02x%02x", paintColor.getRed(), paintColor.getGreen(), paintColor.getBlue());
            ITextComponent paintName = TranslateUtils.subtitle("paintType." + paintColor.getPaintType().getRegistryName());
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.colour", paintColor.getRGB() & 0xffffff));
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hex", hexColor));
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.paintType", paintName));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
    }
}
