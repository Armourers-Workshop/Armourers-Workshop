package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.PaintColor;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
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

    @OnlyIn(Dist.CLIENT)
    public static float isEmpty(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        ISkinPaintType paintType = BottleItem.getPaintType(itemStack);
        return paintType == SkinPaintTypes.NONE ? 1 : 0;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        int color = getColorValue(itemStack);
        if (color != 0) {
            return PaintColor.getPaintType(color) != SkinPaintTypes.NORMAL;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flag) {
        PaintColor paintColor = getColor(itemStack);
        tooltips.add(TranslateUtils.translate("item.armourers_workshop.dye-bottle.flavour"));
        if (paintColor != null) {
            String hexColor = String.format("#%02x%02x%02x", paintColor.getRed(), paintColor.getGreen(), paintColor.getBlue());
            ITextComponent paintName = TranslateUtils.translate("paintType." + paintColor.getPaintType().getRegistryName());
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.colour", paintColor.getRGB() & 0xffffff));
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.hex", hexColor));
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.paintType", paintName));
        } else {
            tooltips.add(TranslateUtils.translate("item.armourers_workshop.rollover.empty"));
        }
    }
}
