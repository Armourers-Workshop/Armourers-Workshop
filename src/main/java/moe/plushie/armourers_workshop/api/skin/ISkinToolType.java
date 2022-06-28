package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.item.Item;

public interface ISkinToolType {

    /**
     * Does tool type contain the specified item?
     */
    boolean contains(Item item);
}
