package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.ItemStack;

public interface IItemTag {

    boolean contains(ItemStack val);
}
