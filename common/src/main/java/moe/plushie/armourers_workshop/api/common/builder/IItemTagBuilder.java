package moe.plushie.armourers_workshop.api.common.builder;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import net.minecraft.world.item.Item;

public interface IItemTagBuilder<T extends Item> extends IEntryBuilder<IItemTagKey<T>> {
}
