package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.item.ItemStack;

public interface IItemColorProvider {

    IPaintColor getItemColor(ItemStack itemStack);
}
