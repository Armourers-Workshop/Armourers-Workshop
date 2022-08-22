package moe.plushie.armourers_workshop.api.common.builder;

import moe.plushie.armourers_workshop.api.common.ITagKey;
import net.minecraft.world.item.Item;

public interface IItemTagBuilder<T extends Item> extends IEntryBuilder<ITagKey<T>> {
}
