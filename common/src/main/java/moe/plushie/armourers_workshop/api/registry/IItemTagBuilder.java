package moe.plushie.armourers_workshop.api.registry;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public interface IItemTagBuilder<T extends Item> extends IEntryBuilder<Tag<T>> {
}
