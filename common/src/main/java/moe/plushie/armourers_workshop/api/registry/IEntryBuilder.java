package moe.plushie.armourers_workshop.api.registry;

@SuppressWarnings("unused")
public interface IEntryBuilder<T> {

    T build(String name);
}
