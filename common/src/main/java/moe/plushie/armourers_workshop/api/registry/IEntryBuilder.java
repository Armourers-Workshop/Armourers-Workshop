package moe.plushie.armourers_workshop.api.registry;

public interface IEntryBuilder<T> {

    T build(String name);
}
