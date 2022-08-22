package moe.plushie.armourers_workshop.api.common;

import net.minecraft.tags.Tag;

public interface ITagKey<T> extends IRegistryKey<Tag<T>> {

    boolean contains(T val);
}
