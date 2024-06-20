package moe.plushie.armourers_workshop.api.data;

public interface IGenericProperty<S, T> {

    void set(S source, T value);

    T get(S source);

    default T getOrDefault(S source, T defaultValue) {
        T value = get(source);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
}
