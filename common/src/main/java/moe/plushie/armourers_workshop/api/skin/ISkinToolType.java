package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ISkinToolType {

    /**
     * Does tool type contain the specified item?
     */
    boolean contains(ItemStack itemStack);
}
