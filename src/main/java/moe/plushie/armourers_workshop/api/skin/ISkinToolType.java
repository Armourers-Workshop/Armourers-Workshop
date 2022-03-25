package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

public interface ISkinToolType {
    ITag<Item> getTag();
}
