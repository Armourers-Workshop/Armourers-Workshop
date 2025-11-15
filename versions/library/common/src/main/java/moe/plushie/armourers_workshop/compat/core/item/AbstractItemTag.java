package moe.plushie.armourers_workshop.compat.core.item;

import moe.plushie.armourers_workshop.api.common.ITagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface AbstractItemTag extends ITagKey<Item> {

    boolean test(ItemStack itemStack);
}
