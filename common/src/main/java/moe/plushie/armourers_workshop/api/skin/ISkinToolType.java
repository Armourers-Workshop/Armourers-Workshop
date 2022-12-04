package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.world.item.ItemStack;

public interface ISkinToolType extends ISkinEquipmentType {

    /**
     * Does tool type contain the specified item?
     */
    boolean contains(ItemStack itemStack);
}
