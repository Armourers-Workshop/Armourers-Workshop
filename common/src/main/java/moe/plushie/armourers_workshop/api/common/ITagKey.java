package moe.plushie.armourers_workshop.api.common;

public interface ITagKey<T> extends IRegistryKey<Object> {

    boolean contains(T val);
}
