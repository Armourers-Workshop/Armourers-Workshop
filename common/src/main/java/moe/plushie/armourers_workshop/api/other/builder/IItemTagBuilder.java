package moe.plushie.armourers_workshop.api.other.builder;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public interface IItemTagBuilder<T extends Item> extends IEntryBuilder<Tag<T>> {
}
