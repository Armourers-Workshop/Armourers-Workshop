package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.world.item.ItemStack;

public interface IItemColorProvider {

    void setItemColor(ItemStack itemStack, IPaintColor paintColor);

    IPaintColor getItemColor(ItemStack itemStack);

    default IPaintColor getItemColor(ItemStack itemStack, IPaintColor defaultValue) {
        IPaintColor paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            return paintColor;
        }
        return defaultValue;
    }
}
