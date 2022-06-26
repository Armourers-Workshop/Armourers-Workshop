package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IItemBlockSelector {

    @Nullable
    default Block getBlock(ItemStack itemStack) {
        return null;
    }

    IPaintColor getItemColor(ItemStack itemStack);
}
