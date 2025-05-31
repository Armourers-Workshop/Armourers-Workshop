package moe.plushie.armourers_workshop.core.data.paint;

import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import net.minecraft.world.item.ItemStack;

public interface IItemPaintable {

    void setItemColor(ItemStack itemStack, SkinPaintColor paintColor);

    SkinPaintColor getItemColor(ItemStack itemStack);

    default SkinPaintColor getItemColor(ItemStack itemStack, SkinPaintColor defaultValue) {
        var paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            return paintColor;
        }
        return defaultValue;
    }
}
