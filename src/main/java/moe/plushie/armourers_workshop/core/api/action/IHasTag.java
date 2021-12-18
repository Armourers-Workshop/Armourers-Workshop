package moe.plushie.armourers_workshop.core.api.action;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

public interface IHasTag {

    ITag<Item> getTag();
}
