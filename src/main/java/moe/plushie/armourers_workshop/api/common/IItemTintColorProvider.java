package moe.plushie.armourers_workshop.api.common;

import net.minecraft.item.ItemStack;

public interface IItemTintColorProvider {

    int getTintColor(ItemStack itemStack, int index);
}
