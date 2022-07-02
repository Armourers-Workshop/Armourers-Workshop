package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IItemBlockSelector {

    IPaintColor getItemColor(ItemStack itemStack);
}
