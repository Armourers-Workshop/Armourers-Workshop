package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IItemGroupProvider {

    void fillItemGroup(List<ItemStack> results, IItemGroup group);
}
